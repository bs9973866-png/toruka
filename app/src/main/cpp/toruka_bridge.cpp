#include <jni.h>
#include <android/log.h>
#include <string>
#include <cstring>
#include <pthread.h>

#define LOG_TAG "TorukaBridge"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// External functions
extern int launchJVM(const char* javaPath, const char* classpath, const char* mainClass, int argc, char** argv);
extern bool initGL(const char* renderer);
extern void cleanupGL();
extern void handleTouch(int action, float x, float y, int pointerId);
extern void handleKey(int keyCode, int action, int modifiers);
extern void resizeSurface(int width, int height);

extern "C" {

JNIEXPORT jboolean JNICALL
Java_com_toruka_launcher_jni_NativeBridge_setupGameEnvironment(
    JNIEnv* env,
    jclass clazz,
    jstring versionId,
    jstring javaPath,
    jstring gameDir,
    jstring accountJson
) {
    const char* ver = env->GetStringUTFChars(versionId, nullptr);
    const char* java = env->GetStringUTFChars(javaPath, nullptr);
    const char* dir = env->GetStringUTFChars(gameDir, nullptr);
    const char* acc = env->GetStringUTFChars(accountJson, nullptr);

    LOGI("Setup environment for version: %s", ver);
    LOGI("Game directory: %s", dir);

    // TODO: Set up environment variables, paths, etc.

    env->ReleaseStringUTFChars(versionId, ver);
    env->ReleaseStringUTFChars(javaPath, java);
    env->ReleaseStringUTFChars(gameDir, dir);
    env->ReleaseStringUTFChars(accountJson, acc);

    return JNI_TRUE;
}

JNIEXPORT jint JNICALL
Java_com_toruka_launcher_jni_NativeBridge_launchJavaVM(
    JNIEnv* env,
    jclass clazz,
    jstring javaPath,
    jstring classpath,
    jstring mainClass,
    jobjectArray args
) {
    const char* jPath = env->GetStringUTFChars(javaPath, nullptr);
    const char* cp = env->GetStringUTFChars(classpath, nullptr);
    const char* mc = env->GetStringUTFChars(mainClass, nullptr);

    jsize argc = env->GetArrayLength(args);
    char** argv = new char*[argc];
    for (jsize i = 0; i < argc; i++) {
        jstring arg = (jstring)env->GetObjectArrayElement(args, i);
        const char* str = env->GetStringUTFChars(arg, nullptr);
        argv[i] = strdup(str);
        env->ReleaseStringUTFChars(arg, str);
    }

    LOGI("Launching JVM with main class: %s", mc);
    int result = launchJVM(jPath, cp, mc, argc, argv);

    for (jsize i = 0; i < argc; i++) {
        free(argv[i]);
    }
    delete[] argv;

    env->ReleaseStringUTFChars(javaPath, jPath);
    env->ReleaseStringUTFChars(classpath, cp);
    env->ReleaseStringUTFChars(mainClass, mc);

    return result;
}

JNIEXPORT void JNICALL
Java_com_toruka_launcher_jni_NativeBridge_setWindowPointer(
    JNIEnv* env,
    jclass clazz,
    jlong window
) {
    LOGI("Set window pointer: %ld", (long)window);
}

JNIEXPORT void JNICALL
Java_com_toruka_launcher_jni_NativeBridge_handleTouchEvent(
    JNIEnv* env,
    jclass clazz,
    jint action,
    jfloat x,
    jfloat y,
    jint pointerId
) {
    handleTouch(action, x, y, pointerId);
}

JNIEXPORT void JNICALL
Java_com_toruka_launcher_jni_NativeBridge_handleKeyEvent(
    JNIEnv* env,
    jclass clazz,
    jint keyCode,
    jint action,
    jint modifiers
) {
    handleKey(keyCode, action, modifiers);
}

JNIEXPORT void JNICALL
Java_com_toruka_launcher_jni_NativeBridge_resizeWindow(
    JNIEnv* env,
    jclass clazz,
    jint width,
    jint height
) {
    resizeSurface(width, height);
}

JNIEXPORT void JNICALL
Java_com_toruka_launcher_jni_NativeBridge_cleanup(
    JNIEnv* env,
    jclass clazz
) {
    LOGI("Cleanup native resources");
    cleanupGL();
}

JNIEXPORT jboolean JNICALL
Java_com_toruka_launcher_jni_NativeBridge_initGLBridge(
    JNIEnv* env,
    jclass clazz,
    jstring renderer
) {
    const char* r = env->GetStringUTFChars(renderer, nullptr);
    bool result = initGL(r);
    env->ReleaseStringUTFChars(renderer, r);
    return result ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_com_toruka_launcher_jni_NativeBridge_setGLAttribute(
    JNIEnv* env,
    jclass clazz,
    jint attr,
    jint value
) {
    // TODO: Set GL attributes
}

JNIEXPORT void JNICALL
Java_com_toruka_launcher_jni_NativeBridge_lwjglSetContext(
    JNIEnv* env,
    jclass clazz,
    jlong window
) {
    // TODO: Set LWJGL context
}

JNIEXPORT void JNICALL
Java_com_toruka_launcher_jni_NativeBridge_lwjglSwapBuffers(
    JNIEnv* env,
    jclass clazz
) {
    // TODO: Swap buffers
}

JNIEXPORT void JNICALL
Java_com_toruka_launcher_jni_NativeBridge_lwjglMakeCurrent(
    JNIEnv* env,
    jclass clazz,
    jlong window
) {
    // TODO: Make context current
}

JNIEXPORT jlong JNICALL
Java_com_toruka_launcher_jni_NativeBridge_lwjglGetProcAddress(
    JNIEnv* env,
    jclass clazz,
    jstring name
) {
    const char* n = env->GetStringUTFChars(name, nullptr);
    // TODO: Get GL proc address
    env->ReleaseStringUTFChars(name, n);
    return 0;
}

} // extern "C"
