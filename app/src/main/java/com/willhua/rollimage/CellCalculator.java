package com.willhua.rollimage;

/**
 * Created by willhua on 2016/9/3.
 */
public interface CellCalculator {
    public static final int FORWARD = 1;
    public static final int BACKWARD = 2;

    public void setCells(Cell[] cells);
    public void setStatus(int direction, float status);
    public void setDimen(int widht, int height);
}
