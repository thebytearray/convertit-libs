# Licences and third-party notices

This repository has two kinds of code, under different licences:

- **Our wrappers** (first-party): the Kotlin/Java API, the JNI glue code, Gradle scripts,
  `build-logic` and `scripts`. Licensed **LGPL-2.1-or-later**; see [`LICENSE`](LICENSE).
- **Upstream libraries** (third-party): Exiv2, TagLib, ImageMagick and the libraries they build
  with. Each stays under **its own upstream licence**, listed below. Our LGPL licence does not
  change or replace them.

Each published artifact is a combination of the two, so its terms are the wrapper's LGPL plus
every upstream licence it bundles. Keep these notices, and the upstream licence texts, in any
distribution of source or binaries.

This is an engineering summary, not legal advice.

## Summary by artifact

| Artifact | Our wrapper | Upstream code it bundles | Usable in a closed-source app? |
|---|---|---|---|
| `org.thebytearray.lib:taglib` | LGPL-2.1-or-later | TagLib (LGPL-2.1-only OR MPL-1.1), utfcpp (BSL-1.0) | Yes, under LGPL/MPL terms (see below) |
| `org.thebytearray.lib:image-magick` | LGPL-2.1-or-later | ImageMagick (ImageMagick License) plus the delegates below, including libheif and libde265 (LGPL-3.0-or-later) | Yes, under LGPL terms (see below) |
| `org.thebytearray.lib:exiv2` | LGPL-2.1-or-later | **Exiv2 (GPL-2.0-or-later)**, {fmt} (MIT) | **No.** Exiv2 is GPL, so the combined library is GPL-2.0-or-later |

## `taglib`

| Component | Location | Licence |
|---|---|---|
| Wrapper: `TagLib.kt`, `Metadata.kt`, `Picture.kt`, `PropertyMap.kt`, `AudioProperties*.kt`, `taglib_android.cpp`, `fileref_ext.*`, `utils.h`, `CMakeLists.txt` | `core/native/taglib/src/main/` | LGPL-2.1-or-later (ours) |
| [TagLib](https://github.com/taglib/taglib) 2.1.1 | `core/native/taglib/src/main/cpp/taglib/` | LGPL-2.1-only OR MPL-1.1, recipient's choice (`COPYING.LGPL`, `COPYING.MPL`) |
| [utfcpp](https://github.com/nemtrif/utfcpp) | `core/native/taglib/src/main/cpp/taglib/3rdparty/utfcpp/` | BSL-1.0 |

TagLib is compiled into `libtaglib_android.so` together with the wrapper. A closed-source app can
use it under either upstream option:

- **MPL-1.1:** make the TagLib source files available, including any changes to them.
- **LGPL-2.1:** also let users replace `libtaglib_android.so` with a modified build, and do not
  forbid reverse engineering for debugging those modifications.

## `image-magick`

| Component | Location | Licence |
|---|---|---|
| Wrapper: `ImageMagickRunner.kt`, `scripts/fetch-imagemagick-jnilibs.sh`, `scripts/fetch_imagemagick_jnilibs.py` | `core/native/image-magick/`, `scripts/` | LGPL-2.1-or-later (ours) |
| Configuration assets | `core/native/image-magick/src/main/assets/usr/` | ImageMagick License (from upstream ImageMagick) |
| ImageMagick runtime (`libmagick_bin.so` and the `.so` files beside it) | `core/native/image-magick/src/main/jniLibs/<abi>/`, fetched at build time from [Android-ImageMagick7 releases](https://github.com/codewithtamim/Android-ImageMagick7/releases) | See the table below |

The runtime is built by [Android-ImageMagick7](https://github.com/codewithtamim/Android-ImageMagick7).
Its source, including the build scripts for the exact release, is the corresponding source for
these binaries. It bundles:

| Library | Licence |
|---|---|
| [ImageMagick](https://imagemagick.org/) 7.1.2 | ImageMagick License |
| [libheif](https://github.com/strukturag/libheif) 1.23.6 | LGPL-3.0-or-later |
| [libde265](https://github.com/strukturag/libde265) 1.0.15 | LGPL-3.0-or-later |
| [libaom](https://aomedia.googlesource.com/aom) | BSD-2-Clause + Alliance for Open Media Patent License 1.0 |
| libjpeg-turbo 2.0.2 | IJG + BSD-3-Clause + Zlib |
| libpng 1.6.37 | libpng License |
| libtiff 4.0.10 | libtiff License |
| FreeType 2.10.1 | FreeType License (FTL), chosen from FTL OR GPL-2.0-or-later |
| libwebp 1.0.3 | BSD-3-Clause |
| OpenJPEG 2.3.1 | BSD-2-Clause |
| libxml2 2.9.9 | MIT |
| libiconv 1.16 (library) | LGPL-2.1-or-later |
| ICU 64.2 | ICU License |
| Little CMS 2.9 | MIT |
| xz / liblzma 5.2.4 | Public domain |
| bzip2 1.0.8 | bzip2 License |
| zlib (Android system library) | Zlib |
| LLVM libc++ and OpenMP runtime (`libc++_shared.so`, `libomp.so`) | Apache-2.0 WITH LLVM-exception |

FFTW (GPL-2.0-or-later) is **not** built into Android-ImageMagick7 releases after commit
`d62ef590`, so `-fft` and `-ift` are unavailable. Releases built before that commit include FFTW
and are GPL.

The LGPL-3.0 libraries (libheif, libde265) and libiconv are linked into the ImageMagick program.
For a closed-source app, the LGPL terms mean: make the Android-ImageMagick7 source and build
scripts for the shipped release available, let users replace the ImageMagick binaries with a
modified build, and do not forbid reverse engineering for debugging those modifications.

## `exiv2`

| Component | Location | Licence |
|---|---|---|
| Wrapper: `Exiv2Android.kt`, `exiv2_android.cpp`, `CMakeLists.txt` | `core/native/exiv2/src/main/` | LGPL-2.1-or-later (ours) |
| [Exiv2](https://github.com/Exiv2/exiv2) | `core/native/exiv2/src/main/cpp/exiv2/` | **GPL-2.0-or-later** |
| [{fmt}](https://github.com/fmtlib/fmt) 10.2.1 | Fetched at CMake configure time | MIT |

Because Exiv2 is GPL-2.0-or-later and is compiled into the same `.so` as the wrapper, the
`exiv2` artifact as a whole is distributed under **GPL-2.0-or-later**. An app that includes it
must be GPL-compatible. Exiv2 offers a separate commercial licence for other uses.
