package org.rug.tracker;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.junit.jupiter.api.Test;
import org.rug.data.ArcanDependencyGraphParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.SortedMap;


class ASTracker2Test {

    private final static Logger logger = LoggerFactory.getLogger(ASTracker2Test.class);

    @Test
    void trackTest(){
        SortedMap<String, Graph> versionedSystem = ArcanDependencyGraphParser.parseGraphML("./arcanrunner/outputs/antlr");

        ASTracker2 tracker = new ASTracker2();
        versionedSystem.forEach( (version, graph) -> {
            logger.info("Tracking version {}", version);
            tracker.track(graph, version);
        });

        tracker.writeTrackGraph("src/test/graphimages/trackgraph.graphml");

        ASTracker2 tracker2 = new ASTracker2(new JaccardSimilarityLinker(), true);
        versionedSystem.forEach( (version, graph) -> {
            logger.info("Tracking version {}", version);
            tracker2.track(graph, version);
        });

        tracker2.writeTrackGraph("src/test/graphimages/trackgraph-nonconsec.graphml");
    }

}