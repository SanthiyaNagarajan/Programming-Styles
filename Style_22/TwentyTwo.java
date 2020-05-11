import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Program follows the Passive Aggressive style where every
 * function checks for the sanity of the arguments and breaks execution
 * if not. Exception handling happens in the higher function calls
 */
public class TwentyTwo {
    private static ArrayList<String> extractWords(String pathToFile) throws IOException {
        ArrayList<String> wordList = new ArrayList<>();
        String word;
        assert(!pathToFile.isEmpty()):"I need a non-empty string! I quit";

        Scanner scanner = new Scanner(new File(pathToFile));
        scanner.useDelimiter("[^A-Za-z]+");

        while (scanner.hasNextLine()) {
            if(scanner.hasNext())
            {
                word = scanner.next().toLowerCase();
                if(word.length()!=1) {
                    wordList.add(word);
                }
            }
            else
                break;
        }
        scanner.close();
        return wordList;
    }

    private static ArrayList<String> removeStopWords(ArrayList<String> wordList) throws IOException {
        ArrayList<String> words = new ArrayList<>();
        ArrayList<String> stopWords = new ArrayList<>();
        String currentStopWord;

        Scanner scanner = new Scanner(new File("../stop_words.txt"));
        scanner.useDelimiter(",");
        while (scanner.hasNext()) {
            currentStopWord = scanner.next();
            stopWords.add(currentStopWord);
        }
        scanner.close();

        //Adding words only if it is not a stop word
        for (String str:wordList){
            if(!stopWords.contains(str)){
                words.add(str);
            }
        }
        return words;
    }

    private static HashMap<String,Integer> frequencies(ArrayList<String> wordList) {
        HashMap<String,Integer> wordFreqs = new HashMap<>();

        assert(!wordList.isEmpty()):"I need a non-empty list! I quit!";
        for(String word:wordList){
            wordFreqs.put(word,wordFreqs.getOrDefault(word,0)+1);
        }

        return wordFreqs;
    }

    private static HashMap<String,Integer> sort(HashMap<String,Integer> wordFreq)  {
        HashMap<String, Integer> sortedWordsFreqs;

        assert(!wordFreq.isEmpty()):"I need a non-empty hashmap! I quit!";
        sortedWordsFreqs = wordFreq.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));

        return sortedWordsFreqs;
    }

    /**
     * Other functions are not guarded with try-catch blocks and simply passes the exception
     * up to the caller and the exceptions are handled by the main function
     */
    public static void main(String[] args) {
        String pathToFile;
        try{
            assert (args.length >= 1): "I need an input file! I quit!";
            pathToFile = args[0];
            List<HashMap.Entry<String, Integer>> sortedWordsFreqList = new ArrayList<>((sort(frequencies(removeStopWords(extractWords(pathToFile))))).entrySet());

            assert (sortedWordsFreqList.size()>=25):"That's less than 25 words mate! I quit!";

            for (HashMap.Entry<String, Integer> entry : sortedWordsFreqList.subList(0, 25)) {
                System.out.println(entry.getKey() + " - " + entry.getValue());
            }
        }catch (AssertionError | Exception ex) {
            System.out.println("Something wrong: "+ex.getMessage());
        }
    }
}
