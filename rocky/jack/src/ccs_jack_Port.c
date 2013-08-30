#include "ccs_jack_Port.h"
#include <jack/jack.h>

extern void throw( JNIEnv *env );

JNIEXPORT void JNICALL Java_ccs_jack_Port_getBuffer( JNIEnv *env, jclass cls, jlong handle, jint samples, jfloatArray buffer ) {
    jack_port_t *p = (jack_port_t*) (size_t) handle;
    void *b = jack_port_get_buffer( p, (jack_nframes_t) samples );
    if ( !b )
        throw( env );
    else
        (*env)->SetFloatArrayRegion( env, buffer, 0, samples, (float*) b );
}

JNIEXPORT void JNICALL Java_ccs_jack_Port_setBuffer( JNIEnv *env, jclass cls, jlong handle, jint samples, jfloatArray buffer ) {
    jack_port_t *p = (jack_port_t*) (size_t) handle;
    void *b = jack_port_get_buffer( p, (jack_nframes_t) samples );
    if ( !b )
        throw( env );
    else
        (*env)->GetFloatArrayRegion( env, buffer, 0, samples, (float*) b );
}

JNIEXPORT jstring JNICALL Java_ccs_jack_Port_getFullName( JNIEnv *env, jclass cls, jlong port ) {
    jack_port_t *p = (jack_port_t*) (size_t) port;
    const char* n = jack_port_name( p );
    if ( !n ) {
        throw( env );
        return 0;
    }
    return (*env)->NewStringUTF( env, n );
}

JNIEXPORT jstring JNICALL Java_ccs_jack_Port_getShortName( JNIEnv *env, jclass cls, jlong port ) {
    jack_port_t *p = (jack_port_t*) (size_t) port;
    const char* n = jack_port_short_name( p );
    if ( !n ) {
        throw( env );
        return 0;
    }
    return (*env)->NewStringUTF( env, n );
}

JNIEXPORT void JNICALL Java_ccs_jack_Port_setShortName( JNIEnv *env, jclass cls, jlong port, jstring name ) {
    jack_port_t *p = (jack_port_t*) (size_t) port;
    const char* n = (*env)->GetStringUTFChars( env, name, NULL );
    int r = jack_port_set_name( p, n );
    if ( r )
        throw( env );
}
