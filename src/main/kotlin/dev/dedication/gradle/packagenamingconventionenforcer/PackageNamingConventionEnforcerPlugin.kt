package dev.dedication.gradle.packagenamingconventionenforcer

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

class PackageNamingConventionEnforcerPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    val extension =
        project.extensions
            .create<PackageNamingConventionEnforcerExtension>("packageNamingConventionEnforcer")
            .apply { reverseDomainName.convention(project.provider { project.group.toString() }) }

    project.tasks.register<PackageNamingConventionEnforcerTask>("enforcePackageNamingConvention") {
      group = "verification"
      description = "Enforces the package naming convention"

      val reverseDomainName = extension.reverseDomainName.get()
      if (reverseDomainName.isEmpty()) {
        throw GradleException("Reverse domain name must not be empty")
      }
      val reverseDomainNameDir = reverseDomainName.replace(".", "/")
      val normalizedProjectName = project.name.replace("-", "").lowercase()
      rootPackageDir.set("${reverseDomainNameDir}/$normalizedProjectName")
    }
  }
}
