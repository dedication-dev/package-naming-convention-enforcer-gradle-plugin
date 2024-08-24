package dev.dedication.gradle.packagenamingconventionenforcer.internal

import java.io.File

class SourceFile(srcDir: File, private val file: File) {
  val relativePath: String = srcDir.toPath().relativize(file.parentFile.toPath()).toString()

  override fun toString(): String {
    return "SourceFile(relativePath='$relativePath', name='${file.name}')"
  }
}
