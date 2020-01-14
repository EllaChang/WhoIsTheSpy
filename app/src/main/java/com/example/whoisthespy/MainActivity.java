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
    private String goodWord;
    private String spyWord;
    private PC p1;
    private PC p2;
    private PC p3;
    private String userWord;
    private boolean userIsSpy;
    private HashMap<String, ArrayList<String>> currWords = new HashMap<>();

    /**
     * A class representing PC players.
     */
    public class PC {
        String word;
        public PC(String word) { this.word = word; }
    }

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

                // record the word itself
                wordMap.put(splitted[0], properties);

                // record the word's descriptions that follow it
                for (int i = 1; i < splitted.length; i++) {
                    String property = splitted[i];
                    properties.add(property.replace("_", " "));
                    wordMap.put(splitted[0], properties);
                }

                // record which two words are pairs
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
        System.out.println(pairs.size());
    }

    public boolean onStart(View view) {
        // pick currWord and spyWord
        Random r = new Random(System.currentTimeMillis());
        int rand1 = r.nextInt(pairs.size());
        int rand2 = r.nextInt(2);
        if (rand2 == 0) {
            goodWord = pairs.get(rand1).get(0);
            spyWord = pairs.get(rand1).get(1);
        } else {
            goodWord = pairs.get(rand1).get(1);
            spyWord = pairs.get(rand1).get(0);
        }
        currWords.put(goodWord, new ArrayList<String>());
        currWords.put(spyWord, new ArrayList<String>());

        // assign who is the spy
        int rand3 = r.nextInt(4);
        switch (rand3) {
            case 0:
                userIsSpy = true;
                userWord = spyWord;
                p1 = new PC(goodWord);
                p2 = new PC(goodWord);
                p3 = new PC(goodWord);
                break;
            case 1:
                userIsSpy = false;
                userWord = goodWord;
                p1 = new PC(spyWord);
                p2 = new PC(goodWord);
                p3 = new PC(goodWord);
                break;
            case 2:
                userIsSpy = false;
                userWord = goodWord;
                p1 = new PC(goodWord);
                p2 = new PC(spyWord);
                p3 = new PC(goodWord);
                break;
            case 3:
                userIsSpy = false;
                userWord = goodWord;
                p1 = new PC(goodWord);
                p2 = new PC(goodWord);
                p3 = new PC(spyWord);
                break;
        }
        return true;
    }
}