# Native libraries: Exiv2, ImageMagick, and TagLib

This document describes how **convertit-libs** packages [Exiv2](https://www.exiv2.org/), [ImageMagick](https://imagemagick.org/), and [TagLib](https://taglib.org/) for Android, what each module is responsible for, and how to call them from Kotlin with working examples.

Published artifacts use the Maven group **`org.thebytearray.lib`**. The artifact names are **`exiv2`**, **`image-magick`**, and **`taglib`**. Replace `VERSION` with a release from [search.maven.org for `org.thebytearray.lib`](https://search.maven.org/search?q=org.thebytearray.lib).

```kotlin
dependencies {
    implementation("org.thebytearray.lib:exiv2:VERSION")
    implementation("org.thebytearray.lib:image-magick:VERSION")
    implementation("org.thebytearray.lib:taglib:VERSION")
}
```

Licensing and third-party credits for the whole repository are in the top-level [LICENSE](../LICENSE) and [THIRD_PARTY_NOTICES.md](../THIRD_PARTY_NOTICES.md) files.

---

## Exiv2 (`:core:native:exiv2`)

**Purpose.** Exiv2 reads and writes image metadata (Exif, IPTC, embedded comments, and related structures). In this project it is built as a static library, linked into a small JNI shared object **`libexiv2_android.so`**, and exposed to Kotlin as **`Exiv2Android`**.

**Supported Android API level.** `minSdk` **24** (see `core/native/exiv2/build.gradle.kts`). ABIs: **arm64-v8a**, **armeabi-v7a**, **x86_64** (see the same file).

**Build profile.** The CMake configuration under `core/native/exiv2/src/main/cpp/CMakeLists.txt` is tuned for a compact native footprint: for example, PNG is enabled, while XMP as a feature, BMFF, video, web-ready networking, the `exiv2` CLI, samples, and unit tests are not built. Details live in that file if you need to align behavior with full desktop Exiv2.

**Public API (Kotlin).** The JNI entry point is a single function that strips metadata and writes the result.

| Call | Behavior |
|------|----------|
| `Exiv2Android.removeExif(inputPath, outputPath)` | If `inputPath` and `outputPath` are the same, metadata is cleared **in place**. Otherwise the image is written to `outputPath` with metadata cleared. Returns `true` on success, `false` on error. |

The native implementation clears Exif, IPTC, XMP, and the image comment (see `exiv2_android.cpp`).

**Example: in-place metadata removal**

```kotlin
import org.thebytearray.exiv2_android.Exiv2Android
import java.io.File

fun stripMetadataInPlace(photo: File): Boolean {
    if (!photo.isFile) return false
    return Exiv2Android.removeExif(photo.absolutePath, photo.absolutePath)
}
```

**Example: copy to a new file without metadata**

```kotlin
import org.thebytearray.exiv2_android.Exiv2Android
import java.io.File

fun exportWithoutMetadata(source: File, dest: File): Boolean {
    return Exiv2Android.removeExif(source.absolutePath, dest.absolutePath)
}
```

**Operational notes**

- Paths must be accessible to the native layer (typically app storage or other paths on the local filesystem, not raw `content://` URIs without copying to a temp file first).
- Failures return `false`; check return values in production code. For debugging, the native code logs to the `Exiv2Android` tag.

**Further reading (upstream).** [Exiv2](https://www.exiv2.org/) and the [Exiv2 GitHub](https://github.com/Exiv2/exiv2) repository.

---

## ImageMagick (`:core:native:image-magick`)

**Purpose.** The ImageMagick **7** `magick` tool is used for image conversion and processing. This module ships a Kotlin helper **`ImageMagickRunner`**, **configuration assets** under `assets/usr/` (for example `etc/ImageMagick-7`), and expects **prebuilt** native executables delivered as `jniLibs` (see below).

**Supported Android API level.** `minSdk` **24** (`core/native/image-magick/build.gradle.kts`).

**How it works at runtime**

1. **`ImageMagickRunner.ensureInstalled()`** (also called from **`convertImage`**) copies the `usr` tree from app assets into `context.filesDir/usr` the first time it is needed.
2. The **`magick`** program is not a separate file on disk: the published binary is expected as **`libmagick_bin.so`** in the per-ABI `jniLibs` directory (it is the upstream `magick` binary renamed, loaded via `File(nativeLibraryDir, "libmagick_bin.so")`).
3. **Environment** is set in **`ImageMagickRunner.buildEnvironment()`**: `MAGICK_HOME`, `MAGICK_CONFIGURE_PATH`, `TMPDIR`, `LD_LIBRARY_PATH` (so dependent `.so` files next to the binary are found), and related variables.

**Public API (Kotlin).**

- **`ImageMagickRunner(context)`** holds paths derived from the application `Context`.
- **`convertImage(inputFile, outputFile, quality, onMagickProgress?)`** runs `libmagick_bin.so` with arguments: `input`, `-quality <1..100>`, `output`. It returns **`ImageMagickResult(exitCode, stdout, stderr)`**. Progress is an optional asymptotic callback on a background thread while the process is running; it does not parse ImageMagick’s own progress, but keeps long jobs from looking stuck.
- On non-zero exit, a truncated `stderr` line may be written to the Android log with the **`ImageMagick`** tag.

**Example: simple JPEG export with default quality 85**

```kotlin
import org.thebytearray.image_magick_android.ImageMagickRunner
import java.io.File

fun convertToJpeg(context: android.content.Context, input: File, output: File): Boolean {
    val runner = ImageMagickRunner(context)
    val result = runner.convertImage(
        inputFile = input,
        outputFile = output,
        quality = 85,
    )
    return result.exitCode == 0
}
```

**Example: progress hook**

```kotlin
import org.thebytearray.image_magick_android.ImageMagickRunner

fun convertWithProgress(context: android.content.Context, input: File, output: File) {
    val runner = ImageMagickRunner(context)
    val result = runner.convertImage(input, output, quality = 90) { phase /* 0f..0.94f */ ->
        // Update UI on main thread with runOnUiThread or a coroutine
    }
    if (result.exitCode != 0) {
        // Use result.stderr or logcat
    }
}
```

**Build and release notes for maintainers**

- The **`libmagick_bin.so`** files and their dependency `.so` files are **not** committed to git (see repository `.gitignore`). Place them per ABI as described in `core/native/image-magick/src/main/jniLibs/README.txt`.
- The project provides **`scripts/fetch-imagemagick-jnilibs.sh`** to download compatible binaries (see the script and README for the **Android-ImageMagick7** source). The Maven Central publish flow runs this before publishing so the AAR contains the same layout a local build would.

**Further reading (upstream).** [ImageMagick](https://imagemagick.org/) and the [ImageMagick usage documentation](https://imagemagick.org/script/command-line-processing.php).

---

## TagLib (`:core:native:taglib`)

**Purpose.** [TagLib](https://taglib.org/) reads and writes audio metadata and embedded cover art. The project links TagLib as **`tag`**, provides **`TagLibExt::FileRef`** (`fileref_ext.cpp`) for stream-based file access, and exposes **`libtaglib_android.so`** to Kotlin as **`org.thebytearray.taglib_android.TagLib`**.

**Supported Android API level.** `minSdk` **21** (`core/native/taglib/build.gradle.kts`).

**Public API (Kotlin, summary).** All entry points use a **POSIX file descriptor** (`Int`) so you can work with `ParcelFileDescriptor` from a `content://` URI, a temp file, or any API that exposes an `fd`. Extension-based format detection (and content sniffing) matches the C++ `fileref_ext` types: MP3, Ogg, FLAC, Opus, MP4 family, WMA, WAV, AIFF, WavPack, APE, DSF, and DSDIFF, among others (see `fileref_ext.cpp` for the authoritative list).

| Method | Use |
|--------|-----|
| `getAudioProperties(fd, readStyle)` | Bitrate, length, sample rate, channel count, with `AudioPropertiesReadStyle` **Fast** / **Average** / **Accurate**. |
| `getMetadata(fd, readPictures)` | Full `Metadata`: `PropertyMap` plus an array of `Picture` if requested. |
| `getMetadataPropertyValues(fd, propertyName)` | All string values for one property key. |
| `getPictures(fd)` / `getFrontCover(fd)` | Embedded images. |
| `savePropertyMap(fd, propertyMap)` | Writes properties; requires a writable `fd` from native code. |
| `savePictures(fd, pictures)` | Writes **PICTURE** complex property list. |

**`PropertyMap`** is a `HashMap<String, Array<String>>` (see `PropertyMap.kt`).

**Example: read title and album from a local file (read-only `fd`)**

```kotlin
import org.thebytearray.taglib_android.TagLib
import java.io.File
import java.io.FileInputStream

fun readTitleAndAlbum(musicFile: File): Pair<String?, String?> {
    FileInputStream(musicFile).use { input ->
        val fd = input.fd
        val meta = TagLib.getMetadata(fd, readPictures = false) ?: return null to null
        val map = meta.propertyMap
        val title = map["TITLE"]?.firstOrNull()
        val album = map["ALBUM"]?.firstOrNull()
        return title to album
    }
}
```

**Example: `content://` URI (open for read)**

```kotlin
import org.thebytearray.taglib_android.TagLib
import androidx.core.net.toUri

fun readPropertiesFromContentUri(
    contentResolver: android.content.ContentResolver,
    uri: android.net.Uri,
): org.thebytearray.taglib_android.Metadata? {
    contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
        return TagLib.getMetadata(pfd.fd, readPictures = true)
    }
    return null
}
```

**Example: save tags (requires write access)**

```kotlin
import org.thebytearray.taglib_android.TagLib
import org.thebytearray.taglib_android.PropertyMap
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile

fun writeTagsInPlace(musicFile: File): Boolean {
    // RandomAccessFile gives a read-write fd
    RandomAccessFile(musicFile, "rw").use { raf ->
        val map = PropertyMap()
        map["ARTIST"] = arrayOf("Example Artist")
        map["TITLE"] = arrayOf("Example Title")
        return TagLib.savePropertyMap(raf.fd, map)
    }
}
```

**Operational notes**

- The native layer resolves a path from the `fd` for format detection, so the underlying file name should have a **correct extension** when the format is ambiguous.
- `getMetadataPropertyValues` must match an existing key in the file’s `PropertyMap`; if you need defensive behavior, call `getMetadata` first and inspect keys, or use `getMetadata` and read from the returned `propertyMap`.
- `savePropertyMap` and `savePictures` use **writable** `TagLib::FileStream` mode in native code; if save fails, confirm the `fd` is open for write and the container format supports tag updates.

**Further reading (upstream).** [TagLib](https://taglib.org/) and the [TagLib API documentation](https://taglib.org/api/).

---

## Publishing tasks (reference)

From the root `build.gradle.kts`, you can publish all three native AARs to Maven Local, or to Sonatype for Maven Central, using the registered Gradle tasks **`publishAllNativeLibrariesToMavenLocal`** and **`publishAllNativeLibrariesToMavenCentral`**. ImageMagick in particular may require the fetch script to populate `jniLibs` before a release build will package the expected binaries (see `core/native/image-magick/src/main/jniLibs/README.txt`).
