cmake_minimum_required(VERSION 3.4.1)

project("paddle")

set(PaddleLite_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../../libs")
include_directories(include)

set(OpenCV_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../../OpenCV/sdk/native/jni")
find_package(OpenCV REQUIRED)
message(STATUS "OpenCV libraries: ${OpenCV_LIBS}")
include_directories(${OpenCV_INCLUDE_DIRS})
include_directories(
        .
        utils
)

add_library(${CMAKE_PROJECT_NAME}
        SHARED
        utils.cc
        paddle.cpp
        clipper.cpp
        rec_process.cc
        cls_process.cc
        det_process.cc
        db_post_process.cc
        )

add_library(
        # Sets the name of the library.
        paddle_light_api_shared
        # Sets the library as a shared library.
        SHARED
        # Provides a relative path to your source file(s).
        IMPORTED)

set_target_properties(
        # Specifies the target library.
        paddle_light_api_shared
        # Specifies the parameter you want to define.
        PROPERTIES
        IMPORTED_LOCATION
        ${PaddleLite_DIR}/${ANDROID_ABI}/libpaddle_light_api_shared.so
        # Provides the path to the library you want to import.
)

target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        log
        paddle_light_api_shared
        ${OpenCV_LIBS}
        GLESv2
        EGL
        jnigraphics
        )