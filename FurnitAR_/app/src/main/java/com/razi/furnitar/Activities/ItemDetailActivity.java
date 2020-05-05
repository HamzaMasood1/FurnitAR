package com.razi.furnitar.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Database.Database;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.ar.core.ArCoreApk;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razi.furnitar.API.LoginService;
import com.razi.furnitar.Adapters.MyRecyclerViewAdapter;
import com.razi.furnitar.Models.DrawerUtil;
import com.razi.furnitar.Models.Item;
import com.razi.furnitar.R;
import com.razi.furnitar.Common;
import com.razi.furnitar.InternetConnectivity;
import com.razi.furnitar.Models.Order;
import com.razi.furnitar.Utils.Constants;
import com.razi.furnitar.Utils.UserPreference;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

public class ItemDetailActivity extends AppCompatActivity {
    TextView item_details_name, item_details_price, item_details_description;
    ImageView item_detail_image;
    CollapsingToolbarLayout toolbar;
    FloatingActionButton fab;
    ElegantNumberButton picker;
    InternetConnectivity it;
    private static Context c;
    String itemId = "";
    Button view_ar;
    Item item;
    @BindView(R.id.toolbar)
    public Toolbar toolBar;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item__detail);
        item = (Item) getIntent().getSerializableExtra("item");
        item_details_name = findViewById(R.id.item_name);
        item_details_price = findViewById(R.id.item_price);
        item_details_description = findViewById(R.id.item_description);
        item_detail_image = findViewById(R.id.detail_image);
        fab = findViewById(R.id.cart_btn);
        picker = findViewById(R.id.quantity_picker);

        toolbar = findViewById(R.id.collapse);
        toolbar.setExpandedTitleTextAppearance(R.style.appbar);
        toolbar.setCollapsedTitleTextAppearance(R.style.Collapsed);
        setSupportActionBar(toolBar);
        DrawerUtil.getDrawer(this, toolBar);
        view_ar = findViewById(R.id.view_AR);
        IntentFilter in = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        it = new InternetConnectivity();
        registerReceiver(it, in);

        Picasso.get().load(item.getImage()).into(item_detail_image);

        item_details_name.setText(item.getName());
        item_details_description.setText(item.getDescription());
        item_details_price.setText("€" + item.getPrice());

        if (item.getIsAR() == 1 && maybeEnableArButton()) {
            view_ar.setVisibility(View.VISIBLE);
            view_ar.setEnabled(true);
        } else {
            view_ar.setVisibility(View.INVISIBLE);
            if (item.getIsAR() == 1) {
                view_ar.setVisibility(View.VISIBLE);
                view_ar.setEnabled(false);
                view_ar.setText("AR Not Supported");
                view_ar.setTextSize(12f);
            }
        }
        picker.setRange(1, item.getQuantity());
        toolbar.setTitle(item.getName());
        try {
            MobileAds.initialize(this, "ca-app-pub-3940256099942544/6300978111");
            AdView adView = new AdView(this);
            adView.setAdSize(AdSize.BANNER);
            adView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            adView.loadAd(adRequest);
        } catch (Exception e) {
            Log.d("qwerty", "ad:" + e.getMessage());
        }
    }

    protected void onDestroy() {
        unregisterReceiver(it);
        super.onDestroy();
    }

    boolean maybeEnableArButton() {
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
        if (availability.isTransient()) {
            // Re-query at 5Hz while compatibility is checked in the background.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    maybeEnableArButton();
                }
            }, 200);
        }
        if (availability.isSupported()) {
            return true;
        }
        return false;
    }

    public void viewInAR(View view) {
        view_ar.setEnabled(true);
        Intent intent = new Intent(getApplicationContext(), ARactivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        startActivity(new Intent(ItemDetailActivity.this, MainActivity.class));
    }

    public void addToCart(View view) {

        Integer user_id = UserPreference.getInstance().get("user_id", 0);
//        Order order = new Order(user_id,
//                item.getId(),
//                item.getName(),
//                item.getPrice(),
//                Integer.parseInt(picker.getNumber()));

        Map<String, Object> body = new HashMap<>();
        body.put("quantity", item.getQuantity() - Integer.parseInt(picker.getNumber()));
        body.put("id", item.getId());
        body.put("name", item.getName());
        body.put("price", item.getPrice());
        body.put("_quantity", Integer.parseInt(picker.getNumber()));
        body.put("user_id", user_id);

        new LoginService(this, Constants.ADD_TO_CART, body, result -> {
            Log.d("DataResult", result);
            try {
                JSONObject resObject = new JSONObject(result);
                int status = resObject.getInt("code");
                if (status == 400) {
                    String message = resObject.getString("failed");
                    Toast.makeText(ItemDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                } else if (status == 204) {
                    String message = resObject.getString("success");
                    Toast.makeText(ItemDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                } else if (status == 206) {
                    String message = resObject.getString("success");
                    Toast.makeText(ItemDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                }
                else if (status == 200) {
                    Toast.makeText(ItemDetailActivity.this, "Item Added to Cart", Toast.LENGTH_SHORT).show();
                    picker.setRange(1, item.getQuantity() - Integer.parseInt(picker.getNumber()));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(ItemDetailActivity.this, "Something went Wrong", Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    public static void signOut() {
        UserPreference.getInstance().clearAll();
        c.startActivity(new Intent(c, LoginActivity.class));
    }

}
