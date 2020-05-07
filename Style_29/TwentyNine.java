import java.io.File;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * program implements data spaces styles
 * by dividing program into one or more units that execute concurrently.
 * No direct data exchanges between concurrent units other than through data spaces.
 */
public class TwentyNine{
    private static ArrayList<String> stopWords = new ArrayList<>();

    //Two data spaces
    private static LinkedBlockingQueue<String> wordSpace = new LinkedBlockingQueue<>();
    private static LinkedBlockingQueue<HashMap> freqSpace = new LinkedBlockingQueue<>();

    /**
     * Worker function that consumes words from the word space
     * and sends partial results to the frequency space
     */
    public static void processWords() throws InterruptedException {
        HashMap<String,Integer> wordFreqs = new HashMap<>();
        int frequency;
        while(true){
            if(!wordSpace.isEmpty()){
                String word = wordSpace.poll(1, TimeUnit.SECONDS);
                if(!stopWords.contains(word)){
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
            }
            else
                break;
        }
        freqSpace.put(wordFreqs);
    }

    //Loads stop words from file
    public static void loadStopWords(){
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

    //Populates the word space
    public static void populate(String path){
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
                        wordSpace.put(word);
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

    public static void main(String[] args) throws InterruptedException {
        if(args.length == 1 ){
            File indexFile = new File(args[0]);
            if (indexFile.exists()){
                loadStopWords();
                populate(args[0]);

                //Creates multiple threads where run function corresponds to starting the processWords function
                Thread[] workers = new Thread[5];
                for(int i=0; i<5; i++){
                    workers[i] = new Thread(()->{
                        try {
                            processWords();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                }

                //Start the worker threads
                for(int i=0; i<5;i++)
                    workers[i].start();

                //Waits for the worker threads to join
                for(int i=0; i<5;i++)
                    workers[i].join();

                //Merges frequency data
                HashMap<String,Integer> wordFreqs = new HashMap<>();
                while (!freqSpace.isEmpty()){
                    HashMap<String,Integer> freqs = freqSpace.take();
                    for(String k:freqs.keySet()){
                        int count;
                        if(wordFreqs.containsKey(k)){
                            count = wordFreqs.get(k) + freqs.get(k);
                        }
                        else{
                            count = freqs.get(k);
                        }
                        wordFreqs.put(k,count);
                    }
                }

                HashMap<String, Integer> sortedWordFreqs;
                //To sort frequency
                sortedWordFreqs = wordFreqs.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                (e1, e2) -> e1, LinkedHashMap::new));

                //To print 25 most frequent words
                int i = 1;
                for(String w:sortedWordFreqs.keySet()){
                    System.out.println(w+" - "+ sortedWordFreqs.get(w));
                    if (i == 25)
                        break;
                    i++;
                }
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
