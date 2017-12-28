// CS 540 HW 07
// Yien Xu
// yxu322@wisc.edu

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;


public class Chatbot {

    /**
     * This class is implemented as a tuple with three elements in Python. All fields (elements) in
     * this class are unchangeable (final).
     * 
     * @author yienxu
     *
     */
    private static class Tuple {
        public final int word;
        public final double left;
        public final double right;

        /**
         * Initializes a new Tuple.
         * 
         * @param word an integer that represents a word
         * @param left the left bound of the probability interval
         * @param right the right bound of the probability interval
         */
        public Tuple(int word, double left, double right) {
            this.word = word;
            this.left = left;
            this.right = right;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append('(').append(left).append(',').append(right).append(')');
            return builder.toString();
        }
    }

    private static String filename = "./WARC201709_wid.txt";

    private static ArrayList<Integer> readCorpus() {
        ArrayList<Integer> corpus = new ArrayList<Integer>();
        try {
            File f = new File(filename);
            Scanner sc = new Scanner(f);
            while (sc.hasNext()) {
                if (sc.hasNextInt()) {
                    int i = sc.nextInt();
                    corpus.add(i);
                } else {
                    sc.next();
                }
            }
            sc.close();
        } catch (FileNotFoundException ex) {
            System.out.println("File Not Found.");
        }
        return corpus;
    }

    /**
     * Returns the probability intervals by summing each probability individually.
     * 
     * @param probabilities
     * @param numWords the number of words (word types) in the corpus
     * @return Probability Intervals
     */
    private static Tuple[] getTuplesByProbabilities(HashMap<Integer, Double> probabilities,
        int numWords) {

        int iter = 0;
        Tuple[] tuples = new Tuple[probabilities.size()];
        for (int word = 0; word < numWords; word++) {
            if (probabilities.containsKey(word)) {
                double prob = probabilities.get(word);
                if (iter == 0) {
                    tuples[iter] = new Tuple(word, 0.0, prob);
                } else {
                    tuples[iter] =
                        new Tuple(word, tuples[iter - 1].right, tuples[iter - 1].right + prob);
                }
                iter++;
            }
        }
        return tuples;
    }

    /**
     * Generates Probability Intervals using Unigram models.
     * 
     * @param corpus
     * @param numWords the number of words (word types) in the corpus
     * @return Probability Intervals
     */
    private static Tuple[] generateUnigramTuples(ArrayList<Integer> corpus, int numWords) {
        HashMap<Integer, Integer> counts = new HashMap<>();
        for (int word : corpus) {
            counts.put(word, counts.getOrDefault(word, 0) + 1);
        }

        HashMap<Integer, Double> probabilities = new HashMap<>();
        for (Integer word : counts.keySet()) {
            probabilities.put(word, (double) counts.get(word) / corpus.size());
        }

        return getTuplesByProbabilities(probabilities, numWords);
    }

    /**
     * Generates Probability Intervals using Bigram models.
     * 
     * @param corpus
     * @param numWords the number of words (word types) in the corpus
     * @param h the history word
     * @return Probability Intervals
     */
    private static Tuple[] generateBigramTuples(ArrayList<Integer> corpus, int numWords, int h) {
        int numWordsAfterH = 0;
        HashMap<Integer, Integer> counts = new HashMap<>();
        for (int index = 0; index < corpus.size() - 1; index++) {
            if (corpus.get(index).equals(h)) {
                numWordsAfterH++;
                int word = corpus.get(index + 1);
                counts.put(word, counts.getOrDefault(word, 0) + 1);
            }
        }

        HashMap<Integer, Double> probabilities = new HashMap<>();
        for (Integer word : counts.keySet()) {
            probabilities.put(word, (double) counts.get(word) / numWordsAfterH);
        }

        return getTuplesByProbabilities(probabilities, numWords);
    }

    /**
     * Generates Probability Intervals using Trigram models.
     * 
     * @param corpus
     * @param numWords the number of words (word types) in the corpus
     * @param h1 the first history word
     * @param h2 the second history word
     * @return <tt>null</tt> if no Trigram models can be found; otherwise, the Probability
     *         Intervals.
     */
    private static Tuple[] generateTrigramTuples(ArrayList<Integer> corpus, int numWords, int h1,
        int h2) {

        int numWordsAfterH1H2 = 0;
        HashMap<Integer, Integer> counts = new HashMap<>();
        for (int index = 0; index < corpus.size() - 2; index++) {
            if (corpus.get(index).equals(h1) && corpus.get(index + 1).equals(h2)) {
                numWordsAfterH1H2++;
                int word = corpus.get(index + 2);
                counts.put(word, counts.getOrDefault(word, 0) + 1);
            }
        }
        if (numWordsAfterH1H2 == 0) {
            return null;
        }

        HashMap<Integer, Double> probabilities = new HashMap<>();
        for (Integer word : counts.keySet()) {
            probabilities.put(word, (double) counts.get(word) / numWordsAfterH1H2);
        }

        return getTuplesByProbabilities(probabilities, numWords);
    }

    /**
     * Prints the word and its Probability Interval, using pseudo-probability r
     * 
     * @param r the pseudo-probability
     * @param tuples the Probability Intervals
     */
    private static void printWordAndInterval(double r, Tuple[] tuples) {
        if (r == 0) {
            System.out.println(tuples[0].word);
            System.out.printf("%.7f\n", tuples[0].left);
            System.out.printf("%.7f\n", tuples[0].right);
            return;
        }
        for (Tuple tuple : tuples) {
            if (r > tuple.left && r <= tuple.right) {
                System.out.println(tuple.word);
                System.out.printf("%.7f\n", tuple.left);
                System.out.printf("%.7f\n", tuple.right);
                return;
            }
        }
    }

    /**
     * Gets the word type through its Probability Interval, using pseudo-probability r
     * 
     * @param r the pseudo-probability
     * @param tuples the Probability Intervals
     * @return
     */
    private static int getWordByTuples(double r, Tuple[] tuples) {
        if (r == 0) {
            return tuples[0].word;
        }
        for (Tuple tuple : tuples) {
            if (r > tuple.left && r <= tuple.right) {
                return tuple.word;
            }
        }
        throw new IllegalArgumentException();
    }

    public static void main(String[] args) {
        ArrayList<Integer> corpus = readCorpus();
        int flag = Integer.valueOf(args[0]);
        int numWords = 0; // number of word types
        for (int i : corpus) {
            if (i > numWords) {
                numWords = i;
            }
        }
        numWords++;

        if (flag == 100) {
            int w = Integer.valueOf(args[1]);
            int count = 0;
            for (int i : corpus) {
                if (w == i) {
                    count++;
                }
            }

            System.out.println(count);
            System.out.println(String.format("%.7f", count / (double) corpus.size()));

        } else if (flag == 200) {
            int n1 = Integer.valueOf(args[1]);
            int n2 = Integer.valueOf(args[2]);
            double r = (double) n1 / n2;
            Tuple[] tuples = generateUnigramTuples(corpus, numWords);
            printWordAndInterval(r, tuples);

        } else if (flag == 300) {
            int h = Integer.valueOf(args[1]);
            int w = Integer.valueOf(args[2]);
            int count = 0;
            ArrayList<Integer> words_after_h = new ArrayList<Integer>();
            for (int index = 0; index < corpus.size() - 1; index++) {
                if (corpus.get(index).equals(h)) {
                    words_after_h.add(corpus.get(index + 1));
                    if (corpus.get(index + 1).equals(w)) {
                        count++;
                    }
                }
            }

            System.out.println(count);
            System.out.println(words_after_h.size());
            System.out.println(String.format("%.7f", count / (double) words_after_h.size()));

        } else if (flag == 400) {
            int n1 = Integer.valueOf(args[1]);
            int n2 = Integer.valueOf(args[2]);
            int h = Integer.valueOf(args[3]);
            double r = (double) n1 / n2;
            Tuple[] tuples = generateBigramTuples(corpus, numWords, h);
            printWordAndInterval(r, tuples);

        } else if (flag == 500) {
            int h1 = Integer.valueOf(args[1]);
            int h2 = Integer.valueOf(args[2]);
            int w = Integer.valueOf(args[3]);
            int count = 0;
            ArrayList<Integer> words_after_h1h2 = new ArrayList<Integer>();
            for (int index = 0; index < corpus.size() - 2; index++) {
                if (corpus.get(index).equals(h1) && corpus.get(index + 1).equals(h2)) {
                    int word = corpus.get(index + 2);
                    words_after_h1h2.add(word);
                    if (word == w) {
                        count++;
                    }
                }
            }

            System.out.println(count);
            System.out.println(words_after_h1h2.size());
            if (words_after_h1h2.size() == 0)
                System.out.println("undefined");
            else
                System.out.println(String.format("%.7f", count / (double) words_after_h1h2.size()));

        } else if (flag == 600) {
            int n1 = Integer.valueOf(args[1]);
            int n2 = Integer.valueOf(args[2]);
            int h1 = Integer.valueOf(args[3]);
            int h2 = Integer.valueOf(args[4]);
            double r = (double) n1 / n2;
            Tuple[] tuples = generateTrigramTuples(corpus, numWords, h1, h2);
            if (tuples == null) {
                System.out.println("undefined");
            } else {
                printWordAndInterval(r, tuples);
            }

        } else if (flag == 700) {
            int seed = Integer.valueOf(args[1]);
            int t = Integer.valueOf(args[2]);
            int h1 = 0;
            int h2 = 0;

            Random rng = new Random();
            if (seed != -1)
                rng.setSeed(seed);

            if (t == 0) {
                double r = rng.nextDouble();
                // Generate first word using r
                Tuple[] tuples = generateUnigramTuples(corpus, numWords);
                h1 = getWordByTuples(r, tuples);
                System.out.println(h1);
                if (h1 == 9 || h1 == 10 || h1 == 12) {
                    return;
                }

                r = rng.nextDouble();
                // Generate second word using r
                tuples = generateBigramTuples(corpus, numWords, h1);
                h2 = getWordByTuples(r, tuples);
                System.out.println(h2);
            } else if (t == 1) {
                h1 = Integer.valueOf(args[3]);
                double r = rng.nextDouble();
                // Generate second word using r
                Tuple[] tuples = generateBigramTuples(corpus, numWords, h1);
                h2 = getWordByTuples(r, tuples);
                System.out.println(h2);
            } else if (t == 2) {
                h1 = Integer.valueOf(args[3]);
                h2 = Integer.valueOf(args[4]);
            }

            while (h2 != 9 && h2 != 10 && h2 != 12) {
                double r = rng.nextDouble();
                int w = 0;
                // Generate new word using h1,h2
                Tuple[] tuples = generateTrigramTuples(corpus, numWords, h1, h2);
                // if new word couldn't be found through Trigrams, use Bigrams
                if (tuples == null) {
                    tuples = generateBigramTuples(corpus, numWords, h2);
                }
                w = getWordByTuples(r, tuples);
                System.out.println(w);
                h1 = h2;
                h2 = w;
            }
        }

        return;
    }

}
