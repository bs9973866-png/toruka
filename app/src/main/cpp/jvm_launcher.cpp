#include <jni.h>
#include <dlfcn.h>
#include <pthread.h>
#include <android/log.h>
#include <string>
#include <vector>

#define LOG_TAG "JVMLauncher"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

typedef jint (*JNI_CreateJavaVMFunc)(JavaVM**, JNIEnv**, void*);

struct JVMConfig {
    std::string javaPath;
    std::string classpath;
    std::string mainClass;
    std::vector<std::string> args;
};

static JavaVM* g_vm = nullptr;
static pthread_mutex_t g_vmMutex = PTHREAD_MUTEX_INITIALIZER;

int launchJVM(const char* javaPath, const char* classpath, const char* mainClass, int argc, char** argv) {
    LOGI("Launching JVM from: %s", javaPath);
    LOGI("Main class: %s", mainClass);
    LOGI("Classpath: %s", classpath);

    // Load libjvm.so
    std::string jvmPath = std::string(javaPath) + "/lib/server/libjvm.so";
    void* libjvm = dlopen(jvmPath.c_str(), RTLD_NOW);
    if (!libjvm) {
        LOGE("Failed to load libjvm.so: %s", dlerror());
        return -1;
    }

    JNI_CreateJavaVMFunc createJVM = (JNI_CreateJavaVMFunc)dlsym(libjvm, "JNI_CreateJavaVM");
    if (!createJVM) {
        LOGE("Failed to find JNI_CreateJavaVM");
        dlclose(libjvm);
        return -1;
    }

    // Prepare JVM options
    std::vector<JavaVMOption> options;
    std::string cpOpt = std::string("-Djava.class.path=") + classpath;
    
    JavaVMOption opt1;
    opt1.optionString = const_cast<char*>(cpOpt.c_str());
    options.push_back(opt1);

    JavaVMInitArgs vmArgs;
    vmArgs.version = JNI_VERSION_1_8;
    vmArgs.nOptions = options.size();
    vmArgs.options = options.data();
    vmArgs.ignoreUnrecognized = JNI_FALSE;

    pthread_mutex_lock(&g_vmMutex);
    
    JNIEnv* env = nullptr;
    jint result = createJVM(&g_vm, &env, &vmArgs);
    
    if (result != JNI_OK || !env) {
        LOGE("Failed to create JVM: %d", result);
        pthread_mutex_unlock(&g_vmMutex);
        dlclose(libjvm);
        return -1;
    }

    LOGI("JVM created successfully");

    // Find main class and method
    jclass mainCls = env->FindClass(mainClass);
    if (!mainCls) {
        LOGE("Failed to find main class: %s", mainClass);
        g_vm->DestroyJavaVM();
        g_vm = nullptr;
        pthread_mutex_unlock(&g_vmMutex);
        dlclose(libjvm);
        return -1;
    }

    jmethodID mainMethod = env->GetStaticMethodID(mainCls, "main", "([Ljava/lang/String;)V");
    if (!mainMethod) {
        LOGE("Failed to find main method");
        g_vm->DestroyJavaVM();
        g_vm = nullptr;
        pthread_mutex_unlock(&g_vmMutex);
        dlclose(libjvm);
        return -1;
    }

    // Prepare args array
    jclass stringCls = env->FindClass("java/lang/String");
    jobjectArray argsArray = env->NewObjectArray(argc, stringCls, nullptr);
    for (int i = 0; i < argc; i++) {
        jstring arg = env->NewStringUTF(argv[i]);
        env->SetObjectArrayElement(argsArray, i, arg);
        env->DeleteLocalRef(arg);
    }

    LOGI("Calling main method...");
    env->CallStaticVoidMethod(mainCls, mainMethod, argsArray);

    // Check for exceptions
    if (env->ExceptionCheck()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
    }

    g_vm->DestroyJavaVM();
    g_vm = nullptr;
    pthread_mutex_unlock(&g_vmMutex);
    dlclose(libjvm);

    LOGI("JVM exited");
    return 0;
}
