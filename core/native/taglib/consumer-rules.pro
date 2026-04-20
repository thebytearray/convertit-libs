# Keep all classes in the taglib_android package that are used by JNI
# The native library creates instances of these classes and calls their methods
-keep class org.thebytearray.taglib_android.** { *; }

# Keep class names for JNI lookup
-keepnames class org.thebytearray.taglib_android.**

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}
