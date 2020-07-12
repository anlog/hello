
add_executable(looper_test
        Looper_test.cpp Looper.cpp
        VectorImpl.cpp
        RefBase.cpp
        StrongPointer.cpp
        Timers.cpp
        String8.cpp
        String16.cpp
        Unicode.cpp
        SharedBuffer.cpp
        StopWatch.cpp)
target_link_libraries(looper_test gtest)
target_link_libraries(looper_test log)

add_executable(timers_test Timers_test.cpp Timers.cpp)
target_link_libraries(timers_test gtest)
target_link_libraries(timers_test log)

if (${TARGET_ABI} MATCHES ${ANDROID_ABI})
    add_custom_command(TARGET looper_test POST_BUILD COMMENT "POST_BUILD" VERBATIM
            COMMAND ${ADB} shell mkdir -p /data/local/tmp/${ANDROID_ABI}
            COMMAND ${ADB} push $<TARGET_FILE:looper_test> /data/local/tmp/${ANDROID_ABI}/
            COMMAND ${ADB} shell "chmod a+x /data/local/tmp/${ANDROID_ABI}/looper_test"
            COMMAND ${ADB} shell "LD_LIBRARY_PATH=/data/local/tmp/${ANDROID_ABI} /data/local/tmp/${ANDROID_ABI}/looper_test")
endif ()
