package com.willhua.rollimage;

import android.graphics.RectF;

/**
 * Created by willhua on 2016/9/3.
 */
public class Cell {
    private RectF mRectF;
    private int mAlpha;

    public Cell(){
        mRectF = new RectF();
        mAlpha = 0;
    }

    public Cell(RectF rectF, int alpha){
        mRectF = rectF;
        mAlpha = alpha;
    }

    public void setWidth(float width){
        float diff = (width - mRectF.width()) / 2;
        mRectF.left -= diff;
        mRectF.right += diff;
    }

    public void moveVertical(float value){
        mRectF.top += value;
        mRectF.bottom += value;
    }

    public void setRectF(RectF rectF){
        mRectF = rectF;
    }

    public void setAlpha(int alpha){
        mAlpha = alpha;
    }

    public RectF getmRectF(){
        return mRectF;
    }

    public int getAlpha(){
        return  mAlpha;
    }
}
