package org.rug.simpletests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.rug.Main;
import org.rug.persistence.PersistenceHub;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("systemTests")
class MainTest {

    private final String inputDirJars = "./test-data/input/";
    private final String inputDirGraphMLs = "./test-data/output/arcanOutput/";
    private final String outputDir = "./test-data/output/";
    private final String arcanCommand = "java -jar ./arcan/Arcan-1.4.0-SNAPSHOT.jar";


    void executeMainArcan(){
        executeMainProjectArcan("antlr");
        executeMainProjectArcan("ant");
    }

    void executeMainProjectArcan(String projectName){

        PersistenceHub.clearAll();

        try {
            Files.delete(Paths.get(outputDir, "trackASOutput", projectName));
            Files.delete(Paths.get(outputDir, "arcanOutput", projectName));
        } catch (IOException e) {}

        Main.main(new String[]{"-p", projectName, "-i", inputDirJars, "-o", outputDir, "-rA", arcanCommand, "-pC", "-pS"});

        assertTrue(Files.exists(Paths.get(outputDir, "arcanOutput", projectName)),
                error(projectName, "checking existence arcanOutput directory"));
        assertTrue(Files.exists(Paths.get(outputDir, "trackASOutput", projectName, "smell-characteristics-consecOnly.csv")),
                error(projectName, "checking existence smell characteristics"));
        assertTrue(Files.exists(Paths.get(outputDir, "trackASOutput", projectName, "similarity-scores-consecOnly.csv")),
                error(projectName, "checking existence score similarity file"));
    }

    @Test
    void systemTestAnt(){
        executeMainProject("ant", false);
    }

    @Test
    void systemTestTextExtraction(){
        executeMainProject("text-extraction", true);
    }

    void executeMainProject(String projectName, boolean isCPPproject){

        try {
            Files.delete(Paths.get(outputDir, "trackASOutput", projectName));
        } catch (IOException e) {}

        PersistenceHub.clearAll();

        Main.main(new String[]{"-p", projectName, "-i", inputDirGraphMLs, "-o", outputDir, "-pC", "-pS", isCPPproject ? "-cppP" : "-jP" });

        assertTrue(Files.exists(Paths.get(outputDir, "trackASOutput", projectName, "smell-characteristics-consecOnly.csv")),
                error(projectName, "checking existence of smell characteristics file"));
        assertTrue(Files.exists(Paths.get(outputDir, "trackASOutput", projectName, "similarity-scores-consecOnly.csv")),
                error(projectName, "checking existence similarity scores file"));
        PersistenceHub.clearAll();
    }

    Supplier<String> error(String projectName, String cause){
            return ()-> String.format("Error %s for project %s.", cause, projectName);
    }
}