#include "audio_lib.h"
#include <fstream>

extern "C" {

const char* audiolib_version(void) {
    return "audiolib 1.0";
}

int load_audio(const char* path) {
    if (path == nullptr || path[0] == '\0') {
        return 1;
    }

    std::ifstream file(path, std::ios::binary);
    if (!file.good()) {
        return 2;
    }

    return 0;
}

}
