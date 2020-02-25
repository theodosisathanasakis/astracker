package org.rug.data.characteristics.smells;

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.rug.data.labels.VertexLabel;
import org.rug.data.smells.CDSmell;

import java.io.IOException;
import java.util.List;

import static org.apache.tinkerpop.gremlin.structure.io.IoCore.graphml;

public class ParentCentralityCharacteristic extends AbstractSmellCharacteristic {
    /**
     * Sets up the name of this smell characteristic.
     *
     */
    protected ParentCentralityCharacteristic() {
        super("parentCentrality");
    }

    @Override
    public String visit(CDSmell smell) {

        //smell.getAffectedElements()


        return super.visit(smell);
    }


    protected void PCTCreation(GraphTraversalSource source) {

        /*TinkerGraph graph2 = TinkerGraph.open();
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
        g2.addV("package").property("name", "org.rug.data.characteristics.smells").next();*/


        TinkerGraph graph = TinkerGraph.open();
        //graph.createIndex("name", Vertex.class);

        GraphTraversalSource g = graph.traversal();


        List<Object> vertexList = source.V().hasLabel(P.within(VertexLabel.getComponentStrings())).values("name").toList();

        for (int i = 0; i < vertexList.size(); i++) {

            String[] vList = vertexList.get(i).toString().split("\\.");

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

        try {
            graph.io(graphml().graph(graph)).writeGraph("graphPCT.graphml");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
