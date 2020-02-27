package org.rug.data.characteristics.smells;

import org.apache.commons.collections.iterators.ObjectGraphIterator;
import org.apache.tinkerpop.gremlin.process.computer.traversal.step.map.ShortestPath;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.rug.data.labels.VertexLabel;
import org.rug.data.smells.CDSmell;

import java.io.IOException;
import java.util.Collections;
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

        //System.out.println(subGraph.traversal().V().toList());

        List<Object> x = subGraph.traversal().withComputer()
                .V()
                .shortestPath()
                .with(ShortestPath.edges, Direction.OUT)
                //.select("name")
                .values("name")
                .toList();

        System.out.println(x.size());

        for (int i = 0; i < x.size(); i++) {

            System.out.println(x.get(i));

        }




        /*subGraph.traversal().V().as("v")
                .repeat(__.out().simplePath().as("v"))
                .emit()
                .filter(project("x","y","z")
                        .by(select(first, "v")).by(select(last, "v")).by(select(all, "v")
                                .count(local)).as("triple").coalesce(select("x","y").as("a").select("triples").unfold()
                                .as("t").select("x","y").where(eq("a")).select("t"), store("triples"))
                        .select("z").as("length")
                        .select("triple").select("z").where(eq("length")))
                .select(all, "v").unfold().groupCount().by("name").next()*/


        return null;


    }



}
