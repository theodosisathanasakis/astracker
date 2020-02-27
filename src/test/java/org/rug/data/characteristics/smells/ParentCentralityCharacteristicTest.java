package org.rug.data.characteristics.smells;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.rug.data.project.IVersion;

import static org.rug.simpletests.TestData.antlr;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParentCentralityCharacteristicTest {

    private Graph graph;
    private IVersion version;

    @BeforeAll
    void inti() {
        version = antlr.getVersionWith(2); // 2.5.0
        graph = version.getGraph();
    }


    @Test
    void testPCTCreation() {

        ParentCentralityCharacteristic x = new ParentCentralityCharacteristic();

        x.PCTCreation(graph.traversal());

    }

    @Test
    void getSubGraph() {

        ParentCentralityCharacteristic x = new ParentCentralityCharacteristic();

        x.getSubGraph(graph.traversal(), null);

    }

    @Test
    void measureBetweennessCentrality() {

        ParentCentralityCharacteristic x = new ParentCentralityCharacteristic();

        TinkerGraph subGraph = x.getSubGraph(graph.traversal(), null);

        TinkerGraph y = x.measureBetweennessCentrality(subGraph);


    }
}