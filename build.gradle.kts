plugins {
  id("com.gradle.plugin-publish") version "1.3.0"
  `kotlin-dsl`
  id("dev.dedication.gradle.code-formatter")
}

group = "dev.dedication.gradle"

version = "1.0"

gradlePlugin {
  plugins {
    create("package-naming-convention-enforcer") {
      id = "dev.dedication.gradle.package-naming-convention-enforcer"
      implementationClass =
          "dev.dedication.gradle.packagenamingconventionenforcer.PackageNamingConventionEnforcerPlugin"

      displayName = "Package naming convention enforcer Gradle Plugin"
      description =
          "A plugin to ensure that package names follow the reverse domain name followed by the Gradle project name."
      tags.set(listOf("java", "kotlin", "convention"))
    }
  }
  website.set("https://github.com/dedication-dev/package-naming-convention-enforcer-gradle-plugin")
  vcsUrl.set("https://github.com/dedication-dev/package-naming-convention-enforcer-gradle-plugin")
}

testing {
  suites {
    val functionalTest =
        register<JvmTestSuite>("functionalTest") { dependencies { implementation(libs.assertj) } }
    project.gradlePlugin { testSourceSets(functionalTest.get().sources) }
  }
}

tasks.named("check") { dependsOn(testing.suites.named("functionalTest")) }
