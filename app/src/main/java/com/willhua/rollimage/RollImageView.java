package com.willhua.rollimage;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willhua on 2016/9/3.
 */
public class RollImageView extends View {


    public static final int SHOW_CNT = 4;
    private static final int DEFALT_WIDHT = 200;
    private static final int DEFALT_HEIGHT = 120;


    private ImageLoader mImageLoader;
    private Bitmap[] mBitmaps; //bitmaps shown current
    private CellCalculator mCellCalculator;
    private Cell[] mCells;

    //实际绘制区域大小
    private int mWidth = DEFALT_WIDHT;
    private int mHeight = DEFALT_HEIGHT;

    public RollImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void dataInit() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> paths = queryImages();
                mImageLoader = new DefaultImageLoader(SHOW_CNT, paths);

            }
        }).start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heihtMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heihtMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heihtMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heihtMeasureSpec);

        int paddX = getPaddingLeft() + getPaddingRight();
        int paddY = getPaddingTop() + getPaddingBottom();
        mWidth = width - paddX;
        mHeight = height - paddY;
        if (widthMode == MeasureSpec.AT_MOST
                && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(DEFALT_WIDHT, DEFALT_HEIGHT);
            mWidth = DEFALT_WIDHT - paddX;
            mHeight = DEFALT_HEIGHT - paddY;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(DEFALT_WIDHT, height);
            mWidth = DEFALT_WIDHT - paddX;
            mHeight = height - paddY;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width, DEFALT_HEIGHT);
            mWidth = width - paddX;
            mHeight = DEFALT_HEIGHT - paddY;
        }
        if(mCellCalculator != null){
            mCellCalculator.setDimen(mWidth, mHeight);
        }
        LOG("onmeasure mwidth:" + mWidth + " mheight:" + mHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setImageLoader(ImageLoader loader) {
        if (loader != null) {
            mImageLoader = loader;
        }
    }

    public void setCellCalculator(CellCalculator cellCalculator) {
        if (cellCalculator != null) {
            mCellCalculator = cellCalculator;
            mCellCalculator.setDimen(mWidth, mHeight);
        }
    }

    private List<String> queryImages(){
        Cursor cursor = getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.ImageColumns.DATA},  null, null, null);
        List<String> paths = new ArrayList<>();
        try {
            while(cursor.moveToNext()){
                String path = cursor.getString(0);
                if(path != null){
                    paths.add(path);
                }
            }

        } catch (Exception e){

        }
        return  paths;
    }

    private void LOG(String msg){
        Log.d("RollImageView","willhua:  " + msg);
    }


}
