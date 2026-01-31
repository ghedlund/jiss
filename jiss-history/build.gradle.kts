plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    api(project(":jiss-core"))
    implementation(libs.jakarta.xml.bind.api)
    implementation(libs.jaxb.impl)
}
