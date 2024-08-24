package dev.dedication.gradle.packagenamingconventionenforcer.internal

import java.io.File
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PackageNamingConventionEnforcer(
    private val srcDir: File,
    private val rootPackageDir: String
) {

  companion object {
    private val LOGGER: Logger =
        LoggerFactory.getLogger(PackageNamingConventionEnforcer::class.java)
  }

  fun collectFilesInViolation(): Sequence<SourceFile> {
    LOGGER.info("Collecting .kt and .java files in violation in $srcDir")
    return srcDir
        .filesWithCode()
        .map { SourceFile(srcDir, it) }
        .filterNot { it.relativePath.startsWith(rootPackageDir) }
  }

  private fun File.filesWithCode(): Sequence<File> =
      walkTopDown().filter { it.extension in listOf("java", "kt") }
}
