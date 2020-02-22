package org.rug.data.characteristics.comps;

import org.apache.tinkerpop.gremlin.process.traversal.IO;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.rug.data.labels.EdgeLabel;
import org.rug.data.labels.VertexLabel;
import org.rug.data.project.IVersion;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import static org.apache.tinkerpop.gremlin.structure.io.IoCore.graphml;

public class ParentCentralityCharacteristic extends AbstractComponentCharacteristic {



    public ParentCentralityCharacteristic() {
        super("parentCentrality", VertexLabel.allComponents(), EnumSet.noneOf(EdgeLabel.class));
    }

    @Override
    public void calculate(IVersion version) {
        version.getGraph();

    }

    @Override
    protected void calculate(Vertex vertex) {

    }

    @Override
    protected void calculate(Edge edge) {

    }

    protected void PCTCreation(GraphTraversalSource source) {

        TinkerGraph graph2 = TinkerGraph.open();
        GraphTraversalSource g2 = graph2.traversal();
        g2.addV("package").property("name", "org.rug").next();
        g2.addV("package").property("name", "org.rug.args").next();
        g2.addV("package").property("name", "org.rug.data").next();
        g2.addV("package").property("name", "org.rug.persistence").next();
        g2.addV("package").property("name", "org.rug.runners").next();
        g2.addV("package").property("name", "org.rug.statefulness").next();
        g2.addV("package").property("name", "org.rug.tracker").next();
        g2.addV("package").property("name", "org.rug.data.characteristics").next();
        g2.addV("package").property("name", "org.rug.data.labels").next();
        g2.addV("package").property("name", "org.rug.data.project").next();
        g2.addV("package").property("name", "org.rug.data.smell").next();
        g2.addV("package").property("name", "org.rug.data.util").next();
        g2.addV("package").property("name", "org.rug.data.characteristics.comps").next();
        g2.addV("package").property("name", "org.rug.data.characteristics.smells").next();


        TinkerGraph graph = TinkerGraph.open();
        graph.createIndex("name", Vertex.class);

        GraphTraversalSource g = graph.traversal();


        List<Object> vertexList = source.V().values("name").toList();

        for (int i = 0; i < vertexList.size(); i++) {

            String[] vList = vertexList.get(i).toString().split("\\.");

            for (int j = 1; j < vList.length; j++) {
                vList[j] = vList[j - 1] + "." + vList[j];
            }

            for (int j = 0; j < vList.length; j++) {

                if (!g.V().has("name", vList[j]).hasNext()) {

                    g.addV("package").property("name", vList[j]).next();

                }

            }

            for (int j = 1; j < vList.length; j++) {

                if (!g.V().has("package", "name", vList[j-1]).out("child").has("package", "name", vList[j]).hasNext()) {

                    g.addE("child")
                            .from(g.V().has("package", "name", vList[j - 1]))
                            .to(g.V().has("package", "name", vList[j]))
                            .next();

                }

            }
        }

        try {
            graph.io(graphml().graph(graph)).writeGraph("graphPCT.graphml");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
