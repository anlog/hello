//
// Created by hello on 2020/6/14.
//

#include <jni.h>
#include "../log_main.h"
#include <android/log.h>

#define LOG_TAG "go_jni"

int initJni(JNIEnv *env);

struct gostring {
    char *str;
    long len;
};

extern int go_add(int a, int b);

extern char *go_hello(gostring hello);

extern "C"
JNIEXPORT jint JNICALL
go_jni_add(JNIEnv *env, jclass clazz, jint a, jint b) {
    return go_add(a, b);
}

extern "C"
JNIEXPORT jstring JNICALL
go_jni_hello(JNIEnv *env, jclass clazz, jstring hello) {
    const char *string = env->GetStringUTFChars(hello, NULL);
    char *hello_from_go = go_hello(gostring{
            str: "hello from go_jni.",
            len: env->GetStringUTFLength(hello)
    });
    env->ReleaseStringUTFChars(hello, string);
    return env->NewStringUTF(hello_from_go);
}

jint JNI_OnLoad(JavaVM *jvm, void *reserved) {

    JNIEnv *e;
    int status;

    ALOGD("foo JNI_OnLoad init\n");

    // Check JNI version
    if (jvm->GetEnv((void **) &e, JNI_VERSION_1_6)) {
        ALOGE("JNI version mismatch error\n");
        return JNI_EVERSION;
    }

    if (initJni(e) < 0) {
        ALOGE("initJni failed.");
        return JNI_ERR;
    }

    return JNI_VERSION_1_6;
}

JNINativeMethod foo_methods[] = {
        {"go_jni_add",   "(II)I",                                  (void *) go_jni_add},
        {"go_jni_hello", "(Ljava/lang/String;)Ljava/lang/String;", (void *) go_jni_hello}
};

int initJni(JNIEnv *env) {
    char className[] = "cc/ifnot/app/jni/GoJniWrap";
    jclass clazz = env->FindClass((const char *) className);
    if (clazz == NULL) {
        ALOGE("initJni: Native registration unable to find class '%s'; aborting...", className);
        return JNI_ERR;
    }

    size_t numMethods = sizeof(foo_methods) / sizeof(*foo_methods);
    ALOGV("Registering %s's %d native methods...", className,
          numMethods);
    int result = env->RegisterNatives(clazz, foo_methods, numMethods);
    if (result < 0) {
        ALOGE("initJni: RegisterNatives failed for '%s'; aborting...", className);
        return JNI_ERR;
    }
    env->DeleteLocalRef(clazz);
    return JNI_OK;
}
