package com.razi.furnitar.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.Database.Database;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.razi.furnitar.API.LoginService;
import com.razi.furnitar.Models.Cart;
import com.razi.furnitar.Models.DrawerUtil;
import com.razi.furnitar.R;
import com.razi.furnitar.SwipeToDelete;
import com.razi.furnitar.InternetConnectivity;
import com.razi.furnitar.Models.Order;
import com.razi.furnitar.Utils.Constants;
import com.razi.furnitar.Utils.UserPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    List<Order> cart = new ArrayList<>();
    TextView total;
    InternetConnectivity it;
    Button checkout;
    CartAdapter adapter;
    @BindView(R.id.toolbar_cart)
    public Toolbar toolBar;
    private static Context c;

    protected void onDestroy() {
        unregisterReceiver(it);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_activity);
        ButterKnife.bind(this);
        c = this;
        toolBar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolBar);

        DrawerUtil.getDrawer(this, toolBar);
        IntentFilter in = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        it = new InternetConnectivity();
        registerReceiver(it, in);
        recyclerView = findViewById(R.id.cart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        total = findViewById(R.id.total);
        checkout = findViewById(R.id.btnCart);
        loadItems();
    }

    public void loadItems() {
        Integer user_id = UserPreference.getInstance().get("user_id", 0);
        Map<String, Object> body = new HashMap<>();
        body.put("user_id", user_id);

        new LoginService(this, Constants.GET_CARTS, body, result -> {
            Log.d("DataResult", result);
            try {
                JSONObject resObject = new JSONObject(result);
                int status = resObject.getInt("code");
                if (status == 400) {
                    String message = resObject.getString("failed");
                    Toast.makeText(CartActivity.this, message, Toast.LENGTH_SHORT).show();
                } else if (status == 204) {
                    String message = resObject.getString("success");
                    Toast.makeText(CartActivity.this, message, Toast.LENGTH_SHORT).show();
                } else if (status == 206) {
                    String message = resObject.getString("success");
                    Toast.makeText(CartActivity.this, message, Toast.LENGTH_SHORT).show();
                }
                else if (status == 200) {
                    JSONArray temp = new JSONArray();
                    temp = resObject.getJSONArray("data");

                    if (temp != null) {
                        for (int i = 0; i < temp.length(); i++) {
                            Order item = new Order();
                            item.setId(temp.getJSONObject(i).getInt("id"));
                            item.setName(temp.getJSONObject(i).getString("name"));
                            item.setPrice(temp.getJSONObject(i).getDouble("price"));
                            item.setQuantity(temp.getJSONObject(i).getInt("quantity"));
                            item.setUserid(temp.getJSONObject(i).getInt("user_id"));
//                            item.setItemid(temp.getJSONObject(i).getInt("item_id"));
                            cart.add(item);
                        }
                    }
                    Log.i("Yes", cart.size() + "");
                    adapter = new CartAdapter(getBaseContext(), cart);
                    recyclerView.setAdapter(adapter);
                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDelete(adapter));
                    itemTouchHelper.attachToRecyclerView(recyclerView);
                    adapter.calTotal(cart, total);

                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(CartActivity.this, "Something went Wrong", Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    public static void signOut() {
        UserPreference.getInstance().clearAll();
        c.startActivity(new Intent(c, LoginActivity.class));
    }

    public void checkOut(View view) {
        adapter.checkOutItem();
        total.setText("€0.00");
        this.finish();
        startActivity(new Intent(CartActivity.this, MainActivity.class));
    }
}
