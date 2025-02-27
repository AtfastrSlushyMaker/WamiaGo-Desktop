package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class BadWordFilter {

    public static Set<String> loadBadWords(String filePath) throws IOException {
        Set<String> badWords = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                badWords.add(line.trim().toLowerCase());
            }
        }
        return badWords;
    }

    public static String filterBadWords(String inputText, Set<String> badWords) {
        for (String badWord : badWords) {
            inputText = inputText.replaceAll("(?i)\\b" + badWord + "\\b", "****");
        }
        return inputText;
    }
}