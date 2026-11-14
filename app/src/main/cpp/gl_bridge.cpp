#include <EGL/egl.h>
#include <GLES2/gl2.h>
#include <android/log.h>
#include <string>

#define LOG_TAG "GLBridge"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

static EGLDisplay g_display = EGL_NO_DISPLAY;
static EGLSurface g_surface = EGL_NO_SURFACE;
static EGLContext g_context = EGL_NO_CONTEXT;
static EGLConfig g_config = nullptr;

static int g_width = 0;
static int g_height = 0;

bool initGL(const char* renderer) {
    LOGI("Initializing GL bridge with renderer: %s", renderer);

    g_display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (g_display == EGL_NO_DISPLAY) {
        LOGE("Failed to get EGL display");
        return false;
    }

    EGLint major, minor;
    if (!eglInitialize(g_display, &major, &minor)) {
        LOGE("Failed to initialize EGL");
        return false;
    }
    LOGI("EGL version: %d.%d", major, minor);

    // Choose config
    const EGLint attribs[] = {
        EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
        EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
        EGL_BLUE_SIZE, 8,
        EGL_GREEN_SIZE, 8,
        EGL_RED_SIZE, 8,
        EGL_DEPTH_SIZE, 24,
        EGL_STENCIL_SIZE, 8,
        EGL_NONE
    };

    EGLint numConfigs;
    eglChooseConfig(g_display, attribs, &g_config, 1, &numConfigs);

    // Create context
    const EGLint contextAttribs[] = {
        EGL_CONTEXT_CLIENT_VERSION, 2,
        EGL_NONE
    };

    g_context = eglCreateContext(g_display, g_config, EGL_NO_CONTEXT, contextAttribs);
    if (g_context == EGL_NO_CONTEXT) {
        LOGE("Failed to create EGL context");
        return false;
    }

    LOGI("GL bridge initialized");
    return true;
}

void cleanupGL() {
    LOGI("Cleaning up GL bridge");

    if (g_display != EGL_NO_DISPLAY) {
        eglMakeCurrent(g_display, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
        
        if (g_context != EGL_NO_CONTEXT) {
            eglDestroyContext(g_display, g_context);
            g_context = EGL_NO_CONTEXT;
        }
        
        if (g_surface != EGL_NO_SURFACE) {
            eglDestroySurface(g_display, g_surface);
            g_surface = EGL_NO_SURFACE;
        }
        
        eglTerminate(g_display);
        g_display = EGL_NO_DISPLAY;
    }
}

void resizeSurface(int width, int height) {
    g_width = width;
    g_height = height;
    LOGI("Surface resized: %dx%d", width, height);
}

void swapBuffers() {
    if (g_display != EGL_NO_DISPLAY && g_surface != EGL_NO_SURFACE) {
        eglSwapBuffers(g_display, g_surface);
    }
}
