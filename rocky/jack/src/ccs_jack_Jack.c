#include "ccs_jack_Jack.h"
#include <jack/jack.h>

JNIEXPORT jlong JNICALL Java_ccs_jack_Jack_getTime( JNIEnv *env, jclass cls ) {
    return jack_get_time( );
}
