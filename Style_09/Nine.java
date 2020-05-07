import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

interface IFunction{
    Object call(Object arg);
}

class ReadFile implements IFunction{

    @Override
    public Object call(Object filePath){
        ArrayList<String> data = new ArrayList<>();
        File indexFile = new File((String) filePath);

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
}

class RemoveStopWords implements IFunction{

    @Override
    public Object call(Object data) {
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
        for (String str: (ArrayList<String>) data){
            if(!stopWords.contains(str)){
                words.add(str);
            }
        }
        return words;
    }
}

class CountFrequency implements IFunction{

    @Override
    public Object call(Object words) {
        HashMap<String,Integer> wordFreqs = new HashMap<>();
        int frequency;

        for(String word: (ArrayList<String>)words){
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
        return wordFreqs;
    }
}

class SortFrequency implements IFunction{

    @Override
    public Object call(Object wordFreqs) {
        HashMap<String, Integer> sortedWordsFreqs;
        //To sort frequency
        sortedWordsFreqs = ((HashMap<String,Integer>) wordFreqs).entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
        return sortedWordsFreqs;
    }
}

class TFTheOne{
    private  Object value;
    TFTheOne(Object v) { value = v;}

    public TFTheOne bind(IFunction func){
        value = func.call(value);
        return this;
    }

    public void print(){
        int i = 1;
        for(String word:((HashMap<String,Integer>) value).keySet()){
            System.out.println(word+" - "+ ((HashMap<String,Integer>) value).get(word));
            if (i == 25)
                break;
            i++;
        }
    }
}

/**
 * The one
 */
public class Nine {
    public static void main(String[] args){
        if(args.length == 1 ){
            File indexFile = new File(args[0]);
            if (indexFile.exists()){
                TFTheOne one = new TFTheOne(args[0]);
                one.bind(new ReadFile()).bind(new RemoveStopWords()).bind(new CountFrequency())
                        .bind(new SortFrequency()).print();
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