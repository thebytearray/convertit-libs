# convertit-libs

Source: [https://github.com/thebytearray/convertit-libs](https://github.com/thebytearray/convertit-libs)

Published Maven group id: `io.github.thebytearray` (override locally with `-PpublishGroup=...`).

## GitHub Packages

1. Create a personal access token (classic) with `read:packages` (and `repo` if the repo is private).

2. In `settings.gradle.kts` (or the `dependencyResolutionManagement` block), add the GitHub Packages Maven repository and credentials:

```kotlin
maven {
    url = uri("https://maven.pkg.github.com/thebytearray/convertit-libs")
    credentials {
        username = providers.environmentVariable("GITHUB_PACKAGES_USER").orElse("thebytearray").get()
        password = providers.environmentVariable("GITHUB_PACKAGES_TOKEN").get()
    }
}
```

Set `GITHUB_PACKAGES_TOKEN` to your PAT (or use Gradle `~/.gradle/gradle.properties`: `gpr.key=` / `gpr.user=` for local CLI publishes).

3. Add dependencies (artifact ids match Gradle project names: `exiv2`, `taglib`, `image-magick`, `ffmpeg-kit`):

```kotlin
dependencies {
    implementation("io.github.thebytearray:exiv2:VERSION")
    implementation("io.github.thebytearray:taglib:VERSION")
    implementation("io.github.thebytearray:image-magick:VERSION")
    implementation("io.github.thebytearray:ffmpeg-kit:VERSION")
}
```

Replace `VERSION` with the release you published (see Actions workflow or the Packages tab on GitHub).

Publishing is automated by [.github/workflows/publish-github-packages.yml](.github/workflows/publish-github-packages.yml) (on `v*` tags, GitHub Releases, or manual `workflow_dispatch`).

## JitPack (alternative)

1. Register JitPack:

```kotlin
maven { url = uri("https://jitpack.io") }
```

2. Use a [git tag](https://github.com/thebytearray/convertit-libs/tags) and confirm coordinates on [JitPack](https://jitpack.io/#thebytearray/convertit-libs). Artifact names follow submodule names (e.g. `exiv2`, `taglib`).

## FFmpeg Kit AAR

The sample app also uses the checked-in AAR:

```kotlin
implementation(files("path/to/convertit-libs/core/native/ffmpeg-kit/libs/ffmpeg-kit.aar"))
```

Or copy `libs/ffmpeg-kit.aar` into your project and point `files(...)` at it.
