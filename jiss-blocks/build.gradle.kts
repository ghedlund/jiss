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
