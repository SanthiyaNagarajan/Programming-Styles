import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Five {

    private static ArrayList<String> read_file(File indexFile){
        ArrayList<String> data = new ArrayList<>();
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
        return data;
    }

    private static ArrayList<String> remove_stop_words(ArrayList<String> data){
        ArrayList<String> words = new ArrayList<>();

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
        return words;
    }

    private static HashMap<String,Integer> frequencies(ArrayList<String> words){
        HashMap<String,Integer> word_freqs = new HashMap<>();
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
        return word_freqs;
    }

    private static HashMap<String,Integer> sort(HashMap<String,Integer> word_freqs){
        HashMap<String, Integer> sorted_words_freqs;
        //To sort frequency
        sorted_words_freqs = word_freqs.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));

        return sorted_words_freqs;
    }

    private static void print(HashMap<String, Integer> sorted_words_freqs,int target){
        int i = 1;
        for(String word:sorted_words_freqs.keySet()){
            System.out.println(word+" - "+ sorted_words_freqs.get(word));
            if (i == target)
                break;
            i++;
        }
    }

    public static void main(String[] args) {
        if(args.length == 1 ){
            File indexFile = new File(args[0]);
            if (indexFile.exists()){
                print(sort(frequencies(remove_stop_words(read_file(indexFile)))),25);
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
