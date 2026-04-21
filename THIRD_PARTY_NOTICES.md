# Third-party notices

Original code in this repository (Gradle scripts, Kotlin/Java wrappers, build-logic, and other first-party files not listed below) is licensed under GPL-3.0; see `LICENSE`.

This repository also contains Android library modules that bundle or build third-party native and Java/Kotlin code. Retain these notices in distributions of source or binaries. This is not legal advice; consult counsel for license compliance (including GPL/LGPL implications for proprietary apps).

| Component | Location / use | SPDX / license (see upstream for full text) |
|-----------|----------------|---------------------------------------------|
| **Exiv2** | `core/native/exiv2/src/main/cpp/exiv2/` | GPL-2.0-or-later |
| **{fmt}** | Fetched at CMake configure time (see `CMakeLists.txt`) | MIT |
| **TagLib** | `core/native/taglib/src/main/cpp/taglib/` | LGPL-2.1-only OR MPL-1.1 (dual-licensed; see upstream) |
| **ImageMagick (runtime)** | `libmagick_bin.so` (upstream `magick` renamed) and dependent `.so` under `image-magick/src/main/jniLibs/<abi>/` from the latest [Android-ImageMagick7](https://github.com/codewithtamim/Android-ImageMagick7) release via [`scripts/fetch-imagemagick-jnilibs.sh`](scripts/fetch-imagemagick-jnilibs.sh); configuration assets under `core/native/image-magick/src/main/assets/usr/` are maintained in-tree | ImageMagick License (see upstream) |
| **Zlib, libjpeg, libpng, etc.** | Pulled in by Exiv2/ImageMagick/NDK as applicable | See respective `LICENSE` / COPYING files in upstream source trees or the NDK |

Upstream projects:

- https://github.com/Exiv2/exiv2  
- https://github.com/taglib/taglib  
- https://github.com/fmtlib/fmt  
- https://imagemagick.org/  
- https://github.com/codewithtamim/Android-ImageMagick7 (shared Android zips for jniLibs)  
