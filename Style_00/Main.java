import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException{

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

        if(args.length == 1 ){
            File indexFile = new File(args[0]);
            //Gets the 25 most frequently occurring words after removing stop words
            if (indexFile.exists()){
                count(stopWords,indexFile);
            }
            else
                System.out.println("File argument invalid!");
        }
        else{
            System.out.println("File argument invalid!");
            System.exit(1);
        }
    }

    public static void count(ArrayList<String> stopWords, File file){

        HashMap<String,Integer> frequencyCounter = new HashMap<>();
        Integer frequency;
        String text;

        try {
            Scanner scanner = new Scanner(file);
            //gets rid of characters other than alphabets
            scanner.useDelimiter("[^A-Za-z]+");

            while (scanner.hasNextLine()) {
                if(scanner.hasNext())
                {
                    text = scanner.next().toLowerCase();

                    if((!stopWords.contains(text)) && (text.length()!=1)) {
                        //add count to the pre-existing key
                        if(frequencyCounter.containsKey(text)){
                            frequency = frequencyCounter.get(text);
                            frequency++;
                            frequencyCounter.put(text,frequency);
                        }

                        //add new entry to the hashmap
                        else {
                            frequency = 1;
                            frequencyCounter.put(text,frequency);
                        }
                    }
                }
                else
                    break;
            }
            scanner.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        //To sort frequency
        Map<String, Integer> sortedFrequencyCounter =
                frequencyCounter.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                (e1, e2) -> e1, LinkedHashMap::new));

        //Select top 25
        int i = 1;
        for(String word:sortedFrequencyCounter.keySet()){
            System.out.println(word+" - "+ sortedFrequencyCounter.get(word));
            if (i==25)
                break;
            i++;
        }
    }
}
