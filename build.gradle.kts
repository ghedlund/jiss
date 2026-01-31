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
        tasks.withType<Javadoc>().configureEach {
            (options as StandardJavadocDocletOptions).addBooleanOption("Xdoclint:none", true)
        }
    }

    pluginManager.withPlugin("java") {
        configure<JavaPluginExtension> {
            toolchain {
                languageVersion = JavaLanguageVersion.of(23)
            }
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
