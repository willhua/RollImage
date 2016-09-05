package com.willhua.rollimage;

import android.graphics.RectF;

/**
 * Created by willhua on 2016/9/3.
 */
public class Cell {
    private RectF mRectF;
    private int mAlpha;

    private float top;
    private float bottom;

    public Cell(){
        mRectF = new RectF();
        mAlpha = 0;
        init();
    }

    public Cell(RectF rectF, int alpha){
        mRectF = rectF;
        mAlpha = alpha;
        init();
    }

    public void setWidth(float width){
        float diff = (width - mRectF.width()) / 2;
        mRectF.left -= diff;
        mRectF.right += diff;
    }

    private void init(){
        top = mRectF.top;
        bottom = mRectF.bottom;
    }

    public void moveVertical(float value){
    //    float diff = (width - mRectF.width()) / 2;
        mRectF.top = top + value;
        mRectF.bottom = bottom + value;
    }

    public void setHeight(float height){
        mRectF.bottom = mRectF.top + height;
    }

    public void resetRect(){
        mRectF.top = 0;
        mRectF.bottom = 0;
        mRectF.left = 0;
        mRectF.right = 0;
        init();
    }

    public void setRectF(RectF rectF){
        mRectF = rectF;
        init();
    }

    public void setAlpha(int alpha){
        mAlpha = alpha;
    }

    public RectF getRectF(){
        return mRectF;
    }

    public int getAlpha(){
        return  mAlpha;
    }
}
