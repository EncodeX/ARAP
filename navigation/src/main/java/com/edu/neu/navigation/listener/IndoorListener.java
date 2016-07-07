package com.edu.neu.navigation.listener;

import android.graphics.Canvas;
import android.graphics.PointF;

import com.edu.neu.navigation.Enum.Instruction;
import com.edu.neu.navigation.util.Point;

import java.util.List;

/**
 * Created by Administrator on 2016/6/28.
 */

public interface IndoorListener {
    void onLocationChange(Point p, int index);
    void onFloorPlanReadyToDownload(String url);
    void onDrawReady(Canvas c, PointF p);
    void onNavigationReady(List<Instruction> instructions);
}
