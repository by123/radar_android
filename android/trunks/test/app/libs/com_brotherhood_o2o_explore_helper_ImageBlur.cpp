#include <com_skynet_android_radar_utils_ImageBlur.h>
#include <ImageBlur.c>
#include <android/log.h>


JNIEXPORT void JNICALL Java_com_brotherhood_o2o_explore_helper_ImageBlur_blurIntArray
(JNIEnv *env, jclass obj, jintArray arrIn, jint w, jint h, jint r)
{
	jint *pix;
	pix = env->GetIntArrayElements(arrIn, 0);
	if (pix == NULL)
		return;
	//Start
	pix = StackBlur(pix, w, h, r);
	//End
	//int size = w * h;
	//jintArray result = env->NewIntArray(size);
	//env->SetIntArrayRegion(result, 0, size, pix);
	env->ReleaseIntArrayElements(arrIn, pix, 0);
	//return result;
}
