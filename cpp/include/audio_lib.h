#ifndef AUDIO_LIB_H
#define AUDIO_LIB_H

#ifdef __cplusplus
extern "C" {
#endif

/**
 * Returns the library version string.
 * Used by JNA to verify the bridge works.
 */
const char* audiolib_version(void);

#ifdef __cplusplus
}
#endif

#endif /* AUDIO_LIB_H */
