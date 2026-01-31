plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    api(project(":jiss-core"))
    implementation(libs.rsyntaxtextarea)
}
