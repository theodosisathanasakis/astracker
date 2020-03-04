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
    }

    @Override
    public String visit(CDSmell smell) {
        if (smell.getLevel().isArchitecturalLevel()) {

            pCTree = PCTCreation(smell.getTraversalSource());
            var subGraph = getSubGraph(smell);
            subGraph = measureBetweennessCentrality(subGraph);

            return measureParentalCentrality(subGraph);
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

        var vertexList = source
                .V().hasLabel(P.within(VertexLabel.getComponentStrings()))
                .values("name")
                .toList();

        for (int i = 0; i < vertexList.size(); i++) {

            var vList = vertexList.get(i).toString().split("\\.");

            for (int j = 1; j < vList.length; j++) {
                vList[j] = vList[j - 1] + "." + vList[j];
            }

            for (int j = 0; j < vList.length; j++) {
                if (!g.V()
                        .has("name", vList[j])
                        .hasNext()) {
                    g.addV("package").property("name", vList[j]).next();
                }
            }

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

        var affectedElementsNames = smell.getAffectedElementsNames();
        for (String name : affectedElementsNames) {
            subGraphTraversal.addV()
                    .property("name", name)
                    .next();
        }

        source.V(smell.getAffectedElements())
                .outE()
                .where(__.inV().is(P.within(smell.getAffectedElements())))
                .forEachRemaining(edge -> {
                    subGraphTraversal.addE(edge.label())
                            .from(subGraphTraversal.V().has("name",  edge.vertices(Direction.OUT).next().value("name").toString()))
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
     * Checks if the edge belongs to the Ep set
     * @param graph The that will be checked
     * @param child The hypothetical child vertex
     * @param parent The hypothetical parent vertex
     * @return True if the edge belongs to the Ep set
     */
    private boolean checkEp(TinkerGraph graph,  Object child, Object parent) {

        return graph.traversal()
                        .V().has("name", child)
                        .out().has("name", parent)
                        .hasNext()
                &&
                pCTree.traversal().withComputer()
                        .V().has("name", parent)
                        .shortestPath()
                        .with(ShortestPath.edges, Direction.OUT)
                        .with(ShortestPath.target, __.has("name", child))
                        .hasNext();
    }

    /**
     * Checks if the edge belongs to the Ep+ set
     * @param graph The that will be checked
     * @param child The hypothetical child vertex
     * @param parent The hypothetical parent vertex
     * @return True if the edge belongs to the Ep+ set
     */
    private boolean checkEpPlus(TinkerGraph graph,  Object child, Object parent) {

        int betweennessParent = Integer.parseInt(graph.traversal()
                .V().has("name", parent)
                .values("between")
                .toList()
                .get(0)
                .toString());


        int betweennessChild = Integer.parseInt(graph.traversal()
                .V().has("name", child)
                .values("between")
                .toList()
                .get(0)
                .toString());

        return betweennessParent > betweennessChild;
    }
    
    /**
     * Measures the parental centrality of the smell
     * @param graph The graph that the parental centrality will be measured
     * @return The value of the parental centrality
     */
    protected String measureParentalCentrality(TinkerGraph graph) {

        List<Object> verticesList = graph.traversal()
                .V()
                .values("name")
                .toList();

        int ep = 0;
        int epPlus = 0;

        for (int child = 0; child < verticesList.size(); child++) {
            for (int parent = 0; parent < verticesList.size(); parent++) {
                if (checkEp(graph, verticesList.get(child), verticesList.get(parent))) {
                    ep++;

                    if (checkEpPlus(graph, verticesList.get(child), verticesList.get(parent)))
                        epPlus++;
                }
            }
        }

        if (ep == 0)
            return "undefined";

        return String.format("%.2f", (double) epPlus / ep);
    }
    
}
