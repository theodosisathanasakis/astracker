package org.rug.data.characteristics.smells;

import org.apache.tinkerpop.gremlin.structure.util.Comparators;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.rug.data.smells.CDSmell;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParentCentralityCharacteristicTest {

    private TinkerGraph graph1;
    private TinkerGraph pCTreeGraph1;
    private TinkerGraph betweenGraph1;
    private TinkerGraph graph2;
    private TinkerGraph pCTreeGraph2;
    private TinkerGraph betweenGraph2;
    private TinkerGraph graph3;
    private TinkerGraph pCTreeGraph3;
    private TinkerGraph betweenGraph3;
    private TinkerGraph graph4;
    private TinkerGraph pCTreeGraph4;
    private TinkerGraph betweenGraph4;

    @BeforeAll
    void init() {
        // Graph 1
        graph1 = TinkerGraph.open();
        var g1 = graph1.traversal()
                .addV("package").property("name", "r.a").as("r.a")
                .addV("package").property("name", "r.b").as("r.b")
                .addV("package").property("name", "r.c").as("r.c")
                .addE("connects").from("r.a").to("r.b")
                .addE("connects").from("r.b").to("r.c")
                .addE("connects").from("r.c").to("r.a")
                .iterate();

        pCTreeGraph1 = TinkerGraph.open();
        var pct1 = pCTreeGraph1.traversal()
                .addV("package").property("name", "r").as("r")
                .addV("package").property("name", "r.a").as("r.a")
                .addV("package").property("name", "r.b").as("r.b")
                .addV("package").property("name", "r.c").as("r.c")
                .addE("child").from("r").to("r.a")
                .addE("child").from("r").to("r.b")
                .addE("child").from("r").to("r.c")
                .iterate();

        betweenGraph1 = TinkerGraph.open();
        var bg1 = betweenGraph1.traversal()
                .addV("package").property("name", "r.a").property("between", 6).as("r.a")
                .addV("package").property("name", "r.b").property("between", 6).as("r.b")
                .addV("package").property("name", "r.c").property("between", 6).as("r.c")
                .addE("connects").from("r.a").to("r.b")
                .addE("connects").from("r.b").to("r.c")
                .addE("connects").from("r.c").to("r.a")
                .iterate();

        // Graph 2
        graph2 = TinkerGraph.open();
        var g2 = graph2.traversal()
                .addV("package").property("name", "a").as("a")
                .addV("package").property("name", "a.b.c").as("a.b.c")
                .addV("package").property("name", "a.b.c.d").as("a.b.c.d")
                .addE("connects").from("a").to("a.b.c")
                .addE("connects").from("a.b.c").to("a.b.c.d")
                .addE("connects").from("a.b.c.d").to("a")
                .addE("connects").from("a").to("a.b.c.d")
                .iterate();

        pCTreeGraph2 = TinkerGraph.open();
        var pct2 = pCTreeGraph2.traversal()
                .addV("package").property("name", "a").as("a")
                .addV("package").property("name", "a.b").as("a.b")
                .addV("package").property("name", "a.b.c").as("a.b.c")
                .addV("package").property("name", "a.b.c.d").as("a.b.c.d")
                .addE("child").from("a").to("a.b")
                .addE("child").from("a.b").to("a.b.c")
                .addE("child").from("a.b.c").to("a.b.c.d")
                .iterate();

        betweenGraph2 = TinkerGraph.open();
        var bg2 = betweenGraph2.traversal()
                .addV("package").property("name", "a").property("between", 6).as("a")
                .addV("package").property("name", "a.b.c").property("between", 5).as("a.b.c")
                .addV("package").property("name", "a.b.c.d").property("between", 6).as("a.b.c.d")
                .addE("connects").from("a").to("a.b.c")
                .addE("connects").from("a.b.c").to("a.b.c.d")
                .addE("connects").from("a.b.c.d").to("a")
                .addE("connects").from("a").to("a.b.c.d")
                .iterate();

        // Graph 3
        graph3 = TinkerGraph.open();
        var g3 = graph3.traversal()
                .addV("package").property("name", "a").as("a")
                .addV("package").property("name", "a.b.c").as("a.b.c")
                .addV("package").property("name", "a.b.c.d").as("a.b.c.d")
                .addE("connects").from("a").to("a.b.c")
                .addE("connects").from("a").to("a.b.c.d")
                .addE("connects").from("a.b.c").to("a")
                .addE("connects").from("a.b.c.d").to("a")
                .addE("connects").from("a.b.c").to("a.b.c.d")
                .iterate();

        pCTreeGraph3 = TinkerGraph.open();
        var pct3 = pCTreeGraph3.traversal()
                .addV("package").property("name", "a").as("a")
                .addV("package").property("name", "a.b").as("a.b")
                .addV("package").property("name", "a.b.c").as("a.b.c")
                .addV("package").property("name", "a.b.c.d").as("a.b.c.d")
                .addE("connects").from("a").to("a.b.c")
                .addE("connects").from("a").to("a.b.c.d")
                .addE("connects").from("a.b.c").to("a")
                .addE("connects").from("a.b.c.d").to("a")
                .addE("connects").from("a.b.c").to("a.b.c.d")
                .iterate();

        betweenGraph3 = TinkerGraph.open();
        var bg3 = betweenGraph3.traversal()
                .addV("package").property("name", "a").property("between", 6).as("a")
                .addV("package").property("name", "a.b.c").property("between", 6).as("a.b.c")
                .addV("package").property("name", "a.b.c.d").property("between", 5).as("a.b.c.d")
                .addE("connects").from("a").to("a.b.c.d")
                .addE("connects").from("a.b.c").to("a.b.c.d")
                .addE("connects").from("a.b.c.d").to("a.b.c")
                .addE("connects").from("a.b.c").to("a")
                .iterate();

        // Graph 4
        graph4 = TinkerGraph.open();
        var g4 = graph4.traversal()
                .addV("package").property("name", "a").as("a")
                .addV("package").property("name", "a.b1").as("a.b1")
                .addV("package").property("name", "a.b1.c1").as("a.b1.c1")
                .addV("package").property("name", "a.b1.c2").as("a.b1.c2")
                .addV("package").property("name", "a.b2").as("a.b2")
                .addE("connects").from("a.b1.c1").to("a.b2")
                .addE("connects").from("a.b2").to("a.b1")
                .addE("connects").from("a.b1").to("a.b1.c1")
                .addE("connects").from("a.b1.c1").to("a.b1")
                .addE("connects").from("a.b1").to("a")
                .addE("connects").from("a").to("a.b1.c2")
                .addE("connects").from("a.b1.c2").to("a.b2")
                .addE("connects").from("a.b1.c2").to("a.b1")
                .iterate();

        pCTreeGraph4 = TinkerGraph.open();
        var pct4 = pCTreeGraph4.traversal()
                .addV("package").property("name", "a").as("a")
                .addV("package").property("name", "a.b1").as("a.b1")
                .addV("package").property("name", "a.b1.c1").as("a.b1.c1")
                .addV("package").property("name", "a.b1.c2").as("a.b1.c2")
                .addV("package").property("name", "a.b2").as("a.b2")
                .addE("child").from("a").to("a.b1")
                .addE("child").from("a").to("a.b2")
                .addE("child").from("a.b1").to("a.b1.c1")
                .addE("child").from("a.b1").to("a.b1.c2")
                .iterate();

        betweenGraph4 = TinkerGraph.open();
        var bg4 = betweenGraph4.traversal()
                .addV("package").property("name", "a").property("between", 12).as("a")
                .addV("package").property("name", "a.b1").property("between", 17).as("a.b1")
                .addV("package").property("name", "a.b1.c1").property("between", 10).as("a.b1.c1")
                .addV("package").property("name", "a.b1.c2").property("between", 10).as("a.b1.c2")
                .addV("package").property("name", "a.b2").property("between", 8).as("a.b2")
                .addE("connects").from("a.b1.c1").to("a.b2")
                .addE("connects").from("a.b2").to("a.b1")
                .addE("connects").from("a.b1").to("a.b1.c1")
                .addE("connects").from("a.b1.c1").to("a.b1")
                .addE("connects").from("a.b1").to("a")
                .addE("connects").from("a").to("a.b1.c2")
                .addE("connects").from("a.b1.c2").to("a.b2")
                .addE("connects").from("a.b1.c2").to("a.b1")
                .iterate();
    }

    private boolean checkEqualGraphs(TinkerGraph g1, TinkerGraph  g2, String[] properties) {

        var v1 = g1.traversal().V().order().by("name").toList();
        var v2 = g2.traversal().V().order().by("name").toList();

        for (int i = 0; i < Math.max(v1.size(), v2.size()); i++) {
            for (int j = 0; j < properties.length; j++) {
                if (Comparators.PROPERTY_COMPARATOR.compare(v1.get(i).property(properties[j]), v2.get(i).property(properties[j])) != 0) {
                    return false;
                }
            }
        }

        /*var edgeList1 = g1.traversal().V().outE().inV().values("name").toList();
        var edgeList2 = g2.traversal().V().outE().inV().values("name").toList();

        for (int i = 0; i < Math.max(edgeList1.size(), edgeList2.size()); i++) {

            System.out.println(edgeList1.get(i));
            System.out.println(edgeList1.get(i));
        }*/

        return true;
    }

    @Test
    void testVisit() {

        ParentCentralityCharacteristic x = new ParentCentralityCharacteristic();

        // Graph 1
        //assertEquals(x.visit((CDSmell) graph1), "undefined");

        // Graph 2
        //assertEquals(x.visit((CDSmell) graph2), "undefined");

        // Graph 3
        //assertEquals(x.visit((CDSmell) graph3), "undefined");

        // Graph 4
        //assertEquals(x.visit((CDSmell) graph4), "undefined");

    }

    @Test
    void testPCTCreation() {

        ParentCentralityCharacteristic x = new ParentCentralityCharacteristic();

        // Graph 1
        x.pCTree = x.PCTCreation(graph1.traversal());
        assertTrue(checkEqualGraphs(pCTreeGraph1, x.pCTree, new String[]{"name"}));

        // Graph 2
        x.pCTree = x.PCTCreation(graph2.traversal());
        assertTrue(checkEqualGraphs(pCTreeGraph2, x.pCTree, new String[]{"name"}));

        // Graph3
        x.pCTree = x.PCTCreation(graph3.traversal());
        assertTrue(checkEqualGraphs(pCTreeGraph3, x.pCTree, new String[]{"name"}));

        // Graph 4
        x.pCTree = x.PCTCreation(graph4.traversal());
        assertTrue(checkEqualGraphs(pCTreeGraph4, x.pCTree, new String[]{"name"}));
    }

    @Test
    void testGetSubGraph() {

        /*ParentCentralityCharacteristic x = new ParentCentralityCharacteristic();

        var subGraph = x.getSubGraph((CDSmell) smellopt.findFirst().get());

        try {
            subGraph.io(graphml().graph(subGraph)).writeGraph("subGraph.graphml");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Test
    void testMeasureBetweennessCentrality() {

        ParentCentralityCharacteristic x1 = new ParentCentralityCharacteristic();
        TinkerGraph bGraph;

        // Graph 1
        bGraph = x1.measureBetweennessCentrality(graph1);
        assertTrue(checkEqualGraphs(betweenGraph1, bGraph, new String[]{"name", "between"}));

        // Graph 2
        bGraph = x1.measureBetweennessCentrality(graph2);
        assertTrue(checkEqualGraphs(betweenGraph2, bGraph, new String[]{"name", "between"}));

        // Graph 3
        bGraph = x1.measureBetweennessCentrality(graph3);
        assertTrue(checkEqualGraphs(betweenGraph3, bGraph, new String[]{"name", "between"}));

        // Graph 4
        bGraph = x1.measureBetweennessCentrality(graph4);
        assertTrue(checkEqualGraphs(betweenGraph4, bGraph, new String[]{"name", "between"}));
    }

    @Test
    void testMeasureParentalCentrality() {

        ParentCentralityCharacteristic x = new ParentCentralityCharacteristic();
        TinkerGraph bc;

        // Graph 1
        x.pCTree = x.PCTCreation(graph1.traversal());
        bc = x.measureBetweennessCentrality(graph1);
        assertEquals(x.measureParentalCentrality(bc), "undefined");

        // Graph 2
        x.pCTree = x.PCTCreation(graph2.traversal());
        bc = x.measureBetweennessCentrality(graph2);
        assertEquals(x.measureParentalCentrality(bc), "0.00");

        // Graph 3
        x.pCTree = x.PCTCreation(graph3.traversal());
        bc = x.measureBetweennessCentrality(graph3);
        assertEquals(x.measureParentalCentrality(bc), "1.00");

        // Graph 4
        x.pCTree = x.PCTCreation(graph4.traversal());
        bc = x.measureBetweennessCentrality(graph4);
        assertEquals(x.measureParentalCentrality(bc), "0.67");
    }


}