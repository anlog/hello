//
// Created by hello on 2020/6/13.
//

#include <dlfcn.h>
#include <stdio.h>
#include <stdlib.h>

int add(int a, int b) {
    return a + b;
}

struct AIBinder;
struct AParcel;
struct AStatus;
struct AIBinder_Class;
typedef struct AIBinder AIBinder;
typedef struct AParcel AParcel;
typedef struct AStatus AStatus;
typedef struct AIBinder_Class AIBinder_Class;
typedef int32_t binder_status_t;
typedef int32_t binder_exception_t;
typedef uint32_t transaction_code_t;
typedef uint32_t binder_flags_t;

typedef void *(*AIBinder_Class_onCreate)(void *args);

typedef void (*AIBinder_Class_onDestroy)(void *userData);

typedef binder_status_t (*AIBinder_Class_onTransact)(AIBinder *binder, transaction_code_t code,
                                                     const AParcel *in, AParcel *out);

typedef const char *(*AParcel_stringArrayElementGetter)(const void *arrayData, size_t index,
                                                        int32_t *outLength);

static AIBinder_Class *
(*AIBinder_Class_define)(const char *interfaceDescriptor, AIBinder_Class_onCreate onCreate,
                         AIBinder_Class_onDestroy onDestroy,
                         AIBinder_Class_onTransact onTransact) __attribute__((warn_unused_result));

static bool (*AIBinder_associateClass)(AIBinder *binder, const AIBinder_Class *clazz);

static void (*AIBinder_decStrong)(AIBinder *binder);

static binder_status_t (*AIBinder_prepareTransaction)(AIBinder *binder, AParcel **in);

static binder_status_t
(*AIBinder_transact)(AIBinder *binder, transaction_code_t code, AParcel **in, AParcel **out,
                     binder_flags_t flags);

static binder_status_t (*AIBinder_ping)(AIBinder *binder);

static binder_status_t
(*AIBinder_dump)(AIBinder *binder, int fd, const char **args, uint32_t numArgs);

static binder_status_t (*AParcel_readStatusHeader)(const AParcel *parcel, AStatus **status);

static binder_status_t (*AParcel_readBool)(const AParcel *parcel, bool *value);

static void (*AParcel_delete)(AParcel *parcel);

static binder_status_t (*AParcel_setDataPosition)(const AParcel *parcel, int32_t position);

static int32_t (*AParcel_getDataPosition)(const AParcel *parcel);

static binder_status_t (*AParcel_writeInt32)(AParcel *parcel, int32_t value);

static binder_status_t
(*AParcel_writeStringArray)(AParcel *parcel, const void *arrayData, int32_t length,
                            AParcel_stringArrayElementGetter getter);

static binder_status_t (*AParcel_writeString)(AParcel *parcel, const char *string, int32_t length);

static bool (*AStatus_isOk)(const AStatus *status);

static void (*AStatus_delete)(AStatus *status);

static binder_exception_t (*AStatus_getExceptionCode)(const AStatus *status);

static int32_t (*AStatus_getServiceSpecificError)(const AStatus *status);

static const char *(*AStatus_getMessage)(const AStatus *status);

static binder_status_t (*AStatus_getStatus)(const AStatus *status);

static AIBinder *
(*AServiceManager_getService)(const char *instance) __attribute__((__warn_unused_result__));

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
    int (*a)(int *b);
    X(a);
#undef X
    dlclose(handler);
}
