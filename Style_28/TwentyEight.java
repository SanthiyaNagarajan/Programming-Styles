import java.io.File;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

class ActiveWFObject extends Thread {
    protected LinkedBlockingQueue<Object[]> queue;
    protected Boolean stopMe;

    public ActiveWFObject() {
        super();
        this.queue = new LinkedBlockingQueue<Object[]>();
        this.stopMe = false;
        start();
    }

    /**
     * Each active object performs continuous loop over its queue,
     * processing one message at a time and blocking if the queue
     * is empty.
     */
    @Override
    public void run() {
        while (!stopMe) {
            try {
                Object[] message = queue.take();
                dispatch(message);
                if(message[0].equals("die"))
                    stopMe = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void dispatch(Object[] message) throws InterruptedException {
    }

}

class Sender{
    public static void send(ActiveWFObject receiver, Object[] message) throws InterruptedException {
        receiver.queue.put(message);
    }
}

class DataStorageManager extends ActiveWFObject{
    private ArrayList<String> data = new ArrayList<>();
    private StopWordManager stopWordManager;

    @Override
    public void dispatch(Object[] message) throws InterruptedException {
        if(message[0].equals("init")){
            init(Arrays.copyOfRange(message,1,message.length));
        }
        else if(message[0].equals("sendWordFreqs"))
            processWords(Arrays.copyOfRange(message,1,message.length));
        else{
            Sender.send(stopWordManager,message);
        }
    }

    private void init(Object[] message){
        File indexFile = new File(message[0].toString());
        stopWordManager = (StopWordManager) message[1];

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

    private void processWords(Object[] message) throws InterruptedException {
        Object recipient = message[0];
        for(String word:data){
            Sender.send(stopWordManager,new Object[]{"filter",word});
        }
        Sender.send(stopWordManager,new Object[]{"top25", recipient});
    }
}

class StopWordManager extends ActiveWFObject{
    private ArrayList<String> stopWords = new ArrayList<>();
    private WordFrequencyManager wordFrequencyManager;

    @Override
    public void dispatch(Object[] message) throws InterruptedException {
        if(message[0].equals("init")){
            init(Arrays.copyOfRange(message,1,message.length));
        }
        else if(message[0].equals("filter"))
            filter(Arrays.copyOfRange(message,1,message.length));
        else{
            Sender.send(wordFrequencyManager,message);
        }
    }

    private void init(Object[] message){
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
        wordFrequencyManager = (WordFrequencyManager) message[0];
    }

    private void filter(Object[] message) throws InterruptedException {
        String word = message[0].toString();
        if(!stopWords.contains(word)) {
            Sender.send(wordFrequencyManager,new Object[]{"word",word});
        }
    }
}

class WordFrequencyManager extends ActiveWFObject{
    private HashMap<String,Integer> wordFreqs = new HashMap<>();

    @Override
    public void dispatch(Object[] message) throws InterruptedException {
        if(message[0].equals("word")){
            incrementCount(Arrays.copyOfRange(message,1,message.length));
        }
        else if(message[0].equals("top25"))
            top25(Arrays.copyOfRange(message,1,message.length));
    }

    private void incrementCount(Object[] message){
        int frequency;
        String word = message[0].toString();
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

    private void top25(Object[] message) throws InterruptedException {
        HashMap<String, Integer> sortedWordFreqs;
        //To sort frequency
        sortedWordFreqs = wordFreqs.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
        ActiveWFObject recipient = (ActiveWFObject) message[0];
        Sender.send(recipient,new Object[]{"top25",sortedWordFreqs});
    }
}


class WordFrequencyController extends ActiveWFObject{
    private DataStorageManager dataStorageManager;

    @Override
    public void dispatch(Object[] message) throws InterruptedException {
        if(message[0].equals("run"))
            run(Arrays.copyOfRange(message,1,message.length));
        else if(message[0].equals("top25"))
            display(Arrays.copyOfRange(message,1,message.length));
        else{
            throw new InterruptedException("Message not understood "+ message[0]);
        }
    }

    public void run(Object[] message) throws InterruptedException {
        dataStorageManager = (DataStorageManager) message[0];
        Sender.send(dataStorageManager,new Object[]{"sendWordFreqs", this});
    }

    public void display(Object[] message) throws InterruptedException {
        HashMap<String, Integer> wordFreqs = (HashMap<String, Integer>) message[0];
        int i = 1;
        for(String word:wordFreqs.keySet()){
            System.out.println(word+" - "+ wordFreqs.get(word));
            if (i == 25)
                break;
            i++;
        }
        Sender.send(dataStorageManager,new Object[]{"die"});
        stopMe = true;
    }
}

/**
 * Program uses the Actor style
 * larger problems are decomposed into things that has a queue for each
 * each thing exposes its ability to receive messages via queue which has its
 * own thread of execution.
 */
public class TwentyEight {
    public static void main(String[] args) throws Exception{

        if(args.length == 1 ){
            File indexFile = new File(args[0]);
            if (indexFile.exists()){

                WordFrequencyManager wordFrequencyManager =  new WordFrequencyManager();

                StopWordManager stopWordManager = new StopWordManager();
                Sender.send(stopWordManager,new Object[]{"init",wordFrequencyManager});

                DataStorageManager dataStorageManager = new DataStorageManager();
                Sender.send(dataStorageManager,new Object[]{"init",args[0],stopWordManager});

                WordFrequencyController wfcontroller = new WordFrequencyController();
                Sender.send(wfcontroller,new Object[]{"run",dataStorageManager});

                //Wait for the active objects to finish
                ActiveWFObject[] threads = new ActiveWFObject[]{wordFrequencyManager,stopWordManager,dataStorageManager,wfcontroller};
                for(ActiveWFObject t:threads){
                    t.join();
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
