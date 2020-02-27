package org.rug.data.characteristics.smells;

import org.apache.tinkerpop.gremlin.process.computer.traversal.step.map.ShortestPath;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
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

        // Extract only affected vertexes from graph
        List<Object> vertexList = source.V()
                .hasLabel(P.within(VertexLabel.getComponentStrings()))
                .values("name")
                .toList();

        // Create vertexes of subGraph
        for (int i = 0; i < vertexList.size(); i++) {

            System.out.println(vertexList.get(i));

            subGraphTraversal.addV()
                    .property("name", vertexList.get(i))
                    .next();

        }


        // Extract only edges between affected vertexes from graph
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


    protected TinkerGraph measureBetweennessCentrality(TinkerGraph subGraph) {

        // Measure Betweenness Centrality for each vertex in subGraph
        List<Map<Object, Long>> betweennessList = subGraph.traversal().withComputer()
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

            subGraph.traversal()
                    .V().has("name", keys[i])
                    .property("between", values[i])
                    .next();

        }



        try {
            subGraph.io(graphml().graph(subGraph)).writeGraph("subGraphBetween.graphml");
        } catch (IOException e) {
            e.printStackTrace();
        }


        return subGraph;

    }



}
