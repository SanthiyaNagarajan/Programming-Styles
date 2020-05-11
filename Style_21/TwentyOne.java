import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Program follows the tantrum style where every function
 * checks for arguments' sanity and refuses to continue if the arguments are
 * unreasonable.
 */
public class TwentyOne {
    private static ArrayList<String> extractWords(String pathToFile) throws Exception {
        ArrayList<String> wordList = new ArrayList<>();
        String word;
        try {
            assert(!pathToFile.isEmpty()):"I need a non-empty string!";
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
        }catch (AssertionError | IOException ex){
            System.out.println("I quit!");
            throw new Exception(ex.getMessage());
        }
        return wordList;
    }

    private static ArrayList<String> removeStopWords(ArrayList<String> wordList) throws Exception {
        ArrayList<String> words = new ArrayList<>();
        ArrayList<String> stopWords = new ArrayList<>();
        String currentStopWord;

        try {
            Scanner scanner = new Scanner(new File("../stop_words.txt"));
            scanner.useDelimiter(",");
            while (scanner.hasNext()) {
                currentStopWord = scanner.next();
                stopWords.add(currentStopWord);
            }
            scanner.close();
        }catch (AssertionError | IOException ex){
            System.out.println("I quit!");
            throw new Exception(ex.getMessage());
        }

        //Adding words only if it is not a stop word
        for (String str:wordList){
            if(!stopWords.contains(str)){
                words.add(str);
            }
        }
        return words;
    }

    private static HashMap<String,Integer> frequencies(ArrayList<String> wordList) throws Exception {
        HashMap<String,Integer> wordFreqs = new HashMap<>();

        try{
            assert(!wordList.isEmpty()):"I need a non-empty list!";
            for(String word:wordList){
                wordFreqs.put(word,wordFreqs.getOrDefault(word,0)+1);
            }
        }catch (AssertionError | Exception ex){
            throw new Exception("Frequencies threw "+ex.getMessage());
        }

        return wordFreqs;
    }

    private static HashMap<String,Integer> sort(HashMap<String,Integer> wordFreq) throws Exception {
        HashMap<String, Integer> sortedWordsFreqs;

        try{
            assert(!wordFreq.isEmpty()):"I need a non-empty hashmap!";
            sortedWordsFreqs = wordFreq.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (e1, e2) -> e1, LinkedHashMap::new));

            return sortedWordsFreqs;
        }catch (AssertionError | Exception ex){
            throw new Exception("Sort threw "+ex.getMessage());
        }
    }

    /**
     * All the functions except main throw exception. This is caught by the
     * main function and printed out. We are passing the exception up the stack.
     */
    public static void main(String[] args) {
        String pathToFile;
        try{
            assert (args.length >= 1): "I need an input file!";
            pathToFile = args[0];
            List<HashMap.Entry<String, Integer>> sortedWordsFreqList = new ArrayList<>((sort(frequencies(removeStopWords(extractWords(pathToFile))))).entrySet());

            assert (sortedWordsFreqList.size()>=25):"That's less than 25 words mate!";

            for (HashMap.Entry<String, Integer> entry : sortedWordsFreqList.subList(0, 25)) {
                System.out.println(entry.getKey() + " - " + entry.getValue());
            }
        }catch (AssertionError | Exception ex) {
            System.out.println("Something wrong: "+ex.getMessage());
        }
    }
}
