package org.thebytearray.convertit.gradle

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register

/**
 * Convention for Android libraries (e.g. native JNI wrappers): shared SDK, Java/Kotlin 17, packaging.
 * Each module still sets its own [namespace], [minSdk], NDK/CMake, and dependencies.
 */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        apply(plugin = "com.android.library")
        apply(plugin = "maven-publish")

        extensions.configure<LibraryExtension> {
            compileSdk = 36
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
            defaultConfig {
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                consumerProguardFiles("consumer-rules.pro")
            }
            buildTypes {
                release {
                    isMinifyEnabled = false
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro",
                    )
                }
            }
            packaging {
                jniLibs {
                    useLegacyPackaging = true
                }
            }
            publishing {
                singleVariant("release") {
                    withSourcesJar()
                }
            }
        }
        configureKotlinAndroid()

        afterEvaluate {
            extensions.configure<PublishingExtension>("publishing") {
                publications {
                    register<MavenPublication>("release") {
                        groupId = project.group.toString()
                        artifactId = project.name
                        version = project.version.toString()
                        from(project.components.getByName("release"))
                    }
                }
            }
        }
    }
}
