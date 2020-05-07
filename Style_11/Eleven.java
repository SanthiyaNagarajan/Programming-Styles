import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

class DataStorageManager{
    ArrayList<String> data = new ArrayList<>();

    public ArrayList<String> dispatch(String[] message){
        if(message[0].equals("init")){
            init(message[1]);
            return null;
        }
        else if(message[0].equals("words"))
            return words();
        else{
            System.out.println("Message not understood "+message[0]);
            return null;
        }
    }

    private void init(String path){
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

    private ArrayList<String> words(){
        return data;
    }
}

class StopWordManager{
    ArrayList<String> stopWords = new ArrayList<>();

    public Boolean dispatch(String[] message){
        if(message[0].equals("init")){
            return init();
        }
        else if(message[0].equals("is_stop_word"))
            return isStopWord(message[1]);
        else{
            System.out.println("Message not understood "+message[0]);
            return true;
        }
    }

    private Boolean init(){
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
        return true;
    }

    private Boolean isStopWord(String word){
        if(stopWords.contains(word)){
            return true;
        }
        else
            return false;
    }
}

class WordFrequencyManager{
    HashMap<String,Integer> wordFreqs = new HashMap<>();

    public HashMap<String,Integer> dispatch(String[] message){
        if(message[0].equals("increment_count")){
            return incrementCount(message[1]);
        }
        else if(message[0].equals("sorted"))
            return sorted();
        else{
            System.out.println("Message not understood "+message[0]);
            return null;
        }
    }

    private HashMap<String,Integer> incrementCount(String word){
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

    private HashMap<String,Integer> sorted(){
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

    public void dispatch(String[] message){
        if(message[0].equals("init"))
            init(message[1]);
        else if(message[0].equals("run"))
            run();
        else{
            System.out.println("Message not understood "+ message[0]);
        }
    }
    private void init(String path){
        storageManager = new DataStorageManager();
        stopWordManager = new StopWordManager();
        wordFreqManager = new WordFrequencyManager();
        storageManager.dispatch(new String[]{"init",path});
        stopWordManager.dispatch(new String[]{"init"});
    }

    private void run(){
        for(String word : storageManager.dispatch(new String[]{"words"})){
            if(!stopWordManager.dispatch(new String[]{"is_stop_word",word})){
                wordFreqManager.dispatch(new String[]{"increment_count",word});
            }
        }
        HashMap<String, Integer> sorted_words_freqs = wordFreqManager.dispatch(new String[]{"sorted"});
        int i = 1;
        for(String word:sorted_words_freqs.keySet()){
            System.out.println(word+" - "+ sorted_words_freqs.get(word));
            if (i == 25)
                break;
            i++;
        }
    }
}

public class Eleven {
    public static void main(String[] args){
        if(args.length == 1 ){
            WordFrequencyController wfController = new WordFrequencyController();
            wfController.dispatch(new String[]{"init", args[0]});
            wfController.dispatch(new String[]{"run"});
        }
        else{
            System.out.println("File argument invalid!");
            System.exit(1);
        }
    }
}