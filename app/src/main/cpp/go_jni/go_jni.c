//
// Created by hello on 2020/6/14.
//

#include <jni.h>
#include "../log_main.h"

#define LOG_TAG "go_jni"

int initJni(JNIEnv *env);

struct go_string {
    char *str;
    long len;
};
typedef struct go_string go_string;

extern int go_add(int a, int b);

extern char *go_hello(go_string hello);

JNIEXPORT jint JNICALL
go_jni_add(JNIEnv *env, jclass clazz, jint a, jint b) {
    return go_add(a, b);
}

JNIEXPORT jstring JNICALL
go_jni_hello(JNIEnv *env, jclass clazz, jstring hello) {
    const char *string = (*env)->GetStringUTFChars(env, hello, NULL);
    char *hello_from_go = go_hello(
            (go_string) {
                    str: "hello from go_jni.",
                    len: (*env)->GetStringUTFLength(env, hello)
            });
    (*env)->ReleaseStringUTFChars(env, hello, string);
    return (*env)->NewStringUTF(env, hello_from_go);
}

jint JNI_OnLoad(JavaVM *jvm, void *reserved) {

    JNIEnv *e;
    int status;

    ALOGD("foo JNI_OnLoad init\n");

    // Check JNI version
    if ((*jvm)->GetEnv(jvm, (void **) &e, JNI_VERSION_1_6)) {
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
    jclass clazz = (*env)->FindClass(env, (const char *) className);
    if (clazz == NULL) {
        ALOGE("initJni: Native registration unable to find class '%s'; aborting...", className);
        return JNI_ERR;
    }

    int numMethods = sizeof(foo_methods) / sizeof(*foo_methods);
    ALOGV("Registering %s's %d native methods...", className,
          numMethods);
    int result = (*env)->RegisterNatives(env, clazz, foo_methods, numMethods);
    if (result < 0) {
        ALOGE("initJni: RegisterNatives failed for '%s'; aborting...", className);
        return JNI_ERR;
    }
    (*env)->DeleteLocalRef(env, clazz);
    return JNI_OK;
}
