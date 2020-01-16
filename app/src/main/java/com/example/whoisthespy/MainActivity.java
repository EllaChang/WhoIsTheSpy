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
    private ImageButton pc1;
    private ImageButton pc2;
    private ImageButton pc3;
    private Button more;
    private TextView textuser;
    private Button yes;
    private Button no;

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
        setContentView(R.layout.game_title);
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
        setContentView(R.layout.activity_main);
        pc1 = (ImageButton) findViewById(R.id.pc1);
        pc2 = (ImageButton) findViewById(R.id.pc2);
        pc3 = (ImageButton) findViewById(R.id.pc3);
        more = (Button) findViewById(R.id.more);
        more.setEnabled(true);
        round = 0;
        textuser = findViewById(R.id.textuser);

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
                textuser.setText(spyWord);
                p1 = new PC(goodWord);
                p1.bank = goodBank;
                p2 = new PC(goodWord);
                p2.bank = goodBank;
                p3 = new PC(goodWord);
                p3.bank = goodBank;
                break;
            case 1:
                textuser.setText(goodWord);
                p1 = new PC(spyWord);
                p1.bank = spyBank;
                p1.isSpy = true;
                p2 = new PC(goodWord);
                p2.bank = goodBank;
                p3 = new PC(goodWord);
                p3.bank = goodBank;
                break;
            case 2:
                textuser.setText(goodWord);
                p1 = new PC(goodWord);
                p1.bank = goodBank;
                p2 = new PC(spyWord);
                p2.bank = spyBank;
                p2.isSpy = true;
                p3 = new PC(goodWord);
                p3.bank = goodBank;
                break;
            case 3:
                textuser.setText(goodWord);
                p1 = new PC(goodWord);
                p1.bank = goodBank;
                p2 = new PC(goodWord);
                p2.bank = goodBank;
                p3 = new PC(spyWord);
                p3.bank = spyBank;
                p3.isSpy = true;
                break;
        }
        if (round == 0) oneRound(null);
        return true;
    }

    public void oneRound(View view) {
        TextView textpc1 = findViewById(R.id.textpc1);
        TextView textpc2 = findViewById(R.id.textpc2);
        TextView textpc3 = findViewById(R.id.textpc3);
        Random r = new Random(System.currentTimeMillis());
        //more = (Button) findViewById(R.id.more);

        // grab word description for p1
        int rand1 = r.nextInt(p1.bank.size());
        while (descriptions.contains(p1.bank.get(rand1))) {
            rand1 = r.nextInt(p1.bank.size());
        }
        textpc1.setText(p1.bank.get(rand1));
        p1.desc.add(p1.bank.get(rand1));
        descriptions.add(p1.bank.get(rand1));

        // grab word description for p2
        int rand2 = r.nextInt(p2.bank.size());
        while (descriptions.contains(p2.bank.get(rand2))) {
            rand2 = r.nextInt(p2.bank.size());
        }
        textpc2.setText(p2.bank.get(rand2));
        p2.desc.add(p2.bank.get(rand2));
        descriptions.add(p2.bank.get(rand2));

        // grab word description for p3
        int rand3 = r.nextInt(p3.bank.size());
        while (descriptions.contains(p3.bank.get(rand3))) {
            rand3 = r.nextInt(p3.bank.size());
        }
        textpc3.setText(p3.bank.get(rand3));
        p3.desc.add(p3.bank.get(rand3));
        descriptions.add(p3.bank.get(rand3));

        round++;
        System.out.println(round);
        // user can't ask for another round if there's already been 3 rounds
        if (round > 3) {
            descriptions.clear();
            more.setEnabled(false);
        }
    }

    public void accuseP1(View view) {
        round = 0;
        setContentView(R.layout.game_end);
        TextView p1text = findViewById(R.id.textpc1);
        TextView p2text = findViewById(R.id.textpc2);
        TextView p3text = findViewById(R.id.textpc3);
        yes = findViewById(R.id.yes);
        no = findViewById(R.id.no);
        if (p1.isSpy) {
            // user wins
            p1text.setText("Ah! I thought I did well! Wanna play again?");
            p2text.setText("Good job! Play again?");
            p3text.setText("Nice my boi. Let's go again?");
        } else {
            // user loses
            p1text.setText("Hey, I'm innocent! Wanna play again?");
            if (p2.isSpy) {
                p2text.setText("Haha it's me! Play again?");
                p3text.setText("Ah man, it's Vivi! Let's go again?");
            }
            else if (p3.isSpy) {
                p2text.setText("Come on man, it's Cici! Play again?");
                p3text.setText("Haha I fooled you! Let's go again?");
            } else {
                p2text.setText("It's been you all along?! Play again?");
                p3text.setText("Ah man, it's you! Let's go again?");
            }
        }
    }

    public void accuseP2(View view) {
        round = 0;
        setContentView(R.layout.game_end);
        TextView p1text = findViewById(R.id.textpc1);
        TextView p2text = findViewById(R.id.textpc2);
        TextView p3text = findViewById(R.id.textpc3);
        yes = findViewById(R.id.yes);
        no = findViewById(R.id.no);
        if (p2.isSpy) {
            p2text.setText("How did you know it was me?! Play again?");
            p1text.setText("You ARE a genius. Wanna play again?");
            p3text.setText("Sherlock Holmes would admire you, too. Let's go again?");
        } else {
            p2text.setText("How could you, the spy, suspect me. Go again?");
            if (p1.isSpy) {
                p1text.setText("LOL it's me! Wanna play again?");
                p3text.setText("Of course it's Kiki! Play again?");
            } else if (p3.isSpy) {
                p1text.setText("It's Cici! Wanna play again?");
                p3text.setText("Yasss I fooled you! Play again?");
            } else {
                p1text.setText("Ah it's you man! Wanna play again?");
                p3text.setText("It's been you all this time! Play again?");
            }
        }
    }

    public void accuseP3(View view) {
        round = 0;
        setContentView(R.layout.game_end);
        TextView p1text = findViewById(R.id.textpc1);
        TextView p2text = findViewById(R.id.textpc2);
        TextView p3text = findViewById(R.id.textpc3);
        yes = findViewById(R.id.yes);
        no = findViewById(R.id.no);
        if (p3.isSpy) {
            p3text.setText("Did you cheat?! Shall we go again?");
            p1text.setText("What a detective you are. Play again?");
            p2text.setText("Nicely done my boi. Let's go again?");
        } else {
            p3text.setText("How could you think it was me! Go again?");
            if (p1.isSpy) {
                p1text.setText("LOL it's me! Wanna play again?");
                p2text.setText("Of course it's Kiki! Play again?");
            } else if (p2.isSpy) {
                p1text.setText("It's Vivi! Wanna play again?");
                p2text.setText("Yasss I fooled you! Play again?");
            } else {
                p1text.setText("It's you! Wanna play again?");
                p2text.setText("It's not Cici but you! Play again?");
            }
        }
    }

    public void accuseMe(View view) {
        round = 0;
        setContentView(R.layout.game_end);
        TextView p1text = findViewById(R.id.textpc1);
        TextView p2text = findViewById(R.id.textpc2);
        TextView p3text = findViewById(R.id.textpc3);
        yes = findViewById(R.id.yes);
        no = findViewById(R.id.no);
        if (userIsSpy) {
            p1text.setText("LOL you're good at this. Play again?");
            p2text.setText("Nicely done my spy. Let's go again?");
            p3text.setText("Did you cheat?! Shall we go again?");
        } else {
            if (p1.isSpy) {
                p1text.setText("It's me LMAO. Play again?");
                p2text.setText("It's Kiki! Let's go again?");
                p3text.setText("Man, you're not the spy! Shall we go again?");
            } else if (p2.isSpy) {
                p1text.setText("Of course it's Vivi! Play again?");
                p2text.setText("Nicely done my boi. Let's go again?");
                p3text.setText("Did you cheat?! Shall we go again?");
            } else {
                p1text.setText("What a detective you are. Play again?");
                p2text.setText("Nicely done my boi. Let's go again?");
                p3text.setText("Did you cheat?! Shall we go again?");
            }
        }
    }

    public void restart(View view) {
        round = 0;
        setContentView(R.layout.activity_main);
        onStart(null);
    }

    public void back(View view) {
        round = 0;
        setContentView(R.layout.game_title);
    }
}