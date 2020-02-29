package org.rug.data.characteristics.smells;

import org.apache.tinkerpop.gremlin.process.computer.traversal.step.map.ShortestPath;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.rug.data.labels.VertexLabel;
import org.rug.data.smells.CDSmell;

import java.util.List;

public class ParentCentralityCharacteristic extends AbstractSmellCharacteristic {

    TinkerGraph pCTree;

    /**
     * Sets up the name of this smell characteristic.
     *
     */
    protected ParentCentralityCharacteristic() {

        super("parentCentrality");

        //PCTCreation(source);

    }

    @Override
    public String visit(CDSmell smell) {
        if (smell.getLevel().isArchitecturalLevel()) {

            pCTree = PCTCreation(smell.getTraversalSource());

            //var subGraph = getSubGraph(smell);
            //subGraph = measureBetweennessCentrality(subGraph);

            //var subgraph = measureBetweennessCentrality(getSubGraph(smell));

            //return measureParentalCentrality(subgraph);

            return measureParentalCentrality(measureBetweennessCentrality(getSubGraph(smell)));
        }


        return NO_VALUE;
    }

    /**
     * Creates the PCTree for the packages of the graph
     * @param source The graph that will be handled
     */
    protected TinkerGraph PCTCreation(GraphTraversalSource source) {

        var tree = TinkerGraph.open();
        var g = tree.traversal();

        // Get only packages
        var vertexList = source
                .V().hasLabel(P.within(VertexLabel.getComponentStrings()))
                .values("name")
                .toList();

        // Split each package with "."
        for (int i = 0; i < vertexList.size(); i++) {

            var vList = vertexList.get(i).toString().split("\\.");

            // Construct all the hierarchic package list
            for (int j = 1; j < vList.length; j++) {
                vList[j] = vList[j - 1] + "." + vList[j];
            }

            // Create vertex if not exists
            for (int j = 0; j < vList.length; j++) {
                if (!g.V()
                        .has("name", vList[j])
                        .hasNext()) {
                    g.addV("package").property("name", vList[j]).next();
                }
            }

            // Create edge between subpackages if not exists
            for (int j = 1; j < vList.length; j++) {
                if (!g.V()
                        .has("package", "name", vList[j-1])
                        .out("child").has("package", "name", vList[j])
                        .hasNext()) {
                    g.addE("child")
                            .from(g.V().has("package", "name", vList[j - 1]))
                            .to(g.V().has("package", "name", vList[j]))
                            .next();
                }
            }
        }

        return tree;
    }

    /**
     * Calculates the subGraph that the parental centrality will be measured
     * @param smell The smell to calculate this smell on
     * @return The subGraph taken according to the smell
     */
    protected TinkerGraph getSubGraph(CDSmell smell) {

        var source = smell.getTraversalSource();

        var subGraph = TinkerGraph.open();

        var subGraphTraversal = subGraph.traversal();

        // Extract only affected vertices from graph
        var affectedElementsNames = smell.getAffectedElementsNames();
        for (String name : affectedElementsNames) {
            subGraphTraversal.addV()
                    .property("name", name)
                    .next();
        }

        // Extract only edges between affected vertices from graph and create create edges of subGraph
        source.V(smell.getAffectedElements())
                .outE()
                .where(__.inV().is(P.within(smell.getAffectedElements())))
                .forEachRemaining(edge -> {
                    subGraphTraversal.addE(edge.label())
                            .from(subGraphTraversal.V().has("name", edge.vertices(Direction.OUT).next().value("name").toString()))
                            .to(subGraphTraversal.V().has( "name", edge.vertices(Direction.IN).next().value("name").toString()))
                            .next();
                });

        return subGraph;
    }

    /**
     * Measures the betweenness centrality of every vertex of the graph
     * @param graph The graph that the betweenness centrality will be measured
     * @return The same graph with a property about betweenness centrality for each vertex
     */
    protected TinkerGraph measureBetweennessCentrality(TinkerGraph graph) {

        // Measure Betweenness Centrality for each vertex in subGraph
        graph.traversal().withComputer()
                .V()
                .shortestPath()
                .with(ShortestPath.edges, Direction.OUT)
                .unfold()
                .groupCount()
                .by("name")
                .next()
                .forEach((k, v) -> {
                    graph.traversal()
                            .V().has("name", k)
                            .property("between", v)
                            .next();
                });

        return graph;
    }

    /**
     * Measures the parental centrality of the smell
     * @param graph The graph that the parental centrality will be measured
     * @return The value of the parental centrality
     */
    protected String measureParentalCentrality(TinkerGraph graph) {

        // Extract vertices of graph
        List<Object> vertexesList = graph.traversal()
                .V()
                .values("name")
                .toList();

        // Initialize Ep and Ep+ values
        int ep = 0;
        int epPlus = 0;

        // Loop between all pairs of vertices
        for (int child = 0; child < vertexesList.size(); child++) {
            for (int parent = 0; parent < vertexesList.size(); parent++) {
                // Check if the pair belongs to Ep
                // (Edge from child to parent)
                if (
                        graph.traversal()
                                .V().has("name", vertexesList.get(child))
                                .out().has("name", vertexesList.get(parent))
                                .hasNext()
                        &&
                        pCTree.traversal().withComputer()
                                .V().has("name", vertexesList.get(parent))
                                .shortestPath()
                                .with(ShortestPath.edges, Direction.OUT)
                                .with(ShortestPath.target, __.has("name", vertexesList.get(child)))
                                .hasNext()
                ) {

                    ep++;

                    // Check if the pair belongs to Ep+
                    // (Parent Betweenness Centrality > Child Betweenness Centrality)
                    int betweennessParent = Integer.parseInt(graph.traversal()
                            .V().has("name", vertexesList.get(parent))
                            .values("between")
                            .toList()
                            .get(0)
                            .toString());


                    int betweennessChild = Integer.parseInt(graph.traversal()
                            .V().has("name", vertexesList.get(child))
                            .values("between")
                            .toList()
                            .get(0)
                            .toString());

                    System.out.println(vertexesList.get(parent) + " " + betweennessParent + ", " + vertexesList.get(child) + " " + betweennessChild);

                    if (betweennessParent > betweennessChild)
                        epPlus++;
                }
            }
        }

        if (ep == 0)
            return "undefined";

        return String.format("%.2f", (double) epPlus / ep);
    }
    
}
