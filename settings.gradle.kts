plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "jiss"

include("jiss-core")
include("jiss-history")
include("jiss-app")
include("jiss-rsyntaxarea-input")
include("jiss-blocks")
