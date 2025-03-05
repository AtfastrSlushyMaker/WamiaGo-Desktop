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
        if (inputText == null || inputText.isEmpty()) {
            return inputText;
        }

        // Diviser le texte en mots en conservant la ponctuation
        String[] words = inputText.split("(?<=\\W)|(?=\\W)");

        // Parcourir chaque mot et le remplacer s'il est interdit
        for (int i = 0; i < words.length; i++) {
            String word = words[i].replaceAll("\\W", "").toLowerCase(); // Ignorer la ponctuation
            if (badWords.contains(word)) {
                words[i] = words[i].replaceAll("\\w+", "****"); // Remplacer uniquement la partie alphabétique
            }
        }

        // Reconstruire le texte filtré
        return String.join("", words);
    }
}