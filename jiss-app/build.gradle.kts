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
