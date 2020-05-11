import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Program follows Constructivist style where program checks
 * for arguments' sanity in every function. When the arguments are unreasonable,
 * the function returns something sensible. The execution continues in spite of
 * unreasonable values.
 */
public class Twenty {
    private static ArrayList<String> extractWords(String pathToFile){
        ArrayList<String> wordList = new ArrayList<>();

        String word;
        try {
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
        }catch (IOException ex){
            System.out.println(ex.getMessage());
            System.out.println("Proceeding with empty word list!");
            return new ArrayList<>();
        }
        return wordList;
    }

    private static ArrayList<String> removeStopWords(ArrayList<String> wordList){
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
        }catch (IOException ex){
            System.out.println(ex.getMessage());
            System.out.println("Proceeding with word list that contains stop words!");
            return wordList;
        }

        //Adding words only if it is not a stop word
        for (String str:wordList){
            if(!stopWords.contains(str)){
                words.add(str);
            }
        }
        return words;
    }

    private static HashMap<String,Integer> frequencies(ArrayList<String> wordList){
        HashMap<String,Integer> wordFreqs = new HashMap<>();

        if(wordList.isEmpty()){
            System.out.println("Proceeding with empty frequency map!");
            return wordFreqs;
        }

        for(String word:wordList){
            wordFreqs.put(word,wordFreqs.getOrDefault(word,0)+1);
        }
        return wordFreqs;
    }

    private static HashMap<String,Integer> sort(HashMap<String,Integer> wordFreq){
        HashMap<String, Integer> sortedWordsFreqs = new HashMap<>();

        if(wordFreq.isEmpty()){
            System.out.println("Proceeding with empty sorted frequency map!");
            return sortedWordsFreqs;
        }

        sortedWordsFreqs = wordFreq.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));

        return sortedWordsFreqs;
    }

    public static void main(String[] args) {
        String pathToFile;
        if(args.length < 1 ){
            System.out.println("No file paths were mentioned..");
            System.out.println("Proceeding with path as ../pride-and-prejudice.txt");
            pathToFile = "../pride-and-prejudice.txt";
        }
        else
            pathToFile = args[0];
        List<HashMap.Entry<String, Integer>> sortedWordsFreqList = new ArrayList<>((sort(frequencies(removeStopWords(extractWords(pathToFile))))).entrySet());
        if(sortedWordsFreqList.isEmpty()){
            System.out.println("Empty sorted frequency list..exiting program!!");
            System.exit(0);
        }

        for(HashMap.Entry<String,Integer> entry:sortedWordsFreqList.subList(0,25)){
            System.out.println(entry.getKey()+" - "+entry.getValue());
        }
    }
}
