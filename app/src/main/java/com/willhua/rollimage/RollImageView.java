package com.willhua.rollimage;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willhua on 2016/9/3.
 */
public class RollImageView extends View {


    public static final int SHOW_CNT = 5;
    private static final int MSG_INVALATE = 0;
    private static final int DEFALT_WIDHT = 200;
    private static final int DEFALT_HEIGHT = 120;


    private List<String> mAllImagePaths;

    private ImageLoader mImageLoader;
    private Bitmap[] mBitmaps; //bitmaps shown current
    private CellCalculator mCellCalculator;
    private Cell[] mCells;

    //实际绘制区域大小
    private int mWidth = DEFALT_WIDHT;
    private int mHeight = DEFALT_HEIGHT;

    private Paint mPaint;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INVALATE:
                    invalidate();
                    break;
                default:
                    break;
            }
        }
    };

    public RollImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
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
        if (mCellCalculator != null) {
            mCellCalculator.setDimen(mWidth, mHeight);
        }
        if (mImageLoader != null) {
            mImageLoader.setDimen(mWidth, mHeight);
        }
        LOG("onmeasure mwidth:" + mWidth + " mheight:" + mHeight);
    }

    private float mDownX = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() > 1) {
            return false;
        }
        float x = event.getX();
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                float diff = x - mDownX;
                if (diff > 0) {
                    mCellCalculator.setStatus(CellCalculator.FORWARD, diff);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mCellCalculator.setStatic();
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        LOG("ondraw");
        canvas.drawARGB(255, 0, 0, 0);
        Bitmap[] bitmaps = mImageLoader.getBitmap(ImageLoader.SAMLL);
        Cell[] cells = mCellCalculator.getCells();
        canvas.translate(getWidth() / 2, 0);
        for (int i = 0; i < SHOW_CNT; i++) {
            Bitmap bitmap = bitmaps[i];
            Cell cell = cells[i];
            if (bitmap != null && !bitmap.isRecycled()) {
                mPaint.setAlpha(cell.getAlpha());
                LOG("ondraw " + i + bitmap.getWidth() + " " + cell.getRectF() + " alpha " + cell.getAlpha());
                canvas.drawBitmap(bitmap, null, cell.getRectF(), mPaint);
            }
        }
    }

    public void setImagePaths(List<String> paths) {
        if (paths != null) {
            mAllImagePaths = paths;
            if (mImageLoader != null) {
                mImageLoader.setImagePaths(mAllImagePaths);
            }
        }
    }

    public void setImageLoader(ImageLoader loader) {
        if (loader != null) {
            mImageLoader = loader;
            mImageLoader.setRefresh(new ImageLoader.Refresh() {
                @Override
                public void refresh() {
                    LOG("refresh");
                    mHandler.sendEmptyMessage(MSG_INVALATE);
                }
            });
            if (mAllImagePaths != null) {
                mImageLoader.setImagePaths(mAllImagePaths);
            }
        }
    }

    public void setCellCalculator(CellCalculator cellCalculator) {
        if (cellCalculator != null) {
            mCellCalculator = cellCalculator;
            mCellCalculator.setDimen(mWidth, mHeight);
        }
    }

    private void LOG(String msg) {
        Log.d("RollImageView", "willhua:  " + msg);
    }


}
