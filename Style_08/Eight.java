import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

interface IFunction{
    void call(Object arg, IFunction func);
}

class ReadFile implements IFunction{

    @Override
    public void call(Object filePath, IFunction func){
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
        func.call(data, new CountFrequency());
    }
}

class RemoveStopWords implements IFunction{

    @Override
    public void call(Object data, IFunction func) {
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
        func.call(words, new SortFrequency());
    }
}

class CountFrequency implements IFunction{

    @Override
    public void call(Object words, IFunction func) {
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
        func.call(wordFreqs, new PrintTop25());
    }
}

class SortFrequency implements IFunction{

    @Override
    public void call(Object wordFreqs, IFunction func) {
        HashMap<String, Integer> sortedWordsFreqs;
        //To sort frequency
        sortedWordsFreqs = ((HashMap<String,Integer>) wordFreqs).entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
        func.call(sortedWordsFreqs, (IFunction)(Object object, IFunction function) ->{});
    }
}

class PrintTop25 implements IFunction{

    @Override
    public void call(Object sortedWordsFreqs, IFunction func){
        int i = 1;
        for(String word:((HashMap<String,Integer>) sortedWordsFreqs).keySet()){
            System.out.println(word+" - "+ ((HashMap<String,Integer>) sortedWordsFreqs).get(word));
            if (i == 25)
                break;
            i++;
        }
        func.call(null,null);
    }
}


/**
 * Kick Forward
 */
public class Eight {
    public static void main(String[] args){
        if(args.length == 1 ){
            File indexFile = new File(args[0]);
            if (indexFile.exists()){
                ReadFile readFile = new ReadFile();
                readFile.call(args[0],new RemoveStopWords());
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
