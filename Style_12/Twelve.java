import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The functions in the following functional interfaces are defined in the
 * hashmap objects as lambda function definitions
 */

@FunctionalInterface
interface extractI{
    /**
     * Extracts word from the file from the given path and stores under key
     * "data" for hashmap object
     * @param object : data_storage_obj
     * @param path : File path
     */
    void extract_words(HashMap<String, Object> object, String path);
}

@FunctionalInterface
interface getDataI {
    /**
     * Function gets the data extracted as Array list
     * @param object : data_storage_obj
     * @return : Returns array list of the words extracted
     */
    ArrayList<String> getData(HashMap<String, Object> object);
}

@FunctionalInterface
interface stopI{
    /**
     * Opens already existing file and loads stop words to Hashmap object under
     * key "stop_words"
     * @param object : stop_words_obj
     */
    void loadStopWords(HashMap<String, Object> object);
}

@FunctionalInterface
interface isStopWordI{
    /**
     * Checks if the passed word is a Stop word
     * @param list : Stop word list
     * @param word : word to be checked if stop word
     * @return : Returns True if it is a stop word and false otherwise
     */
    boolean isStopWord(ArrayList<String> list, String word);
}

@FunctionalInterface
interface incrementCountI{
    /**
     * To add count to the word in a Hashmap
     * @param object : word_freqs_obj
     * @param word : word to append to hashmap or increase count of
     */
    void incrementCount(HashMap<String, Object> object,String word);
}

@FunctionalInterface
interface sortedI{
    /**
     * Sorts the Hashmap passed in decreasing order of frequency
     * @param object : word_freqs_obj
     * @return : Returns sorted hashmap
     */
    void sorted(HashMap<String, Object> object);
}

public class Twelve {
    private static HashMap<String, Object> dataStorageObj = new HashMap<>(){{
        this.put("data", new ArrayList<String>());
        this.put("init", (extractI) (HashMap<String, Object> object, String path)->{ArrayList<String> data = new ArrayList<>();
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
            object.put("data",data);});
        this.put("words", (getDataI)(HashMap<String, Object> object) -> ((ArrayList<String>) object.get("data")));
    }};

    private static HashMap<String, Object> stopWordsObj = new HashMap<>(){{
        this.put("stop_words", new ArrayList<String>());
        this.put("init", (stopI)(HashMap<String, Object> object) -> {
            ArrayList<String> stopWords = new ArrayList<>();
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
            object.put("stop_words", stopWords);
        });
        this.put("is_stop_word", (isStopWordI)(ArrayList<String> stopWords, String word) -> {
            if(stopWords.contains(word)){
                return true;
            }
            else
                return false;
        });
    }};

    private static HashMap<String, Object> wordFreqsObj = new HashMap<>(){{
        this.put("freqs", new HashMap<String, Integer>());
        this.put("increment_count", (incrementCountI)(HashMap<String, Object> object, String word) -> {
            int frequency;
            //add count to the pre-existing key
            if(((HashMap<String, Integer>) object.get("freqs")).containsKey(word)){
                frequency = ((HashMap<String, Integer>) (object.get("freqs"))).get(word);
                frequency++;
                ((HashMap<String, Integer>) (object.get("freqs"))).put(word,frequency);
            }

            //add new entry to the hashmap
            else {
                frequency = 1;
                ((HashMap<String, Integer>) (object.get("freqs"))).put(word,frequency);
            }
        });
        this.put("sorted", (sortedI)(HashMap<String, Object> object) -> {
            HashMap<String, Integer> sorted_words_freqs;
            //To sort frequency
            sorted_words_freqs = ((HashMap<String, Integer>) (object.get("freqs"))).entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (e1, e2) -> e1, LinkedHashMap::new));

            //Calls print function
            print(sorted_words_freqs,25);
        });
    }};

    public static void main(String[] args) {
        if(args.length == 1 ){
            File indexFile = new File(args[0]);
            if (indexFile.exists()){
                //Returns extractI Interface object which extracts word from given file path
                ((extractI) dataStorageObj.get("init")).extract_words(dataStorageObj,args[0]);

                //Returns stopI Interface object which loads stop words
                ((stopI) stopWordsObj.get("init")).loadStopWords(stopWordsObj);

                //Runs through the words returned from arraylist extracted and adds to hashmap if
                // it is not a stopword and calls for incrementing count
                for(String word : ((getDataI) dataStorageObj.get("words")).getData(dataStorageObj)){
                    if(!((isStopWordI) stopWordsObj.get("is_stop_word")).isStopWord(((ArrayList<String>) stopWordsObj.get("stop_words")), word)){
                        ((incrementCountI) wordFreqsObj.get("increment_count")).incrementCount(wordFreqsObj,word);
                    }
                }
                ((sortedI) wordFreqsObj.get("sorted")).sorted(wordFreqsObj);
            }
            else
                System.out.println("File argument invalid!");
        }
        else{
            System.out.println("File argument invalid!");
            System.exit(1);
        }
    }

    /**
     * Prints the target amount of words
     * @param sorted_words_freqs : Sorted Hashmap
     * @param target : Number of words to print
     */

    private static void print(HashMap<String, Integer> sorted_words_freqs,int target){
        int i = 1;
        for(String word:sorted_words_freqs.keySet()){
            System.out.println(word+" - "+ sorted_words_freqs.get(word));
            if (i == target)
                break;
            i++;
        }
    }
}
