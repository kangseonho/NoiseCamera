#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>

using namespace cv;
extern "C"
JNIEXPORT void JNICALL
Java_com_hada_noisecamera_MainActivity_ConvertRGBtoGray(JNIEnv *env, jobject thiz,
                                                        jlong mat_addr_input,
                                                        jlong mat_addr_result) {
    Mat &matInput = *(Mat *)mat_addr_input;
    Mat &matResult = *(Mat *)mat_addr_result;

//    cvtColor(matInput, matResult, COLOR_RGB2BGR);
    cv::rotate(matInput,matResult,cv::ROTATE_90_CLOCKWISE);
//    cvtColor(matInput, matResult, COLOR_RGB2HSV);

//    fastNlMeansDenoisingColored(matInput,matResult,10,10,7,21);
}extern "C"
JNIEXPORT void JNICALL
Java_com_hada_noisecamera_MainActivity_ConvertNoise(JNIEnv *env, jobject thiz, jlong mat_addr_input,
                                                    jlong mat_addr_result, jint idx) {
    // TODO: implement ConvertNoise()
    Mat &matInput = *(Mat *)mat_addr_input;
    Mat &matResult = *(Mat *)mat_addr_result;

    fastNlMeansDenoisingColored(matInput,matResult,idx,10,7,21);

}