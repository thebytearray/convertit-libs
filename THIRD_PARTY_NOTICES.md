# Third-party notices

Original code in this repository (Gradle scripts, Kotlin/Java wrappers, build-logic, and other first-party files not listed below) is licensed under GPL-3.0; see `LICENSE`.

This repository also contains Android library modules that bundle or build third-party native and Java/Kotlin code. Retain these notices in distributions of source or binaries. This is not legal advice; consult counsel for license compliance (including GPL/LGPL implications for proprietary apps).

| Component | Location / use | SPDX / license (see upstream for full text) |
|-----------|----------------|---------------------------------------------|
| **Exiv2** | `core/native/exiv2/src/main/cpp/exiv2/` | GPL-2.0-or-later |
| **{fmt}** | Fetched at CMake configure time (see `CMakeLists.txt`) | MIT |
| **TagLib** | `core/native/taglib/src/main/cpp/taglib/` | LGPL-2.1-only OR MPL-1.1 (dual-licensed; see upstream) |
| **ImageMagick** | Runtime binary `libmagick_bin.so` (you supply prebuilts under `jniLibs/`); config assets under `core/native/image-magick/src/main/assets/` | ImageMagick License (Apache-style; see https://imagemagick.org/script/license.php ) |
| **FFmpeg / FFmpeg Kit** | Prebuilt `.so` under `core/native/ffmpeg-kit/src/main/jniLibs/` (you supply); Java API typically `com.arthenica:ffmpeg-kit-*` from Maven (see convertit-pro) | FFmpeg: LGPL/GPL components per build; FFmpeg Kit and https://ffmpeg.org/legal.html |
| **Zlib, libjpeg, libpng, etc.** | Pulled in by Exiv2/ImageMagick/NDK as applicable | See respective `LICENSE` / COPYING files under vendored trees or NDK |

Upstream projects:

- https://github.com/Exiv2/exiv2  
- https://github.com/taglib/taglib  
- https://github.com/fmtlib/fmt  
- https://imagemagick.org/  
- https://github.com/arthenica/ffmpeg-kit  
- https://ffmpeg.org/  
