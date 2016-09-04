package com.willhua.rollimage;

import android.graphics.Bitmap;
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

    private int mSmallWidth;
    private int mSmallHeight;
    private int mBigWidht;
    private int mBigHeight;

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
            if(mCurrentPaths.contains(path)){
                mRefresh.refresh();
            }
        }
    };


    public DefaultImageLoader(int showcnt){
        mShowCnt = showcnt + 1;
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
        if(mCurrentPaths != null){
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
    }


}
