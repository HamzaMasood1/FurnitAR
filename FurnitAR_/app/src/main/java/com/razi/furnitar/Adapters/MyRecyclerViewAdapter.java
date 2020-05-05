package com.razi.furnitar.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.razi.furnitar.Models.Item;
import com.razi.furnitar.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private MyRecyclerViewAdapter.OnItemClickListener onItemClickListener;
    private Context mContext;
    private ArrayList<Item> items;

    public MyRecyclerViewAdapter(Context context, ArrayList<Item> items) {
        this.items = items;
        this.mContext = context;
    }

    @NonNull
    @Override
    public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_view, viewGroup, false);
        MyRecyclerViewAdapter.ViewHolder holder = new MyRecyclerViewAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewAdapter.ViewHolder viewHolder, int i) {

        final Item model = items.get(i);
        String AR = "";
        if (model.getIsAR() == 1) {
            AR = "AR";
        } else if (model.getIsAR() == 0) {
            AR = "";
        }
        if (!AR.equals("")) {
            TextDrawable drawable = TextDrawable.builder().buildRound(AR, R.color.colorPrimaryDark);
            viewHolder.item_view_AR.setImageDrawable(drawable);
        }
        viewHolder.item_view_name.setText(model.getName());
        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        viewHolder.item_view_price.setText(fmt.format(model.getPrice()));
        String url = model.getImage();
        Picasso.get()
                .load(url)
                .into(viewHolder.item_view_image);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Item item, int pos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView item_view_name, item_view_price;
        ImageView item_view_image, item_view_AR;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_view_name = itemView.findViewById(R.id.item_view_name);
            item_view_price = itemView.findViewById(R.id.item_view_price);
            item_view_image = itemView.findViewById(R.id.item_view_image);
            item_view_AR = itemView.findViewById(R.id.item_view_AR);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION && onItemClickListener != null) {
                        onItemClickListener.onItemClick(items.get(pos), pos);
                    }
                }
            });
        }
    }
}