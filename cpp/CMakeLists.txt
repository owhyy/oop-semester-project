cmake_minimum_required(VERSION 3.14)
project(audiolib CXX)

set(CMAKE_CXX_STANDARD 17)

add_library(audiolib SHARED
    src/audio_lib.cpp
    src/miniaudio_impl.cpp
)

set_target_properties(audiolib PROPERTIES
    OUTPUT_NAME "audiolib"
)

target_include_directories(audiolib PUBLIC
    include
    third_party
)

if (UNIX AND NOT APPLE)
    target_link_libraries(audiolib PRIVATE m dl pthread)
endif()
