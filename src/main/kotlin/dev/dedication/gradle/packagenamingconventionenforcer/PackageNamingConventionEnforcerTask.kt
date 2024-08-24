package dev.dedication.gradle.packagenamingconventionenforcer

import dev.dedication.gradle.packagenamingconventionenforcer.internal.PackageNamingConventionEnforcer
import dev.dedication.gradle.packagenamingconventionenforcer.internal.SourceFile
import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class PackageNamingConventionEnforcerTask : DefaultTask() {
  @get:Input abstract val rootPackageDir: Property<String>

  @TaskAction
  fun apply() {
    val resolvedRootPackageDir = rootPackageDir.get()
    val filesInViolation = collectFilesInViolation(resolvedRootPackageDir)
    if (filesInViolation.any()) {
      filesInViolation.forEach { logger.error("$it is in violation") }
      throw GradleException(
          "Some files are not in the expected root package dir '$resolvedRootPackageDir'")
    }
  }

  private fun collectFilesInViolation(rootPackageDir: String): List<SourceFile> =
      javaPluginExtension().mainSrcDirs().flatMap { srcDir ->
        PackageNamingConventionEnforcer(srcDir = srcDir, rootPackageDir = rootPackageDir)
            .collectFilesInViolation()
      }

  private fun javaPluginExtension(): JavaPluginExtension =
      project.extensions.getByType(JavaPluginExtension::class.java)

  private fun JavaPluginExtension.mainSrcDirs(): Set<File> =
      sourceSets.getByName("main").allSource.srcDirs
}
