package com.willhua.rollimage;

/**
 * Created by willhua on 2016/9/3.
 */
public interface CellCalculator {
    public static final int FORWARD = 1;
    public static final int BACKWARD = 2;

    public Cell[] getCells();
    public void setStatus(int direction, int distance);
    public void setDimen(int widht, int height);
}
