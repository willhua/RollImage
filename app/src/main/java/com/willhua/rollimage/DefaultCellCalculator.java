package com.willhua.rollimage;

import android.graphics.RectF;

/**
 * Created by willhua on 2016/9/3.
 */
public class DefaultCellCalculator implements CellCalculator {

    private static final float WIDHT_INDENT = 0.05F;
    private static final int HEIGHT_INDENT = 5;

    private final int[] STATIC_ALPHA; //the alpha of every image when without move
    private final int mCnt;
    private final int mViewWidth;
    private final int mViewHeight;
    private final int mWidhtIndent;
    private final int[] mWidths; //width for every image when without move
    private final int mImageHeight; //height for every image when without move

    private float[] mAlphas; //alpha for every image
    private Cell[] mCells;

    public DefaultCellCalculator(int showCnt, int widht, int height){
        mCnt = showCnt + 1;
        mCells = new Cell[mCnt];
        mAlphas = new float[mCnt];
        STATIC_ALPHA = new int[mCnt];
        int alphaUnit = 255 / mCnt;
        for(int i = 0; i < mCnt; i++){
            STATIC_ALPHA[i] = i * alphaUnit;
        }


        mViewWidth = widht;
        mViewHeight = height;
        mWidhtIndent = (int)(WIDHT_INDENT * mViewWidth);
        mWidths = new int[mCnt];
        for(int i = 0; i < mCnt; i++){
            mWidths[i] = mViewWidth - i * mWidhtIndent;
        }
        //每张图片的高度。
        //假如显示四张图，那么在上面会有三个高度落差，然后最底部保留一个高度落差，所以是mcnt-1
        mImageHeight = mViewHeight - (mCnt - 1) * HEIGHT_INDENT;
        initCells();
    }

    @Override
    public Cell[] getCells() {
        return  mCells;
    }

    @Override
    public void setStatus(int direction, int distance) {
        if(distance > 0){
            calculateForward(distance);
        } else if(distance < 0){
            calculateBackward(distance);
        } else{
            initCells();
        }
    }

    @Override
    public void setDimen(int widht, int height) {

    }

    private void calculateForward(float status){
        float scale = status / mImageHeight;
        for(int i = 0; i < mCnt - 1; i++){
            mCells[i].setWidth(interpolate(scale, mWidths[i], mWidths[i + 1]));
            mCells[i].moveVertical(scale * HEIGHT_INDENT);
            mCells[i].setAlpha((int)interpolate(scale, STATIC_ALPHA[i], STATIC_ALPHA[i + 1]));
        }
        mCells[mCnt - 1].moveVertical(status);
        mCells[mCnt - 1].setAlpha((int)interpolate(scale, 255, 0));
    }

    private void calculateBackward(float status){
        float scale = Math.abs(status / mImageHeight);
        for(int i = 0; i < mCnt - 1; i++){
            mCells[i].setWidth(interpolate(scale, mWidths[i + 1], mWidths[i]));
            mCells[i].moveVertical(-scale * HEIGHT_INDENT);
            mCells[i].setAlpha((int)interpolate(scale, STATIC_ALPHA[i + 1], STATIC_ALPHA[i]));
        }
        mCells[mCnt - 1].resetRect();
        mCells[mCnt - 1].setHeight(mImageHeight);
        mCells[mCnt - 1].moveVertical(status);
        mCells[mCnt - 1].setAlpha((int)interpolate(scale, 0, 255));
    }

    /**
     * status without move
     */
    private void initCells(){
        int top = -HEIGHT_INDENT;
        for(int i = 0; i < mCnt; i++){
            RectF rectF = new RectF(0,0,0,0);
            rectF.top = top + i * HEIGHT_INDENT;
            rectF.bottom = rectF.top + mImageHeight;
            mCells[i] = new Cell(rectF, STATIC_ALPHA[i]);
            mCells[i].setWidth(mWidths[i]);
        }
    }

    private float interpolate(float scale, float start, float end){
        return start + scale * (end - start);
    }
}
