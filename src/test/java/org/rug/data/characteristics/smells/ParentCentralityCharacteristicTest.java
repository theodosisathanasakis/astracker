package org.rug.data.characteristics.smells;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.rug.data.project.IVersion;
import org.rug.data.smells.CDSmell;

import java.io.IOException;

import static org.apache.tinkerpop.gremlin.structure.io.IoCore.graphml;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.rug.simpletests.TestData.antlr;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParentCentralityCharacteristicTest {

    private Graph graph;
    private IVersion version;
    private CDSmell smell;

    @BeforeAll
    void init() {
        version = antlr.getVersionWith(2); // 2.5.0
        graph = version.getGraph();
        var smellopt = antlr.getArchitecturalSmellsIn(version).stream().filter(s -> s instanceof CDSmell).filter(s -> s.getLevel().isArchitecturalLevel()).findFirst();
        assertTrue(smellopt.isPresent());
        smell = (CDSmell)smellopt.get();
    }


    @Test
    void testPCTCreation() {

        ParentCentralityCharacteristic x = new ParentCentralityCharacteristic();

        x.PCTCreation(graph.traversal());

        try {
            x.pCTree.io(graphml().graph(x.pCTree)).writeGraph("graphPCT.graphml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetSubGraph() {

        ParentCentralityCharacteristic x = new ParentCentralityCharacteristic();

        var subGraph = x.getSubGraph(smell);

        try {
            subGraph.io(graphml().graph(subGraph)).writeGraph("subGraph.graphml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testMeasureBetweennessCentrality() {

        ParentCentralityCharacteristic x = new ParentCentralityCharacteristic();

        TinkerGraph subGraph = x.getSubGraph(smell);

        TinkerGraph y = x.measureBetweennessCentrality(subGraph);


        try {
            graph.io(graphml().graph(graph)).writeGraph("subGraphBetween.graphml");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Test
    void testMeasureParentalCentrality() {

        ParentCentralityCharacteristic x = new ParentCentralityCharacteristic();

        x.PCTCreation(graph.traversal());

        TinkerGraph subGraph = x.getSubGraph(smell);

        TinkerGraph y = x.measureBetweennessCentrality(subGraph);

        String z = x.measureParentalCentrality(y);

    }
}