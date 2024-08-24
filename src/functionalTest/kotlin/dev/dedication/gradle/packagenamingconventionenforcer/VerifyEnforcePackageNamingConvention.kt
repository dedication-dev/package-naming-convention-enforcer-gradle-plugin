package dev.dedication.gradle.packagenamingconventionenforcer

import java.io.File
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class VerifyEnforcePackageNamingConvention {

  @TempDir private lateinit var projectDir: File

  @Test
  fun `does not care about non source dirs`() {
    val project = Project(projectDir, group = "dev.dedication", name = "name")
    project.addFile("config", "example.java")

    project.enforcePackageNamingConvention()
  }

  @Test
  fun `does not care about test source dirs`() {
    val project = Project(projectDir, group = "dev.dedication", name = "name")
    project.addJavaTestFile("dev.dedication.test", "Main.java")

    project.enforcePackageNamingConvention()
  }

  @Test
  fun `fails for packages with non matching reverse domain name`() {
    val project = Project(projectDir, group = "dev.dedication", name = "name")
    project.addJavaFile("com.dedication.name", "Main.java")

    val result = project.enforcePackageNamingConvention(expectedOutcome = TaskOutcome.FAILED)
    assertThat(result.buildResult.output)
        .contains(
            fileInViolation(relativePath = "com/dedication/name", name = "Main.java"),
            notInExpectedRootPackageDir("dev/dedication/name"))
  }

  @Test
  fun `fails for files in any dir up to reverse domain name + normalized project name`() {
    val project = Project(projectDir, group = "dev.dedication", name = "name")
    project.addJavaFile("dev.dedication" /* missing project name */, "Main.java")

    val result = project.enforcePackageNamingConvention(expectedOutcome = TaskOutcome.FAILED)
    assertThat(result.buildResult.output)
        .contains(
            fileInViolation(relativePath = "dev/dedication", name = "Main.java"),
            notInExpectedRootPackageDir("dev/dedication/name"))
  }

  @Test
  fun `finds violations in kotlin code`() {
    val project = Project(projectDir, group = "dev.dedication", name = "name")
    project.addKotlinFile("com.dedication.name", "Main.kt")

    val result = project.enforcePackageNamingConvention(expectedOutcome = TaskOutcome.FAILED)
    assertThat(result.buildResult.output)
        .contains(
            fileInViolation(relativePath = "com/dedication/name", name = "Main.kt"),
            notInExpectedRootPackageDir("dev/dedication/name"))
  }

  @Nested
  inner class WithProjectName {

    @Test
    fun `including dashes works`() {
      val project = Project(projectDir, group = "dev.dedication", name = "name-with-dashes")
      project.addJavaFile("dev.dedication.namewithdashes", "Main.java")
      project.addJavaFile("dev.dedication.name", "Main.java")

      val result = project.enforcePackageNamingConvention(expectedOutcome = TaskOutcome.FAILED)
      assertThat(result.buildResult.output)
          .contains(
              fileInViolation(relativePath = "dev/dedication/name", name = "Main.java"),
              notInExpectedRootPackageDir("dev/dedication/namewithdashes"))
    }

    @Test
    fun `including uppercase letters works`() {
      val project = Project(projectDir, group = "dev.dedication", name = "NameWithUppercase")
      project.addJavaFile("dev.dedication.namewithuppercase", "Main.java")

      project.enforcePackageNamingConvention()
    }
  }

  @Nested
  inner class OnProjectWithoutGroup {

    @Test
    fun `and reverse domain name fails`() {
      val project = Project(projectDir, group = "", name = "name")
      project.addJavaFile("dev.dedication.name", "Main.java")

      val result = project.tryEnforcePackageNamingConvention()
      assertThat(result.buildResult.output).contains("> Reverse domain name must not be empty")
    }

    @Test
    fun `but reverse domain name works`() {
      val project = Project(projectDir, group = "", name = "name")
      project.setReverseDomainName("dev.dedication.legacy")
      project.addJavaFile("dev.dedication.legacy.name", "Main.java")

      project.enforcePackageNamingConvention()
    }
  }

  @Test
  fun `custom prefix overrules group`() {
    val project = Project(projectDir, group = "dev.dedication", name = "name")
    project.setReverseDomainName("dev.dedication.legacy")
    project.addJavaFile("dev.dedication.legacy.name", "Main.java")

    project.enforcePackageNamingConvention()
  }

  @Test
  fun `works with multiple source dirs`() {
    val project = Project(projectDir, group = "dev.dedication", name = "name")
    project.addJavaFile("dev.dedication.java", "Main.java")
    project.addKotlinFile("dev.dedication.kt", "Main.kt")

    val result = project.enforcePackageNamingConvention(expectedOutcome = TaskOutcome.FAILED)
    assertThat(result.buildResult.output)
        .contains(
            fileInViolation(relativePath = "dev/dedication/java", name = "Main.java"),
            fileInViolation(relativePath = "dev/dedication/kt", name = "Main.kt"),
            notInExpectedRootPackageDir("dev/dedication/name"))
  }

  private fun notInExpectedRootPackageDir(expectedRootPackageDir: String) =
      "> Some files are not in the expected root package dir '$expectedRootPackageDir'"

  private fun fileInViolation(relativePath: String, name: String) =
      "SourceFile(relativePath='$relativePath', name='$name') is in violation"
}
