#include "ccs_jack_Client.h"
#include <stdlib.h>
#include <string.h>
#include <jack/jack.h>

typedef struct {
    JavaVM *vm;
    JNIEnv *env;
    jclass cls;
    jmethodID mth;
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
        a->cls = (*(a->env))->FindClass( a->env, "ccs/jack/Jack" );
        a->mth = (*(a->env))->GetStaticMethodID( a->env, a->cls, "process", "(II)I" );
    }
    return (int) (*(a->env))->CallStaticIntMethod( a->env, a->cls, a->mth, a->id, n );
}

JNIEXPORT jlong JNICALL Java_ccs_jack_Client_open(JNIEnv *env, jclass cls, jstring name, jint id) {
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
    return (jlong)(size_t) c;
}

JNIEXPORT void JNICALL Java_ccs_jack_Client_activate(JNIEnv *env, jclass cls, jlong client) {
    if ( jack_activate( (jack_client_t*) (size_t) client ) )
        throw( env );
}

JNIEXPORT void JNICALL Java_ccs_jack_Client_deactivate(JNIEnv *env, jclass cls, jlong client) {
    if ( jack_deactivate( (jack_client_t*) (size_t) client ) )
        throw( env );
}

JNIEXPORT jint JNICALL Java_ccs_jack_Client_close(JNIEnv *env, jclass cls, jlong client) {
    return jack_client_close( (jack_client_t*) (size_t) client );
}
