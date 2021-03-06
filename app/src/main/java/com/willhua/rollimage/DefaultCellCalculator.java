package com.willhua.rollimage;

import android.graphics.RectF;
import android.util.Log;

/**
 * Created by willhua on 2016/9/3.
 */
public class DefaultCellCalculator implements CellCalculator {

    private static final float WIDHT_INDENT = 0.08F;
    private static final int HEIGHT_INDENT = 15;

    private final int[] STATIC_ALPHA; //the alpha of every image when standing still
    private final int FIRST_ALPHA = 180; //the first image aplpha when standing still
    private final int mCnt;
    private int mViewWidth;
    private int mViewHeight;
    private int mWidhtIndent;
    private int[] mWidths; //width for every image when standing still
    private int mImageHeight; //height for every image when standing still

    private float[] mAlphas; //alpha for every image
    private Cell[] mCells;

    public DefaultCellCalculator(int showCnt){
        mCnt = showCnt;
        mCells = new Cell[mCnt];
        mAlphas = new float[mCnt];
        STATIC_ALPHA = new int[mCnt];
        STATIC_ALPHA[mCnt - 1] = 0;
        int alphaUnit = (255 - FIRST_ALPHA) / (mCnt - 2);
        for(int i = mCnt - 2; i >= 0; i--){
            STATIC_ALPHA[i] = FIRST_ALPHA + (mCnt - 2 - i) * alphaUnit;
        }
    }

    @Override
    public Cell[] getCells() {
        return  mCells;
    }

    @Override
    public int setStatus(float distance) {
        if(distance > 0){
            return calculateForward(distance);
        } else if(distance < 0){
            return calculateBackward(distance);
        } else{
            initCells();
        }
        return 0;
    }

    @Override
    public void setDimen(int widht, int height) {
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
        LOG("mImageHeight " + mImageHeight);
        initCells();
    }

    @Override
    public void setStatic() {
        initCells();
    }

    private int calculateForward(float status){
        float scale = status / mImageHeight;
        LOG("scale " + scale + " mImageHeight " + mImageHeight + " status " + status);
        for(int i = 1; i < mCnt; i++){
            mCells[i].setWidth(interpolate(scale * 3, mWidths[i], mWidths[i - 1])); // *3 使得后面的宽度快速增大，经验值
            mCells[i].moveVertical(interpolate(scale * 10, 0, HEIGHT_INDENT));  //*10使得后面的图片迅速向前，向前的动画感更强
            mCells[i].setAlpha((int)interpolate(scale, STATIC_ALPHA[i], STATIC_ALPHA[i - 1]));
        }
        mCells[0].moveVertical(status);
        mCells[0].setAlpha((int)interpolate(scale, 255, 0));
        if(status >= mImageHeight / 3){
            return ROLL_FORWARD;
        } else {
            return 0;
        }
    }

    private int calculateBackward(float status){
        float scale = Math.abs(status / mImageHeight);
        for(int i = 1; i < mCnt; i++){
            mCells[i].setWidth(interpolate(scale * 3, mWidths[i - 1], mWidths[i]));
            mCells[i].moveVertical(interpolate(scale * 10, 0, HEIGHT_INDENT));
            mCells[i].setAlpha((int)interpolate(scale, STATIC_ALPHA[i - 1], STATIC_ALPHA[i]));
        }
        mCells[0].resetRect();
        mCells[0].setWidth(mWidths[0]);
        mCells[0].setHeight(mImageHeight);
        mCells[0].moveVertical(mImageHeight + status);
        mCells[0].setAlpha((int)interpolate(scale, 0, 255));
        if(-status >= mImageHeight / 3){
            return ROLL_BACKWARD;
        } else {
            return 0;
        }
    }

    /**
     * status without move
     */
    private void initCells(){
        int top = -HEIGHT_INDENT;
        for(int i = 0; i < mCnt; i++){
            RectF rectF = new RectF(0,0,0,0);
            rectF.top = top + (mCnt - 1 - i) * HEIGHT_INDENT;
            rectF.bottom = rectF.top + mImageHeight;
            mCells[i] = new Cell(rectF, STATIC_ALPHA[i]);
            mCells[i].setWidth(mWidths[i]);
        }
    }

    private float interpolate(float scale, float start, float end){
        if(scale > 1){
            scale = 1;
        } else if(scale < -1){
            scale = -1;
        }
        return start + scale * (end - start);
    }

    private void LOG(String msg){
        Log.d("DefaultCellCalculator", "willhua: " + msg);
    }
}
