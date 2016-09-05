package com.willhua.rollimage;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willhua on 2016/9/3.
 */
public class DefaultImageLoader implements ImageLoader {


    public interface DecodeFinish{
        public void DecodeFinish(String path, Bitmap bitmap);
    }

    private int mSmallWidth = 100;
    private int mSmallHeight = 80;
    private int mBigWidht = 300;
    private int mBigHeight = 200;

    private int mImagesCnt;
    private final int mShowCnt;
    private String[] mAllImagePaths;
    private List<String> mCurrentPaths = new ArrayList<String>();
    private int mCurrentIndex;
    private Bitmap[] mCurrentBitmaps;

    private BitmapCache mBitmapCache;
    private Refresh mRefresh;
    private DecodeFinish mDecodeFinish = new DecodeFinish() {
        @Override
        public void DecodeFinish(String path, Bitmap bitmap) {
            LOG("DecodeFinish " + path);
            if(mCurrentPaths.contains(path)){
                LOG("DecodeFinish refresh " + path);
                mRefresh.refresh();
            }
        }
    };


    public DefaultImageLoader(int showcnt){
        mShowCnt = showcnt;
        mCurrentIndex = mShowCnt;
        mCurrentBitmaps = new Bitmap[mShowCnt];
        mBitmapCache = new BitmapCache(mDecodeFinish);
    }

    @Override
    public void rollForward() {
        mCurrentIndex++;
        if(mCurrentIndex > mImagesCnt){
            mCurrentIndex = mImagesCnt;
        }
        setCurrentPaths();
    }

    @Override
    public void rollBackward() {
        mCurrentIndex--;
        if(mCurrentIndex < mShowCnt){
            mCurrentIndex = mShowCnt;
        }
        setCurrentPaths();
    }

    @Override
    public Bitmap[] getBitmap(int size) {
        LOG("getBitmap");
        if(mCurrentPaths != null){
            LOG("getBitmap paths nut null");
            for(int i = mCurrentIndex, j = 0; j < mShowCnt; j++, i--){
                mCurrentBitmaps[j] = mBitmapCache.getBimap(mAllImagePaths[i], ImageLoader.SAMLL);
            }
        }
        return  mCurrentBitmaps;
    }

    private void setCurrentPaths(){
        mCurrentPaths.clear();
        for(int i = mCurrentIndex, j = 0; j < mShowCnt; i--, j++){
            mCurrentPaths.add(mAllImagePaths[i]);
        }
        LOG(mCurrentPaths.toString());
    }

    @Override
    public void setRefresh(Refresh refresh){
        mRefresh = refresh;
    }

    @Override
    public void setImagePaths(List<String> paths) {
        mImagesCnt = paths.size();
        mAllImagePaths = new String[mImagesCnt];
        paths.toArray(mAllImagePaths);
        mCurrentIndex = mShowCnt;
        setCurrentPaths();
    }

    @Override
    public void setDimen(int width, int height) {
        mBigWidht = width;
        mBigHeight = height;
        mSmallWidth = width / 4;
        mSmallHeight = height / 4;
        if(mBitmapCache != null){
            mBitmapCache.setDimen(mSmallWidth, mSmallHeight, mBigWidht, mBigHeight);
        }
    }

    private void LOG(String msg){
        Log.d("DefaultImageLoader", "willhua: " + msg);
    }
}
