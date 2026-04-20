// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("org.thebytearray.convertit.android.library") apply false
}

group = "com.github.thebytearray.convertitlibs"
version = "1.0.0-SNAPSHOT"

subprojects {
    group = rootProject.group
    version = rootProject.version
}

tasks.register("publishAllNativeLibrariesToMavenLocal") {
    group = "publishing"
    description = "Publishes all :core:native:* Android libraries to Maven Local (for JitPack-style verification)."
    dependsOn(
        ":core:native:exiv2:publishReleasePublicationToMavenLocal",
        ":core:native:taglib:publishReleasePublicationToMavenLocal",
        ":core:native:image-magick:publishReleasePublicationToMavenLocal",
        ":core:native:ffmpeg-kit:publishReleasePublicationToMavenLocal",
    )
}
