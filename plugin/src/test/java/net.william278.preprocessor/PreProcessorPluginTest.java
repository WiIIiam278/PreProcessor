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
        assertNotNull(project.getPlugins().apply("net.william278.preprocessor"));
    }
}
