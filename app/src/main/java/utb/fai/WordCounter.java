package utb.fai;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WordCounter {
    private static final WordCounter INSTANCE = new WordCounter();
    private final ConcurrentHashMap<String, Integer> counts = new ConcurrentHashMap<>();

    private WordCounter() {}

    public static WordCounter getInstance() {
        return INSTANCE;
    }

    public void addWord(String word) {
        Integer c = counts.get(word);

        if (c == null) {
            counts.put(word, 1);
        } else {
            counts.put(word, c + 1);
        }
    }

    public void printTopWords(int n) {
        counts.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .limit(n)
            .forEach(e -> System.out.println(e.getKey() + ";" + e.getValue()));
    }
}