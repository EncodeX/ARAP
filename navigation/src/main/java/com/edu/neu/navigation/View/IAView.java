package com.edu.neu.navigation.View;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.edu.neu.navigation.R;
import com.edu.neu.navigation.listener.IndoorListener;
import com.edu.neu.navigation.util.NavigationUtil;
import com.edu.neu.navigation.util.Point;
import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;
import com.indooratlas.android.sdk.IARegion;
import com.indooratlas.android.sdk.resources.IAFloorPlan;
import com.indooratlas.android.sdk.resources.IALatLng;
import com.indooratlas.android.sdk.resources.IAResourceManager;
import com.indooratlas.android.sdk.resources.IAResult;
import com.indooratlas.android.sdk.resources.IAResultCallback;
import com.indooratlas.android.sdk.resources.IATask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/6/28.
 */

public class IAView extends SubsamplingScaleImageView implements Target, SensorEventListener {
    private static final  String TAG ="IAView";
    private IndoorListener indoorLocationListener;
    Context context;
    IAView iaView ;

    public IAView getIaView() {
        return iaView;
    }

    private IALocationManager mIALocationManager;
    private IALocationListener mIALocationListener;
    private IAResourceManager mIAResourceManager;
    private IALocation mIALocation;
    private IATask<IAFloorPlan> mPendingAsyncResult;
    private IAFloorPlan mIAFloorPlan;
    private IARegion.Listener mRegionListener;
    // blue dot radius in meters
    private static final float dotRadius = 1.0f;

    double latitude;
    double longitude;

    private float radius = 1f;
    private PointF dotCenter = null;


    private double currentDegree;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private int mTargetIndex = 0;

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setDotCenter(PointF dotCenter) {
        this.dotCenter = dotCenter;
    }

    public IAView(Context context) {
        this(context, null);
    }

    public IAView(Context context, AttributeSet attr) {
        super(context, attr);

        this.context = context;
        initIndoorSDK();
        initialise();

        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    }
    private Point nearPoint;
    private PointF point;
    private void initIndoorSDK()
    {
        iaView= this;
        Bundle bundle = new Bundle(2);
        bundle.putString(IALocationManager.EXTRA_API_KEY, "412a56b4-9a1f-4781-96e4-85c127981252");
        bundle.putString(IALocationManager.EXTRA_API_SECRET, "nK4QXcXp7cKKNvnWhfTWdWENj6s2HoMUtNTG2RrhWKtb0Mugx8gdc2ZWs5SIQEGfU+KALtQqfnu7b40JWtXOhiigcGQ+DWyXZ67kSHjp3X0+ACuim//7aQSP6FokDg==");

        mIALocationManager = IALocationManager.create(context, bundle);
        mIALocationListener = new IALocationListener() {
            @Override
            public void onLocationChanged(IALocation iaLocation) {
                Log.i(TAG, "Latitude: " + iaLocation.getLatitude());
                Log.i(TAG, "Longitude: " + iaLocation.getLongitude());
                latitude = iaLocation.getLatitude();
                longitude = iaLocation.getLongitude();
                if (this != null && isReady()) {
                    IALatLng latLng = new IALatLng(iaLocation.getLatitude(), iaLocation.getLongitude());
                    point= mIAFloorPlan.coordinateToPoint(latLng);
                    Log.d(TAG,"测试"+point.x);
                    Log.d(TAG,"测试"+point.y);

                    if(nearPoint!=null)
                    {
                        indoorLocationListener.onLocationChange(nearPoint,poiIndex);
                    }

                    setDotCenter(point);

                    postInvalidate();
                }


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.i("IALocation", "onStatusChanged");

                switch (i) {
                    case IALocationManager.STATUS_AVAILABLE:
                        Log.i("IALocation", "STATUS_AVAILABLE");
                        break;
                    case IALocationManager.STATUS_CALIBRATION_CHANGED:
                        Log.i("IALocation", "STATUS_CALIBRATION_CHANGED");
                        break;
                    case IALocationManager.STATUS_LIMITED:
                        Log.i("IALocation", "STATUS_LIMITED");
                        break;
                    case IALocationManager.STATUS_OUT_OF_SERVICE:
                        Log.i("IALocation", "STATUS_OUT_OF_SERVICE");
                        break;
                    case IALocationManager.STATUS_TEMPORARILY_UNAVAILABLE:
                        Log.i("IALocation", "STATUS_TEMPORARILY_UNAVAILABLE");
                        break;
                }
            }
        };

        mRegionListener = new IARegion.Listener() {
            @Override
            public void onEnterRegion(IARegion region) {
//				if (region.getType() == IARegion.TYPE_FLOOR_PLAN) {
//					fetchFloorPlan(region.getId());
//				}
                Log.i("IALocation", "Region Entered");
            }

            @Override
            public void onExitRegion(IARegion region) {
                // leaving a previously entered region
            }
        };

        mIAResourceManager = IAResourceManager.create(context);

        String floorId ="02588c9d-6c01-45ad-9528-ad244b9a2fce";

        //String floorId ="0638f62a-38d3-4cd5-ae46-c067b21bfbca";
        mIALocation = IALocation.from(IARegion.floorPlan(floorId));
        Log.d("IALocation", "fetching floor plan...");
        fetchFloorPlan(floorId);
    }
    private void fetchFloorPlan(String id) {

        cancelPendingNetworkCalls();
        final IATask<IAFloorPlan> asyncResult = mIAResourceManager.fetchFloorPlanWithId(id);
        mPendingAsyncResult = asyncResult;
        if (mPendingAsyncResult != null) {
            mPendingAsyncResult.setCallback(new IAResultCallback<IAFloorPlan>() {
                @Override
                public void onResult(IAResult<IAFloorPlan> result) {
                    Log.d("IALocation", "fetch floor plan result:" + result);
                    if (result.isSuccess() && result.getResult() != null) {
                        mIAFloorPlan = result.getResult();

                        setRadius(mIAFloorPlan.getMetersToPixels() * dotRadius);


                        indoorLocationListener.onFloorPlanReadyToDownload(mIAFloorPlan.getUrl());
                        Picasso.with(context)
                                .load(mIAFloorPlan.getUrl())
                                .into(iaView);
                    } else {
                        // do something with error
                        if (!asyncResult.isCancelled()) {
                            Log.i("IALocation", result.getError() != null
                                    ? "error loading floor plan: " + result.getError()
                                    : "access to floor plan denied");
                        }
                    }
                }
            }, Looper.getMainLooper()); // deliver callbacks in main thread
        }
    }

    private void cancelPendingNetworkCalls() {
        if (mPendingAsyncResult != null && !mPendingAsyncResult.isCancelled()) {
            mPendingAsyncResult.cancel();
        }
    }

    private void initialise() {
        setWillNotDraw(false);
        setPanLimit(SubsamplingScaleImageView.PAN_LIMIT_CENTER);
       // inflate(this,)
    }
    private Canvas c;
    private PointF p;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isReady()) {
            return;
        }

        if (dotCenter != null) {
            //dotCenter = new PointF((float)290,(float)910);
            PointF vPoint = sourceToViewCoord(dotCenter);
            c = canvas;
            p = vPoint;
            Log.d(TAG,"dotCenter.x"+dotCenter.x);
            Log.d(TAG,"dotCenter.x"+dotCenter.y);
            Log.d(TAG,""+vPoint.y);
            float scaledRadius = getScale() * radius ;
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(getResources().getColor(R.color.ia_blue));
            canvas.drawCircle(vPoint.x, vPoint.y, scaledRadius, paint);
//            Log.d(TAG,"vpointx "+vPoint.x);
//            Log.d(TAG,"vpointy "+vPoint.y);
            //initPoints(canvas,vPoint);
            indoorLocationListener.onDrawReady(canvas,vPoint);

            //initNavigationTriangle(canvas,vPoint);

        }
    }


    private List<Point> poiList;
    //目前位置在pioList中第几个
    private int poiIndex;

    public List<Point> getPoiList() {
        return poiList;
    }
    public void setPoiList(List<Point> poiList) {
        List<Point> pList = new ArrayList<>();
        for (Point p:poiList) {
            IALatLng latLng = new IALatLng(p.getLatitude(), p.getLongitude());
            PointF floorPoint= mIAFloorPlan.coordinateToPoint(latLng);
            p.setX(floorPoint.x);
            p.setY(floorPoint.y);
            pList.add(p);
        }
        //传值为经纬度
        this.poiList = pList;
        //传值为图上坐标
        //this.poiList =poiList;

        initPoints(c,p);
        nearPoint=nearSomePoint(new Point(point.x,point.y,"当前位置"));
        //nearPoint=nearSomePoint(new Point((float)290,(float)910,"当前位置"));
    }


    private Point nearSomePoint(Point currentPoint)
    {
        float distance_limit =40;
        List<Point> poiList= getPoiList();
        for (Point p:poiList) {
            float xDistance =currentPoint.getX()-p.getX();
            float yDistance =currentPoint.getY()-p.getY();
            Log.d(TAG,"xDistance "+ xDistance);
            Log.d(TAG,"yDistance "+ yDistance);
            float distance=(currentPoint.getX()-p.getX())*(currentPoint.getX()-p.getX())+(currentPoint.getY()-p.getY())*(currentPoint.getY()-p.getY());

            if(distance<(distance_limit*distance_limit))
            {
                Log.d(TAG,"distance "+ distance);
                return  p;
            }
        }
        return null;
    }

    private void initPoints(Canvas canvas, PointF vPoint) {
        float scaledRadius = getScale() * radius ;
        //init points
        List<Point> poiList = getPoiList();

        List<PointF> pathList = new ArrayList<>();
        Paint anotherPaint = new Paint();
        anotherPaint.setAntiAlias(true);
        anotherPaint.setStyle(Paint.Style.FILL);
        anotherPaint.setColor(getResources().getColor(R.color.ia_red));
        for (Point p:poiList) {
            PointF ePoint=sourceToViewCoord(new PointF(p.getX(),p.getY()));
            pathList.add(ePoint);
            canvas.drawCircle(ePoint.x, ePoint.y, scaledRadius, anotherPaint);
        }
        int total_points=pathList.size();
        int base_point =getBase(vPoint,pathList);
        poiIndex =base_point;
        PointF base = new PointF(vPoint.x,vPoint.y);
        PointF start =base;
        for(int i =0;i<total_points;i++)
        {

            if(i>=base_point)
            {

                canvas.drawLine(start.x,start.y,pathList.get(i).x,pathList.get(i).y,anotherPaint);
                start=pathList.get(i);

            }
        }
    }
    private void initNavigationTriangle(Canvas canvas, PointF vPoint) {
        double angle =30*Math.PI/180;
        int length=30;
        int vertical=20;

        PointF a = new PointF((float)(vPoint.x+(length+vertical)*Math.sin(angle)),(float)(vPoint.y+(length+vertical)*Math.cos(angle)));

        PointF b = new PointF((float)(vPoint.x+(length)*Math.sin(angle)-vertical*Math.cos(angle)),(float)(vPoint.y+length*Math.cos(angle)+vertical*Math.sin(angle)));

        PointF c = new PointF((float)(vPoint.x+length*Math.sin(angle)+vertical*Math.cos(angle)),(float)(vPoint.y+length*Math.cos(angle)-vertical*Math.sin(angle)));
        float scaledRadius = getScale() * radius *0.5f;
        Path path = new Path();
        path.moveTo(a.x,a.y);
        path.lineTo(b.x,b.y);
        path.lineTo(c.x,c.y);
        path.close();

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.ia_red));
        canvas.drawPath(path, paint);



    }

    private int getBase(PointF vPoint, List<PointF> pathList) {
        int position=0;
        int total = pathList.size();
        for(int i =0;i<total;i++)
        {
            if(i-1>=0)
            {
                if(withInRectangle(pathList.get(i-1),pathList.get(i),vPoint))
                {
                    position=i;
                }
            }
        }
        return position;
    }
    private boolean withInRectangle( PointF start, PointF end,PointF target) {
        float error =0.5f;
        float maxX =Math.max(start.x,end.x);
        float minX =Math.min(start.x,end.x);
        float maxY =Math.max(start.y,end.y);
        float minY =Math.min(start.y,end.y);
        if(maxX-minX<error)
        {
            minX=minX-error;
            maxX=maxX+error;
        }
        if(maxY-minY<error)
        {
            minY=minY-error;
            maxY=maxY+error;
        }
        if(target.x<=maxX&&target.x>=minX&&target.y<=maxY&&target.y>=minY)
        {
            return  true;
        }

        else
        {
            return false;
        }

    }



    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        setImage(ImageSource.bitmap(bitmap));
    }


    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
    public void setIndoorLocationListener(IndoorListener indoorLocationListener) {
        this.indoorLocationListener = indoorLocationListener;
    }
    public void initResume()
    {
        mIALocationManager.requestLocationUpdates(IALocationRequest.create(), mIALocationListener);
        mIALocationManager.registerRegionListener(mRegionListener);

        mIALocationManager.setLocation(mIALocation);

        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);  //为传感器注册监听器
    }
    public void initPause()
    {
        mIALocationManager.removeLocationUpdates(mIALocationListener);
        mIALocationManager.unregisterRegionListener(mRegionListener);
        mSensorManager.unregisterListener(this);
    }
    public void initDestroy()
    {
        mIALocationManager.destroy();
    }



    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            currentDegree = sensorEvent.values[0];
        }

        Log.i("IndoorMapActivity", "Navigation Ready");
        NavigationUtil util = new NavigationUtil();
        if(p !=null){
            List ins = util.getNavigation(new Point(latitude,longitude,p.x,p.y,"当前位置"), getPoiList().get(mTargetIndex),currentDegree);

            if(indoorLocationListener!=null){
                indoorLocationListener.onNavigationReady(ins);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}
