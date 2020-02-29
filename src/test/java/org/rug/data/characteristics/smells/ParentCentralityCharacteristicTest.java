package org.rug.data.characteristics.smells;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.rug.data.project.IVersion;
import org.rug.data.smells.CDSmell;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.apache.tinkerpop.gremlin.structure.io.IoCore.graphml;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.rug.simpletests.TestData.antlr;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParentCentralityCharacteristicTest {

    private Graph graph;
    private IVersion version;
    private Stream smellopt;
    private CDSmell smell;

    @BeforeAll
    void init() {
        version = antlr.getVersionWith(3); // 2.5.0
        graph = version.getGraph();
        smellopt = antlr.getArchitecturalSmellsIn(version).stream().filter(s -> s instanceof CDSmell).filter(s -> s.getLevel().isArchitecturalLevel());//.findFirst();
        //assertTrue(smellopt.isPresent());
        //smell = (CDSmell)smellopt.get();
    }

    @Test
    void visit() {

        ParentCentralityCharacteristic x = new ParentCentralityCharacteristic();

        System.out.println(version.getVersionString());

        System.out.println(x.visit((CDSmell) smellopt.findFirst().get()));

        /*smellopt.forEachOrdered(smell -> {
            System.out.println(x.visit((CDSmell) smell));
        });*/
    }

    @Test
    void testPCTCreation() {

        ParentCentralityCharacteristic x = new ParentCentralityCharacteristic();

        x.pCTree = x.PCTCreation(graph.traversal());

        try {
            x.pCTree.io(graphml().graph(x.pCTree)).writeGraph("graphPCT.graphml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetSubGraph() {

        ParentCentralityCharacteristic x = new ParentCentralityCharacteristic();

        var subGraph = x.getSubGraph((CDSmell) smellopt.findFirst().get());

        try {
            subGraph.io(graphml().graph(subGraph)).writeGraph("subGraph.graphml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testMeasureBetweennessCentrality() {

        System.out.println(version.getVersionString());

        ParentCentralityCharacteristic x = new ParentCentralityCharacteristic();

        AtomicInteger i = new AtomicInteger(1);

        smellopt.forEachOrdered(smell -> {
            TinkerGraph subGraph = x.getSubGraph((CDSmell) smell);

            TinkerGraph y = x.measureBetweennessCentrality(subGraph);


            try {
                y.io(graphml().graph(graph)).writeGraph("subGraphBetween" + i + ".graphml");
            } catch (IOException e) {
                e.printStackTrace();
            }
            i.getAndIncrement();
        });
    }

    @Test
    void testMeasureParentalCentrality() {

        System.out.println(version.getVersionString());

        ParentCentralityCharacteristic x = new ParentCentralityCharacteristic();

        x.PCTCreation(graph.traversal());

        TinkerGraph subGraph = x.getSubGraph(smell);

        TinkerGraph y = x.measureBetweennessCentrality(subGraph);

        String z = x.measureParentalCentrality(y);

    }


}