import java.io.*;
import java.util.Scanner;
import java.util.Random;
import java.util.Hashtable;
import java.util.Enumeration;


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
            w1 = w2;
        }
        System.out.println("\nfin du compte rendu.");
    }
    /*Pour vérifier que votre programme génère bien des mots d'une manière qui est «conforme» au modèle créé dans la phase d'apprentissage il serait nécessaire de réaliser un/des tests statistiques.

On se propose ici de vérifier «simplement» que les fréquences observées pour les mots de plus hautes fréquences sont conformes au modèle (i.e. à la fréquence observée dans le texte d'apprentissage). La fréquence f d'un mot dans un texte est son nombre d'occurrence n dans le texte divisé par le nombre de mots total nt du texte : f=n/nt */

    void test() {
        // This is were you need to test the language model
        // You can use the following code to test the
        // frequency of the words in the learning file
        // and compare it with the frequency in the language model
        Hashtable<String, Integer> h = new Hashtable<String, Integer>();
        int nbWord = 0;
        try {
            while (in1.hasNext()) {
                String word = in1.next();
                if (h.containsKey(word)) {
                    h.put(word, h.get(word) + 1);
                } else {
                    h.put(word, 1);
                }
                nbWord++;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        Enumeration<String> e = h.keys();
        while (e.hasMoreElements()) {
            String w = e.nextElement();
            System.out.println(w + " " + h.get(w) + " " + (double) h.get(w) / nbWord);
        }
    }
}

