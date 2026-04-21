# convertit-libs

Source: [https://github.com/thebytearray/convertit-libs](https://github.com/thebytearray/convertit-libs)

The published Maven `groupId` is **`org.thebytearray.lib`** (override at publish time with `-PpublishGroup=...` if needed). Artifact names match Gradle project names: **`exiv2`**, **`taglib`**, **`image-magick`**.

## Maven Central (consumers)

1. Ensure `mavenCentral()` is on your `repositories` (default for many Android/Gradle projects).

2. Add dependencies, replacing `VERSION` with the [release you want on Central](https://search.maven.org/search?q=org.thebytearray.lib):

```kotlin
dependencies {
    implementation("org.thebytearray.lib:exiv2:VERSION")
    implementation("org.thebytearray.lib:taglib:VERSION")
    implementation("org.thebytearray.lib:image-magick:VERSION")
}
```

3. This repository is published from CI using [.github/workflows/publish-maven-central.yml](.github/workflows/publish-maven-central.yml) (on `v*` tags, when a GitHub Release is published, or manual `workflow_dispatch`).

## ImageMagick native pieces (`:core:native:image-magick`)

Native binaries for `image-magick` are **not** built in this repository. Run [`scripts/fetch-imagemagick-jnilibs.sh`](scripts/fetch-imagemagick-jnilibs.sh) to download the **latest** [Android-ImageMagick7](https://github.com/codewithtamim/Android-ImageMagick7) release (GitHub API) and install `libmagick_bin.so` plus the matching `.so` set under [`core/native/image-magick/src/main/jniLibs/`](core/native/image-magick/src/main/jniLibs/). The `usr/` config tree under that module’s `src/main/assets/` is maintained here as source; the fetch script does not change it. Those prebuilt `*.so` are **not committed** (see `.gitignore`); the [publish workflow](.github/workflows/publish-maven-central.yml) runs the same fetch before publishing to Central.

## Publishing (maintainers)

Configure **Sonatype Central** credentials and a PGP key as Central expects (see [Central Portal / publish guide](https://central.sonatype.org/publish/publish-portal-ossrh-staging-api/)). For GitHub Actions, add these repository **secrets** (consumed as Gradle `ORG_GRADLE_PROJECT_*` project properties in the workflow):

| Secret | Purpose |
|--------|---------|
| `MAVEN_CENTRAL_USERNAME` | Central user token username |
| `MAVEN_CENTRAL_PASSWORD` | Central user token password |
| `MAVEN_SIGNING_KEY` | ASCII-armored private signing key |
| `MAVEN_SIGNING_PASSWORD` | Key passphrase (if the key is encrypted) |

For local dry runs, you can use [`publishAllNativeLibrariesToMavenLocal`](build.gradle.kts) with a local SDK and, when applicable, the same `signingKey` / `signingPassword` and `sonatypeUsername` / `sonatypePassword` in `~/.gradle/gradle.properties` if you need to test signing or staging end-to-end. Run `./scripts/fetch-imagemagick-jnilibs.sh` first so `:core:native:image-magick` can assemble with jniLibs, matching CI.
