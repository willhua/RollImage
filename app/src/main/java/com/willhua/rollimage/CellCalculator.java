package com.willhua.rollimage;

/**
 * Created by willhua on 2016/9/3.
 */
public interface CellCalculator {

    public Cell[] getCells();

    /**
     *
     * @param direction
     * @param distance
     * @return 0 means no roll, positive number means roll forward and negative means roll backward
     */
    public int setStatus(int direction, float distance);
    public void setDimen(int widht, int height);
    public void setStatic();
}
