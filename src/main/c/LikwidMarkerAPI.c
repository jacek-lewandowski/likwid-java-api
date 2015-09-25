#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jni.h>
#include <likwid.h>
#include "LikwidMarkerAPI.h"

/*
 * Class:     org_jl_perftools_likwid_LikwidMarkerAPI
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_jl_perftools_likwid_LikwidMarkerAPI_init
  (JNIEnv *env, jobject obj) {
  	likwid_markerInit();
  }

/*
 * Class:     org_jl_perftools_likwid_LikwidMarkerAPI
 * Method:    threadInit
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_jl_perftools_likwid_LikwidMarkerAPI_threadInit
  (JNIEnv *env, jobject obj) {
  	likwid_markerThreadInit();
  }

/*
 * Class:     org_jl_perftools_likwid_LikwidMarkerAPI
 * Method:    register
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_jl_perftools_likwid_LikwidMarkerAPI_register
  (JNIEnv *env, jobject obj, jstring _tag) {
  	const char* tag = (*env)->GetStringUTFChars(env, _tag, NULL);
 	likwid_markerRegisterRegion(tag);
    (*env)->ReleaseStringUTFChars(env, _tag, tag);
  }

/*
 * Class:     org_jl_perftools_likwid_LikwidMarkerAPI
 * Method:    start
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_jl_perftools_likwid_LikwidMarkerAPI_start
  (JNIEnv *env, jobject obj, jstring _tag) {
  	const char* tag = (*env)->GetStringUTFChars(env, _tag, NULL);
  	likwid_markerStartRegion(tag);
    (*env)->ReleaseStringUTFChars(env, _tag, tag);
  }

/*
 * Class:     org_jl_perftools_likwid_LikwidMarkerAPI
 * Method:    stop
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_jl_perftools_likwid_LikwidMarkerAPI_stop
  (JNIEnv *env, jobject obj, jstring _tag) {
  	const char* tag = (*env)->GetStringUTFChars(env, _tag, NULL);
  	likwid_markerStopRegion(tag);
    (*env)->ReleaseStringUTFChars(env, _tag, tag);
  }

/*
 * Class:     org_jl_perftools_likwid_LikwidMarkerAPI
 * Method:    getResults
 * Signature: (Ljava/lang/String;I)Lorg/rrze/likwid/LikwidMarkerResults;
 */
JNIEXPORT void JNICALL Java_org_jl_perftools_likwid_LikwidMarkerAPI_getResults
  (JNIEnv *env, jobject obj, jstring _tag, jint _num, jobject _results) {
  	const char* tag = (*env)->GetStringUTFChars(env, _tag, NULL);
  	int iNum = (int) _num;
  	int iCount;
  	double dTime;
  	double dEvents[iNum];

  	likwid_markerGetRegion(tag, &iNum, dEvents, &dTime, &iCount);

	jclass _class = (*env)->GetObjectClass(env, _results);
	if (_class != NULL) {
    	jmethodID _setCount = (*env)->GetMethodID(env, _class, "setCount", "(I)V");
    	jmethodID _setTime = (*env)->GetMethodID(env, _class, "setTime", "(D)V");
    	jmethodID _setEvents = (*env)->GetMethodID(env, _class, "setEvents", "([D)V");

		if (_setTime != NULL) {
			(*env)->CallVoidMethod(env, _results, _setTime, (jdouble) dTime);
		}
		if (_setCount != NULL) {
			(*env)->CallVoidMethod(env, _results, _setCount, (jint) iCount);
		}
		if (_setEvents != NULL) {
			jdoubleArray _events = (*env)->NewDoubleArray(env, _num);
			(*env)->SetDoubleArrayRegion(env, _events, 0, _num, dEvents);
			(*env)->CallVoidMethod(env, _results, _setEvents, _events);
		}
    }
  	
    (*env)->ReleaseStringUTFChars(env, _tag, tag);
  }

/*
 * Class:     org_jl_perftools_likwid_LikwidMarkerAPI
 * Method:    nextGroup
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_jl_perftools_likwid_LikwidMarkerAPI_nextGroup
  (JNIEnv *env, jobject obj) {
  	likwid_markerNextGroup();
  }

/*
 * Class:     org_jl_perftools_likwid_LikwidMarkerAPI
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_jl_perftools_likwid_LikwidMarkerAPI_close
  (JNIEnv *env, jobject obj) {
  	likwid_markerClose();
  }

