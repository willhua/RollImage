package com.willhua.rollimage;

import android.graphics.Bitmap;
import android.view.View;

import java.util.List;

/**
 * Created by willhua on 2016/9/3.
 */
public interface ImageLoader {

    /**
     * the images shown roll forward
     */
    public void rollForward();

    /**
     * the images shown roll backward
     */
    public void rollBackward();

    /**
     * get bitmaps
     * @return
     */
    public Bitmap[] getBitmap();

    /**
     * use invalidate to invalidate the view
     * @param invalidate
     */
    public void setInvalidate(RollImageView.InvalidateView invalidate);

    /**
     * set the dimen of view
     * @param width
     * @param height
     */
    public void setDimen(int width, int height);


    /**
     * the image path to be show
     * @param paths
     */
    public void setImagePaths(List<String> paths);

    /**
     * get large bitmap while static
     */
    public void loadCurrentLargeBitmap();
}
