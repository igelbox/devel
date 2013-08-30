#include "ccs_jack_Client.h"
#include <stdlib.h>
#include <string.h>
#include <jack/jack.h>

typedef struct {
    JavaVM *vm;
    JNIEnv *env;
    jclass c_jack;
    jmethodID m_process;
    jint id;
} Arg;

void throw( JNIEnv *env ) {
    jclass c = (*env)->FindClass( env, "ccs/jack/Error" );
    jmethodID m = (*env)->GetMethodID( env, c, "<init>", "()V" );
    jobject e = (*env)->NewObject( env, c, m );
    (*env)->Throw( env, (jthrowable) e );
}

JNIEnv* getEnv( JavaVM* vm, char* thread_name ) {
    JNIEnv *env;
    int r = (*vm)->GetEnv( vm, (void**) &env, JNI_VERSION_1_6 );
    if ( r == JNI_EDETACHED ) {
        JavaVMAttachArgs args = { JNI_VERSION_1_6, thread_name };
        r = (*vm)->AttachCurrentThreadAsDaemon( vm, (void**) &env, &args );
    }
    if ( (r != JNI_OK) || !env )
        return NULL;
    return env;
}

int process( jack_nframes_t n, void* arg ) {
    Arg *a = arg;
    if ( !a->env ) {
        a->env = getEnv( a->vm, "process thread" );
        a->c_jack = (*(a->env))->FindClass( a->env, "ccs/jack/Jack" );
    }
    if ( !a->m_process )
        a->m_process = (*(a->env))->GetStaticMethodID( a->env, a->c_jack, "onProcess", "(II)I" );
    return (int) (*(a->env))->CallStaticIntMethod( a->env, a->c_jack, a->m_process, a->id, n );
}

void on_client_reg( const char* name, int reg, void* arg ) {
    Arg *a = arg;
    if ( !a->env ) {
        a->env = getEnv( a->vm, "process thread" );
        a->c_jack = (*(a->env))->FindClass( a->env, "ccs/jack/Jack" );
    }
    jmethodID m = (*(a->env))->GetStaticMethodID( a->env, a->c_jack, "onClientReg", "(Ljava/lang/String;Z)V" );
    jstring n = (*(a->env))->NewStringUTF( a->env, name );
    jboolean r = reg != 0;
    (*(a->env))->CallStaticIntMethod( a->env, a->c_jack, m, n, r );
}

void on_port_reg( jack_port_id_t port, int reg, void* arg ) {
    Arg *a = arg;
    if ( !a->env ) {
        a->env = getEnv( a->vm, "process thread" );
        a->c_jack = (*(a->env))->FindClass( a->env, "ccs/jack/Jack" );
    }
    jmethodID m = (*(a->env))->GetStaticMethodID( a->env, a->c_jack, "onPortReg", "(JZ)V" );
    jlong h = port;
    jboolean r = reg != 0;
    (*(a->env))->CallStaticIntMethod( a->env, a->c_jack, m, h, r );
}

JNIEXPORT jlong JNICALL Java_ccs_jack_Client_open( JNIEnv *env, jclass cls, jstring name, jint id ) {
    const char* n = (*env)->GetStringUTFChars( env, name, NULL );
    jack_client_t* c = jack_client_open( n, 0, NULL );
    if ( !c ) {
        throw( env );
        return 0;
    }
    Arg *a = malloc( sizeof (Arg) );
    memset( a, 0, sizeof (Arg) );
    (*env)->GetJavaVM( env, &a->vm );
    a->id = id;
    int r = jack_set_process_callback( c, process, a );
    if ( r ) {
        throw( env );
        return 0;
    }
    /*
        if ( jack_set_client_registration_callback( c, on_client_reg, a ) ) {
            throw( env );
            return 0;
        }
        if ( jack_set_port_registration_callback( c, on_port_reg, a ) ) {
            throw( env );
            return 0;
        }
     */
    return (jlong) (size_t) c;
}

JNIEXPORT void JNICALL Java_ccs_jack_Client_activate( JNIEnv *env, jclass cls, jlong client ) {
    if ( jack_activate( (jack_client_t*) (size_t) client ) )
        throw( env );
}

JNIEXPORT void JNICALL Java_ccs_jack_Client_deactivate( JNIEnv *env, jclass cls, jlong client ) {
    if ( jack_deactivate( (jack_client_t*) (size_t) client ) )
        throw( env );
}

JNIEXPORT jint JNICALL Java_ccs_jack_Client_close( JNIEnv *env, jclass cls, jlong client ) {
    return jack_client_close( (jack_client_t*) (size_t) client );
}

JNIEXPORT jobjectArray JNICALL Java_ccs_jack_Client_findPorts( JNIEnv *env, jclass cls, jlong client, jstring namep, jstring typep, jlong flags ) {
    jack_client_t* c = (jack_client_t*) client;
    const char* np = namep ? (*env)->GetStringUTFChars( env, namep, NULL ) : NULL;
    const char* tp = typep ? (*env)->GetStringUTFChars( env, typep, NULL ) : NULL;
    const char** p = jack_get_ports( c, np, tp, (unsigned long) flags );
    if ( !p ) {
        throw( env );
        return 0;
    }
    const char* tmp[1024];
    int idx = 0;
    while ( *p )
        tmp[idx++] = *p++;
    jclass c_s = (*env)->FindClass( env, "java/lang/String" );
    jobjectArray r = (*env)->NewObjectArray( env, idx, c_s, 0 );
    for ( int i = 0; i < idx; i++ ) {
        jstring s = (*env)->NewStringUTF( env, tmp[i] );
        (*env)->SetObjectArrayElement( env, r, i, s );
    }
    return r;
}

JNIEXPORT jlong JNICALL Java_ccs_jack_Client_portByName( JNIEnv *env, jclass cls, jlong client, jstring name ) {
    jack_client_t *c = (jack_client_t*) client;
    const char* n = (*env)->GetStringUTFChars( env, name, NULL );
    jack_port_t *p = jack_port_by_name( c, n );
    if ( !p )
        throw( env );
    return (jlong) p;
}

JNIEXPORT jlong JNICALL Java_ccs_jack_Client_regPort( JNIEnv *env, jclass cls, jlong client, jstring name, jstring type, jlong flags, jlong bs ) {
    jack_client_t *c = (jack_client_t*) client;
    const char* n = (*env)->GetStringUTFChars( env, name, NULL );
    const char* t = (*env)->GetStringUTFChars( env, type, NULL );
    jack_port_t *p = jack_port_register( c, n, t, flags, bs );
    if ( !p )
        throw( env );
    return (jlong) p;
}

JNIEXPORT void JNICALL Java_ccs_jack_Client_unregPort( JNIEnv *env, jclass cls, jlong client, jlong port ) {
    jack_client_t *c = (jack_client_t*) client;
    jack_port_t *p = (jack_port_t*) port;
    int r = jack_port_unregister( c, p );
    if ( r )
        throw( env );
}

JNIEXPORT jint JNICALL Java_ccs_jack_Client_bufferSize( JNIEnv *env, jclass cls, jlong client ) {
    return jack_get_buffer_size( (jack_client_t*) client );
}

JNIEXPORT jint JNICALL Java_ccs_jack_Client_sampleRate( JNIEnv *env, jclass cls, jlong client ) {
    return jack_get_sample_rate( (jack_client_t*) client );
}

JNIEXPORT void JNICALL Java_ccs_jack_Client_connectPorts( JNIEnv *env, jclass cls, jlong client, jstring source, jstring destination ) {
    jack_client_t *c = (jack_client_t*) client;
    const char* p = (*env)->GetStringUTFChars( env, source, NULL );
    const char* a = (*env)->GetStringUTFChars( env, destination, NULL );
    int r = jack_connect( c, p, a );
    if ( r )
        throw( env );
}

JNIEXPORT void JNICALL Java_ccs_jack_Client_disconnectPorts( JNIEnv *env, jclass cls, jlong client, jstring source, jstring destination ) {
    jack_client_t *c = (jack_client_t*) client;
    const char* p = (*env)->GetStringUTFChars( env, source, NULL );
    const char* a = (*env)->GetStringUTFChars( env, destination, NULL );
    int r = jack_disconnect( c, p, a );
    if ( r )
        throw( env );
}
