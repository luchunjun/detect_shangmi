package com.lu.portable.detect.view;

public class ListTableRow {
    public ListTableCell[] cell;
    public int rowColor = -16777216;

    public ListTableRow(ListTableCell[] cells) {
        this.cell = cells;
    }

    public ListTableCell getCellValue(int index) {
        if (index >= cell.length)
            return null;
        return cell[index];
    }

    public int getSize() {
        return cell.length;
    }
}