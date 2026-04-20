# convertit-libs

Source: [https://github.com/thebytearray/convertit-libs](https://github.com/thebytearray/convertit-libs)

## Add with Gradle

1. Register the JitPack Maven repository (for example in `settings.gradle.kts` next to `google()` and `mavenCentral()`):

```kotlin
maven { url = uri("https://jitpack.io") }
```

2. Add the libraries you need. Replace `TAG` with a [Git tag](https://github.com/thebytearray/convertit-libs/tags) or commit hash. After the first JitPack build, confirm the exact artifact names on [JitPack for this repo](https://jitpack.io/#thebytearray/convertit-libs).

```kotlin
dependencies {
    implementation("com.github.thebytearray:convertit-libs:exiv2:TAG")
    implementation("com.github.thebytearray:convertit-libs:taglib:TAG")
    implementation("com.github.thebytearray:convertit-libs:image-magick:TAG")
}
```

Use only the lines for the modules you depend on.
