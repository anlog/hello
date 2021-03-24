cmake_minimum_required(VERSION 3.4.1)

add_library( # Sets the name of the library.
        native-lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        native-lib.cpp)

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

target_link_libraries( # Specifies the target library.
        native-lib

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})

add_executable(ip-up-vpn
        ip-up-vpn.c)
target_link_libraries(ip-up-vpn
        ${log-lib})

message("ANDROID_SDK_ROOT: ${ANDROID_SDK_ROOT}") # ANDROID_SDK_ROOT should be passed as variable to this script.
find_program(ADB NAMES adb PATHS ${ANDROID_SDK_ROOT}/platform-tools)

execute_process(COMMAND adb shell getprop "ro.product.cpu.abi" OUTPUT_VARIABLE TARGET_ABI)
message("TARGET_ABI: ${TARGET_ABI}")

add_library(foo
        SHARED
        foo.cpp foo_jni.cpp)
target_link_libraries(foo
        ${log-lib})

# add gtest
set(GOOGLETEST_ROOT ${ANDROID_NDK}/sources/third_party/googletest)
add_library(gtest STATIC ${GOOGLETEST_ROOT}/src/gtest_main.cc ${GOOGLETEST_ROOT}/src/gtest-all.cc)
target_include_directories(gtest PRIVATE ${GOOGLETEST_ROOT})
target_include_directories(gtest PUBLIC ${GOOGLETEST_ROOT}/include)

add_executable(footest test/foo_unittest.cpp foo.cpp)
target_link_libraries(footest gtest)

#find_program(ADB adb)
if (${TARGET_ABI} MATCHES ${ANDROID_ABI})
    add_custom_command(TARGET footest POST_BUILD COMMENT "POST_BUILD" VERBATIM
            COMMAND ${ADB} shell mkdir -p /data/local/tmp/${ANDROID_ABI}
            COMMAND ${ADB} push $<TARGET_FILE:native-lib> /data/local/tmp/${ANDROID_ABI}/
            COMMAND ${ADB} push $<TARGET_FILE:ip-up-vpn> /data/local/tmp/${ANDROID_ABI}/
            COMMAND ${ADB} push $<TARGET_FILE:footest> /data/local/tmp/${ANDROID_ABI}/
            COMMAND ${ADB} shell "chmod a+x /data/local/tmp/${ANDROID_ABI}/footest"
            COMMAND ${ADB} shell "LD_LIBRARY_PATH=/data/local/tmp/${ANDROID_ABI} /data/local/tmp/${ANDROID_ABI}/footest")
    #        COMMAND ${ADB} shell "LD_LIBRARY_PATH=/data/local/tmp/${ANDROID_ABI} chmod a+x /data/local/tmp/${ANDROID_ABI}/footest /data/local/tmp/${ANDROID_ABI}/footest")
endif ()
# go_jni test

## this is for jni indexing(with no SHARED)
add_library(go_jni go_jni/go_jni.c)

string(TOLOWER ${CMAKE_HOST_SYSTEM_NAME} OS)
string(TOLOWER ${CMAKE_HOST_SYSTEM_PROCESSOR} ARCH)

## custom go build for go_jni
add_custom_target(libgo_jni_${VERSION}.so WORKING_DIRECTORY "${CMAKE_CURRENT_SOURCE_DIR}/go_jni" COMMENT "Building go_jni" VERBATIM COMMAND
        ${ANDROID_NDK}/prebuilt/${OS}-${ARCH}/bin/make
        ANDROID_ARCH_NAME=${ANDROID_ARCH_NAME}
        ANDROID_C_COMPILER=${ANDROID_C_COMPILER}
        ANDROID_TOOLCHAIN_ROOT=${ANDROID_TOOLCHAIN_ROOT}
        ANDROID_LLVM_TRIPLE=${ANDROID_LLVM_TRIPLE}
        ANDROID_SYSROOT=${ANDROID_SYSROOT}
        ANDROID_PACKAGE_NAME=${ANDROID_PACKAGE_NAME}
        VERSION=${VERSION}
        GRADLE_USER_HOME=${GRADLE_USER_HOME}
        CFLAGS=${CMAKE_C_FLAGS}\ -Wno-unused-command-line-argument
        LDFLAGS=${CMAKE_SHARED_LINKER_FLAGS}\ #-fuse-ld=gold
        DESTDIR=${CMAKE_LIBRARY_OUTPUT_DIRECTORY}
        BUILDDIR=${CMAKE_LIBRARY_OUTPUT_DIRECTORY}/../generated-src
        )

# hack here, no dependencies if fact
add_dependencies(footest libgo_jni_${VERSION}.so)

## sub projects

set(native_activity_build ../native_activity/build)
file(MAKE_DIRECTORY ${native_activity_build})
add_subdirectory(../native_activity ${native_activity_build})

set(aosp_build ../aosp/build)
file(MAKE_DIRECTORY ${aosp_build})
add_subdirectory(../aosp ${aosp_build})

#set(aosp_looper_build ../looper/build)
#file(MAKE_DIRECTORY ${aosp_looper_build})
#add_subdirectory(../looper ${aosp_looper_build})
