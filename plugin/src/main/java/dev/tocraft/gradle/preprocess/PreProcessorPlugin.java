/*
 * This source file was generated by the Gradle 'init' task
 */
package dev.tocraft.gradle.preprocess;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

@SuppressWarnings({"unused", "deprecation"})
public class PreProcessorPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getTasks().create(PreProcessTask.ID, PreProcessTask.class);
    }
}
