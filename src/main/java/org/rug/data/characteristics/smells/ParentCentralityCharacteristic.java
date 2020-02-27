package org.rug.data.characteristics.smells;

import com.sun.jdi.ShortValue;
import org.apache.commons.collections.iterators.ObjectGraphIterator;
import org.apache.tinkerpop.gremlin.process.computer.traversal.step.map.ShortestPath;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.rug.data.labels.VertexLabel;
import org.rug.data.smells.CDSmell;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.apache.tinkerpop.gremlin.structure.io.IoCore.graphml;

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

        //smell.getAffectedElements()


        return super.visit(smell);
    }


    protected void PCTCreation(GraphTraversalSource source) {

        pCTree = TinkerGraph.open();

        GraphTraversalSource g = pCTree.traversal();

        // Get only packages
        List<Object> vertexList = source.V().hasLabel(P.within(VertexLabel.getComponentStrings())).values("name").toList();

        // Split each package with "."
        for (int i = 0; i < vertexList.size(); i++) {

            String[] vList = vertexList.get(i).toString().split("\\.");

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

        try {
            pCTree.io(graphml().graph(pCTree)).writeGraph("graphPCT.graphml");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    protected TinkerGraph getSubGraph(GraphTraversalSource source, CDSmell smell) {

        TinkerGraph subGraph = TinkerGraph.open();

        GraphTraversalSource subGraphTraversal = subGraph.traversal();

        // Extract only affected vertices from graph
        List<Object> vertexList = source.V()
                .hasLabel(P.within(VertexLabel.getComponentStrings()))
                .values("name")
                .toList();

        // Create vertices of subGraph
        for (int i = 0; i < vertexList.size(); i++) {

            subGraphTraversal.addV()
                    .property("name", vertexList.get(i))
                    .next();

        }


        // Extract only edges between affected vertices from graph
        List<Map<String, Object>> edgeList = source.V()
                .hasLabel(P.within(VertexLabel.getComponentStrings())).as("a")
                .out().as("b")
                .select("a", "b").by("name")
                .toList();

        // Create edges of subGraph
        for (int i = 0; i < edgeList.size(); i++) {

            subGraphTraversal.addE("connects")
                    .from(subGraphTraversal.V().has("name", edgeList.get(i).get("a").toString()))
                    .to(subGraphTraversal.V().has( "name", edgeList.get(i).get("b").toString()))
                    .next();

        }



        try {
            subGraph.io(graphml().graph(subGraph)).writeGraph("subGraph.graphml");
        } catch (IOException e) {
            e.printStackTrace();
        }



        return subGraph;

    }


    protected TinkerGraph measureBetweennessCentralityVertex(TinkerGraph graph) {

        // Measure Betweenness Centrality for each vertex in subGraph
        List<Map<Object, Long>> betweennessList = graph.traversal().withComputer()
                .V()
                .shortestPath()
                .with(ShortestPath.edges, Direction.OUT)
                .unfold()
                .groupCount()
                .by("name")
                .toList();


        // Extract keys and values
        Object[] keys = betweennessList.get(0).keySet().toArray();
        Object[] values = betweennessList.get(0).values().toArray();


        // Add the Betweenness Centrality value to property in each vertex
        for (int i = 0; i < keys.length; i++) {

            graph.traversal()
                    .V().has("name", keys[i])
                    .property("between", values[i])
                    .next();

        }



        try {
            graph.io(graphml().graph(graph)).writeGraph("subGraphBetween.graphml");
        } catch (IOException e) {
            e.printStackTrace();
        }


        return graph;

    }


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
        for (int i = 0; i < vertexesList.size(); i++) {

            for (int j = 0; j < vertexesList.size(); j++) {

                // Check if the pair belongs to Ep
                // (Edge from child to parent)
                if (
                        graph.traversal()
                                .V().has("name", vertexesList.get(i))
                                .out().has("name", vertexesList.get(j))
                                .hasNext()
                        &&
                        pCTree.traversal().withComputer()
                                .V().has("name", vertexesList.get(j))
                                .shortestPath()
                                .with(ShortestPath.edges, Direction.OUT)
                                .with(ShortestPath.target, __.has("name", vertexesList.get(i)))
                                .hasNext()

                ) {
                    ep++;

                    // Check if the pair belongs to Ep+
                    // (Parent Betweenness Centrality > Child Betweenness Centrality)
                    int betweennessParent = Integer.parseInt(graph.traversal()
                            .V().has("name", vertexesList.get(i))
                            .values("between")
                            .toList()
                            .get(0)
                            .toString());


                    int betweennessChild = Integer.parseInt(graph.traversal()
                            .V().has("name", vertexesList.get(j))
                            .values("between")
                            .toList()
                            .get(0)
                            .toString());

                    System.out.println(vertexesList.get(i) + " " + betweennessParent);
                    System.out.println(vertexesList.get(j) + " " + betweennessChild);

                    if (betweennessParent > betweennessChild) {

                        epPlus++;

                    }

                }

            }

        }

        if (ep == 0)
            return "undefined";

        double pc = (double) epPlus / ep;

        System.out.println(pc);

        return String.valueOf(pc);
    }


}
