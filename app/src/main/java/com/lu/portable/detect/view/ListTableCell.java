package com.lu.portable.detect.view;

public class ListTableCell {
    public static final int DATA = 2;
    public static final int IMAGE = 1;
    public static final int TITLE = 0;
    public int color = -1;
    public int height;
    public int type;
    public Object value;
    public int width;

    public ListTableCell(Object value, int width, int height, int type) {
        this.value = value;
        this.width = width;
        this.height = height;
        this.type = type;
    }
}