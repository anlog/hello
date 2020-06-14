//
// Created by hello on 2020/6/13.
//

#include <dlfcn.h>
#include <cstdio>
#include <cstdlib>
#include <jni.h>

#include "foo.h"
#include "log_main.h"

#define LOG_TAG "foo"

int add(int a, int b) {
    return a + b;
}

extern "C"
JNIEXPORT jint JNICALL
add_jni(JNIEnv *env, jclass clazz, jint a, jint b) {
    return a + b;
}

extern "C"
JNIEXPORT jobject JNICALL
binder(JNIEnv *env, jclass clazz, jstring name) {
    if (!AServiceManager_getService || !AIBinder_toJavaBinder) {
        dl_test();
        if (!AServiceManager_getService || !AIBinder_toJavaBinder) {
            ALOGE("AServiceManager_getService: %p - AIBinder_toJavaBinder: %p\n",
                  AServiceManager_getService, AIBinder_toJavaBinder);
            return NULL;
        }
    }

    jboolean isCopy;
    const char *service_name = env->GetStringUTFChars(name, &isCopy);

    AIBinder *service = AServiceManager_getService(service_name);

    env->ReleaseStringUTFChars(name, service_name);
    return AIBinder_toJavaBinder(env, service);
}

void dl_test() {
    void *handler = dlopen("libbinder_ndk.so", RTLD_LAZY);
    if (!handler) {
        fprintf(stderr, "failed to open libbinder_ndk.so\n");
        exit(-2);
    }

#define X(sym) do {   \
                if(!((sym) = (typeof(sym))dlsym(handler, #sym))) { \
                fprintf(stderr, "failed to import " #sym " from libbinder_ndk.so\n"); \
                exit(-1);  \
                }   \
                } while(0)
    X(AIBinder_Class_define);
    X(AIBinder_associateClass);
    X(AIBinder_decStrong);
    X(AIBinder_prepareTransaction);
    X(AIBinder_transact);
    X(AIBinder_ping);
    X(AIBinder_dump);
    X(AParcel_readStatusHeader);
    X(AParcel_readBool);
    X(AParcel_delete);
    X(AParcel_setDataPosition);
    X(AParcel_getDataPosition);
    X(AParcel_writeInt32);
    X(AParcel_writeStringArray);
    X(AParcel_writeString);
    X(AStatus_isOk);
    X(AStatus_delete);
    X(AStatus_getExceptionCode);
    X(AStatus_getServiceSpecificError);
    X(AStatus_getMessage);
    X(AStatus_getStatus);
    X(AServiceManager_getService);
    X(AIBinder_toJavaBinder);
    AIBinder *binder = AServiceManager_getService("activity");
    printf("AServiceManager_getService: %p: \n", binder);
//    AIBinder_dump(binder, 1, NULL, 0);

    // failed
//    int (*a)(int *b);
//    X(a);
#undef X
//    dlclose(handler);
}
