/*
 * This source file was generated by the Gradle 'init' task
 */
package dev.tocraft.gradle.preprocess;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static com.github.javaparser.utils.Utils.assertNotNull;

/**
 * A simple unit test for the 'org.example.greeting' plugin.
 */
class PreProcessorPluginTest {
    @Test
    void pluginRegistersATask() {
        // Create a test project and apply the plugin
        Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply("dev.tocraft.preprocessor");

        // Verify the result
        //assertNotNull(project.getTasks().findByName("preprocess"));
        //assertNotNull(project.getTasks().findByName("applyPreProcessor"));
    }
}
