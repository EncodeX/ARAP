package com.edu.neu.navigation.View;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.edu.neu.navigation.R;
import com.edu.neu.navigation.util.Point;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;


/**
 * Extends great ImageView library by Dave Morrissey. See more:
 * https://github.com/davemorrissey/subsampling-scale-image-view.
 */
public class BlueDotView extends SubsamplingScaleImageView implements Target {
    private static final  String TAG ="BlueDotView";

    private float radius = 1f;
    private PointF dotCenter = null;

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setDotCenter(PointF dotCenter) {
        this.dotCenter = dotCenter;
    }

    public BlueDotView(Context context) {
        this(context, null);
    }

    public BlueDotView(Context context, AttributeSet attr) {
        super(context, attr);
        initialise();
    }

    private void initialise() {
        setWillNotDraw(false);
        setPanLimit(SubsamplingScaleImageView.PAN_LIMIT_CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isReady()) {
            return;
        }

        if (dotCenter != null) {
            //dotCenter = new PointF((float)290,(float)800);
            PointF vPoint = sourceToViewCoord(dotCenter);
            Log.d(TAG,"dotCenter.x"+dotCenter.x);
            Log.d(TAG,"dotCenter.x"+dotCenter.y);
            Log.d(TAG,""+vPoint.y);
            float scaledRadius = getScale() * radius ;
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(getResources().getColor(R.color.ia_blue));
            canvas.drawCircle(vPoint.x, vPoint.y, scaledRadius, paint);
            Log.d(TAG,"vpointx "+vPoint.x);
            Log.d(TAG,"vpointy "+vPoint.y);
            init(canvas,vPoint);

        }
    }


    private void init(Canvas canvas, PointF vPoint) {
        float scaledRadius = getScale() * radius ;
        //init points
        List<Point> poiList = new ArrayList<>();
        poiList.add(new Point((float)290,(float)913.5112,"504门口"));
        poiList.add(new Point((float)290,(float)550,"拐弯口"));
        poiList.add(new Point((float)2150,(float)550,"路口"));
        poiList.add(new Point((float)2150,(float)1100,"厕所"));
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
}
