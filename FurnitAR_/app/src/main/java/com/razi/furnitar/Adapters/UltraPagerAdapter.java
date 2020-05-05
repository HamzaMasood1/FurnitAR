package com.razi.furnitar.Adapters;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.razi.furnitar.R;

import java.util.ArrayList;

public class UltraPagerAdapter extends PagerAdapter {

    private boolean isMultiScr;
//    public static int pos = 0;
    private ArrayList<Integer> items;

    public UltraPagerAdapter(boolean isMultiScr, ArrayList<Integer> items) {
        this.isMultiScr = isMultiScr;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(container.getContext()).inflate(R.layout.layout_child, null);
        //new LinearLayout(container.getContext());
        TextView textView = (TextView) linearLayout.findViewById(R.id.pager_textview);
//        textView.setText(position + "");
        linearLayout.setId(R.id.item_id);
        Integer item = items.get(position);
        linearLayout.setBackgroundResource(item);
        container.addView(linearLayout);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Selected Position", String.valueOf(position));
//                pos = position;
            }

        });


//        linearLayout.getLayoutParams().width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, container.getContext().getResources().getDisplayMetrics());
//        linearLayout.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, container.getContext().getResources().getDisplayMetrics());
        return linearLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        LinearLayout view = (LinearLayout) object;
        container.removeView(view);
    }

    public void updateArray(ArrayList<Integer> data) {
        this.items = data;
        notifyDataSetChanged();
    }

}
