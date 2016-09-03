package com.willhua.rollimage;

/**
 * Created by willhua on 2016/9/3.
 */
public class DefaultCellCalculator implements CellCalculator {

    private int mWidth;
    private int mHeight;

    public DefaultCellCalculator(){

    }

    @Override
    public void setCells(Cell[] cells) {

    }

    @Override
    public void setStatus(int direction, float status) {

    }

    @Override
    public void setDimen(int widht, int height) {
        mWidth = widht;
        mHeight = height;
    }
}
