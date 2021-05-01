package com.example.homework5;

import android.animation.ObjectAnimator;
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
import android.view.animation.Animation;
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
    ArrayList<Integer> order;


    public MyCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        activePaths = new HashMap<Integer, PathWrap>();
        inactivePaths = new ArrayList<PathWrap>();
        imagesArray = new ArrayList<ImageView>();
        order = new ArrayList<Integer>();

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

        order.add(1); // 1 == paint, 0 == image
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
        if (order.size() > 0) {
            if (order.get(order.size() - 1) == 1) { // last was paint
                if (activePaths.size() > 0) {
                    activePaths.remove(activePaths.size() - 1);
                } else if (inactivePaths.size() > 0) {
                    inactivePaths.remove(inactivePaths.size() - 1);
                }
            }
            else {
                ImageView imageView = imagesArray.get(imagesArray.size() - 1);
                imageView.setVisibility(INVISIBLE);
                imageView.clearAnimation();
                imagesArray.remove(imagesArray.size() - 1);
            }
            order.remove(order.size() - 1);
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
    }

    public void onLongPresss(MotionEvent event) {
        addImage(event, 1);
    }

    private void addImage(MotionEvent event, int type) {
        order.add(0);
        ImageView imageView = new ImageView(getContext());
        imageView.setX(event.getX() - 85);
        imageView.setY(event.getY() - 85);

        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(200, 200));
        cameraActivity.constraintLayout.addView(imageView);
        imagesArray.add(imageView);


        if (type == 0) {
            imageView.setBackgroundResource(R.drawable.kidao);
            ObjectAnimator imageViewAnimator = ObjectAnimator.ofFloat(imageView,View.ROTATION, 20f);
            imageViewAnimator.setRepeatCount(Animation.INFINITE);
            imageViewAnimator.setRepeatMode(ObjectAnimator.REVERSE);
            imageViewAnimator.setDuration(100);
            imageViewAnimator.start();
        }
        else {
            imageView.setBackgroundResource(R.drawable.hokies);
            ObjectAnimator imageViewAnimator = ObjectAnimator.ofFloat(imageView,View.ROTATION, 360);
            imageViewAnimator.setRepeatCount(Animation.INFINITE);
            imageViewAnimator.setRepeatMode(ObjectAnimator.REVERSE);
            imageViewAnimator.setDuration(2000);
            imageViewAnimator.start();
        }
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
