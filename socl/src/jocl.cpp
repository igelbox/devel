#include "jocl.h"
#include <CL/cl.h>

bool check(JNIEnv *env, cl_int err) {
    if (err == CL_SUCCESS)
        return true;
    jclass cls = env->FindClass("ccs/jocl/Error");
    jmethodID cons = env->GetMethodID(cls, "<init>", "(I)V");
    jobject t = env->NewObject(cls, cons, (jint) err);
    env->Throw((jthrowable) t);
    return false;
}

JNIEXPORT jint JNICALL Java_ccs_socl_CL_00024_getPlatformIDs(JNIEnv *env, jobject, jlongArray platforms, jint offset, jint length) {
    if (!platforms) {
        cl_uint count;
        cl_int err = clGetPlatformIDs(0, NULL, &count);
        check(env, err);
        return count;
    }
    cl_platform_id cl_buff[length];
    cl_uint count;
    cl_int err = clGetPlatformIDs(1, cl_buff, &count);
    check(env, err);
    jlong buff[count];
    for (int i = 0; i < count; i++)
        buff[i] = (jlong) cl_buff[i];
    env->SetLongArrayRegion(platforms, offset, count, buff);
    return count;
}

JNIEXPORT jlong Java_ccs_socl_CL_00024_createContextFromType(JNIEnv *env, jobject, jlongArray properties, jint deviceType) {
    jint pcount = properties ? env->GetArrayLength(properties) : 0;
    cl_context_properties props[pcount + 1];
    if (pcount) {
        jlong buff[pcount];
        env->GetLongArrayRegion(properties, 0, pcount, buff);
        for (int i = 0; i < pcount; i++)
            props[i] = (cl_context_properties) buff[i];
    }
    props[pcount] = NULL;
    cl_int err;
    cl_context r = clCreateContextFromType(props, (cl_device_type) deviceType, NULL, NULL, &err);
    check(env, err);
    return (jlong) r;
}

JNIEXPORT void JNICALL Java_ccs_socl_CL_00024_releaseContext(JNIEnv *env, jobject, jlong context) {
    cl_int err = clReleaseContext((cl_context) context);
    check(env, err);
}

JNIEXPORT jint JNICALL Java_ccs_socl_CL_00024_getContextInfo(JNIEnv *env, jobject, jlong context, jint paramName, jbyteArray out, jint offset, jint length) {
    if (!out) {
        size_t count;
        cl_int err = clGetContextInfo((cl_context) context, (cl_context_info) paramName, 0, 0, &count);
        check(env, err);
        return count;
    }
    jbyte buff[length];
    size_t count;
    cl_int err = clGetContextInfo((cl_context) context, (cl_context_info) paramName, length, buff, &count);
    check(env, err);
    env->SetByteArrayRegion(out, offset, count, buff);
    return count;
}

JNIEXPORT jlong JNICALL Java_ccs_socl_CL_00024_createCommandQueue(JNIEnv *env, jobject, jlong context, jlong device) {
    cl_int err;
    cl_command_queue r = clCreateCommandQueue((cl_context) context, (cl_device_id) device, 0, &err);
    check(env, err);
    return (jlong) r;
}

JNIEXPORT void JNICALL Java_ccs_socl_CL_00024_releaseCommandQueue(JNIEnv *env, jobject, jlong queue) {
    cl_int err = clReleaseCommandQueue((cl_command_queue) queue);
    check(env, err);
}

JNIEXPORT jlong JNICALL Java_ccs_socl_CL_00024_createProgramWithSource(JNIEnv *env, jobject, jlong context, jbyteArray source) {
    jint length = env->GetArrayLength(source);
    jbyte buff[length + 1];
    env->GetByteArrayRegion(source, 0, length, buff);
    buff[length] = 0;
    cl_int err;
    const char* ps[1];
    ps[0] = (const char*) buff;
    cl_program r = clCreateProgramWithSource((cl_context) context, 1, ps, 0, &err);
    check(env, err);
    return (jlong) r;
}

JNIEXPORT void JNICALL Java_ccs_socl_CL_00024_buildProgram(JNIEnv *env, jobject, jlong program) {
    cl_int err = clBuildProgram((cl_program) program, 0, 0, 0, 0, 0);
    check(env, err);
}

JNIEXPORT void JNICALL Java_ccs_socl_CL_00024_releaseProgram(JNIEnv *env, jobject, jlong program) {
    cl_int err = clReleaseProgram((cl_program) program);
    check(env, err);
}

JNIEXPORT jlong JNICALL Java_ccs_socl_CL_00024_createKernel(JNIEnv *env, jobject, jlong program, jbyteArray name) {
    jint length = env->GetArrayLength(name);
    jbyte buff[length + 1];
    env->GetByteArrayRegion(name, 0, length, buff);
    buff[length] = 0;

    cl_int err;
    cl_kernel r = clCreateKernel((cl_program) program, (const char*) buff, &err);
    check(env, err);
    return (jlong) r;
}

JNIEXPORT void JNICALL Java_ccs_socl_CL_00024_setKernelArg(JNIEnv *env, jobject, jlong kernel, jint idx, jlong memObj) {
    cl_int err = clSetKernelArg((cl_kernel) kernel, (cl_uint) idx, sizeof (cl_mem), &memObj);
    check(env, err);
}

JNIEXPORT void JNICALL Java_ccs_socl_CL_00024_releaseKernel(JNIEnv *env, jobject, jlong kernel) {
    cl_int err = clReleaseKernel((cl_kernel) kernel);
    check(env, err);
}

JNIEXPORT jlong JNICALL Java_ccs_socl_CL_00024_createBuffer__JI_3B(JNIEnv *env, jobject, jlong context, jint flags, jbyteArray data) {
    jint length = env->GetArrayLength(data);
    jbyte buff[length];
    env->GetByteArrayRegion(data, 0, length, buff);

    cl_int err;
    cl_mem r = clCreateBuffer((cl_context) context, (cl_mem_flags) flags | CL_MEM_COPY_HOST_PTR, length, buff, &err);
    check(env, err);
    return (jlong) r;
}

JNIEXPORT jlong JNICALL Java_ccs_socl_CL_00024_createBuffer__JII(JNIEnv *env, jobject, jlong context, jint flags, jint size) {
    cl_int err;
    cl_mem r = clCreateBuffer((cl_context) context, (cl_mem_flags) flags, (size_t) size, NULL, &err);
    check(env, err);
    return (jlong) r;
}

JNIEXPORT void JNICALL Java_ccs_socl_CL_00024_releaseMemObj(JNIEnv *env, jobject, jlong memObj) {
    cl_int err = clReleaseMemObject((cl_mem) memObj);
    check(env, err);
}

JNIEXPORT void JNICALL Java_ccs_socl_CL_00024_enqueueNDRangeKernel(JNIEnv *env, jobject, jlong queue, jlong kernel, jint workSize) {
    size_t ws = workSize;
    cl_int err = clEnqueueNDRangeKernel((cl_command_queue) queue, (cl_kernel) kernel, 1, 0, &ws, 0, 0, 0, 0);
    check(env, err);
}

JNIEXPORT void JNICALL Java_ccs_socl_CL_00024_enqueueReadBuffer(JNIEnv *env, jobject, jlong queue, jlong buffer, jboolean sync, jbyteArray dst) {
    jint length = env->GetArrayLength(dst);
    jbyte buff[length];
    cl_int err = clEnqueueReadBuffer((cl_command_queue) queue, (cl_mem) buffer, (cl_bool) sync, 0, length, buff, 0, 0, 0);
    check(env, err);
    env->SetByteArrayRegion(dst, 0, length, buff);
}
