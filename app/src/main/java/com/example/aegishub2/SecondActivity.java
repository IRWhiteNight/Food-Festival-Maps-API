package com.example.aegishub2;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String URL_PLACES = "http://192.168.0.119/mobile/places";
    private static final String TAG = SecondActivity.class.getSimpleName();

    ListView listView;
    ImageButton btnBack;
    SwipeRefreshLayout swipeRefreshLayout;
    List<Place> placeList = new ArrayList<>();
    PlaceAdapter placeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondactivity);

        swipeRefreshLayout = findViewById(R.id.swipe);
        listView = findViewById(R.id.list);

        placeAdapter = new PlaceAdapter(this, placeList);
        listView.setAdapter(placeAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected place
                Place place = placeList.get(position);

                Intent intent = new Intent(SecondActivity.this, MapActivity.class);
                intent.putExtra("place_name", place.getName());
                intent.putExtra("place_address", place.getAddress());
                intent.putExtra("place_imgUrl", place.getImgUrl());
                intent.putExtra("place_open", place.getOpen());
                intent.putExtra("place_menu", place.getMenu());
                intent.putExtra("longitude_altitude", place.getLongitudeLatitude());
                startActivity(intent);
            }
        });

        // Load data initially if network is available
        if (isNetworkAvailable()) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                    fetchPlaces();
                }
            });
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }

        // Set up the back button
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToMapActivity();
            }
        });
    }


    @Override
    public void onRefresh() {
        if (isNetworkAvailable()) {
            fetchPlaces();
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private void fetchPlaces() {
        swipeRefreshLayout.setRefreshing(true);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL_PLACES, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            placeList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);

                                Place place = new Place();
                                place.setId(obj.getString("place_id"));
                                place.setName(obj.getString("name"));
                                place.setAddress(obj.getString("address"));
                                place.setImgUrl(obj.getString("img"));
                                place.setOpen(obj.getString("open"));
                                place.setLongitudeLatitude(obj.getString("longitude_altitude"));
                                place.setMenu(obj.getString("menu"));

                                placeList.add(place);
                            }

                            placeAdapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SecondActivity.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = "Failed to fetch data. Error: " + error.getMessage();
                Toast.makeText(SecondActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void navigateToMapActivity() {
        Intent intent = new Intent(SecondActivity.this, MapActivity.class);
        startActivity(intent);
    }
}
