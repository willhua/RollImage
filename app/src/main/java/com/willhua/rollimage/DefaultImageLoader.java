package com.willhua.rollimage;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willhua on 2016/9/3.
 */
public class DefaultImageLoader implements ImageLoader {


    public interface DecodeFinish{
        public void decodeFinish(String path, Bitmap bitmap);
    }

    public static final String NO_PATH = "no_path";

    private int mSmallWidth = 100;
    private int mSmallHeight = 80;
    private int mBigWidht = 300;
    private int mBigHeight = 200;

    private int mImagesCnt;
    private final int mShowCnt;
    private String[] mAllImagePaths;
    private List<String> mCurrentPaths = new ArrayList<>();
    private int mCurrentIndex;
    private Bitmap[] mCurrentBitmaps;
    private RollImageView.InvalidateView mInvalidateView;

    private BitmapCache mBitmapCache;
    private DecodeFinish mDecodeFinish = new DecodeFinish() {
        @Override
        public void decodeFinish(String path, Bitmap bitmap) {
            LOG("decodeFinish " + path);
            if(mCurrentPaths.contains(path)){
                LOG("decodeFinish refresh " + path);
                if(mInvalidateView != null){
                    mInvalidateView.invalidateView();
                }
            }
        }
    };

    @Override
    public void loadCurrentLargeBitmap() {
        for(int i = mCurrentIndex - 1; i < mCurrentIndex + 2; i++){
            if(i >= 0 && i < mImagesCnt - 1){
                mBitmapCache.getLargeBitmap(mAllImagePaths[i]);
            }
        }
    }

    public DefaultImageLoader(int showcnt){
        mShowCnt = showcnt;
        mCurrentIndex = 0;
        mCurrentBitmaps = new Bitmap[mShowCnt];
        mBitmapCache = new BitmapCache(mDecodeFinish);
    }

    @Override
    public void rollForward() {
        LOG("rollForward");
        mCurrentIndex++;
        if(mCurrentIndex > mImagesCnt - 1){
            mCurrentIndex = mImagesCnt - 1;
        }
        setCurrentPaths();
    }

    @Override
    public void rollBackward() {
        LOG("rollBackward");
        mCurrentIndex--;
        if(mCurrentIndex < 0){
            mCurrentIndex = 0;
        }
        setCurrentPaths();
    }

    @Override
    public Bitmap[] getBitmap() {
        if(mCurrentPaths != null){
            LOG("getBitmap paths nut null");
            for(int i = mCurrentIndex, j = 0; j < mShowCnt; j++, i++){
                if(i >= 0 && i < mImagesCnt){
                    mCurrentBitmaps[j] = mBitmapCache.getBimap(mAllImagePaths[i]);
                } else{
                    mCurrentBitmaps[j] = mBitmapCache.getBimap(NO_PATH);
                }
            }
        }
        return  mCurrentBitmaps;
    }

    @Override
    public void setInvalidate(RollImageView.InvalidateView invalidate) {
        mInvalidateView = invalidate;
    }

    @Override
    public void setDimen(int width, int height) {
        mBigWidht = width;
        mBigHeight = height;
        mSmallWidth = mBigWidht / 16;
        mSmallHeight = mBigHeight / 16;
        if(mBitmapCache != null){
            mBitmapCache.setDimen(mSmallWidth, mSmallHeight, mBigWidht, mBigHeight);
        }
    }

    private void setCurrentPaths(){
        mCurrentPaths.clear();
        for(int i = mCurrentIndex, j = 0; j < mShowCnt; j++){
            if(i >= 0 && i < mImagesCnt){
                mCurrentPaths.add(mAllImagePaths[i++]);
            } else {
                mCurrentPaths.add(NO_PATH);
            }
        }
        LOG(mCurrentPaths.toString());
    }

    @Override
    public void setImagePaths(List<String> paths) {
        if(paths != null){
            mImagesCnt = paths.size();
            mAllImagePaths = new String[mImagesCnt];
            paths.toArray(mAllImagePaths);
            mCurrentIndex = 0;
            setCurrentPaths();
        }
    }

    private void LOG(String msg){
        Log.d("DefaultImageLoader", "willhua: " + msg);
    }
}
