/**
* Copyright (c) 2015 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
* EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
* and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
*/

#include "ar.hpp"
#include "renderer.hpp"
#include <jni.h>
#include <math.h>
#include <GLES2/gl2.h>
#include <android/log.h>
#include <string.h>

#define JNIFUNCTION_NATIVE(sig) Java_edu_neu_arap_activity_MainActivity_##sig

extern "C" {
    JNIEXPORT jboolean JNICALL JNIFUNCTION_NATIVE(nativeInit(JNIEnv* env, jobject object));
    JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeDestory(JNIEnv* env, jobject object));
    JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeInitGL(JNIEnv* env, jobject object));
    JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeResizeGL(JNIEnv* env, jobject object, jint w, jint h));
    JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeRender(JNIEnv* env, jobject obj));
    JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeRotationChange(JNIEnv* env, jobject obj, jboolean portrait));
};

namespace EasyAR {
namespace samples {

class HelloAR : public AR
{
public:
    HelloAR();
    ~HelloAR();
    virtual void initGL();
    virtual void resizeGL(int width, int height);
    virtual void render();
    void render(JNIEnv*, jobject);
    virtual bool clear();
private:
    Vec2I view_size;
    Renderer renderer;
    bool target_detected;
    bool target_last_state;
    bool new_image_captured;
    double fovyRadians;
    double fovRadians;
    char imageReplica[1382400];

    VideoRenderer* videoRenderer[3];
    int tracked_target;
    int active_target;
    int texid[3];
    ARVideo* video;
    VideoRenderer* video_renderer;
};

HelloAR::HelloAR()
{
    view_size[0] = -1;

    tracked_target = 0;
    active_target = 0;
    for(int i = 0; i < 3; ++i) {
        texid[i] = 0;
        videoRenderer[i] = new VideoRenderer;
    }
    video = NULL;
    video_renderer = NULL;
}

HelloAR::~HelloAR()
{
    for(int i = 0; i < 3; ++i) {
        delete videoRenderer[i];
    }
}

void HelloAR::initGL()
{
    renderer.init();
    augmenter_ = Augmenter();
    augmenter_.attachCamera(camera_);
    target_detected = false;
    target_last_state = false;
    new_image_captured = false;

    for(int i = 0; i < 3; ++i) {
        videoRenderer[i]->init();
        texid[i] = videoRenderer[i]->texId();
    }
}

void HelloAR::resizeGL(int width, int height)
{
    view_size = Vec2I(width, height);
}

void HelloAR::render()
{
    // 清屏
    glClearColor(0.f, 0.f, 0.f, 1.f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    // 获取当前帧
    Frame frame = augmenter_.newFrame();
    // 首次渲染重置大小？
    if(view_size[0] > 0){
        AR::resizeGL(view_size[0], view_size[1]);
        if(camera_ && camera_.isOpened())
            view_size[0] = -1;
    }
    // 绘制相机取景内容
    augmenter_.setViewPort(viewport_);
    augmenter_.drawVideoBackground();
    glViewport(viewport_[0], viewport_[1], viewport_[2], viewport_[3]);

    // 设置追踪

    if (target_last_state != target_detected && target_detected){
        ImageList imageList = frame.images();
//        __android_log_print(ANDROID_LOG_INFO, "EasyAR", "%d", imageList.size());
        Image image = imageList[0];

        const char * imageData = (const char*)image.data();

//        int size = sizeof(imageData);
//        char string[1280*720];
//        strncpy(imageReplica, imageData, 1382400);
        strcpy(imageReplica, imageData);

//        __android_log_print(ANDROID_LOG_INFO, "EasyAR", "成功了吗?\n");
//        __android_log_print(ANDROID_LOG_INFO, "EasyAR", "%d %d", image.width(), image.height());
//        __android_log_print(ANDROID_LOG_INFO, "EasyAR", "%d", strlen(imageData));
//        __android_log_print(ANDROID_LOG_INFO, "EasyAR", "%c", imageData[1382401]);
//        __android_log_print(ANDROID_LOG_INFO, "EasyAR", "%d %d %d %d", imageData[0], imageData[1], imageData[2], imageData[3]);

//        new_image_captured = true;
    }


//    ImageList imageList = frame.images();
////    __android_log_print(ANDROID_LOG_INFO, "EasyAR", "%d", imageList.size());
//    if (imageList.size() > 0){
//        Image image = imageList[0];
//
//        const char * imageData = (const char*)image.data();
//
////        int size = sizeof(imageData);
////        char string[1280*720];
////        strcpy(string, imageData);
//
////        __android_log_print(ANDROID_LOG_INFO, "EasyAR", "成功了吗?\n");
////        __android_log_print(ANDROID_LOG_INFO, "EasyAR", "%d %d", image.width(), image.height());
//        __android_log_print(ANDROID_LOG_INFO, "EasyAR", "%d", strlen(imageData));
////        __android_log_print(ANDROID_LOG_INFO, "EasyAR", "%s", imageData);
////        __android_log_print(ANDROID_LOG_INFO, "EasyAR", "%d %d %d %d", imageData[0], imageData[1], imageData[2], imageData[3]);
//    }

    target_last_state = target_detected;

    AugmentedTarget::Status status = frame.targets()[0].status();
    if(status == AugmentedTarget::kTargetStatusTracked){
//        __android_log_print(ANDROID_LOG_INFO, "EasyAR", "成功了吗\n");
        // Todo: 此处重要！
//        // 获取投影矩阵
//        Matrix44F projectionMatrix = getProjectionGL(camera_.cameraCalibration(), 0.2f, 500.f);
//        // 获取摄像机姿态
//        Matrix44F cameraview = getPoseGL(frame.targets()[0].pose());

//        Matrix44F rotateMatrix;
//        for(int i = 0;i<16;i++){
//            rotateMatrix.data[i] = 0;
//        }
//        rotateMatrix.data[0] = 1; rotateMatrix.data[5] = -1;
//        rotateMatrix.data[10] = -1; rotateMatrix.data[15] = 1;

//        for(int i = 1; i < 3; i++){
//            for (int j = 0; j < 4; j++){
//                cameraview.data[j*4 + i] = - cameraview.data[j*4 + i];
////                cameraview.data[i*4 + j] = - cameraview.data[i*4 + j];
//            }
//        }

//        ImageTarget target = frame.targets()[0].target().cast_dynamic<ImageTarget>();
//        renderer.render(projectionMatrix, cameraview, target.size());
//
//        CameraCalibration cameraCalibration = camera_.cameraCalibration();
//
//        Vec2I size = cameraCalibration.size();
//        Vec2F focalLength = cameraCalibration.focalLength();
//
//        fovyRadians = 2 * atan(0.5f * size.data[1] / focalLength.data[1]);
//        fovRadians = 2 * atan(0.5f * size.data[0] / focalLength.data[0]);
//
////        __android_log_print(ANDROID_LOG_INFO, "EasyARCamera", "focalLength: [%f, %f]", focalLength.data[0], focalLength.data[1]);
////        __android_log_print(ANDROID_LOG_INFO, "EasyARCamera", "size: [%d, %d]", size.data[0], size.data[1]);
////        __android_log_print(ANDROID_LOG_INFO, "EasyARCamera", "fovyRadians: %f, fovRadians: %f", fovRadians, fovyRadians);
//
////        __android_log_print(ANDROID_LOG_INFO, "EasyARCamera", "_______");
////        for(int i = 0; i<2;i++){
////            __android_log_print(ANDROID_LOG_INFO, "EasyARCamera", "[%f]", cameraCalibration.principalPoint().data[i]);
////        }
//
//        target_detected = true;


        /// Video ///
        int id = frame.targets()[0].target().id();
        if(active_target && active_target != id) {
            video->onLost();
            delete video;
            video = NULL;
            tracked_target = 0;
            active_target = 0;
        }
        if (!tracked_target) {
            if (video == NULL) {
                if(frame.targets()[0].target().name() == std::string("argame") && texid[0]) {
                    video = new ARVideo;
                    video->openVideoFile("video.mp4", texid[0]);
                    video_renderer = videoRenderer[0];
                }
                else if(frame.targets()[0].target().name() == std::string("namecard") && texid[1]) {
                    video = new ARVideo;
                    video->openTransparentVideoFile("transparentvideo.mp4", texid[1]);
                    video_renderer = videoRenderer[1];
                }
                else if(frame.targets()[0].target().name() == std::string("idback") && texid[2]) {
//                    video = new ARVideo;
//                    video->openStreamingVideo("http://7xl1ve.com5.z0.glb.clouddn.com/sdkvideo/EasyARSDKShow201520.mp4", texid[2]);
//                    video_renderer = videoRenderer[2];
                    target_detected = true;
                }
            }
            if (video) {
                video->onFound();
                tracked_target = id;
                active_target = id;
            }
        }
        Matrix44F projectionMatrix = getProjectionGL(camera_.cameraCalibration(), 0.2f, 500.f);
        Matrix44F cameraview = getPoseGL(frame.targets()[0].pose());
        ImageTarget target = frame.targets()[0].target().cast_dynamic<ImageTarget>();

        if (target_detected){
            renderer.render(projectionMatrix, cameraview, target.size());

            CameraCalibration cameraCalibration = camera_.cameraCalibration();

            Vec2I size = cameraCalibration.size();
            Vec2F focalLength = cameraCalibration.focalLength();

            fovyRadians = 2 * atan(0.5f * size.data[1] / focalLength.data[1]);
            fovRadians = 2 * atan(0.5f * size.data[0] / focalLength.data[0]);
        }

        if(tracked_target) {
            video->update();
            video_renderer->render(projectionMatrix, cameraview, target.size());
        }

    }else{
        target_detected = false;

        if (tracked_target) {
            video->onLost();
            tracked_target = 0;
        }
    }

}

void HelloAR::render(JNIEnv * env, jobject thiz) {

    jclass clazz = (*env).GetObjectClass(thiz);
    jmethodID method;

    if (!clazz){
        return;
    }

    method = (*env).GetMethodID(clazz, "onDetectionStateChanged", "(Z)V");

    if (!method){
        return;
    }

    if (target_detected){
        jfloatArray cameraArray = (*env).NewFloatArray(4 * 4);
        jfloatArray projectionArray = (*env).NewFloatArray(4*4);
        float* ptrCamera = (*env).GetFloatArrayElements(cameraArray, NULL);
        float * ptrProjection = (*env).GetFloatArrayElements(projectionArray, NULL);
        if (ptrCamera && ptrProjection){
            for(int i = 0; i<16; ++i){
                ptrCamera[i] = renderer.camera_data[i];
                ptrProjection[i] = renderer.projection_data[i];
            }

            ptrCamera[1] = -ptrCamera[1]; ptrCamera[2] = -ptrCamera[2];
            ptrCamera[5] = -ptrCamera[5]; ptrCamera[6] = -ptrCamera[6];
            ptrCamera[9] = -ptrCamera[9]; ptrCamera[10] = -ptrCamera[10];
            ptrCamera[13] = -ptrCamera[13]; ptrCamera[14] = -ptrCamera[14];

            (*env).ReleaseFloatArrayElements(cameraArray, ptrCamera, JNI_COMMIT);
            (*env).ReleaseFloatArrayElements(projectionArray, ptrProjection, JNI_COMMIT);
        }

        jmethodID methodSetCamera;

        methodSetCamera = (*env).GetMethodID(clazz, "onCameraDataChanged", "([F[FDD)V");

        if (methodSetCamera != NULL){
            (*env).CallVoidMethod(thiz, methodSetCamera, cameraArray, projectionArray, fovyRadians, fovRadians);
        }
    }

    if (new_image_captured){
        jcharArray dataArray = (*env).NewCharArray(1382400);
        jchar * ptrArray = (*env).GetCharArrayElements(dataArray, NULL);

        if (ptrArray){
            for(int i = 0 ; i< 1382400; ++i){
                ptrArray[i] = (jchar)imageReplica[i];
            }

            (*env).ReleaseCharArrayElements(dataArray, ptrArray, JNI_COMMIT);
        }

        jmethodID  methodSendImageData;

        methodSendImageData = (*env).GetMethodID(clazz, "onNewImageCaptured", "([C)V");

        if (methodSendImageData != NULL){
            (*env).CallVoidMethod(thiz, methodSendImageData, dataArray);
        }

        new_image_captured = false;
    }

    (*env).CallVoidMethod(thiz, method, target_detected);
}

    bool HelloAR::clear()
    {
        AR::clear();
        if(video){
            delete video;
            video = NULL;
            tracked_target = 0;
            active_target = 0;
        }
        return true;
    }

}
}
EasyAR::samples::HelloAR ar;

JNIEXPORT jboolean JNICALL JNIFUNCTION_NATIVE(nativeInit(JNIEnv*, jobject))
{
    // 读取tracker信息
    jboolean status = (jboolean)ar.initCamera();
    ar.loadFromJsonFile("targets.json", "argame");
    ar.loadFromJsonFile("targets.json", "idback");
    ar.loadAllFromJsonFile("targets2.json");
    ar.loadFromImage("namecard.jpg");
    status &= ar.start();

    return status;
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeDestory(JNIEnv*, jobject))
{
    ar.clear();
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeInitGL(JNIEnv*, jobject))
{
    // 在static函数中使用GetObjectClass会返回Class类 得不到目标类
    ar.initGL();
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeResizeGL(JNIEnv*, jobject, jint w, jint h))
{
    ar.resizeGL(w, h);
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeRender(JNIEnv* env, jobject thiz))
{
    ar.render();
    ar.render(env, thiz);
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeRotationChange(JNIEnv*, jobject, jboolean portrait))
{
    ar.setPortrait(portrait);
}
