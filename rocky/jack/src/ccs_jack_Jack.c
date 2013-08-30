#include "ccs_jack_Jack.h"
#include <jack/jack.h>

JNIEXPORT jlong JNICALL Java_ccs_jack_Jack_client_1open(JNIEnv *env, jclass cls, jstring name) {
    const char* n = (*env)->GetStringUTFChars( env, name, NULL );
    return (jlong)(size_t) jack_client_open( n, 0, NULL );
}

JNIEXPORT jint JNICALL Java_ccs_jack_Jack_client_1close(JNIEnv *env, jclass cls, jlong client) {
    return jack_client_close( (jack_client_t*) (size_t) client );
}
