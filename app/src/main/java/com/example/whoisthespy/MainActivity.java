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
    private ArrayList<String> goodBank;
    private ArrayList<String> spyBank;
    private PC p1;
    private PC p2;
    private PC p3;
    private boolean userIsSpy = false;
    private int round = 1;
    private HashSet<String> descriptions = new HashSet<>();
    private ArrayList<String> userDesc = new ArrayList<>();

    /**
     * A class representing PC players.
     */
    public class PC {
        boolean isSpy = false;
        String word;
        ArrayList<String> bank;
        ArrayList<String> desc = new ArrayList<>();
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
        goodBank = wordMap.get(goodWord);
        spyBank = wordMap.get(spyWord);

        // assign who is the spy
        int rand3 = r.nextInt(4);
        switch (rand3) {
            case 0:
                userIsSpy = true;
                p1 = new PC(goodWord);
                p1.bank = goodBank;
                p2 = new PC(goodWord);
                p2.bank = goodBank;
                p3 = new PC(goodWord);
                p3.bank = goodBank;
                break;
            case 1:
                p1 = new PC(spyWord);
                p1.bank = spyBank;
                p1.isSpy = true;
                p2 = new PC(goodWord);
                p2.bank = goodBank;
                p3 = new PC(goodWord);
                p3.bank = goodBank;
                break;
            case 2:
                p1 = new PC(goodWord);
                p1.bank = goodBank;
                p2 = new PC(spyWord);
                p2.bank = spyBank;
                p2.isSpy = true;
                p3 = new PC(goodWord);
                p3.bank = goodBank;
                break;
            case 3:
                p1 = new PC(goodWord);
                p1.bank = goodBank;
                p2 = new PC(goodWord);
                p2.bank = goodBank;
                p3 = new PC(spyWord);
                p3.bank = spyBank;
                p3.isSpy = true;
                break;
        }
        if (round == 1) { oneRound(); }
        return true;
    }

    public void oneRound() {
        System.out.println("ss");
        Random r = new Random(System.currentTimeMillis());

        // p1
        int rand1 = r.nextInt(p1.bank.size());
        while (descriptions.contains(p1.bank.get(rand1))) {
            rand1 = r.nextInt(p1.bank.size());
        }
        p1.desc.add(p1.bank.get(rand1));
        descriptions.add(p1.bank.get(rand1));

        // p2
        int rand2 = r.nextInt(p2.bank.size());
        while (descriptions.contains(p2.bank.get(rand2))) {
            rand2 = r.nextInt(p2.bank.size());
        }
        p2.desc.add(p2.bank.get(rand2));
        descriptions.add(p2.bank.get(rand2));

        // p3
        int rand3 = r.nextInt(p3.bank.size());
        while (descriptions.contains(p3.bank.get(rand3))) {
            rand3 = r.nextInt(p3.bank.size());
        }
        p3.desc.add(p3.bank.get(rand3));
        descriptions.add(p3.bank.get(rand3));

        System.out.println(p1.desc.get(p1.desc.size()-1));
        System.out.println(p2.desc.get(p2.desc.size()-1));
        System.out.println(p3.desc.get(p3.desc.size()-1));
        round++;
    }
}