# Package naming convention enforcer

A Gradle plugin to enforce package naming conventions for Kotlin and Java projects.  
Ensures that package names follow the reverse domain name followed by the gradle project name,  
with hyphens removed and all characters in lowercase.

Allows configuration of the domain name as follows:

```kotlin
packageNamingConventionEnforcer { reverseDomainName = "dev.dedication.example" }
```
