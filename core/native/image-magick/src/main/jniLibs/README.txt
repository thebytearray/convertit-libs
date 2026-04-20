Place prebuilt ImageMagick binaries here (required at runtime for ImageMagickRunner):

  arm64-v8a/libmagick_bin.so
  armeabi-v7a/libmagick_bin.so
  x86_64/libmagick_bin.so

Copy any additional .so dependencies next to libmagick_bin.so in the same ABI folder.
The AAR packages these into jniLibs for consumers (including JitPack).
