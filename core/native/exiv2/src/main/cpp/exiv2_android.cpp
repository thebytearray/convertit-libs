#include <jni.h>
#include <string>
#include <fstream>
#include <vector>
#include <android/log.h>
#include <exiv2/exiv2.hpp>

#define LOG_TAG "Exiv2Android"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jboolean JNICALL
Java_org_thebytearray_exiv2_1android_Exiv2Android_removeExif(
        JNIEnv *env,
        jobject,
        jstring inputPath,
        jstring outputPath) {

    const char *input = env->GetStringUTFChars(inputPath, nullptr);
    const char *output = env->GetStringUTFChars(outputPath, nullptr);

    LOGI("removeExif called");
    LOGD("Input: %s", input);
    LOGD("Output: %s", output);

    jboolean result = JNI_FALSE;

    try {
        std::string inputStr(input);
        std::string outputStr(output);

        if (inputStr == outputStr) {
            LOGD("Processing in-place");
            auto image = Exiv2::ImageFactory::open(inputStr);
            image->readMetadata();

            LOGD("Clearing EXIF data");
            image->clearExifData();
            LOGD("Clearing IPTC data");
            image->clearIptcData();
            LOGD("Clearing XMP data");
            image->clearXmpData();
            LOGD("Clearing comment");
            image->clearComment();

            LOGD("Writing metadata");
            image->writeMetadata();
        } else {
            LOGD("Processing to separate output file");
            std::ifstream inFile(inputStr, std::ios::binary);
            if (!inFile.is_open()) {
                LOGE("Failed to open input file: %s", input);
                env->ReleaseStringUTFChars(inputPath, input);
                env->ReleaseStringUTFChars(outputPath, output);
                return JNI_FALSE;
            }

            std::vector<Exiv2::byte> buffer((std::istreambuf_iterator<char>(inFile)),
                                             std::istreambuf_iterator<char>());
            inFile.close();
            LOGD("Read %zu bytes from input file", buffer.size());

            auto image = Exiv2::ImageFactory::open(buffer.data(), buffer.size());
            image->readMetadata();

            LOGD("Clearing EXIF data");
            image->clearExifData();
            LOGD("Clearing IPTC data");
            image->clearIptcData();
            LOGD("Clearing XMP data");
            image->clearXmpData();
            LOGD("Clearing comment");
            image->clearComment();

            LOGD("Writing metadata");
            image->writeMetadata();

            auto& io = image->io();
            io.seek(0, Exiv2::BasicIo::beg);

            std::ofstream outFile(outputStr, std::ios::binary);
            if (!outFile.is_open()) {
                LOGE("Failed to open output file: %s", output);
                env->ReleaseStringUTFChars(inputPath, input);
                env->ReleaseStringUTFChars(outputPath, output);
                return JNI_FALSE;
            }

            std::vector<Exiv2::byte> outBuffer(io.size());
            io.read(outBuffer.data(), outBuffer.size());
            outFile.write(reinterpret_cast<const char*>(outBuffer.data()), outBuffer.size());
            outFile.close();
            LOGD("Wrote %zu bytes to output file", outBuffer.size());
        }

        LOGI("Successfully removed metadata");
        result = JNI_TRUE;
    } catch (Exiv2::Error &e) {
        LOGE("Exiv2 error: %s", e.what());
        result = JNI_FALSE;
    } catch (std::exception &e) {
        LOGE("Standard exception: %s", e.what());
        result = JNI_FALSE;
    } catch (...) {
        LOGE("Unknown exception occurred");
        result = JNI_FALSE;
    }

    env->ReleaseStringUTFChars(inputPath, input);
    env->ReleaseStringUTFChars(outputPath, output);

    return result;
}
