package org.rug.data.characteristics.smells;

import org.apache.tinkerpop.gremlin.process.computer.traversal.step.map.ShortestPath;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.rug.data.smells.CDSmell;

public class ParentCentrality extends AbstractSmellCharacteristic {

    /**
     * Sets up the name of this smell characteristic.
     *
     */
    public ParentCentrality() {
        super("parentCentrality");
    }

    @Override
    public String visit(CDSmell smell) {
        if (smell.getLevel().isArchitecturalLevel()) {
            var subGraph = getSubGraph(smell);
            subGraph = measureBetweennessCentrality(subGraph);

            return measureParentalCentrality(subGraph);
        }

        return NO_VALUE;
    }

    /**
     * Calculates the subGraph that the parental centrality will be measured
     * @param smell The smell to calculate this smell on
     * @return The subGraph taken according to the smell
     */
    protected TinkerGraph getSubGraph(CDSmell smell) {

        var source = smell.getTraversalSource();

        var subGraph = TinkerGraph.open();
        var subGraphTraversal = subGraph.traversal();

        var affectedElementsNames = smell.getAffectedElementsNames();
        for (String name : affectedElementsNames)
            subGraphTraversal.addV()
                    .property("name", name)
                    .next();

        source.V(smell.getAffectedElements())
                .outE()
                .where(__.inV().is(P.within(smell.getAffectedElements())))
                .forEachRemaining(edge ->
                        subGraphTraversal.addE(edge.label())
                                .from(subGraphTraversal.V().has("name",  edge.vertices(Direction.OUT).next().value("name").toString()))
                                .to(subGraphTraversal.V().has( "name", edge.vertices(Direction.IN).next().value("name").toString()))
                                .next());

        return subGraph;
    }

    /**
     * Measures the betweenness centrality of every vertex of the graph
     * @param graph The graph that the betweenness centrality will be measured
     * @return The same graph with a property about betweenness centrality for each vertex
     */
    protected TinkerGraph measureBetweennessCentrality(TinkerGraph graph) {

        graph.traversal().withComputer()
                .V()
                .shortestPath()
                .with(ShortestPath.edges, Direction.OUT)
                .unfold()
                .groupCount()
                .by("name")
                .next()
                .forEach((k, v) ->
                        graph.traversal()
                                .V().has("name", k)
                                .property("between", v)
                                .next());

        return graph;
    }

    /**
     * Checks if the edge belongs to the Ep set
     * (i.e. child to parent edge)
     * @param edge The edge that will be checked
     * @return True if the edge belongs to the Ep set
     */
    private boolean checkEp(Edge edge) {

        var child = edge.vertices(Direction.OUT).next().value("name").toString();
        var parent = edge.vertices(Direction.IN).next().value("name").toString();

        return child.startsWith(parent);
    }

    /**
     * Checks if the edge belongs to the Ep+ set
     * (i.e. parent's betweenness centrality greater than child's one)
     * @param edge The edge that will be checked
     * @return True if the edge belongs to the Ep+ set
     */
    private boolean checkEpPlus(Edge edge) {

        var betweennessChild = Integer.parseInt(edge.vertices(Direction.OUT).next().value("between").toString());
        var betweennessParent = Integer.parseInt(edge.vertices(Direction.IN).next().value("between").toString());

        return betweennessParent >= betweennessChild;
    }

    /**
     * Measures the parental centrality of the smell
     * @param graph The graph that the parental centrality will be measured
     * @return The value of the parental centrality
     */
    protected String measureParentalCentrality(TinkerGraph graph) {

       var list = graph.traversal().V().outE().toList();

        list.removeIf(edge -> !checkEp(edge));
        var ep = list.size();

        list.removeIf(edge -> !checkEpPlus(edge));
        var epPlus = list.size();

        if (ep == 0)
            return "undefined";

        return String.format("%.2f", (double) epPlus / ep);
    }
    
}
