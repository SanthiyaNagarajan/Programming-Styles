import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class app1 implements  ITermFrequency {
    @Override
    public List<String> extractWords(String path){
        File indexFile = new File(path);
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

    @Override
    public HashMap<String,Integer> top25(List<String> words){
        HashMap<String,Integer> wordFreqs = new HashMap<>();
        int frequency;

        for(String word:words){
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
        }
        HashMap<String, Integer> sortedWordsFreqs;
        //To sort frequency
        sortedWordsFreqs = wordFreqs.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));

        return sortedWordsFreqs;
    }
}