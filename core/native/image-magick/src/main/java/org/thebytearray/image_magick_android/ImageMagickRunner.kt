package org.thebytearray.image_magick_android

import android.content.Context
import android.content.res.AssetManager
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread
import kotlin.math.exp

data class ImageMagickResult(
    val exitCode: Int,
    val stdout: String,
    val stderr: String,
)

class ImageMagickRunner(
    private val context: Context,
) {
    private val magickHome = File(context.filesDir, "usr")
    private val tmpDir = File(context.filesDir, "tmp")

    fun ensureInstalled() {
        val configDir = File(magickHome, "etc/ImageMagick-7")
        if (!configDir.exists()) {
            copyAssetPath("usr")
        }
        tmpDir.mkdirs()
    }

    /**
     * Runs ImageMagick on [inputFile] and writes [outputFile].
     * [onMagickProgress] is invoked on a background thread with values in (0,1) while the process
     * is running (asymptotic heartbeat so long HEIC/AVIF runs show movement). Stdout/stderr are
     * drained on worker threads so pipes cannot fill and stall the process.
     */
    fun convertImage(
        inputFile: File,
        outputFile: File,
        quality: Int = 85,
        onMagickProgress: ((Float) -> Unit)? = null,
    ): ImageMagickResult {
        ensureInstalled()
        val magickBinary = getMagickBinary()

        val command = mutableListOf(
            magickBinary.absolutePath,
            inputFile.absolutePath,
            "-quality", quality.coerceIn(1, 100).toString(),
            outputFile.absolutePath,
        )

        val process = ProcessBuilder(command).apply {
            directory(context.filesDir)
            environment().putAll(buildEnvironment())
        }.start()

        var stdout = ""
        var stderr = ""
        val outThread = thread(name = "imagemagick-stdout") {
            try {
                stdout = process.inputStream.bufferedReader().readText()
            } catch (_: Exception) {
            }
        }
        val errThread = thread(name = "imagemagick-stderr") {
            try {
                stderr = process.errorStream.bufferedReader().readText()
            } catch (_: Exception) {
            }
        }

        val heartbeat = if (onMagickProgress != null) {
            thread(name = "imagemagick-progress", isDaemon = true) {
                var step = 0
                while (!Thread.currentThread().isInterrupted && process.isRunningCompat()) {
                    try {
                        Thread.sleep(320L)
                    } catch (_: InterruptedException) {
                        break
                    }
                    step++
                    val phase = ((1.0 - exp(-step / 7.0)) * 0.94).toFloat()
                    onMagickProgress(phase.coerceIn(0.02f, 0.94f))
                }
            }
        } else {
            null
        }

        val exitCode = process.waitFor()
        heartbeat?.interrupt()
        heartbeat?.join(200L)
        outThread.join(120_000L)
        errThread.join(120_000L)

        if (exitCode != 0 && stderr.isNotBlank()) {
            android.util.Log.w("ImageMagick", stderr.take(4000))
        }

        return ImageMagickResult(exitCode, stdout, stderr)
    }


    private fun Process.isRunningCompat(): Boolean =
        try {
            exitValue()
            false
        } catch (_: IllegalThreadStateException) {
            true
        }

    private fun buildEnvironment(): Map<String, String> {
        val env = mutableMapOf<String, String>()
        env["MAGICK_HOME"] = magickHome.absolutePath
        env["MAGICK_CONFIGURE_PATH"] = File(magickHome, "etc/ImageMagick-7").absolutePath
        env["ICU_DATA_DIR_PREFIX"] = magickHome.absolutePath
        env["TMPDIR"] = tmpDir.absolutePath
        env["LD_LIBRARY_PATH"] = context.applicationInfo.nativeLibraryDir
        return env
    }

    private fun getMagickBinary(): File {
        return File(context.applicationInfo.nativeLibraryDir, "libmagick_bin.so")
    }

    private fun copyAssetPath(assetPath: String) {
        val assetManager = context.assets
        val assets = assetManager.list(assetPath) ?: return
        if (assets.isEmpty()) {
            copyAssetFile(assetManager, assetPath)
            return
        }
        val dir = File(context.filesDir, assetPath)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        assets.forEach { child ->
            copyAssetPath("$assetPath/$child")
        }
    }

    private fun copyAssetFile(assetManager: AssetManager, assetPath: String) {
        val outFile = File(context.filesDir, assetPath)
        if (outFile.exists()) {
            return
        }
        outFile.parentFile?.mkdirs()
        assetManager.open(assetPath).use { input ->
            FileOutputStream(outFile).use { output ->
                input.copyTo(output)
            }
        }
    }
}
