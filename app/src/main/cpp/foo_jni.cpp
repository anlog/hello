
#include <jni.h>
#include "log_main.h"
#include "foo.h"

#define LOG_TAG "foo_jni"

int initJni(JNIEnv *env);

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
        {"add",    "(II)I",                                    (void *) add_jni},
        {"binder", "(Ljava/lang/String;)Landroid/os/IBinder;", (void *) binder}
};

int initJni(JNIEnv *env) {
    char className[] = "cc/ifnot/app/jni/FooWrap";
    jclass clazz = env->FindClass((const char *) className);
    if (clazz == NULL) {
        ALOGE("initJni: Native registration unable to find class '%s'; aborting...", className);
        return JNI_ERR;
    }

    int numMethods = sizeof(foo_methods) / sizeof(*foo_methods);
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
