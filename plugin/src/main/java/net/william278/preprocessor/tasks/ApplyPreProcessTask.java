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

package net.william278.preprocessor.tasks;

import net.william278.preprocessor.data.Keywords;
import net.william278.preprocessor.util.PreProcessor;
import net.william278.preprocessor.util.ReMapper;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * Task to overwrite the original source files with the results of the {@link PreProcessTask}
 */
public class ApplyPreProcessTask extends DefaultTask {
    private final Property<Boolean> removeComments;
    private final MapProperty<String, Object> vars;
    private final MapProperty<String, String> remap;
    private final MapProperty<String, Keywords> keywords;
    private final ListProperty<File> targets;
    private final ConfigurableFileCollection comingFiles;

    /**
     * @param factory        some object factory to crate the properties
     * @param preProcessTask the delgate preprocess task to be used
     */
    @Inject
    public ApplyPreProcessTask(final @NotNull ObjectFactory factory, final @NotNull TaskProvider<PreProcessTask> preProcessTask) {
        this.removeComments = factory.property(Boolean.class).convention(false);

        this.targets = factory.listProperty(File.class).convention(preProcessTask.flatMap(PreProcessTask::getSources));

        this.vars = factory.mapProperty(String.class, Object.class).convention(preProcessTask.flatMap(PreProcessTask::getVars));
        this.remap = factory.mapProperty(String.class, String.class).convention(preProcessTask.flatMap(PreProcessTask::getRemap));
        this.keywords = factory.mapProperty(String.class, Keywords.class).convention(preProcessTask.flatMap(PreProcessTask::getKeywords));

        this.comingFiles = factory.fileCollection();
    }

    /**
     * @return if the preprocess task will remove commented preprocessor commands
     */
    @Internal
    public Property<Boolean> getRemoveComments() {
        return removeComments;
    }

    /**
     * @return target folder where the sources files should be overwritten
     */
    @InputFiles
    public ListProperty<File> getTargets() {
        return targets;
    }

    /**
     * @return the overwritten files
     */
    @OutputFiles
    public FileCollection getComingFiles() {
        return this.comingFiles;
    }

    @Internal
    @Override
    public String getDescription() {
        return "PreProcess files.";
    }

    /**
     * The actual task action
     */
    @TaskAction
    public void applyPreProcess() {
        Set<File> foundFiles = new HashSet<>();

        PreProcessor preProcessor = new PreProcessor(removeComments.get(), vars.get(), keywords.get());
        ReMapper reMapper = new ReMapper(remap.get());

        // place file in their original source folder
        for (File srcFolder : targets.get()) {
            final File srcFolderFile = srcFolder.isAbsolute() ? srcFolder : new File(getProject().getProjectDir(), srcFolder.getPath());
            Path outBasePath = srcFolderFile.toPath();
            // iterate over the existing files in the targets folders so the preprocessed files can be copied to their exact source folder
            // might be buggy when interfered by externals
            for (File file : getProject().fileTree(outBasePath)) {
                // old school preprocessing
                preProcessor.convertFile(reMapper, file, file);

                foundFiles.add(file);
            }
        }

        this.comingFiles.setFrom(foundFiles);
    }
}
