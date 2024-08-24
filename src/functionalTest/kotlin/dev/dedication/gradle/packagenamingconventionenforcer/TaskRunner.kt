package dev.dedication.gradle.packagenamingconventionenforcer

import java.io.File
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.BuildTask
import org.gradle.testkit.runner.GradleRunner

class TaskRunner(private val projectDir: File) {
  fun tryRun(taskName: String) =
      RunResult(
          taskName,
          GradleRunner.create()
              .withProjectDir(projectDir)
              .withArguments(taskName)
              .withPluginClasspath()
              .withDebug(true)
              .run())

  class RunResult(private val taskName: String, val buildResult: BuildResult) {
    val task: BuildTask
      get() =
          buildResult.task(":$taskName")
              ?: throw AssertionError(
                  "Task '$taskName' not found, see output:${buildResult.output}")
  }
}
