package com.example.simpleapilistapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity {

    ListView postListView;
    ArrayList<HashMap<String, String>> postList = new ArrayList<>();
    SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        postListView = findViewById(R.id.postListView);

        adapter = new SimpleAdapter(
                this,
                postList,
                R.layout.item_post,
                new String[]{"title", "body"},
                new int[]{R.id.titleText, R.id.bodyText}
        );

        postListView.setAdapter(adapter);
        fetchPosts();
    }

    private void fetchPosts() {
        new Thread(() -> {
            try {
                URL url = new URL("https://jsonplaceholder.typicode.com/posts");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                );

                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONArray jsonArray = new JSONArray(result.toString());

                for (int i = 0; i < 20; i++) {
                    JSONObject post = jsonArray.getJSONObject(i);

                    HashMap<String, String> item = new HashMap<>();
                    item.put("title", post.getString("title"));
                    item.put("body", post.getString("body"));
                    postList.add(item);
                }

                new Handler(Looper.getMainLooper()).post(() -> adapter.notifyDataSetChanged());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
