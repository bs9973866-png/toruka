cmake_minimum_required(VERSION 3.22.1)
project("toruka_bridge")

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Optimization flags
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -O3 -DNDEBUG -fvisibility=hidden")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -DNDEBUG -fvisibility=hidden -ffunction-sections -fdata-sections")
set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -Wl,--gc-sections")

find_library(log-lib log)
find_library(android-lib android)
find_library(EGL-lib EGL)
find_library(GLESv2-lib GLESv2)

set(SOURCES
    toruka_bridge.cpp
    jvm_launcher.cpp
    gl_bridge.cpp
    input_bridge.cpp
    lwjgl_bridge.cpp
)

add_library(toruka_bridge SHARED ${SOURCES})

target_link_libraries(toruka_bridge
    ${log-lib}
    ${android-lib}
    ${EGL-lib}
    ${GLESv2-lib}
)
