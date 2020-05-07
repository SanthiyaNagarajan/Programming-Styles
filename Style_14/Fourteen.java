import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
The following interface definition is used in the program to implement
instances of Function and its corresponding apply method.

@FunctionalInterface
public interface Function<T, R> {
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
    R apply(T t);
}
*/

class WordFrequencyFramework{
    List<Function> loadEventHandler = new ArrayList<>();
    List<Function> doWorkEventHandler = new ArrayList<>();
    List<Function> endEventHandler = new ArrayList<>();

    /**
     * The application has been decomposed into three phases
     * a load phase, a doWork phase and an end phase.
     * Other entities of the application register for callbacks
     * for each of those phases by calling the following functions
     */

    public void registerForLoadEvent(Function handler){
        this.loadEventHandler.add(handler);
    }

    public void registerForDoworkEvent(Function handler){
        this.doWorkEventHandler.add(handler);
    }

    public void registerForEndEvent(Function handler){
        this.endEventHandler.add(handler);
    }

    public void run(String path){
        for(Function handler:loadEventHandler){
            handler.apply(path);
        }

        for(Function handler: doWorkEventHandler){
            handler.apply("");
        }

        for (Function handler:endEventHandler){
            handler.apply("");
        }
    }
}

class DataStorage{
    private ArrayList<String> data = new ArrayList<>();
    private StopWordFilter stopWordFilter;

    //class produces word events that other entities register for
    private List<Function> wordEventHandlers = new ArrayList<>();

    public DataStorage(WordFrequencyFramework wfapp,StopWordFilter stopWordFilter){
        this.stopWordFilter = stopWordFilter;
        wfapp.registerForLoadEvent(load);
        wfapp.registerForDoworkEvent(produceWords);
    }

    /**
     * On load events,
     * class opens and reads the entire contents of the input file
     * filters the characters and normalises them to words
     */
    Function<String,Object> load = path -> {
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
        return null;
    };

    /**
     * On doWork events,
     * class calls handlers of entities that have registered for word events
     * if it is not a stop word.
     */
    Function<String,Object> produceWords = string -> {
        for (String word:data){
            if(!stopWordFilter.isStopWord(word)){
                for(Function handler:wordEventHandlers){
                    handler.apply(word);
                }
            }
        }
        return null;
    };

    //Event registration method
    public void registerForWordEvent(Function handler){
        wordEventHandlers.add(handler);
    }
}

class StopWordFilter{
    private ArrayList<String> stopWords = new ArrayList<>();

    public StopWordFilter(WordFrequencyFramework wfapp){
        wfapp.registerForLoadEvent(load);
    }

    /**
     * opens the stop word file and produces a list of stop words
     */
    Function<String,Object> load = string -> {
        //Make an array list holding stop words from text file
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
        return null;
    };

    /**
     * class exposes this method to check if a word
     * is a stop word
     */
    public Boolean isStopWord(String word){
        return stopWords.contains(word);
    }
}

class WordFrequencyCounter{
    HashMap<String,Integer> wordFreqs = new HashMap<>();

    public WordFrequencyCounter(WordFrequencyFramework wfapp, DataStorage dataStorage){
        dataStorage.registerForWordEvent(incrementCount);
        wfapp.registerForEndEvent(printFreqs);
    }

    /**
     * this helps with incrementing count for each word
     */
    Function<String,Object> incrementCount = word -> {
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
        return null;
    };

    /**
     * When the handler for EndEvent is called,
     * it prints the frequencies
     */
    Function<String,Object> printFreqs = string ->{
        HashMap<String, Integer> sortedWordFreqs;
        //To sort frequency
        sortedWordFreqs = wordFreqs.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));

        int i = 1;
        for(String word:sortedWordFreqs.keySet()){
            System.out.println(word+" - "+ sortedWordFreqs.get(word));
            if (i == 25)
                break;
            i++;
        }
        return null;
    };
}

class ZWordCounter{
    ArrayList<String> zWords = new ArrayList<>();

    public ZWordCounter(WordFrequencyFramework wfapp, DataStorage dataStorage){
        dataStorage.registerForWordEvent(incrementZCount);
        wfapp.registerForEndEvent(printZFreqs);
    }

    Function<String,Object> incrementZCount = word -> {
        //add unique words starting with z
        if(word.contains("z")&&(!zWords.contains(word))){
            zWords.add(word);
        }
        return null;
    };

    Function<String,Object> printZFreqs = string ->{
        System.out.println("Number of words with z: "+zWords.size());
        return null;
    };
}

/**
 * This program is in Hollywood style
 * where the entities provide interfaces for other entities to
 * be able to register for callbacks
 */
public class Fourteen {
    public static void main (String[] args){
        if(args.length == 1 ){
            File indexFile = new File(args[0]);
            if (indexFile.exists()){

                //responsible for executing the program
                WordFrequencyFramework wfapp = new WordFrequencyFramework();
                StopWordFilter stopWordFilter = new StopWordFilter(wfapp);
                DataStorage dataStorage = new DataStorage(wfapp,stopWordFilter);
                WordFrequencyCounter wordFrequencyCounter = new WordFrequencyCounter(wfapp,dataStorage);

                //Ex 14.2 uses same logic as above to count words with z
                ZWordCounter zWordCounter = new ZWordCounter(wfapp,dataStorage);

                wfapp.run(args[0]);
            }
            else
                System.out.println("File argument invalid!");
        }
        else{
            System.out.println("File argument invalid!");
            System.exit(1);
        }
    }
}