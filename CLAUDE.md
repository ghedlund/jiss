# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Java Interactive Scripting Shell (JISS) — a Swing component for embedding a JSR-223 scripting shell in Java applications. Multi-module Gradle project (Kotlin DSL), Java 23, Apache 2.0 license.

## Build Commands

```bash
./gradlew build                    # Build all modules
./gradlew :jiss-core:build         # Build single module
./gradlew :jiss-app:run            # Run standalone app
./gradlew publishToMavenLocal      # Publish to local Maven repo
./gradlew publish                  # Publish to GitHub Packages
```

## Modules

```
jiss-core                 # Foundation: JissModel, JissConsole, preprocessor pipeline, extension system
jiss-history              # History management plugin (JAXB serialization)
jiss-blocks               # Code block management plugin (depends on core + history)
jiss-rsyntaxarea-input    # RSyntaxTextArea-based syntax-highlighted input plugin
jiss-app                  # Standalone Swing application (entry: JissApp)
```

Dependency flow: `jiss-app → jiss-core ← jiss-history ← jiss-blocks`, `jiss-core ← jiss-rsyntaxarea-input`

## Architecture

- **JissModel** (`ca.hedlund.jiss`): Central model holding ScriptEngine, ScriptContext, Processor, and preprocessor list. Implements IExtendable for plugin support.
- **JissConsole** (`ca.hedlund.jiss.ui`): JTextPane-based REPL with custom document, caret, and navigation filtering.
- **Preprocessor chain** (`ca.hedlund.jiss.preprocessor`): Chain of responsibility — commands like `::exec`, `::info`, `::lang`, `::reset` are handled by JissPreprocessor implementations before reaching the script engine.
- **Extension system** (`ca.hedlund.dp.extensions`): ServiceLoader-based plugin architecture. Extensions discovered via both `provides` directives in `module-info.java` and `META-INF/services` files.
- **Java modules**: All subprojects are fully JPMS-compliant with `module-info.java` declaring `requires`, `exports`, `provides`, `uses`, and `opens` directives.

## Key Dependencies

Managed in `gradle/libs.versions.toml`:
- Apache Commons Lang 3.11
- Jakarta XML Bind API 4.0.0 + JAXB impl (history/blocks serialization)
- RSyntaxTextArea 3.6.1 (syntax highlighting plugin)

## Conventions

- Uses Java Platform Module System (JPMS) — every module has `module-info.java` with full `provides`/`uses` declarations
- Plugin discovery via ServiceLoader (`provides` in module-info.java + `META-INF/services/` for classpath compat)
- JAXB packages opened via targeted `opens ... to com.sun.xml.bind` directives
- Swing EDT conventions for UI code
- Package root: `ca.hedlund.jiss` (core), `ca.hedlund.dp` (design pattern utilities)
- Java 23 toolchain with foojay auto-provisioning
