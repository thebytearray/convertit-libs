# Keep JNI and public API for consumers
-keep class org.thebytearray.image_magick_android.** { *; }
-keepclasseswithmembernames class * {
    native <methods>;
}
