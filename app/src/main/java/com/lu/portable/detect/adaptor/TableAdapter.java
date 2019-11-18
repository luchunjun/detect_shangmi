package com.lu.portable.detect.adaptor;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lu.portable.detect.view.ListTableCell;
import com.lu.portable.detect.view.ListTableRow;

import java.util.List;

public class TableAdapter extends BaseAdapter {
    private Context context;
    private List <ListTableRow> table;

    public TableAdapter(Context context, List <ListTableRow> rows) {
        this.context = context;
        table = rows;
    }

    public int getCount() {
        return this.table.size();
    }

    public ListTableRow getItem(int paramInt) {
        return (ListTableRow) this.table.get(paramInt);
    }

    public long getItemId(int paramInt) {
        return paramInt;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
        ListTableRow listTableRow = (ListTableRow) this.table.get(paramInt);
        return new ListTableRowView(context, listTableRow);
    }

    public static class ListTableRowView extends LinearLayout
            implements Checkable {
        private boolean mChecked;

        public ListTableRowView(Context context, ListTableRow listTableRow) {
            super(context);
            setOrientation(LinearLayout.HORIZONTAL);
            //setBackgroundColor(context.getResources().getColor(R.color.white,);

            for (int i = 0; i < listTableRow.getSize(); i++) {
                ListTableCell listTableCell = listTableRow.getCellValue(i);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(listTableCell.width, listTableCell.height);
                layoutParams.setMargins(0, 0, 1, 1);
                TextView textView;
                if (listTableCell.type == 0) {
                    textView = new TextView(context);
                    textView.setLines(1);
                    textView.setGravity(17);
                    textView.setBackgroundColor(-1);
                    textView.setText(String.valueOf(listTableCell.value));
                    addView(textView, layoutParams);
                } else if (listTableCell.type == 2) {
                    textView = new TextView(context);
                    textView.setLines(1);
                    textView.setGravity(17);
                    textView.setBackgroundColor(-1);
                    textView.setText(String.valueOf(listTableCell.value));
                    if (listTableCell.color != -1) ;
                    for (int j = listTableCell.color; ; j = listTableRow.rowColor) {
                        textView.setTextColor(j);
                        addView(textView, layoutParams);
                        break;
                    }
                } else if (listTableCell.type == 1) {
                    ImageView imageView = new ImageView(context);
                    imageView.setBackgroundColor(-1);
                    imageView.setImageResource((int) listTableCell.value);
                    addView(imageView, layoutParams);
                }
            }
            //setBackgroundColor(-1);
        }

        public boolean isChecked() {
            return this.mChecked;
        }

        public void setChecked(boolean checked) {
            mChecked = checked;
            if (checked) ;
            for (int i = Color.rgb(96, 206, 230); ; i = -1) {
                setColor(i);
                return;
            }
        }

        public void setColor(int paramInt) {
            int j = getChildCount();
            int i = 0;
            while (i < j) {
                getChildAt(i).setBackgroundColor(paramInt);
                i += 1;
            }
        }

        public void toggle() {
            if (!this.mChecked) ;
            for (boolean bool = true; ; bool = false) {
                setChecked(bool);
                return;
            }
        }
    }
}
