// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.library) apply false
    id("org.thebytearray.convertit.android.library") apply false
    alias(libs.plugins.nexus.publish)
}

group = findProperty("publishGroup")?.toString() ?: "org.thebytearray.lib"
version = findProperty("publishVersion")?.toString()
    ?: findProperty("version")?.toString()
    ?: "1.0.0-SNAPSHOT"

subprojects {
    group = rootProject.group
    version = rootProject.version
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
        }
    }
}

tasks.register("publishAllNativeLibrariesToMavenLocal") {
    group = "publishing"
    description = "Publishes all :core:native:* Android libraries to Maven Local (local verification only)."
    dependsOn(
        ":core:native:exiv2:publishReleasePublicationToMavenLocal",
        ":core:native:taglib:publishReleasePublicationToMavenLocal",
        ":core:native:image-magick:publishReleasePublicationToMavenLocal",
    )
}

// Single entry point: Sonatype close/release task depends (via the nexus plugin) on publishing to staging.
tasks.register("publishAllNativeLibrariesToMavenCentral") {
    group = "publishing"
    description =
        "Publishes all :core:native:* Android libraries to Maven Central (requires signing + sonatype + Central credentials)."
    dependsOn("closeAndReleaseSonatypeStagingRepository")
}
