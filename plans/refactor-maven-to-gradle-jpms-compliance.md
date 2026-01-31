# Refactor: Maven to Gradle + Full JPMS Compliance

**Date:** 2026-01-31
**Type:** refactor
**Version:** 1.3.14 → 1.4.0

## Overview

Convert the JISS multi-module build from Maven to Gradle (Kotlin DSL) targeting Java 23, and fix all module-info.java files for full JPMS compliance. This includes adding missing `provides`, `uses`, and `opens` directives, upgrading RSyntaxTextArea for stable JPMS module naming, and configuring Gradle publishing to GitHub Packages.

## Problem Statement

- Maven build targets Java 17; project needs Java 23.
- module-info.java files are incomplete: no `provides` directives (ServiceLoader will fail under strict JPMS), no `opens` for JAXB reflection, missing `uses` for `JissPreprocessor`.
- RSyntaxTextArea 2.0.2 has no stable JPMS module name (filename-derived).
- Build system modernization needed for ongoing maintenance.

## Technical Approach

### Decisions Made

| Decision | Choice | Rationale |
|---|---|---|
| Gradle DSL | Kotlin DSL (`.kts`) | Type-safe, better IDE support |
| Gradle version | 9.3.1 | Latest stable, best Java 23 support |
| Java target | 23 (toolchain) | Per requirements |
| Version | 1.4.0 | Signals build system change, avoids artifact collision |
| RSyntaxTextArea | Upgrade 2.0.2 → 3.6.1 | Stable `Automatic-Module-Name` |
| META-INF/services | Keep alongside `provides` | Dual classpath/module-path compat |
| JAXB `opens` | Targeted (`opens ... to com.sun.xml.bind`) | Minimum exposure |
| Publishing | All 5 modules to GitHub Packages | Matches current Maven behavior |

### Module Dependency Graph

```
jiss-core          (no internal deps)
  ↑
jiss-history       → jiss-core
  ↑
jiss-blocks        → jiss-core, jiss-history
jiss-rsyntaxarea-input → jiss-core
jiss-app           → jiss-core, jiss-history (+ blocks, rsyntaxarea as runtime)
```

---

## Implementation Phases

### Phase 1: Create Gradle Build Files

**Goal:** Project compiles with Gradle. Maven files still present for rollback.

**Files to create:**

#### `settings.gradle.kts`
```kotlin
rootProject.name = "jiss"

include("jiss-core")
include("jiss-history")
include("jiss-app")
include("jiss-rsyntaxarea-input")
include("jiss-blocks")
```

#### `gradle/libs.versions.toml`
```toml
[versions]
jaxb = "4.0.0"
commons-lang3 = "3.11"
rsyntaxtextarea = "3.6.1"

[libraries]
commons-lang3 = { module = "org.apache.commons:commons-lang3", version.ref = "commons-lang3" }
jakarta-xml-bind-api = { module = "jakarta.xml.bind:jakarta.xml.bind-api", version.ref = "jaxb" }
jaxb-impl = { module = "com.sun.xml.bind:jaxb-impl", version.ref = "jaxb" }
rsyntaxtextarea = { module = "com.fifesoft:rsyntaxtextarea", version.ref = "rsyntaxtextarea" }
```

#### Root `build.gradle.kts`
```kotlin
plugins {
    `java-library` apply false
    `maven-publish` apply false
    application apply false
}

subprojects {
    group = "ca.hedlund"
    version = "1.4.0"

    repositories {
        mavenCentral()
    }

    pluginManager.withPlugin("java-library") {
        configure<JavaPluginExtension> {
            toolchain {
                languageVersion = JavaLanguageVersion.of(23)
            }
            withSourcesJar()
            withJavadocJar()
        }
        tasks.withType<JavaCompile>().configureEach {
            options.encoding = "UTF-8"
        }
    }

    pluginManager.withPlugin("maven-publish") {
        configure<PublishingExtension> {
            repositories {
                maven {
                    name = "GitHubPackages"
                    url = uri("https://maven.pkg.github.com/ghedlund/jiss")
                    credentials {
                        username = project.findProperty("gpr.user") as String?
                            ?: System.getenv("GITHUB_ACTOR")
                        password = project.findProperty("gpr.key") as String?
                            ?: System.getenv("GITHUB_TOKEN")
                    }
                }
            }
            publications {
                register<MavenPublication>("gpr") {
                    from(components["java"])
                }
            }
        }
    }
}
```

#### `jiss-core/build.gradle.kts`
```kotlin
plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    api(libs.commons.lang3)
}
```

#### `jiss-history/build.gradle.kts`
```kotlin
plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    api(project(":jiss-core"))
    implementation(libs.jakarta.xml.bind.api)
    implementation(libs.jaxb.impl)
}
```

#### `jiss-blocks/build.gradle.kts`
```kotlin
plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    api(project(":jiss-core"))
    api(project(":jiss-history"))
    implementation(libs.jakarta.xml.bind.api)
    implementation(libs.jaxb.impl)
}
```

#### `jiss-rsyntaxarea-input/build.gradle.kts`
```kotlin
plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    api(project(":jiss-core"))
    implementation(libs.rsyntaxtextarea)
}
```

#### `jiss-app/build.gradle.kts`
```kotlin
plugins {
    application
    `maven-publish`
}

application {
    mainModule = "ca.hedlund.jiss.app"
    mainClass = "ca.hedlund.jiss.app.JissApp"
}

dependencies {
    implementation(project(":jiss-core"))
    implementation(project(":jiss-history"))
    runtimeOnly(project(":jiss-blocks"))
    runtimeOnly(project(":jiss-rsyntaxarea-input"))
}
```

**Validation:** `./gradlew build` compiles all modules.

**Pause for review.**

---

### Phase 2: Fix module-info.java for Full JPMS Compliance

**Goal:** All modules declare `provides`, missing `uses`, and `opens` directives.

#### `jiss-core/src/main/java/module-info.java`

Add:
```java
uses ca.hedlund.jiss.JissPreprocessor;

provides ca.hedlund.dp.extensions.ExtensionProvider with
    ca.hedlund.jiss.ui.bindings.SoftReturn,
    ca.hedlund.jiss.ui.bindings.RunCommand,
    ca.hedlund.jiss.ui.bindings.Cancel;

provides ca.hedlund.jiss.JissPreprocessor with
    ca.hedlund.jiss.preprocessor.InfoPreprocessor,
    ca.hedlund.jiss.preprocessor.ExecPreprocessor,
    ca.hedlund.jiss.preprocessor.LangPreprocessor,
    ca.hedlund.jiss.preprocessor.ResetPreprocessor;
```

#### `jiss-history/src/main/java/module-info.java`

Add:
```java
requires jakarta.xml.bind;

opens ca.hedlund.jiss.history to com.sun.xml.bind;

provides ca.hedlund.dp.extensions.ExtensionProvider with
    ca.hedlund.jiss.history.JissHistoryManager,
    ca.hedlund.jiss.history.JissHistoryBindings;

provides ca.hedlund.jiss.JissPreprocessor with
    ca.hedlund.jiss.history.JissHistoryPreprocessor;
```

#### `jiss-blocks/src/main/java/module-info.java`

Add:
```java
requires jakarta.xml.bind;

opens ca.hedlund.jiss.blocks to com.sun.xml.bind;

provides ca.hedlund.jiss.JissPreprocessor with
    ca.hedlund.jiss.blocks.preprocessor.SaveBlockPreprocessor,
    ca.hedlund.jiss.blocks.preprocessor.LoadBlockPreprocessor,
    ca.hedlund.jiss.blocks.preprocessor.ListBlocksPreprocessor;
```

#### `jiss-rsyntaxarea-input/src/main/java/module-info.java`

Change `requires rsyntaxtextarea;` to:
```java
requires org.fife.RSyntaxTextArea;
```

Add:
```java
provides ca.hedlund.dp.extensions.ExtensionProvider with
    ca.hedlund.jiss.textarea.OpenTextAreaAction;
```

**Validation:** `./gradlew build` succeeds with module-path resolution.

**Pause for review.**

---

### Phase 3: RSyntaxTextArea API Compatibility + Deprecation Fixes

**Goal:** Verify RSyntaxTextArea 3.6.1 compiles cleanly, fix deprecated API usage.

- [x] Verify `OpenTextAreaAction.java` compiles against RSyntaxTextArea 3.6.1 API
- [x] Replace `KeyEvent.CTRL_MASK` → `KeyEvent.CTRL_DOWN_MASK` in:
  - `jiss-rsyntaxarea-input/src/main/java/ca/hedlund/jiss/textarea/OpenTextAreaAction.java:162`
  - `jiss-core/src/main/java/ca/hedlund/jiss/ui/bindings/Cancel.java` (if present)
- [x] Fix any other compilation errors from the RSyntaxTextArea upgrade

**Validation:** `./gradlew build` succeeds with zero warnings from deprecated API usage.

**Pause for review.**

---

### Phase 4: Cleanup + Configuration

**Goal:** Remove Maven artifacts, update .gitignore, update CLAUDE.md.

- [x] Delete all `pom.xml` files (root + 5 modules)
- [x] Delete `pom.xml.versionsBackup`
- [x] Initialize Gradle wrapper: `gradle wrapper --gradle-version 9.3.1`
- [x] Update `.gitignore`:
  ```
  # Gradle
  .gradle/
  build/
  !gradle/wrapper/gradle-wrapper.jar
  ```
- [x] Update `CLAUDE.md` with new build commands:
  ```
  ./gradlew build          # Build all modules
  ./gradlew :jiss-core:build  # Build single module
  ./gradlew :jiss-app:run  # Run standalone app
  ./gradlew publish        # Publish to GitHub Packages
  ```
- [x] Verify publishing config: `./gradlew publishToMavenLocal` (dry run)

**Validation:** `./gradlew clean build` from clean state. `./gradlew :jiss-app:run` launches the app.

**Pause for review.**

---

## Risk Analysis

| Risk | Severity | Mitigation |
|---|---|---|
| RSyntaxTextArea 3.6.1 has breaking API changes | Medium | Phase 3 isolates this; can pin to 3.5.x if needed |
| JAXB reflection fails under strict JPMS | High | `opens` directives added in Phase 2; validate with `./gradlew :jiss-app:run` |
| ServiceLoader finds no providers | High | `provides` directives + keep META-INF/services; validate app startup |
| Gradle 9.3.1 plugin incompatibility | Low | No third-party Gradle plugins used; only built-in plugins |
| Java 23 toolchain not available locally | Low | Gradle auto-provisions via toolchain resolvers |
| Downstream consumers break | Medium | Version bump to 1.4.0 signals breaking change |

## Acceptance Criteria

### Functional
- [x] `./gradlew build` compiles all 5 modules successfully
- [ ] `./gradlew :jiss-app:run` launches the application with all plugins loaded (history, blocks, key bindings, syntax highlighting)
- [x] `./gradlew publishToMavenLocal` produces artifacts in `~/.m2/repository/ca/hedlund/`
- [x] All module-info.java files have correct `requires`, `exports`, `provides`, `uses`, and `opens` directives
- [x] No Maven files remain in the project

### Non-Functional
- [x] Gradle wrapper committed (gradlew, gradlew.bat, gradle/wrapper/)
- [x] .gitignore updated for Gradle conventions
- [x] CLAUDE.md reflects new build commands
- [x] Version catalog (`gradle/libs.versions.toml`) manages all dependency versions

## Unresolved Questions

1. Should `jiss-app` publish to GitHub Packages? (It's an application, not a library consumed by others. Current plan: publish all to match Maven behavior.)
2. Does `ca.hedlund.jiss.history.actions` package also need `opens` for JAXB, or only the package with `@XmlRootElement` classes? (Will verify during Phase 2.)
3. Should we add a CI workflow (GitHub Actions) for the new Gradle build? (Out of scope for this plan but recommended as follow-up.)
