import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

interface IDataStorage{
    ArrayList<String> words();
}

interface IStopWordFilter{
    Boolean isStopWord(String word);
}

interface IWordFrequencyCounter{
    HashMap<String,Integer> incrementCount(String word);
    HashMap<String,Integer> sorted();
}

class DataStorageManager implements IDataStorage{
    ArrayList<String> data = new ArrayList<>();

    public DataStorageManager(String path){
        File indexFile = new File(path);
        String word;

        try {
            Scanner scanner = new Scanner(indexFile);
            //gets rid of characters other than alphabets
            scanner.useDelimiter("[^A-Za-z]+");
            while (scanner.hasNextLine()) {
                if(scanner.hasNext())
                {
                    word = scanner.next().toLowerCase();
                    if(word.length()!=1) {
                        data.add(word);
                    }
                }
                else
                    break;
            }
            scanner.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public ArrayList<String> words(){
        return data;
    }
}

class StopWordManager implements IStopWordFilter{
    ArrayList<String> stopWords = new ArrayList<>();

    public StopWordManager(){
        File stopWordsfile = new File("../stop_words.txt");
        String currentStopWord;
        try {
            Scanner scanner = new Scanner(stopWordsfile);
            scanner.useDelimiter(",");
            while (scanner.hasNext()) {
                currentStopWord = scanner.next();
                stopWords.add(currentStopWord);
            }
            scanner.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public Boolean isStopWord(String word){
        if(stopWords.contains(word)){
            return true;
        }
        else
            return false;
    }
}

class WordFrequencyManager implements IWordFrequencyCounter{
    HashMap<String,Integer> wordFreqs = new HashMap<>();

    @Override
    public HashMap<String,Integer> incrementCount(String word){
        int frequency;

        //add count to the pre-existing key
        if(wordFreqs.containsKey(word)){
            frequency = wordFreqs.get(word);
            frequency++;
            wordFreqs.put(word,frequency);
        }

        //add new entry to the hashmap
        else {
            frequency = 1;
            wordFreqs.put(word,frequency);
        }
        return wordFreqs;
    }

    @Override
    public HashMap<String,Integer> sorted(){
        HashMap<String, Integer> sortedWordFreqs;
        //To sort frequency
        sortedWordFreqs = wordFreqs.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));

        return sortedWordFreqs;
    }
}

class WordFrequencyController{
    DataStorageManager storageManager;
    StopWordManager stopWordManager;
    WordFrequencyManager wordFreqManager;

    public WordFrequencyController(String path){
        storageManager = new DataStorageManager(path);
        stopWordManager = new StopWordManager();
        wordFreqManager = new WordFrequencyManager();
    }

    public void run(){
        for(String word : storageManager.words()){
            if(!stopWordManager.isStopWord(word)){
                wordFreqManager.incrementCount(word);
            }
        }
        HashMap<String, Integer> sortedWordsFreqs = wordFreqManager.sorted();
        int i = 1;
        for(String word:sortedWordsFreqs.keySet()){
            System.out.println(word+" - "+ sortedWordsFreqs.get(word));
            if (i == 25)
                break;
            i++;
        }
    }
}

public class Thirteen {
    public static void main(String[] args){
        if(args.length == 1 ){
            new WordFrequencyController(args[0]).run();
        }
        else{
            System.out.println("File argument invalid!");
            System.exit(1);
        }
    }
}