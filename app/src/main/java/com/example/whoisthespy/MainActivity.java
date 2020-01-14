package com.example.whoisthespy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.*;
import android.widget.*;
import android.view.*;
import java.util.*;
import java.io.*;

public class MainActivity extends AppCompatActivity {
    private HashMap<String, ArrayList<String>> wordMap;
    private ArrayList<ArrayList<String>> pairs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AssetManager assetManager = getAssets();
        wordMap = new HashMap<>();
        pairs = new ArrayList<>();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            int count = 0, pairCount = 1;
            while((line = in.readLine()) != null) {
                String[] splitted = line.split("\\s+");
                ArrayList<String> properties = new ArrayList<>();
                wordMap.put(splitted[0], properties);
                for (int i = 1; i < splitted.length; i++) {
                    String property = splitted[i];
                    properties.add(property.replace("_", " "));
                    wordMap.put(splitted[0], properties);
                }
                if (pairCount == 1) {
                    ArrayList<String> pair = new ArrayList<>();
                    pair.add(splitted[0]);
                    pairs.add(pair);
                    pairCount++;
                } else {
                    pairs.get(count).add(splitted[0]);
                    count++;
                    pairCount--;
                }
            }
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
        onStart(null);
    }

    public boolean onStart(View view) {
        return true;
    }
}