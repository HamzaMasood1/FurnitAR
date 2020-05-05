package com.razi.furnitar.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.razi.furnitar.API.LoginService;
import com.razi.furnitar.Adapters.MyRecyclerViewAdapter;
import com.razi.furnitar.Models.DrawerUtil;
import com.razi.furnitar.Models.Item;
import com.razi.furnitar.R;
import com.razi.furnitar.Utils.Constants;
import com.razi.furnitar.InternetConnectivity;
import com.razi.furnitar.Utils.UserPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static Context c;
    InternetConnectivity it;

    @BindView(R.id.toolbar_main)
    public Toolbar toolBar;
    Button ar, nonAR, searchButton;
    Drawable d;
    boolean showAR;
    String searchQuery;
    TextView searchbar;
    boolean cancel;
    private MyRecyclerViewAdapter adapter, ARadapter, NonARadapter, searchAdapter;
    ArrayList<Item> items = new ArrayList<>();
    RecyclerView recyclerView;

    public static void signOut() {
        UserPreference.getInstance().clearAll();
        c.startActivity(new Intent(c, LoginActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
//        adapter.startListening();
    }

    protected void onDestroy() {
        unregisterReceiver(it);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        toolBar.setTitle("");
        setSupportActionBar(toolBar);

        DrawerUtil.getDrawer(this, toolBar);
        c = this;
        IntentFilter in = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        it = new InternetConnectivity();
        registerReceiver(it, in);

        // Obtain the FirebaseAnalytics instance.
        ar = findViewById(R.id.ar_filter);
        showAR = true;
        searchbar = (TextView) findViewById(R.id.searchbar);
        searchButton = (Button) findViewById(R.id.search_button);
        cancel = false;
        d = ar.getBackground();
//        query = db.collection("items").whereGreaterThan("quantity", 0);
//
//        options = new FirestoreRecyclerOptions.Builder<Item>()
//                .setQuery(query, Item.class)
//                .build();

//        adapter = new RecyclerViewAdapter(options);
//
//        query = db.collection("items").whereGreaterThan("quantity", 0);
//
//        options = new FirestoreRecyclerOptions.Builder<Item>()
//                .setQuery(query, Item.class)
//                .build();
//        ARadapter = new RecyclerViewAdapter(options);
//
//        query = db.collection("items")
//                .whereEqualTo("isAR", false)
//                .whereGreaterThan("quantity", 0);
//
//
//        options = new FirestoreRecyclerOptions.Builder<Item>()
//                .setQuery(query, Item.class)
//                .build();
//        NonARadapter = new RecyclerViewAdapter(options);

//        initRecyclerView();
//        aL = firebaseAuth -> {
//            if (firebaseAuth.getCurrentUser() == null) {
//                startActivity(new Intent(MainActivity.this, Login.class));
//            }
//        }

        Map<String, Object> body = new HashMap<>();
        new LoginService(this, Constants.GET_DATA, body, result -> {
            Log.d("DataResult", result);
            try {
                JSONObject resObject = new JSONObject(result);
                int status = resObject.getInt("code");
                if (status == 400) {
                    String message = resObject.getString("failed");
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                } else if (status == 204) {
                    String message = resObject.getString("success");
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                } else if (status == 206) {
                    String message = resObject.getString("success");
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                }
                else if (status == 200) {

                    JSONArray data = resObject.getJSONArray("data");
                    if (data != null) {
                        for (int i = 0; i < data.length(); i++) {
                            Item item = new Item();
                            item.setId(data.getJSONObject(i).getInt("id"));
                            item.setName(data.getJSONObject(i).getString("name"));
                            item.setImage(data.getJSONObject(i).getString("image"));
                            item.setDescription(data.getJSONObject(i).getString("description"));
                            item.setIsAR(data.getJSONObject(i).getInt("isAR"));
                            item.setPrice(data.getJSONObject(i).getDouble("price"));
                            item.setQuantity(data.getJSONObject(i).getInt("quantity"));
                            items.add(item);
                        }
                    }

                    adapter = new MyRecyclerViewAdapter(MainActivity.this, items);
                    initRecyclerView();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).execute();
    }

    private void initRecyclerView() {

        recyclerView = findViewById(R.id.list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        clickListener();
    }

    private void clickListener() {
        adapter.setOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item, int pos) {
                Intent intent = new Intent(getApplicationContext(), ItemDetailActivity.class);
                intent.putExtra("item", item);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
//        adapter.stopListening();
    }

    public void seacrhItem(View view) {
//        adapter.stopListening();
        if(!cancel){
            searchQuery = searchbar.getText().toString();
            searchQuery = searchQuery.toLowerCase();
            String s = searchQuery.substring(0,searchQuery.length() - 1);
            char c = searchQuery.charAt(searchQuery.length() - 1);
            c++;
            s += c;

            Map<String, Object> body = new HashMap<>();
            body.put("searchQuery", searchQuery);
            body.put("s", s);
            new LoginService(this, Constants.SEARCH_DATA, body, result -> {
                Log.d("DataResult", result);
                try {
                    JSONObject resObject = new JSONObject(result);
                    int status = resObject.getInt("code");
                    if (status == 400) {
                        String message = resObject.getString("failed");
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    } else if (status == 204) {
                        String message = resObject.getString("success");
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    } else if (status == 206) {
                        String message = resObject.getString("success");
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                    else if (status == 200) {

                        JSONArray data = resObject.getJSONArray("data");
                        ArrayList<Item> search = new ArrayList<>();
                        if (data != null) {
                            for (int i = 0; i < data.length(); i++) {
                                Item item = new Item();
                                item.setId(data.getJSONObject(i).getInt("id"));
                                item.setName(data.getJSONObject(i).getString("name"));
                                item.setImage(data.getJSONObject(i).getString("image"));
                                item.setDescription(data.getJSONObject(i).getString("description"));
                                item.setIsAR(data.getJSONObject(i).getInt("isAR"));
                                item.setPrice(data.getJSONObject(i).getDouble("price"));
                                item.setQuantity(data.getJSONObject(i).getInt("quantity"));
                                search.add(item);
                            }
                        }

                        adapter = new MyRecyclerViewAdapter(MainActivity.this, search);
                        initRecyclerView();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }).execute();

            searchButton.setBackgroundResource(R.drawable.ic_cancel_black_24dp);
        }
        else{
            searchbar.setText("");
            adapter = new MyRecyclerViewAdapter(MainActivity.this, items);
            searchButton.setBackground(getBaseContext().getDrawable(R.drawable.ic_search_black_24dp));
        }

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        clickListener();
//        adapter.startListening();
        cancel = !cancel;
    }


    public void showAR(View view) {
        showAR = !showAR;
        ArrayList<Item> AR_temp = new ArrayList<>();
        ArrayList<Item> nonAR_temp = new ArrayList<>();
//        adapter.stopListening();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getIsAR() == 1) {
                AR_temp.add(items.get(i));

            } else {
                nonAR_temp.add(items.get(i));

            }
        }
        if(showAR){

            adapter = new MyRecyclerViewAdapter(MainActivity.this, AR_temp);
//            query = db.collection("items").whereGreaterThan("quantity", 0);
//            options = new FirestoreRecyclerOptions.Builder<Item>()
//                    .setQuery(query, Item.class)
//                    .build();
//            adapter = new RecyclerViewAdapter(options);
        }
        else{
            adapter = new MyRecyclerViewAdapter(MainActivity.this, items);
//            query = db.collection("items")
//                    .whereEqualTo("isAR", false)
//                    .whereGreaterThan("quantity", 0);
//            options = new FirestoreRecyclerOptions.Builder<Item>()
//                    .setQuery(query, Item.class)
//                    .build();
//            adapter = new RecyclerViewAdapter(options);
        }
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        clickListener();
//        adapter.startListening();
    }
}
