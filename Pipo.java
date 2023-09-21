import java.io.*;
import java.util.*;
import java.util.Scanner;
import java.util.Random;
import java.util.Hashtable;


class Pipo {
    String f1; // the learning file
    Scanner in1; // the scanner associated to the file
    Hashtable<String,Hashtable<String,Integer>> LangModel; // the language model
    Random generator;
    
    Pipo(String f1) {
        this.f1 = f1;
        try {in1 = new Scanner(new FileInputStream(f1)); }
        catch (Exception e) {System.out.println(e);}
        LangModel = new Hashtable<String,Hashtable<String,Integer>>();
        generator=new Random(); // Seed to be given... Eventually
    }

    public void newWorsSeq(String w1, String w2) {
        //System.out.println(" "+w1+"  "+w2+" ");
        // This is were you need to update the language model (hash of hashes)

        if (LangModel.containsKey(w1)) { // w1 is already in the language model
            Hashtable<String,Integer> h = LangModel.get(w1); // the hash of w1
            if (h.containsKey(w2)) { // w2 is already in the hash of w1
                h.put(w2, h.get(w2)+1);
            } else {// w2 is not in the hash of w1
                h.put(w2, 1); // w2 is added to the hash of w1
            }
        } else {
            Hashtable<String,Integer> h = new Hashtable<String,Integer>();
            h.put(w2, 1);
            LangModel.put(w1, h);
        }
    }

    public void Learn() {
        String word1;
        word1="."; // A ghost word beeing before the first word of the text
        try {
            while (in1.hasNext()) {
                String word2 = in1.next();
                if (word2.matches("(.*)[.,!?<>=+-/]")) {
                    // word2 is glued with a punctuation mark
                    String[] splitedWord= word2.split("(?=[.,!?<>=+-/])|(?<=])");
                    for (String s : splitedWord) {
                        newWorsSeq(word1,s); // update de language model
                        word1=s;
                    }
                } else { // word2 is a single word
                    newWorsSeq(word1,word2); // update de language model
                    word1=word2;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void Talk(int nbWord) {
        Map<String, Integer> wordCounts = new HashMap<>();
        // Taking advantage of the generative skills of the language model
        System.out.println("Compte rendu de l'apnée 3 algo 6 :");
        // génère des séquences de mots conformément aux probabilités du modèle.
        String w1 = ".";
        String w2 = "";

        for (int i=0; i<nbWord; i++) {
            Hashtable<String,Integer> h = LangModel.get(w1);
            Enumeration<String> e = h.keys();
            int sum = 0;
            while (e.hasMoreElements()) { // sum of the values of the hash
                String w = e.nextElement();
                sum += h.get(w);
            }
            int r = generator.nextInt(sum); // a random number between 0 and sum
            e = h.keys(); // reset the enumeration
            while (e.hasMoreElements()) { // find the word corresponding to r
                String w = e.nextElement();
                r -= h.get(w);
                if (r<0) { // the word is found
                    w2 = w; // the word is the next word of the sequence
                    break;
                }
            }
            if (w2.matches("(.*)[.,!?<>=+-/]")) {
                System.out.print(w2);
            } else {
                System.out.print(" "+w2);
            }
            if (wordCounts.containsKey(w2)) {
              wordCounts.put(w2, wordCounts.get(w2) + 1);
            } else {
                wordCounts.put(w2, 1);
            }
            w1 = w2;
        }
        System.out.println("\nfin du compte rendu.");

        // Sort the wordCounts map based on the frequency of each word
        List<Map.Entry<String, Integer>> wordCountList = new ArrayList<>(wordCounts.entrySet());
        wordCountList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
    
        // Print the top 10 most frequent words
        System.out.println("Top 10 most frequent words:");
        int count = 0;
        for (Map.Entry<String, Integer> entry : wordCountList) {
            if (count == 10) {
                break;
            }
            if (!entry.getKey().matches("(.*)[.,!?<>=+-/]")) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
            count++;}
        }
    }  
}