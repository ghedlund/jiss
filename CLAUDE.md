# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Java Interactive Scripting Shell (JISS) — a Swing component for embedding a JSR-223 scripting shell in Java applications. Multi-module Maven project, Java 17+, Apache 2.0 license.

## Build Commands

```bash
mvn clean install              # Build all modules
mvn -pl jiss-core clean install  # Build single module
mvn test                       # Run tests (jiss-history, jiss-blocks have tests)
mvn -pl jiss-history test      # Test single module
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
- **Extension system** (`ca.hedlund.dp.extensions`): ServiceLoader-based plugin architecture. Extensions discovered via META-INF/services files.
- **Java modules**: All subprojects declare `module-info.java` (JPMS).

## Key Dependencies

- Apache Commons Lang 3.11
- Jakarta XML Bind API 4.0.0 + JAXB impl (history/blocks serialization)
- RSyntaxTextArea 2.0.2 (syntax highlighting plugin)

## Conventions

- Uses Java Platform Module System (JPMS) — every module has `module-info.java`
- Plugin discovery via `META-INF/services/` (ServiceLoader pattern)
- Swing EDT conventions for UI code
- Package root: `ca.hedlund.jiss` (core), `ca.hedlund.dp` (design pattern utilities)
