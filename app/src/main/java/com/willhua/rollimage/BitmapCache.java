package com.willhua.rollimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by willhua on 2016/9/4.
 */
public class BitmapCache {

    private int mSmallWidth;
    private int mSmallHeight;
    private int mLargeWidth;
    private int mLargeHeight;

    private Bitmap mDefaultBitmap;

    private DefaultImageLoader.DecodeFinish mDecodeFinish;

    private ExecutorService mExecutorService = Executors.newFixedThreadPool(4);

    private LruCache<String, Bitmap> mBitmapCache = new LruCache<String, Bitmap>(8 * 1024 * 1024){
        @Override
        protected int sizeOf(String path, Bitmap bitmap){
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    };

    public BitmapCache(DefaultImageLoader.DecodeFinish decodeFinish){
        mDecodeFinish = decodeFinish;
        mDefaultBitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
        mDefaultBitmap.eraseColor(0);
    }

    public void setDimen(int smallWidht, int smallHeight, int largeWidht, int largeHeight){
        mSmallWidth = smallWidht;
        mSmallHeight = smallHeight;
        mLargeWidth = largeWidht;
        mLargeHeight = largeHeight;
    }

    public Bitmap getBimap(String path, int size){
        LOG("getBitmap "+ path);
        LOG("cache " + mBitmapCache.size());
        if(size == ImageLoader.SAMLL){
            Bitmap bitmap = mBitmapCache.get(path);
            if(bitmap == null || bitmap.isRecycled()){
                bitmap = mDefaultBitmap;
                LOG("getBitmap  submit "+ path);
                mExecutorService.submit(new DecodeTask(path, mSmallWidth, mSmallHeight));
            }
            return bitmap;
        } else if(size == ImageLoader.LARGE){
            Bitmap bitmap = mBitmapCache.get(path);
            if(bitmap == null || bitmap.isRecycled() || bitmap.getWidth() < mLargeWidth){
                bitmap = mDefaultBitmap;
                mExecutorService.submit(new DecodeTask(path, mLargeWidth, mLargeHeight));
            }
            return bitmap;
        }
        return mDefaultBitmap;
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
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mPath, options);
            options.inSampleSize = getSample(options, mWidth, mHeight);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(mPath, options);
            if(bitmap != null){
               synchronized (BitmapCache.this){
                   mBitmapCache.put(mPath, bitmap);
                   LOG("run put " + mPath);
                   LOG("mBitmapCache " + mBitmapCache.size());
                   if(mDecodeFinish != null){
                        mDecodeFinish.DecodeFinish(mPath, bitmap);
                    }
                }
            }
        }
    }

    private int getSample(BitmapFactory.Options options, int width, int height){
        int oriW = options.outWidth;
        int oriH = options.outHeight;
        int sample = 1;
        while(oriH / sample / 2 > height && oriW / sample / 2 > width){
            sample *= 2;
        }
        return  sample;
    }

    private void LOG(String msg){
        Log.d("BitmapCache", "willhua: " + msg);
    }


}
