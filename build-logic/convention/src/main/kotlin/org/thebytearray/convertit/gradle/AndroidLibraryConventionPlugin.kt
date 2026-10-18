package org.thebytearray.convertit.gradle

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register
import org.gradle.plugins.signing.SigningExtension

/**
 * Convention for Android libraries (e.g. native JNI wrappers): shared SDK, Java/Kotlin 17, packaging.
 * Each module still sets its own [namespace], [minSdk], NDK/CMake, and dependencies.
 */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        apply(plugin = "com.android.library")
        apply(plugin = "maven-publish")
        apply(plugin = "signing")

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
                        pom {
                            name.set(project.name)
                            description.set(
                                "Android JNI wrapper / native build for ${
                                    project.name.replace(
                                        "-",
                                        " ",
                                    )
                                } (Convertit Libs).",
                            )
                            url.set("https://github.com/thebytearray/convertit-libs")
                            // The wrapper is LGPL; each artifact also carries the licences of the
                            // upstream code it bundles (see THIRD_PARTY_NOTICES.md).
                            licenses {
                                (artifactLicenses[project.name] ?: listOf(WRAPPER_LICENSE)).forEach { (licenseName, licenseUrl) ->
                                    license {
                                        name.set(licenseName)
                                        url.set(licenseUrl)
                                    }
                                }
                            }
                            developers {
                                developer {
                                    name.set("The Byte Array")
                                }
                            }
                            scm {
                                connection.set("scm:git:git://github.com/thebytearray/convertit-libs.git")
                                developerConnection.set("scm:git:git@github.com:thebytearray/convertit-libs.git")
                                url.set("https://github.com/thebytearray/convertit-libs")
                            }
                        }
                    }
                }
            }

            val pgpKey = (findProperty("signingKey") as String?)?.trim()?.takeIf { it.isNotEmpty() }
            val pgpPassword = (findProperty("signingPassword") as String?)?.trim()?.takeIf { it.isNotEmpty() }
            val releasePublication = (extensions.getByName("publishing") as PublishingExtension)
                .publications
                .getByName("release")
            extensions.configure<SigningExtension>("signing") {
                isRequired = pgpKey != null && pgpPassword != null
                if (pgpKey != null && pgpPassword != null) {
                    useInMemoryPgpKeys(pgpKey, pgpPassword)
                }
                sign(releasePublication)
            }
        }
    }
}

private val WRAPPER_LICENSE =
    "GNU Lesser General Public License v2.1 or later" to "https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html"

/** Upstream licences bundled in each artifact, after the wrapper's own. */
private val artifactLicenses = mapOf(
    "taglib" to listOf(
        WRAPPER_LICENSE,
        "TagLib: GNU Lesser General Public License v2.1 only OR Mozilla Public License 1.1" to
            "https://github.com/taglib/taglib/blob/master/COPYING.LGPL",
    ),
    "image-magick" to listOf(
        WRAPPER_LICENSE,
        "ImageMagick License" to "https://imagemagick.org/script/license.php",
        "libheif and libde265: GNU Lesser General Public License v3.0 or later" to
            "https://www.gnu.org/licenses/lgpl-3.0.html",
        "Other bundled libraries: see THIRD_PARTY_NOTICES.md" to
            "https://github.com/thebytearray/convertit-libs/blob/main/THIRD_PARTY_NOTICES.md",
    ),
    // Exiv2 is GPL, so the combined library is GPL-2.0-or-later.
    "exiv2" to listOf(
        "GNU General Public License v2.0 or later (Exiv2)" to "https://www.gnu.org/licenses/old-licenses/gpl-2.0.html",
        WRAPPER_LICENSE,
    ),
)
