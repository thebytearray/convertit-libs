cmake_minimum_required(VERSION 3.22.1)

project("exiv2_android")

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} -Os -ffunction-sections -fdata-sections -fvisibility=hidden -fvisibility-inlines-hidden")
set(CMAKE_C_FLAGS_RELEASE "${CMAKE_C_FLAGS_RELEASE} -Os -ffunction-sections -fdata-sections -fvisibility=hidden")
# 16 KiB max page alignment for all ABIs (required for flexible-page-size devices; arm32 otherwise stays 4 KiB).
set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -Wl,--gc-sections -Wl,--strip-all -Wl,-z,max-page-size=16384")

include(FetchContent)
FetchContent_Declare(
    fmt
    GIT_REPOSITORY https://github.com/fmtlib/fmt.git
    GIT_TAG 10.2.1
)
set(FMT_INSTALL OFF CACHE BOOL "" FORCE)
FetchContent_MakeAvailable(fmt)

list(APPEND CMAKE_MODULE_PATH ${CMAKE_CURRENT_SOURCE_DIR}/cmake)

# ============================================================
# EXIV2 BUILD CONFIGURATION - Optimized for metadata removal only
# ============================================================
# Build as static library to link into our shared lib
set(BUILD_SHARED_LIBS OFF CACHE BOOL "" FORCE)

# DISABLE XMP support - saves significant space
set(EXIV2_ENABLE_XMP OFF CACHE BOOL "" FORCE)
set(EXIV2_ENABLE_EXTERNAL_XMP OFF CACHE BOOL "" FORCE)

# ENABLE PNG support - users may have PNG images with metadata
set(EXIV2_ENABLE_PNG ON CACHE BOOL "" FORCE)

# DISABLE translations
set(EXIV2_ENABLE_NLS OFF CACHE BOOL "" FORCE)

# DISABLE Nikon lens database - saves ~2-3MB of lens data tables!
set(EXIV2_ENABLE_LENSDATA OFF CACHE BOOL "" FORCE)

set(EXIV2_ENABLE_DYNAMIC_RUNTIME ON CACHE BOOL "" FORCE)

# DISABLE web/network features
set(EXIV2_ENABLE_WEBREADY OFF CACHE BOOL "" FORCE)
set(EXIV2_ENABLE_CURL OFF CACHE BOOL "" FORCE)

# DISABLE BMFF (HEIF/HEIC) - saves space, most users have JPEG
set(EXIV2_ENABLE_BMFF OFF CACHE BOOL "" FORCE)
set(EXIV2_ENABLE_BROTLI OFF CACHE BOOL "" FORCE)

# DISABLE video support
set(EXIV2_ENABLE_VIDEO OFF CACHE BOOL "" FORCE)

# DISABLE inih config parser
set(EXIV2_ENABLE_INIH OFF CACHE BOOL "" FORCE)

# Keep filesystem access for file operations
set(EXIV2_ENABLE_FILESYSTEM_ACCESS ON CACHE BOOL "" FORCE)

# DISABLE all build tools
set(EXIV2_BUILD_SAMPLES OFF CACHE BOOL "" FORCE)
set(EXIV2_BUILD_EXIV2_COMMAND OFF CACHE BOOL "" FORCE)
set(EXIV2_BUILD_UNIT_TESTS OFF CACHE BOOL "" FORCE)
set(EXIV2_BUILD_FUZZ_TESTS OFF CACHE BOOL "" FORCE)
set(EXIV2_BUILD_DOC OFF CACHE BOOL "" FORCE)

# DISABLE developer features
set(EXIV2_TEAM_EXTRA_WARNINGS OFF CACHE BOOL "" FORCE)
set(EXIV2_TEAM_WARNINGS_AS_ERRORS OFF CACHE BOOL "" FORCE)
set(EXIV2_TEAM_USE_SANITIZERS OFF CACHE BOOL "" FORCE)
set(EXIV2_TEAM_OSS_FUZZ OFF CACHE BOOL "" FORCE)
set(EXIV2_TEAM_PACKAGING OFF CACHE BOOL "" FORCE)

# DISABLE debug/security features for smaller size
set(BUILD_WITH_STACK_PROTECTOR OFF CACHE BOOL "" FORCE)
set(BUILD_WITH_CCACHE OFF CACHE BOOL "" FORCE)
set(BUILD_WITH_COVERAGE OFF CACHE BOOL "" FORCE)
set(BUILD_TESTING OFF CACHE BOOL "" FORCE)

macro(install)
endmacro()

add_subdirectory(exiv2)

macro(install)
    _install(${ARGV})
endmacro()

add_library(${CMAKE_PROJECT_NAME} SHARED
    exiv2_android.cpp)

target_include_directories(${CMAKE_PROJECT_NAME} PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}/exiv2/include
    ${CMAKE_CURRENT_SOURCE_DIR}/exiv2/include/exiv2
    ${CMAKE_CURRENT_BINARY_DIR}
)

target_link_libraries(${CMAKE_PROJECT_NAME}
    android
    log
    exiv2lib)
