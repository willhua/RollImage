package com.willhua.rollimage;

/**
 * Created by willhua on 2016/9/3.
 */
public interface CellCalculator {
    public static final int FORWARD = 1;
    public static final int BACKWARD = 2;

    public Cell[] getCells();
    public int setStatus(int direction, float distance);
    public void setDimen(int widht, int height);
    public void setStatic();
}
