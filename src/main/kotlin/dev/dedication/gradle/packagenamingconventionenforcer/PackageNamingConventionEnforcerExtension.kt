package dev.dedication.gradle.packagenamingconventionenforcer

import org.gradle.api.provider.Property

interface PackageNamingConventionEnforcerExtension {
  val reverseDomainName: Property<String>
}
