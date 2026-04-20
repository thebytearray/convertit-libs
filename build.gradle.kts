// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("org.thebytearray.convertit.android.library") apply false
}

group = findProperty("publishGroup")?.toString() ?: "io.github.thebytearray"
version = findProperty("publishVersion")?.toString()
    ?: findProperty("version")?.toString()
    ?: "1.0.0-SNAPSHOT"

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

tasks.register("publishAllNativeLibrariesToGitHubPackages") {
    group = "publishing"
    description = "Publishes all :core:native:* Android libraries to GitHub Packages (requires gpr.* or GITHUB_TOKEN)."
    dependsOn(
        ":core:native:exiv2:publishReleasePublicationToGitHubPackagesRepository",
        ":core:native:taglib:publishReleasePublicationToGitHubPackagesRepository",
        ":core:native:image-magick:publishReleasePublicationToGitHubPackagesRepository",
        ":core:native:ffmpeg-kit:publishReleasePublicationToGitHubPackagesRepository",
    )
}
