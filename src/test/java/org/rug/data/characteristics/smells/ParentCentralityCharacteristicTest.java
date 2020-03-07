package org.rug.data.characteristics.smells;

import org.apache.tinkerpop.gremlin.structure.util.Comparators;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.rug.data.project.IVersion;
import org.rug.data.smells.ArchitecturalSmell;
import org.rug.data.smells.CDSmell;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.rug.simpletests.TestData.antlr;
import static org.rug.simpletests.TestData.ant;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParentCentralityCharacteristicTest {

    private TinkerGraph graph1;
    //private TinkerGraph pCTreeGraph1;
    private TinkerGraph betweenGraph1;
    private CDSmell smell1;
    private TinkerGraph graph2;
    //private TinkerGraph pCTreeGraph2;
    private TinkerGraph betweenGraph2;
    private CDSmell smell2;
    private TinkerGraph graph3;
    //private TinkerGraph pCTreeGraph3;
    private TinkerGraph betweenGraph3;
    private CDSmell smell3;
    private TinkerGraph graph4;
    //private TinkerGraph pCTreeGraph4;
    private TinkerGraph betweenGraph4;
    private CDSmell smell4;

    @BeforeAll
    void init() {

        // Graph 1
        graph1 = TinkerGraph.open();
        graph1.traversal()
                .addV("package").property("name", "r.a").as("r.a")
                .addV("package").property("name", "r.b").as("r.b")
                .addV("package").property("name", "r.c").as("r.c")
                .addE("connects").from("r.a").to("r.b")
                .addE("connects").from("r.b").to("r.c")
                .addE("connects").from("r.c").to("r.a")
                .iterate();

        /*pCTreeGraph1 = TinkerGraph.open();
        pCTreeGraph1.traversal()
                .addV("package").property("name", "r").as("r")
                .addV("package").property("name", "r.a").as("r.a")
                .addV("package").property("name", "r.b").as("r.b")
                .addV("package").property("name", "r.c").as("r.c")
                .addE("child").from("r").to("r.a")
                .addE("child").from("r").to("r.b")
                .addE("child").from("r").to("r.c")
                .iterate();*/

        betweenGraph1 = TinkerGraph.open();
        betweenGraph1.traversal()
                .addV("package").property("name", "r.a").property("between", 6).as("r.a")
                .addV("package").property("name", "r.b").property("between", 6).as("r.b")
                .addV("package").property("name", "r.c").property("between", 6).as("r.c")
                .addE("connects").from("r.a").to("r.b")
                .addE("connects").from("r.b").to("r.c")
                .addE("connects").from("r.c").to("r.a")
                .iterate();

        var bigGraph1 = TinkerGraph.open();
        bigGraph1.traversal()
                .addV("package").property("name", "r.a").as("r.a")
                .addV("package").property("name", "r.b").as("r.b")
                .addV("package").property("name", "r.c").as("r.c")
                .addV("package").property("name", "r.d").as("r.d")
                .addV("package").property("name", "r.a.e").as("r.a.e")
                .addV("smell").property("type", ArchitecturalSmell.Type.CD.toString()).property("vertexType", "package").as("sm")
                .addV("cycleShape").property("shapeType", "circle").as("sh")
                .addE("shape").from("sh").to("sm")
                .addE("affects").from("sm").to("r.a")
                .addE("affects").from("sm").to("r.b")
                .addE("affects").from("sm").to("r.c")
                .addE("connects").from("r.a").to("r.b")
                .addE("connects").from("r.b").to("r.c")
                .addE("connects").from("r.c").to("r.a")
                .addE("connects").from("r.a").to("r.a.e")
                .addE("connects").from("r.d").to("r.b")
                .iterate();

        smell1 = new CDSmell(bigGraph1.traversal().V().hasLabel("smell").next());

        // Graph 2
        graph2 = TinkerGraph.open();
        graph2.traversal()
                .addV("package").property("name", "a").as("a")
                .addV("package").property("name", "a.b.c").as("a.b.c")
                .addV("package").property("name", "a.b.c.d").as("a.b.c.d")
                .addE("connects").from("a").to("a.b.c")
                .addE("connects").from("a.b.c").to("a.b.c.d")
                .addE("connects").from("a.b.c.d").to("a")
                .addE("connects").from("a").to("a.b.c.d")
                .iterate();

        /*pCTreeGraph2 = TinkerGraph.open();
        pCTreeGraph2.traversal()
                .addV("package").property("name", "a").as("a")
                .addV("package").property("name", "a.b").as("a.b")
                .addV("package").property("name", "a.b.c").as("a.b.c")
                .addV("package").property("name", "a.b.c.d").as("a.b.c.d")
                .addE("child").from("a").to("a.b")
                .addE("child").from("a.b").to("a.b.c")
                .addE("child").from("a.b.c").to("a.b.c.d")
                .iterate();*/

        betweenGraph2 = TinkerGraph.open();
        betweenGraph2.traversal()
                .addV("package").property("name", "a").property("between", 6).as("a")
                .addV("package").property("name", "a.b.c").property("between", 5).as("a.b.c")
                .addV("package").property("name", "a.b.c.d").property("between", 6).as("a.b.c.d")
                .addE("connects").from("a").to("a.b.c")
                .addE("connects").from("a.b.c").to("a.b.c.d")
                .addE("connects").from("a.b.c.d").to("a")
                .addE("connects").from("a").to("a.b.c.d")
                .iterate();

        var bigGraph2 = TinkerGraph.open();
        bigGraph2.traversal()
                .addV("package").property("name", "a").as("a")
                .addV("package").property("name", "a.b.c").as("a.b.c")
                .addV("package").property("name", "a.b.c.d").as("a.b.c.d")
                .addV("package").property("name", "a.b.c.d.e").as("a.b.c.d.e")
                .addV("package").property("name", "a.b.f.g").as("a.b.f.g")
                .addV("smell").property("type", ArchitecturalSmell.Type.CD.toString()).property("vertexType", "package").as("sm")
                .addV("cycleShape").property("shapeType", "circle").as("sh")
                .addE("shape").from("sh").to("sm")
                .addE("affects").from("sm").to("a")
                .addE("affects").from("sm").to("a.b.c")
                .addE("affects").from("sm").to("a.b.c.d")
                .addE("connects").from("a").to("a.b.c")
                .addE("connects").from("a.b.c").to("a.b.c.d")
                .addE("connects").from("a.b.c.d").to("a")
                .addE("connects").from("a").to("a.b.c.d")
                .addE("connects").from("a").to("a.b.c.d.e")
                .addE("connects").from("a.b.f.g").to("a.b.c")
                .iterate();

        smell2 = new CDSmell(bigGraph2.traversal().V().hasLabel("smell").next());

        // Graph 3
        graph3 = TinkerGraph.open();
        graph3.traversal()
                .addV("package").property("name", "a").as("a")
                .addV("package").property("name", "a.b.c").as("a.b.c")
                .addV("package").property("name", "a.b.c.d").as("a.b.c.d")
                .addE("connects").from("a").to("a.b.c")
                .addE("connects").from("a").to("a.b.c.d")
                .addE("connects").from("a.b.c").to("a")
                .addE("connects").from("a.b.c.d").to("a")
                .addE("connects").from("a.b.c").to("a.b.c.d")
                .iterate();

        /*pCTreeGraph3 = TinkerGraph.open();
        pCTreeGraph3.traversal()
                .addV("package").property("name", "a").as("a")
                .addV("package").property("name", "a.b").as("a.b")
                .addV("package").property("name", "a.b.c").as("a.b.c")
                .addV("package").property("name", "a.b.c.d").as("a.b.c.d")
                .addE("connects").from("a").to("a.b.c")
                .addE("connects").from("a").to("a.b.c.d")
                .addE("connects").from("a.b.c").to("a")
                .addE("connects").from("a.b.c.d").to("a")
                .addE("connects").from("a.b.c").to("a.b.c.d")
                .iterate();*/

        betweenGraph3 = TinkerGraph.open();
        betweenGraph3.traversal()
                .addV("package").property("name", "a").property("between", 6).as("a")
                .addV("package").property("name", "a.b.c").property("between", 6).as("a.b.c")
                .addV("package").property("name", "a.b.c.d").property("between", 5).as("a.b.c.d")
                .addE("connects").from("a").to("a.b.c.d")
                .addE("connects").from("a.b.c").to("a.b.c.d")
                .addE("connects").from("a.b.c.d").to("a.b.c")
                .addE("connects").from("a.b.c").to("a")
                .iterate();

        var bigGraph3 = TinkerGraph.open();
        bigGraph3.traversal()
                .addV("package").property("name", "a").as("a")
                .addV("package").property("name", "a.b.c").as("a.b.c")
                .addV("package").property("name", "a.b.c.d").as("a.b.c.d")
                .addV("package").property("name", "a.b.c.d.e").as("a.b.c.d.e")
                .addV("package").property("name", "a.b.f.g").as("a.b.f.g")
                .addV("smell").property("type", ArchitecturalSmell.Type.CD.toString()).property("vertexType", "package").as("sm")
                .addV("cycleShape").property("shapeType", "circle").as("sh")
                .addE("shape").from("sh").to("sm")
                .addE("affects").from("sm").to("a")
                .addE("affects").from("sm").to("a.b.c")
                .addE("affects").from("sm").to("a.b.c.d")
                .addE("connects").from("a").to("a.b.c")
                .addE("connects").from("a").to("a.b.c.d")
                .addE("connects").from("a.b.c").to("a")
                .addE("connects").from("a.b.c.d").to("a")
                .addE("connects").from("a.b.c").to("a.b.c.d")
                .addE("connects").from("a.b.c").to("a.b.c.d.e")
                .addE("connects").from("a.b.f.g").to("a")
                .iterate();

        smell3 = new CDSmell(bigGraph3.traversal().V().hasLabel("smell").next());

        // Graph 4
        graph4 = TinkerGraph.open();
        graph4.traversal()
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

        /*pCTreeGraph4 = TinkerGraph.open();
        pCTreeGraph4.traversal()
                .addV("package").property("name", "a").as("a")
                .addV("package").property("name", "a.b1").as("a.b1")
                .addV("package").property("name", "a.b1.c1").as("a.b1.c1")
                .addV("package").property("name", "a.b1.c2").as("a.b1.c2")
                .addV("package").property("name", "a.b2").as("a.b2")
                .addE("child").from("a").to("a.b1")
                .addE("child").from("a").to("a.b2")
                .addE("child").from("a.b1").to("a.b1.c1")
                .addE("child").from("a.b1").to("a.b1.c2")
                .iterate();*/

        betweenGraph4 = TinkerGraph.open();
        betweenGraph4.traversal()
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

        var bigGraph4 = TinkerGraph.open();
        bigGraph4.traversal()
                .addV("package").property("name", "a").as("a")
                .addV("package").property("name", "a.b1").as("a.b1")
                .addV("package").property("name", "a.b1.c1").as("a.b1.c1")
                .addV("package").property("name", "a.b1.c2").as("a.b1.c2")
                .addV("package").property("name", "a.b2").as("a.b2")
                .addV("package").property("name", "a.b3").as("a.b3")
                .addV("package").property("name", "a.b2.c1.d1").as("a.b2.c1.d1")
                .addV("smell").property("type", ArchitecturalSmell.Type.CD.toString()).property("vertexType", "package").as("sm")
                .addV("cycleShape").property("shapeType", "circle").as("sh")
                .addE("shape").from("sh").to("sm")
                .addE("affects").from("sm").to("a")
                .addE("affects").from("sm").to("a.b1")
                .addE("affects").from("sm").to("a.b1.c1")
                .addE("affects").from("sm").to("a.b1.c2")
                .addE("affects").from("sm").to("a.b2")
                .addE("connects").from("a.b1.c1").to("a.b2")
                .addE("connects").from("a.b2").to("a.b1")
                .addE("connects").from("a.b1").to("a.b1.c1")
                .addE("connects").from("a.b1.c1").to("a.b1")
                .addE("connects").from("a.b1").to("a")
                .addE("connects").from("a").to("a.b1.c2")
                .addE("connects").from("a.b1.c2").to("a.b2")
                .addE("connects").from("a.b1.c2").to("a.b1")
                .addE("connects").from("a.b1").to("a.b2.c1.d1")
                .addE("connects").from("a.b1").to("a.b3")
                .iterate();

        smell4 = new CDSmell(bigGraph4.traversal().V().hasLabel("smell").next());
    }

    private boolean checkEqualGraphs(TinkerGraph g1, TinkerGraph  g2, String[] properties) {

        var v1 = g1.traversal().V().order().by("name").toList();
        var v2 = g2.traversal().V().order().by("name").toList();

        for (int i = 0; i < Math.max(v1.size(), v2.size()); i++) {
            for (String propName : properties) {
                if (Comparators.PROPERTY_COMPARATOR.compare(v1.get(i).property(propName), v2.get(i).property(propName)) != 0) {
                    return false;
                }
            }
        }

        var edgeList1 = g1.traversal().V().as("a")
                .out().as("b")
                .select("a", "b").by("name")
                .toList();
        var edgeList2 = g1.traversal().V().as("a")
                .out().as("b")
                .select("a", "b").by("name")
                .toList();

        for (var edge : edgeList1) {
            if (!edgeList2.contains(edge))
                return false;
        }
        for (var edge : edgeList2) {
            if (!edgeList1.contains(edge))
                return false;
        }

        return true;
    }

    @Test
    void testVisit() {

        ParentCentralityCharacteristic x = new ParentCentralityCharacteristic();

        // Graph 1
        assertEquals(x.visit(smell1), "undefined");

        // Graph 2
        assertEquals(x.visit(smell2), "0.00");

        // Graph 3
        assertEquals(x.visit(smell3), "1.00");

        // Graph 4
        assertEquals(x.visit(smell4), "0.67");


    }

    /*@Test
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
    }*/

    @Test
    void testGetSubGraph() {

        ParentCentralityCharacteristic x = new ParentCentralityCharacteristic();
        TinkerGraph subGraph;

        // Graph 1
        subGraph = x.getSubGraph(smell1);
        assertTrue(checkEqualGraphs(graph1, subGraph, new String[]{"name"}));

        // Graph 1
        subGraph = x.getSubGraph(smell2);
        assertTrue(checkEqualGraphs(graph2, subGraph, new String[]{"name"}));

        // Graph 1
        subGraph = x.getSubGraph(smell3);
        assertTrue(checkEqualGraphs(graph3, subGraph, new String[]{"name"}));

        // Graph 1
        subGraph = x.getSubGraph(smell4);
        assertTrue(checkEqualGraphs(graph4, subGraph, new String[]{"name"}));
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
        //x.pCTree = x.PCTCreation(graph1.traversal());
        bc = x.measureBetweennessCentrality(graph1);
        assertEquals(x.measureParentalCentrality(bc), "undefined");

        // Graph 2
        //x.pCTree = x.PCTCreation(graph2.traversal());
        bc = x.measureBetweennessCentrality(graph2);
        assertEquals(x.measureParentalCentrality(bc), "0.00");

        // Graph 3
        //x.pCTree = x.PCTCreation(graph3.traversal());
        bc = x.measureBetweennessCentrality(graph3);
        assertEquals(x.measureParentalCentrality(bc), "1.00");

        // Graph 4
        //x.pCTree = x.PCTCreation(graph4.traversal());
        bc = x.measureBetweennessCentrality(graph4);
        assertEquals(x.measureParentalCentrality(bc), "0.67");
    }

    @Test
    void testOnSystem(){
        IVersion version;
        List<ArchitecturalSmell> smells;
        List<String> list;
        var pcMetric = new ParentCentralityCharacteristic();

        // Version antlr 2.7.6
        version = antlr.getVersionWith(10);
        smells = antlr.getArchitecturalSmellsIn(version);

        for (ArchitecturalSmell as : smells) {
            as.getCharacteristicsMap().put(pcMetric.getName(), as.accept(pcMetric));
        }

        list = smells.stream()
                .map(as -> as.getCharacteristicsMap().get(pcMetric.getName()))
                .collect(Collectors.toList());
        list.removeIf(sm -> sm.equals("0"));
        assertEquals(list, Collections.singletonList("1.00"));

        // Version antlr 3.0
        version = antlr.getVersionWith(12);
        smells = antlr.getArchitecturalSmellsIn(version);

        for (ArchitecturalSmell as : smells) {
            as.getCharacteristicsMap().put(pcMetric.getName(), as.accept(pcMetric));
        }

        list = smells.stream()
                .map(as -> as.getCharacteristicsMap().get(pcMetric.getName()))
                .collect(Collectors.toList());
        list.removeIf(sm -> sm.equals("0"));
        assertEquals(list, Arrays.asList("0.00", "0.00", "undefined", "undefined", "0.00", "0.00", "undefined"));

        // Version ant 1.2
        version = ant.getVersionWith(2);
        smells = ant.getArchitecturalSmellsIn(version);

        for (ArchitecturalSmell as : smells) {
            as.getCharacteristicsMap().put(pcMetric.getName(), as.accept(pcMetric));
        }

        list = smells.stream()
                .map(as -> as.getCharacteristicsMap().get(pcMetric.getName()))
                .collect(Collectors.toList());
        list.removeIf(sm -> sm.equals("0"));
        assertEquals(list, Collections.singletonList("0.00"));

        // Version ant 1.4.1
        version = ant.getVersionWith(5);
        smells = ant.getArchitecturalSmellsIn(version);

        for (ArchitecturalSmell as : smells) {
            as.getCharacteristicsMap().put(pcMetric.getName(), as.accept(pcMetric));
        }

        list = smells.stream()
                .map(as -> as.getCharacteristicsMap().get(pcMetric.getName()))
                .collect(Collectors.toList());
        list.removeIf(sm -> sm.equals("0"));
        assertEquals(list, Arrays.asList("0.50", "0.00", "0.00", "1.00"));

        //System.out.println(version.getVersionString());
        //System.out.println(list);
        //var cdSmell =
        //smells.stream().filter(as -> as.getLevel().equals(ArchitecturalSmell.Level.PACKAGE) && as.getType().equals(ArchitecturalSmell.Type.CD)).forEach(System.out::println);//.findFirst().get();

       //System.out.println(cdSmell);
    }

}