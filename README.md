# convertit-libs

Source: [https://github.com/thebytearray/convertit-libs](https://github.com/thebytearray/convertit-libs)

Published on Maven Central as group **`org.thebytearray.lib`**. Artifacts: **`exiv2`**, **`taglib`**, **`image-magick`**.

## Use from Maven Central

1. Ensure `mavenCentral()` is in your `repositories` block (it usually is in Android/Gradle projects).

2. Add dependencies, replacing `VERSION` with a version from [search.maven.org for `org.thebytearray.lib`](https://search.maven.org/search?q=org.thebytearray.lib):

```kotlin
dependencies {
    implementation("org.thebytearray.lib:exiv2:VERSION")
    implementation("org.thebytearray.lib:taglib:VERSION")
    implementation("org.thebytearray.lib:image-magick:VERSION")
}
```

3. License and third-party credits: [LICENSE](LICENSE) and [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).

## Native libraries (Exiv2, ImageMagick, TagLib)

API overview, `minSdk`, build notes, and Kotlin examples: [docs/native-libraries.md](docs/native-libraries.md).
