Place ImageMagick Android binaries here (required at runtime for ImageMagickRunner):

  arm64-v8a/libmagick_bin.so
  armeabi-v7a/libmagick_bin.so
  x86_64/libmagick_bin.so

`libmagick_bin.so` is the upstream `magick` executable renamed (see script below). Copy any
additional .so dependencies next to it in the same ABI folder (from the same `shared/` extract).

Populate from the **latest** [Android-ImageMagick7](https://github.com/codewithtamim/Android-ImageMagick7) GitHub release:

  ./scripts/fetch-imagemagick-jnilibs.sh

The AAR packages jniLibs for Maven Central consumers; `usr/` under assets is project config only and is not touched by that script.

These `*.so` files are not committed to git (see repo `.gitignore`). The Maven Central publish workflow
runs the fetch script before `./gradlew publishAllNativeLibrariesToMavenCentral` so the published AAR
contains the same binaries a local `fetch` + build would produce.
