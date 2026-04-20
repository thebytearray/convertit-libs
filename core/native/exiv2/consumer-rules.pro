# Keep all classes in the exiv2_android package that are used by JNI
# The native library may create instances of these classes and calls their methods
-keep class org.thebytearray.exiv2_android.** { *; }

# Keep class names for JNI lookup
-keepnames class org.thebytearray.exiv2_android.**

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}
