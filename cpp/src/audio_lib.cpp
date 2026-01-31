#include "audio_lib.h"
#include "miniaudio.h"
#include <mutex>

namespace {
    ma_engine g_engine;
    bool g_engine_init = false;
    ma_sound g_sound;
    bool g_sound_init = false;
    std::mutex g_audio_mutex;

    int ensure_engine() {
        if (g_engine_init) {
            return 0;
        }
        ma_result result = ma_engine_init(NULL, &g_engine);
        if (result != MA_SUCCESS) {
            return 10;
        }
        g_engine_init = true;
        return 0;
    }

    void stop_internal() {
        if (g_sound_init) {
            ma_sound_stop(&g_sound);
            ma_sound_uninit(&g_sound);
            g_sound_init = false;
        }
    }
}

extern "C" {

const char* audiolib_version(void) {
    return "audiolib 1.0";
}

int load_audio(const char* path) {
    if (path == nullptr || path[0] == '\0') {
        return 1;
    }

    ma_decoder decoder;
    ma_result result = ma_decoder_init_file(path, NULL, &decoder);
    if (result != MA_SUCCESS) {
        return 2;
    }
    ma_decoder_uninit(&decoder);
    return 0;
}

int play_audio(const char* path) {
    if (path == nullptr || path[0] == '\0') {
        return 1;
    }

    std::lock_guard<std::mutex> lock(g_audio_mutex);
    int engine_code = ensure_engine();
    if (engine_code != 0) {
        return engine_code;
    }

    stop_internal();
    ma_result result = ma_sound_init_from_file(&g_engine, path, MA_SOUND_FLAG_STREAM, NULL, NULL, &g_sound);
    if (result != MA_SUCCESS) {
        return 2;
    }
    g_sound_init = true;

    result = ma_sound_start(&g_sound);
    if (result != MA_SUCCESS) {
        stop_internal();
        return 3;
    }
    return 0;
}

void stop_audio(void) {
    std::lock_guard<std::mutex> lock(g_audio_mutex);
    stop_internal();
}

}
