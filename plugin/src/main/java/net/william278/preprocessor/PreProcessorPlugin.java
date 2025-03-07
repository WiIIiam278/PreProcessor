/*
 * This file is part of WiIIiam278/PreProcessor, licensed under CC BY-NC-SA 4.0 (the "License").
 * The License applies under the Adapted Material clause of CC BY-NC-SA 4.0 (see Section 1 - Definitions)
 * WiIIiam278/PreProcessor is a derivative work of ToCraft/PreProcessor (https://github.com/ToCraft/PreProcessor)
 *
 *  Copyright (c) To_Craft <development@tocraft.dev>
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) contributors
 *
 * You can obtain a copy of the license at: https://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package net.william278.preprocessor;

import net.william278.preprocessor.data.PreprocessExtension;
import net.william278.preprocessor.tasks.ApplyPreProcessTask;
import net.william278.preprocessor.tasks.PreProcessTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.language.jvm.tasks.ProcessResources;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile;

import java.io.File;

/**
 * Main class for the PreProcessor-Plugin
 */
@SuppressWarnings({"unused"})
public class PreProcessorPlugin implements Plugin<Project> {
    @Override
    public void apply(@NotNull Project project) {
        PreprocessExtension ext = project.getExtensions().create("preprocess", PreprocessExtension.class);

        boolean hasKotlin = project.getPlugins().hasPlugin("kotlin");

        SourceSetContainer sourceSetContainer = project.getExtensions().findByType(SourceSetContainer.class);
        if (sourceSetContainer != null) {
            sourceSetContainer.configureEach(sourceSet -> {
                String generated = "generated" + File.separatorChar + "preprocessed" + File.separatorChar + sourceSet.getName() + File.separatorChar;

                // Java Source
                TaskProvider<PreProcessTask> preprocessJava = project.getTasks().register(sourceSet.getTaskName("preprocess", "Java"), PreProcessTask.class, task -> {
                    task.getSources().convention(sourceSet.getJava().getSrcDirs());
                    task.getVars().convention(ext.vars);
                    task.getRemap().convention(ext.remapper);
                    task.getKeywords().convention(ext.keywords);
                    task.getTarget().set(project.getLayout().getBuildDirectory().file(generated + "java").map(RegularFile::getAsFile));
                    task.getOutputs().upToDateWhen(t -> false);
                });

                project.getTasks().named(sourceSet.getCompileJavaTaskName(), JavaCompile.class, task -> {
                    task.dependsOn(preprocessJava);
                    task.setSource(preprocessJava.flatMap(PreProcessTask::getTarget));
                });

                project.getTasks().register(sourceSet.getTaskName("applyPreProcess", "Java"), ApplyPreProcessTask.class, preprocessJava).configure(task -> task.dependsOn(preprocessJava));

                // Kotlin
                if (hasKotlin) {
                    TaskProvider<PreProcessTask> preprocessKotlin = project.getTasks().register(sourceSet.getTaskName("preprocess", "Kotlin"), PreProcessTask.class, task -> {
                        task.getSources().convention(((SourceDirectorySet) sourceSet.getExtensions().getByName("kotlin")).getSrcDirs());
                        task.getVars().convention(ext.vars);
                    task.getRemap().convention(ext.remapper);
                    task.getKeywords().convention(ext.keywords);
                        task.getTarget().set(project.getLayout().getBuildDirectory().file(generated + "kotlin").map(RegularFile::getAsFile));
                        task.getOutputs().upToDateWhen(t -> false);
                    });

                    project.getTasks().named(sourceSet.getCompileTaskName("kotlin"), KotlinCompile.class, task -> {
                        task.dependsOn(preprocessKotlin);
                        task.setSource(preprocessKotlin.flatMap(PreProcessTask::getTarget));
                    });

                    project.getTasks().register(sourceSet.getTaskName("applyPreProcess", "Kotlin"), ApplyPreProcessTask.class, preprocessKotlin).configure(task -> task.dependsOn(preprocessKotlin));
                }

                // Resources
                TaskProvider<PreProcessTask> preprocessResources = project.getTasks().register(sourceSet.getTaskName("preprocess", "Resources"), PreProcessTask.class, task -> {
                    task.getSources().convention(sourceSet.getResources().getSrcDirs());
                    task.getVars().convention(ext.vars);
                    task.getRemap().convention(ext.remapper);
                    task.getKeywords().convention(ext.keywords);
                    task.getTarget().set(project.getLayout().getBuildDirectory().file(generated + "resources").map(RegularFile::getAsFile));
                    task.getOutputs().upToDateWhen(t -> false);
                });

                project.getTasks().named(sourceSet.getProcessResourcesTaskName(), ProcessResources.class, task -> {
                    task.dependsOn(preprocessResources);
                    task.from(preprocessResources.flatMap(PreProcessTask::getTarget));
                    // why do I need this?!?
                    task.setDuplicatesStrategy(DuplicatesStrategy.INCLUDE);
                });

                project.getTasks().register(sourceSet.getTaskName("applyPreProcess", "Resources"), ApplyPreProcessTask.class, preprocessResources).configure(task -> task.dependsOn(preprocessResources));
            });
        }

        project.getTasks().register("applyPreProcess").configure(task -> {
            for (ApplyPreProcessTask subApplyPreProcessTask : project.getTasks().withType(ApplyPreProcessTask.class)) {
                task.dependsOn(subApplyPreProcessTask);
            }

            for (Project subproject : project.getSubprojects()) {
                task.dependsOn(subproject.getTasks().named("applyPreProcess"));
            }
        });
    }
}
