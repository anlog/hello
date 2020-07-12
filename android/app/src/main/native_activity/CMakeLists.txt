
# build native_app_glue as a static lib
set(${CMAKE_C_FLAGS}, "${CMAKE_C_FLAGS}")

set(native_app_glue_ROOT ${ANDROID_NDK}/sources/android/native_app_glue)
add_library(native_app_glue STATIC
        ${native_app_glue_ROOT}/android_native_app_glue.c)
target_include_directories(native_app_glue PRIVATE ${native_app_glue_ROOT})
target_include_directories(native_app_glue PUBLIC ${native_app_glue_ROOT})

# now build app's shared lib
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11 -Wall -Werror")

# Export ANativeActivity_onCreate(),
# Refer to: https://github.com/android-ndk/ndk/issues/381.
set(CMAKE_SHARED_LINKER_FLAGS
        "${CMAKE_SHARED_LINKER_FLAGS} -u ANativeActivity_onCreate")

add_library(native_activity SHARED natie_activity.cc)

# add lib dependencies
target_link_libraries(native_activity
        android
        native_app_glue
        EGL
        GLESv1_CM
        log)