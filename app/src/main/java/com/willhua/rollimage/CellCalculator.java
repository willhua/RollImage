package com.willhua.rollimage;

/**
 * Created by willhua on 2016/9/3.
 */
public interface CellCalculator {


    /**
     * get all rects for drawing image
     * @return
     */
    public Cell[] getCells();

    /**
     *
     * @param speed the speed of move
     * @param distance the motion distance during the period from ACTION_DOWN to this moment
     * @return 0 means no roll, positive number means roll forward and negative means roll backward
     */
    public int setStatus(float speed, float distance);


    /**
     * set the dimen of view
     * @param widht
     * @param height
     */
    public void setDimen(int widht, int height);

    /**
     * set to the status for static
     */
    public void setStatic();
}
