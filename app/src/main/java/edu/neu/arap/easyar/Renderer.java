/**
* Copyright (c) 2015 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
* EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
* and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
*/

package edu.neu.arap.easyar;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import edu.neu.arap.activity.EasyARActivity;

public class Renderer implements GLSurfaceView.Renderer {

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        EasyARActivity.nativeInitGL();
    }

    public void onSurfaceChanged(GL10 gl, int w, int h) {
        EasyARActivity.nativeResizeGL(w, h);
    }

    public void onDrawFrame(GL10 gl) {
        EasyARActivity.nativeRender();
    }

}
