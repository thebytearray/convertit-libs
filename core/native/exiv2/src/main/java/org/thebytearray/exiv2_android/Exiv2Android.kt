package org.thebytearray.exiv2_android

object Exiv2Android {

    init {
        System.loadLibrary("exiv2_android")
    }

    external fun removeExif(inputPath: String, outputPath: String): Boolean
}
