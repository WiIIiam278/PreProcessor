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

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;

import static org.gradle.internal.impldep.org.junit.Assert.assertEquals;

/**
 * A simple functional test for the 'org.example.greeting' plugin.
 */
class PreProcessorPluginFunctionalTest {
    @TempDir
    File projectDir;

    @Contract(value = " -> new", pure = true)
    private @NotNull File getBuildFile() {
        return new File(projectDir, "build.gradle");
    }

    @Contract(value = " -> new", pure = true)
    private @NotNull File getSettingsFile() {
        return new File(projectDir, "settings.gradle");
    }

    @Contract(value = " -> new", pure = true)
    private @NotNull File getTestJavaFile() {
        return new File(projectDir, "src/main/java/test/Test.java");
    }

    @Contract(value = " -> new", pure = true)
    private @NotNull File getTestKotlinFile() {
        return new File(projectDir, "src/main/kotlin/test/Test.kt");
    }

    @Contract(value = " -> new", pure = true)
    private @NotNull File getTestJsonFile() {
        return new File(projectDir, "src/main/resources/test.json5");
    }

    private GradleRunner setupGradle() throws IOException {
        writeString(getSettingsFile(), "");
        writeString(getBuildFile(),
                "plugins {\n" +
                        "id('java')\n" +
                        "id('org.jetbrains.kotlin.jvm') version '2.0.0'\n" +
                        "id('net.william278.preprocessor')\n" +
                        "}\n" +
                        "repositories {\n" +
                        "mavenCentral()\n" +
                        "}\n" +
                        "preprocess {\n" +
                        "vars.put(\"a\", \"1\");\n" +
                        "}\n"
        );

        GradleRunner runner = GradleRunner.create();
        runner.forwardOutput();
        runner.withPluginClasspath();
        runner.withProjectDir(projectDir);
        return runner;
    }

    @Test
    void testApplyOnJava() throws IOException {
        writeString(getTestJavaFile(),
        "package test;\n" +
                "class Test {\n" +
                "public void main(String... args) {\n" +
                "//#if a\n" +
                "//$$ System.out.println(\"Test succeeded.\");\n" +
                "//#else\n" +
                "System.out.println(\"Test failed\");\n" +
                "//#endif\n" +
                "}\n" +
                "}\n"
        );

        GradleRunner runner = setupGradle();

        // Run the java build
        runner.withArguments("applyPreProcessJava");
        BuildResult javaResult = runner.build();

        // Verify the result
        for (BuildTask task : javaResult.getTasks()) {
            assertEquals(TaskOutcome.SUCCESS, task.getOutcome());
        }

        assertEquals(
                "package test;\n" +
                "class Test {\n" +
                "public void main(String... args) {\n" +
                "//#if a\n" +
                "System.out.println(\"Test succeeded.\");\n" +
                "//#else\n" +
                "//$$ System.out.println(\"Test failed\");\n" +
                "//#endif\n" +
                "}\n" +
                "}\n", new String(Files.readAllBytes(getTestJavaFile().toPath())));
    }

    @Test
    void testApplyOnKotlin() throws IOException {
        writeString(getTestKotlinFile(),
                "class Test {\n" +
                        "fun main(args : Array<String>) {\n" +
                        "//#if a\n" +
                        "//$$ System.out.println(\"Test succeeded.\");\n" +
                        "//#else\n" +
                        "System.out.println(\"Test failed\");\n" +
                        "//#endif\n" +
                        "}\n" +
                        "}\n"
        );

        GradleRunner runner = setupGradle();

        // Run the kotlin build
        runner.withArguments("applyPreProcessKotlin");
        BuildResult javaResult = runner.build();

        // Verify the result
        for (BuildTask task : javaResult.getTasks()) {
            assertEquals(TaskOutcome.SUCCESS, task.getOutcome());
        }

        assertEquals(
                "class Test {\n" +
                        "fun main(args : Array<String>) {\n" +
                        "//#if a\n" +
                        "System.out.println(\"Test succeeded.\");\n" +
                        "//#else\n" +
                        "//$$ System.out.println(\"Test failed\");\n" +
                        "//#endif\n" +
                        "}\n" +
                        "}\n", new String(Files.readAllBytes(getTestKotlinFile().toPath())));
    }

    @Test
    void testApplyOnResources() throws IOException {
        writeString(getTestJsonFile(),
                "{\n" +
                        "//#if a\n" +
                        "//$$ \"test\": \"123\"\n" +
                        "//#else\n" +
                        "\"test\": \"456\"\n" +
                        "//#endif\n" +
                        "}\n"
        );

        GradleRunner runner = setupGradle();

        // Run the resources build
        runner.withArguments("applyPreProcessResources");
        BuildResult resourcesResult = runner.build();

        // Verify the result
        for (BuildTask task : resourcesResult.getTasks()) {
            assertEquals(TaskOutcome.SUCCESS, task.getOutcome());
        }

        assertEquals(
                "{\n" +
                        "//#if a\n" +
                        "\"test\": \"123\"\n" +
                        "//#else\n" +
                        "//$$ \"test\": \"456\"\n" +
                        "//#endif\n" +
                        "}\n", new String(Files.readAllBytes(getTestJsonFile().toPath())));
    }

    private void writeString(@NotNull File file, String string) throws IOException {
        //noinspection ResultOfMethodCallIgnored
        file.getParentFile().mkdirs();
        try (Writer writer = new FileWriter(file)) {
            writer.write(string);
        }
    }
}
