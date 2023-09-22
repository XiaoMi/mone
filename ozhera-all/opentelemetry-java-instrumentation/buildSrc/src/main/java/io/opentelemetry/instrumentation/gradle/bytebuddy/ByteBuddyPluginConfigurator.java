/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.gradle.bytebuddy;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.bytebuddy.build.gradle.ByteBuddySimpleTask;
import net.bytebuddy.build.gradle.Transformation;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.compile.AbstractCompile;

/**
 * Starting from version 1.10.15, ByteBuddy gradle plugin transformation task autoconfiguration is
 * hardcoded to be applied to javaCompile task. This causes the dependencies to be resolved during
 * an afterEvaluate that runs before any afterEvaluate specified in the build script, which in turn
 * makes it impossible to add dependencies in afterEvaluate. Additionally the autoconfiguration will
 * attempt to scan the entire project for tasks which depend on the compile task, to make each task
 * that depends on compile also depend on the transformation task. This is an extremely inefficient
 * operation in this project to the point of causing a stack overflow in some environments.
 *
 * <p>To avoid all the issues with autoconfiguration, this class manually configures the ByteBuddy
 * transformation task. This also allows it to be applied to source languages other than Java. The
 * transformation task is configured to run between the compile and the classes tasks, assuming no
 * other task depends directly on the compile task, but instead other tasks depend on classes task.
 * Contrary to how the ByteBuddy plugin worked in versions up to 1.10.14, this changes the compile
 * task output directory, as starting from 1.10.15, the plugin does not allow the source and target
 * directories to be the same. The transformation task then writes to the original output directory
 * of the compile task.
 */
public class ByteBuddyPluginConfigurator {
  private static final List<String> LANGUAGES = Arrays.asList("java", "scala", "kotlin");

  private final Project project;
  private final SourceSet sourceSet;
  private final String pluginClassName;
  private final FileCollection inputClasspath;

  public ByteBuddyPluginConfigurator(
      Project project, SourceSet sourceSet, String pluginClassName, FileCollection inputClasspath) {
    this.project = project;
    this.sourceSet = sourceSet;
    this.pluginClassName = pluginClassName;

    // add build resources dir to classpath if it's present
    File resourcesDir = sourceSet.getOutput().getResourcesDir();
    this.inputClasspath =
        resourcesDir == null ? inputClasspath : inputClasspath.plus(project.files(resourcesDir));
  }

  public void configure() {
    String taskName = getTaskName();

    List<TaskProvider<?>> languageTasks =
        LANGUAGES.stream()
            .map(
                language -> {
                  if (project.fileTree("src/" + sourceSet.getName() + "/" + language).isEmpty()) {
                    return null;
                  }
                  String compileTaskName = sourceSet.getCompileTaskName(language);
                  if (!project.getTasks().getNames().contains(compileTaskName)) {
                    return null;
                  }
                  TaskProvider<?> compileTask = project.getTasks().named(compileTaskName);

                  // We also process resources for SPI classes.
                  return createLanguageTask(
                      compileTask, taskName + language, sourceSet.getProcessResourcesTaskName());
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    TaskProvider<?> byteBuddyTask =
        project.getTasks().register(taskName, task -> task.dependsOn(languageTasks));

    project
        .getTasks()
        .named(sourceSet.getClassesTaskName())
        .configure(task -> task.dependsOn(byteBuddyTask));
  }

  private TaskProvider<?> createLanguageTask(
      TaskProvider<?> compileTaskProvider, String name, String processResourcesTaskName) {
    return project
        .getTasks()
        .register(
            name,
            ByteBuddySimpleTask.class,
            task -> {
              task.setGroup("Byte Buddy");
              task.getOutputs().cacheIf(unused -> true);

              Task maybeCompileTask = compileTaskProvider.get();
              if (maybeCompileTask instanceof AbstractCompile) {
                AbstractCompile compileTask = (AbstractCompile) maybeCompileTask;
                File classesDirectory = compileTask.getDestinationDir();
                File rawClassesDirectory =
                    new File(classesDirectory.getParent(), classesDirectory.getName() + "raw")
                        .getAbsoluteFile();

                task.dependsOn(compileTask);
                compileTask.setDestinationDir(rawClassesDirectory);

                task.setSource(rawClassesDirectory);
                task.setTarget(classesDirectory);
                task.setClassPath(compileTask.getClasspath());

                task.dependsOn(compileTask, processResourcesTaskName);
              }

              task.getTransformations().add(createTransformation(inputClasspath, pluginClassName));
            });
  }

  private String getTaskName() {
    if (SourceSet.MAIN_SOURCE_SET_NAME.equals(sourceSet.getName())) {
      return "byteBuddy";
    } else {
      return sourceSet.getName() + "ByteBuddy";
    }
  }

  private static Transformation createTransformation(
      FileCollection classPath, String pluginClassName) {
    Transformation transformation = new ClasspathTransformation(classPath, pluginClassName);
    transformation.setPlugin(ClasspathByteBuddyPlugin.class);
    return transformation;
  }
}
