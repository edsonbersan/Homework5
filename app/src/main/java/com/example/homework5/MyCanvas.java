package com.example.homework5;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.Image;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.GestureDetectorCompat;

import java.util.ArrayList;
import java.util.HashMap;


public class MyCanvas extends View {

    private class PathWrap {
        public Path path;
        public int color;

        public PathWrap(Path path, int color) {
            this.path = path;
            this.color = color;
        }

    }

    ArrayList<ImageView> imagesArray;
    GestureDetectorCompat gestureDetectorCompat;
    private int color;
    ArrayList<PathWrap> inactivePaths;
    HashMap <Integer, PathWrap> activePaths;
    Paint paint;
    CameraActivity cameraActivity;


    public MyCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        activePaths = new HashMap<Integer, PathWrap>();
        inactivePaths = new ArrayList<PathWrap>();
        imagesArray = new ArrayList<ImageView>();

        gestureDetectorCompat = new GestureDetectorCompat(getContext(), new MyGestureListener());

        // SETTING PAINT COLORS //////
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(30);

        color = Color.RED;
        paint.setColor(color);
    }

    public void setActivity(CameraActivity cameraActivity) {
        this.cameraActivity = cameraActivity;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for(PathWrap pathWrap: activePaths.values()){
            paint.setColor(pathWrap.color);
            canvas.drawPath(pathWrap.path, paint);
        }
        for(PathWrap pathWrap: inactivePaths){
            paint.setColor(pathWrap.color);
            canvas.drawPath(pathWrap.path, paint);
        }
        super.onDraw(canvas);
    }

    public void addPath(int id, float x, float y) {
        Path path = new Path();
        path.moveTo(x, y);
        if (activePaths.containsKey(id)) {
            inactivePaths.add(activePaths.get(id));
        }

        activePaths.put(id, new PathWrap(path, this.color));
        invalidate();
    }

    public void updatePath(int id, float x, float y) {
        PathWrap pathWrap = activePaths.get(id);
        Path path = pathWrap.path;
        if(path != null){
            path.lineTo(x, y);
        }
        invalidate();
    }

    public void undoPath() {
        if (activePaths.size() > 0) {
            activePaths.remove(activePaths.size() - 1);
        }
        else if (inactivePaths.size() > 0) {
            inactivePaths.remove(inactivePaths.size() - 1);
        }
        invalidate();
    }

    public void clearPath() {
        activePaths.clear();
        inactivePaths.clear();
        for (ImageView im : imagesArray) {
            im.setVisibility(INVISIBLE);
        }
        invalidate();
    }

    public void setPaintFieldColor(int color) {
        this.color = color;
        invalidate();
    }

    public void onDoubleTapp(MotionEvent event) {
        addImage(event, 0);
        undoPath();
    }

    public void onLongPresss(MotionEvent event) {
        addImage(event, 1);
    }

    private void addImage(MotionEvent event, int type) {
        ImageView imageView = new ImageView(getContext());
        imageView.setX(event.getX() - 85);
        imageView.setY(event.getY() - 85);

        if (type == 0) {
            imageView.setBackgroundResource(R.drawable.kidao);
        }
        else {
            imageView.setBackgroundResource(R.drawable.hokies);
        }

        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(200, 200));
        cameraActivity.constraintLayout.addView(imageView);
        imagesArray.add(imageView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetectorCompat.onTouchEvent(event);
        int maskedAction = event.getActionMasked();
        switch(maskedAction){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                for(int i= 0, size = event.getPointerCount(); i< size; i++){
                    int id = event.getPointerId(i);
                    addPath(id, event.getX(i), event.getY(i));
                }
                break;
            case MotionEvent.ACTION_MOVE:
                for(int i= 0, size = event.getPointerCount(); i< size; i++){
                    int id = event.getPointerId(i);
                    updatePath(id, event.getX(i), event.getY(i));
                }
                break;
        }
        invalidate();
        return true;
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            onDoubleTapp(e);
            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            onLongPresss(e);
            super.onLongPress(e);
        }
    }
}
