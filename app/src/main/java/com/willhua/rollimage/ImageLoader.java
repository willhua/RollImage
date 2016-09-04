package com.willhua.rollimage;

import android.graphics.Bitmap;
import android.view.View;

import java.util.List;

/**
 * Created by willhua on 2016/9/3.
 */
public interface ImageLoader {

    public interface Refresh{
        public void refresh();
    }

    int SAMLL = 0;
    int LARGE = 1;
    public void rollForward();
    public void rollBackward();
    public Bitmap[] getBitmap(int size);
    public void setRefresh(Refresh refresh);
    public void setImagePaths(List<String> paths);
    public void setDimen(int width, int height);
}
