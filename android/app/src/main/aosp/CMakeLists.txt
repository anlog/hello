# add gtest
#set(GOOGLETEST_ROOT ${ANDROID_NDK}/sources/third_party/googletest)
#add_library(gtest STATIC ${GOOGLETEST_ROOT}/src/gtest_main.cc ${GOOGLETEST_ROOT}/src/gtest-all.cc)
#target_include_directories(gtest PRIVATE ${GOOGLETEST_ROOT})
#target_include_directories(gtest PUBLIC ${GOOGLETEST_ROOT}/include)

add_executable(ashmem_test ashmem_test.cpp ashmem-dev.cpp)
target_link_libraries(ashmem_test gtest)
target_link_libraries(ashmem_test ${log-lib})

add_executable(memfd_test memfd_test.c)

if (${TARGET_ABI} MATCHES ${ANDROID_ABI})
    add_custom_command(TARGET ashmem_test POST_BUILD COMMENT "POST_BUILD" VERBATIM
            COMMAND ${ADB} shell mkdir -p /data/local/tmp/${ANDROID_ABI}
            COMMAND ${ADB} push $<TARGET_FILE:memfd_test> /data/local/tmp/${ANDROID_ABI}/
            COMMAND ${ADB} shell "chmod a+x /data/local/tmp/${ANDROID_ABI}/memfd_test"
            COMMAND ${ADB} shell "LD_LIBRARY_PATH=/data/local/tmp/${ANDROID_ABI} /data/local/tmp/${ANDROID_ABI}/memfd_test")
endif ()


if (${TARGET_ABI} MATCHES ${ANDROID_ABI})
    add_custom_command(TARGET ashmem_test POST_BUILD COMMENT "POST_BUILD" VERBATIM
            COMMAND ${ADB} shell mkdir -p /data/local/tmp/${ANDROID_ABI}
            COMMAND ${ADB} push $<TARGET_FILE:ashmem_test> /data/local/tmp/${ANDROID_ABI}/
            COMMAND ${ADB} shell "chmod a+x /data/local/tmp/${ANDROID_ABI}/ashmem_test"
            COMMAND ${ADB} shell "LD_LIBRARY_PATH=/data/local/tmp/${ANDROID_ABI} /data/local/tmp/${ANDROID_ABI}/ashmem_test")
endif ()