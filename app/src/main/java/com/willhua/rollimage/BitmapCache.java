package com.willhua.rollimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by willhua on 2016/9/4.
 */
public class BitmapCache {

    private int mSmallWidth = 100;
    private int mSmallHeight = 70;
    private int mLargeWidth = 800;
    private int mLargeHeight = 600;

    private Bitmap mDefaultBitmap;

    private DefaultImageLoader.DecodeFinish mDecodeFinish;

    private ExecutorService mExecutorService = new ThreadPoolExecutor(5, 5, 1, TimeUnit.MINUTES,
            new LinkedBlockingDeque<Runnable>());
    private LruCache<String, Bitmap> mBitmapCache = new LruCache<String, Bitmap>(40 * 1024 * 1024){
        @Override
        protected int sizeOf(String path, Bitmap bitmap){
            return  bitmap.getRowBytes() * bitmap.getHeight();
        }
    };

    public BitmapCache(DefaultImageLoader.DecodeFinish decodeFinish){
        mDecodeFinish = decodeFinish;
        mDefaultBitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
        mDefaultBitmap.eraseColor(0x66434343);
    }

    public void setDimen(int smallWidht, int smallHeight, int largeWidht, int largeHeight){
        mSmallWidth = smallWidht;
        mSmallHeight = smallHeight;
        mLargeWidth = largeWidht;
        mLargeHeight = largeHeight;
        LOG("setDimen " + mSmallWidth + " " + mSmallHeight + " " + mLargeWidth + " " + mLargeHeight);
    }

    public Bitmap getLargeBitmap(String path){
        if(DefaultImageLoader.NO_PATH.equals(path)){
            return mDefaultBitmap;
        }
        LOG("getlarge " + path);
        Bitmap bitmap = mBitmapCache.get(path);
        if(bitmap == null || bitmap.isRecycled()){
            bitmap = mDefaultBitmap;
            LOG("getlarge null " + path + " " + mLargeWidth);
            mExecutorService.submit(new DecodeTask(path, mLargeWidth, mLargeHeight));
        } else if(bitmap.getWidth() < mLargeWidth){
            LOG("getlarge < " + path + " " + mLargeWidth);
            mExecutorService.submit(new DecodeTask(path, mLargeWidth, mLargeHeight));
        }
        return  bitmap;
    }

    public Bitmap getBimap(String path){
        LOG("getBitmap "+ path);
        if(DefaultImageLoader.NO_PATH.equals(path)){
            return mDefaultBitmap;
        }
        LOG("cache " + mBitmapCache.size());
        Bitmap bitmap = mBitmapCache.get(path);
        if(bitmap == null || bitmap.isRecycled()){
            bitmap = mDefaultBitmap;
            LOG("getBitmap  submit "+ path);
            mExecutorService.submit(new DecodeTask(path, mSmallWidth, mSmallHeight));
        }
        return bitmap;
    }

    private class DecodeTask implements Runnable{

        final String mPath;
        final int mWidth;
        final int mHeight;

        public DecodeTask(String path, int width, int height){
            mPath = path;
            mWidth = width;
            mHeight = height;
        }

        @Override
        public void run() {
            LOG("start decode " + mPath);
            Bitmap bitmapOrigin = null;
            synchronized (BitmapCache.this.mBitmapCache){
                bitmapOrigin = mBitmapCache.get(mPath);
            }
            if(bitmapOrigin != null && bitmapOrigin.getWidth() >= mWidth && bitmapOrigin.getHeight() >= mHeight){
                LOG("has large one " + mPath);
                return;
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mPath, options);
            options.inSampleSize = getSample(options, mWidth, mHeight);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(mPath, options);
            if(bitmap != null){
               synchronized (BitmapCache.this.mBitmapCache){
                   bitmapOrigin = mBitmapCache.get(mPath);
                   if(bitmapOrigin != null && bitmap.getWidth() < bitmapOrigin.getWidth()){
                       LOG("has large one " + mPath);
                       return;
                   }
                   LOG("setdimen load bitmap " + bitmap.getWidth() + " " + bitmap.getHeight());
                   mBitmapCache.put(mPath, bitmap);
                   LOG("run put " + mPath + " " + bitmap.getWidth() + " need " + mWidth);
                   LOG("mBitmapCache " + mBitmapCache.size());
                   if(mDecodeFinish != null){
                        mDecodeFinish.decodeFinish(mPath, bitmap);
                    }
                }
            }
        }
    }

    private int getSample(BitmapFactory.Options options, int width, int height){
        int oriW = options.outWidth;
        int oriH = options.outHeight;
        int sample = 1;
        while(oriH / sample / 2 >= (int)(height * 0.8) && oriW / sample / 2 >= (int)(width * 0.8)){
            sample *= 2;
        }
        LOG("getSample " + oriW + " " + oriH + " " + width + " " + height + "   " + sample);
        return  sample;
    }

    private void LOG(String msg){
        Log.d("BitmapCache", "willhua: " + msg);
    }


}
