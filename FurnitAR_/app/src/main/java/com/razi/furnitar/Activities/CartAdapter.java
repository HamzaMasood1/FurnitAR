package com.razi.furnitar.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Database.Database;
import com.amulyakhare.textdrawable.TextDrawable;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.razi.furnitar.API.LoginService;
import com.razi.furnitar.Adapters.MyRecyclerViewAdapter;
import com.razi.furnitar.HistoryService;
import com.razi.furnitar.R;
import com.razi.furnitar.Models.Order;
import com.razi.furnitar.Utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView cart_item_name, cart_item_price;
    public ImageView cart_item_quantity;
    private MyRecyclerViewAdapter.OnItemClickListener itemClickListener;

    public void setCart_item_name(TextView cart_item_name) {
        this.cart_item_name = cart_item_name;
    }

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        cart_item_name = itemView.findViewById(R.id.cart_item_name);
        cart_item_price = itemView.findViewById(R.id.cart_item_price);
        cart_item_quantity = itemView.findViewById(R.id.cart_item_quantity);

    }

    @Override
    public void onClick(View view) {

    }
}

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {
    private Context context;
    private List<Order> items = new ArrayList<>();
    private List<Order> cart = new ArrayList<>();
    private TextView total;

    public CartAdapter(Context context, List<Order> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_view, viewGroup, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder CartViewHolder, int i) {
        TextDrawable drawable = TextDrawable.builder().buildRound("" + items.get(i).getQuantity(), R.color.colorPrimaryDark);
        CartViewHolder.cart_item_quantity.setImageDrawable(drawable);

        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        double price = (items.get(i).getPrice()) * items.get(i).getQuantity();
        CartViewHolder.cart_item_price.setText(fmt.format(price));
        CartViewHolder.cart_item_name.setText(items.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public Context getContext() {
        return context;
    }

    public void deleteItem(int position) {
        Order o = items.get(position);
        Map<String, Object> body = new HashMap<>();
        body.put("quantity", o.getQuantity());
        body.put("id", o.getId());
        body.put("name", o.getName());
        body.put("price", o.getPrice());
        body.put("user_id", o.getUserid());

        new LoginService(getContext(), Constants.REMOVE_FROM_CART, body, result -> {
            Log.d("DataResult", result);
            try {
                JSONObject resObject = new JSONObject(result);
                int status = resObject.getInt("code");
                if (status == 400) {
                    String message = resObject.getString("failed");
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                } else if (status == 204) {
                    String message = resObject.getString("success");
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                } else if (status == 206) {
                    String message = resObject.getString("success");
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
                else if (status == 200) {
                    items.remove(position);
                    notifyItemRemoved(position);
                    calTotal(cart, total);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("this", "Something went Wrong");
            }
        }).execute();

    }

    public void checkOutItem() {
        ArrayList<Order> orders = new ArrayList<>();
        for (int position = 0; position < items.size(); ) {
            orders.add(items.get(position));
            items.remove(position);
            notifyItemRemoved(position);
        }
        for (int position = 0; position < orders.size(); position++) {

            Order o = orders.get(position);
            Map<String, Object> body = new HashMap<>();
            body.put("quantity", o.getQuantity());
            body.put("id", o.getId());
            body.put("name", o.getName());
            body.put("price", o.getPrice());
            body.put("user_id", o.getUserid());

            new LoginService(getContext(), Constants.REMOVE_FROM_CART, body, result -> {
                Log.d("DataResult", result);
                try {
                    JSONObject resObject = new JSONObject(result);
                    int status = resObject.getInt("code");
                    if (status == 400) {
                        String message = resObject.getString("failed");
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    } else if (status == 204) {
                        String message = resObject.getString("success");
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    } else if (status == 206) {
                        String message = resObject.getString("success");
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                    else if (status == 200) {
                        Map<String, Object> history_item = new HashMap<>();
                        history_item.put("id", resObject.getInt("id"));
                        history_item.put("quantity", resObject.getInt("quantity"));
                        history_item.put("user", resObject.getInt("user_id"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("this", "Something went Wrong");
                }
            }).execute();

            final String[] id = new String[1];
            int finalPosition = position;
            String purchase = "Name: "
                    + orders.get(position).getName() + " Quantity: "
                    + orders.get(position).getQuantity() + " Total Price: €"
                    + (orders.get(position).getQuantity() * orders.get(position).getPrice());
            Intent intent = new Intent(context, HistoryService.class);
            intent.putExtra("purchase", purchase);
            context.startService(intent);
        }

    }

    public void calTotal(List<Order> cart_t, TextView total_t) {
        cart = items;
        total = total_t;
        int totalP = 0;
        for (Order o : cart) {
            totalP += o.getPrice() * o.getQuantity();
        }
        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        total.setText(fmt.format(totalP));
    }

}
