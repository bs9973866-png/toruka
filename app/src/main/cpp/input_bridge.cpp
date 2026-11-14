#include <android/log.h>

#define LOG_TAG "InputBridge"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

static float g_lastX = 0.0f;
static float g_lastY = 0.0f;
static bool g_touchDown = false;

void handleTouch(int action, float x, float y, int pointerId) {
    switch (action) {
        case 0: // ACTION_DOWN
            g_touchDown = true;
            g_lastX = x;
            g_lastY = y;
            LOGI("Touch down: ptr=%d x=%.1f y=%.1f", pointerId, x, y);
            break;
        case 1: // ACTION_UP
            g_touchDown = false;
            LOGI("Touch up: ptr=%d", pointerId);
            break;
        case 2: // ACTION_MOVE
            if (g_touchDown) {
                float dx = x - g_lastX;
                float dy = y - g_lastY;
                g_lastX = x;
                g_lastY = y;
                // TODO: Send mouse move event to game
                LOGI("Touch move: ptr=%d dx=%.1f dy=%.1f", pointerId, dx, dy);
            }
            break;
    }
}

void handleKey(int keyCode, int action, int modifiers) {
    LOGI("Key event: code=%d action=%d mods=%d", keyCode, action, modifiers);
    // TODO: Map Android key codes to LWJGL key codes and send events
}
