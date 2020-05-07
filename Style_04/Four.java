import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Four {
    static ArrayList<String> data = new ArrayList<>();
    static ArrayList<String> words = new ArrayList<>();
    static HashMap<String,Integer> word_freqs = new HashMap<>();
    static HashMap<String, Integer> sorted_words_freqs = new HashMap<>();

    private static void read_file(File indexFile){
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

    private static void remove_stop_words(){
        //Make an array list holding stop words from text file
        File stopWordsfile = new File("../stop_words.txt");
        ArrayList<String> stopWords = new ArrayList<>();
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

        //Adding words only if it is not a stop word
        for (String str:data){
            if(!stopWords.contains(str)){
                words.add(str);
            }
        }
    }

    private static void frequencies(){
        int frequency;
        for(String word:words){
            //add count to the pre-existing key
            if(word_freqs.containsKey(word)){
                frequency = word_freqs.get(word);
                frequency++;
                word_freqs.put(word,frequency);
            }

            //add new entry to the hashmap
            else {
                frequency = 1;
                word_freqs.put(word,frequency);
            }
        }
    }

    private static void sort(){
        //To sort frequency
        sorted_words_freqs = word_freqs.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    private static void print(int target){
        int i = 1;
        for(String word:sorted_words_freqs.keySet()){
            System.out.println(word+" - "+ sorted_words_freqs.get(word));
            if (i == target)
                break;
            i++;
        }
    }

    public static void main(String[] args){

        if(args.length == 1 ){
            File indexFile = new File(args[0]);
            if (indexFile.exists()){
                read_file(indexFile);
                remove_stop_words();
                frequencies();
                sort();
                //To print 25 most frequent words
                print(25);
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