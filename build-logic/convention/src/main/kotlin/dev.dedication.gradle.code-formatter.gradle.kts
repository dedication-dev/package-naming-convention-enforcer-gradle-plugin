plugins { id("com.diffplug.spotless") }

spotless {
  kotlinGradle { ktfmt() }
  kotlin {
    // Convention plugins are converted to kt files in build/generated-sources/kotlin-dsl-accessors
    targetExclude("build/**")
    ktfmt()
  }
}
