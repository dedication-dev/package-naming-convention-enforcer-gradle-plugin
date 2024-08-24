package dev.dedication.gradle.packagenamingconventionenforcer

import java.io.File
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome

private const val s = "enforcePackageNamingConvention"

class Project(projectDir: File, group: String, name: String) {
  private val projectDir = projectDir.resolve(name).also { it.mkdirs() }

  private val taskRunner = TaskRunner(this.projectDir)

  private val buildFile =
      File(this.projectDir, "build.gradle.kts").also {
        it.writeText(
            """
           |plugins {
		   |  java
		   |  id("org.jetbrains.kotlin.jvm") version "1.9.25"
           |  id("dev.dedication.gradle.package-naming-convention-enforcer")
           |}
           |
           |group = "$group"
		   |"""
                .trimMargin())
      }

  fun setReverseDomainName(value: String) {
    buildFile.appendText(
        """
	   |
	   |packageNamingConventionEnforcer {
	   |  reverseDomainName.set("$value")
	   |}
	   |"""
            .trimMargin())
  }

  fun addJavaFile(packageName: String, fileName: String) {
    addSourceFile("src/main/java", packageName, fileName)
  }

  fun addJavaTestFile(packageName: String, fileName: String) {
    addSourceFile("src/test/java", packageName, fileName)
  }

  fun addKotlinFile(packageName: String, fileName: String) {
    addSourceFile("src/main/kotlin", packageName, fileName)
  }

  private fun addSourceFile(sourceSetDir: String, packageName: String, fileName: String) {
    val packageNameAsDir = packageName.replace(".", "/")
    addFile(File(sourceSetDir).resolve(packageNameAsDir).path, fileName)
  }

  fun addFile(dir: String, fileName: String) {
    val packageDir = projectDir.resolve(dir).also { it.mkdirs() }
    packageDir.resolve(fileName).createNewFile()
  }

  fun enforcePackageNamingConvention(
      expectedOutcome: TaskOutcome = TaskOutcome.SUCCESS,
  ): TaskRunner.RunResult {
    val result = tryEnforcePackageNamingConvention()
    assertThat(result.task.outcome).`as`(result.buildResult.output).isEqualTo(expectedOutcome)
    return result
  }

  fun tryEnforcePackageNamingConvention(): TaskRunner.RunResult {
    return taskRunner.tryRun("enforcePackageNamingConvention")
  }
}
