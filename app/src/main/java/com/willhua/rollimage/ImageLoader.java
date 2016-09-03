package com.willhua.rollimage;

import android.graphics.Bitmap;

/**
 * Created by willhua on 2016/9/3.
 */
public interface ImageLoader {

    public void rollForward();
    public void rollBackward();
    public void getBitmap(Bitmap[] bitmaps);
}
