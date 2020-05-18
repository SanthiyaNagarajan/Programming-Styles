import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

interface IDataStorage{
    ArrayList<String> words();
}

interface IStopWordFilter{
    Boolean isStopWord(String word);
}

interface IWordFrequencyCounter{
    HashMap<String,Integer> incrementCount(String word);
    HashMap<String,Integer> sorted();
}

class DataStorageManager implements IDataStorage{
    private ArrayList<String> data = new ArrayList<>();

    public DataStorageManager(String path){
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
    }

    @Override
    public ArrayList<String> words(){
        return data;
    }
}

class StopWordManager implements IStopWordFilter{
    private ArrayList<String> stopWords = new ArrayList<>();

    public StopWordManager(){
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

    @Override
    public Boolean isStopWord(String word){
        return stopWords.contains(word);
    }
}

class WordFrequencyManager implements IWordFrequencyCounter{
    private HashMap<String,Integer> wordFreqs = new HashMap<>();

    @Override
    public HashMap<String,Integer> incrementCount(String word){
        wordFreqs.put(word,wordFreqs.getOrDefault(word,0)+1);
        return wordFreqs;
    }

    @Override
    public HashMap<String,Integer> sorted(){
        HashMap<String, Integer> sortedWordFreqs;
        sortedWordFreqs = wordFreqs.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));

        return sortedWordFreqs;
    }
}

class WordFrequencyController{
    private DataStorageManager storageManager;
    private StopWordManager stopWordManager;
    private WordFrequencyManager wordFreqManager;

    public WordFrequencyController(String path){
        storageManager = new DataStorageManager(path);
        stopWordManager = new StopWordManager();
        wordFreqManager = new WordFrequencyManager();
    }

    /**
     * Invokes the methods of the DataStorageManager, StopWordManager,
     * and WordFrequencyCounter objects using reflection.
     */
    public void run(){
        Class storageManagerClass = storageManager.getClass();
        Class stopWordManagerClass = stopWordManager.getClass();
        Class wordFreqManagerClass = wordFreqManager.getClass();

        Method wordsM = null;
        Method isStopWordM = null;
        Method incrementCountM = null;
        Method sortedM = null;

        try{
            wordsM = storageManagerClass.getDeclaredMethod("words");
            isStopWordM = stopWordManagerClass.getDeclaredMethod("isStopWord",String.class);
            incrementCountM = wordFreqManagerClass.getDeclaredMethod("incrementCount",String.class);
            sortedM = wordFreqManagerClass.getDeclaredMethod("sorted");
        }catch (Exception e) {
            System.out.println(e.getStackTrace());
        }

        HashMap<String, Integer> sortedWordsFreqs = new HashMap<>();
        if((wordsM != null) && (isStopWordM != null) && (incrementCountM != null) && (sortedM != null)){
            try{
                for(String word : (ArrayList<String>) wordsM.invoke(storageManager)){
                    if(!((boolean)isStopWordM.invoke(stopWordManager,word))){
                        incrementCountM.invoke(wordFreqManager,word);
                    }
                }
                sortedWordsFreqs = (HashMap<String, Integer>) sortedM.invoke(wordFreqManager);
            }catch (Exception e) {
                System.out.println(e.getStackTrace());
            }
        }

        List<HashMap.Entry<String, Integer>> sortedWordsFreqList = new ArrayList<>(sortedWordsFreqs.entrySet());
        for (HashMap.Entry<String, Integer> entry : sortedWordsFreqList.subList(0, 25)) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }
    }
}

/**
 * The ability to examine and modify itself is called reflection.
 * This program exercises this style by accessing the information about itself
 * during runtime.
 */
public class Seventeen {
    public static void main(String[] args) {
        if(args.length == 1 ) {
            new WordFrequencyController(args[0]).run();

            System.out.println("-------------------------------------------------------------------------------------");

            Scanner in = new Scanner(System.in);
            System.out.println("Enter a class name to inspect: ");
            String name = in.nextLine();
            getClassDetails(name);
        }
        else{
            System.out.println("File argument invalid!");
            System.exit(1);
        }
    }

    /**
     * Generic function to print out class details given the class name
     * @param name class name
     */
    public static void getClassDetails(String name){
        System.out.println("Getting information about class: " + name);

        Class cls = null;
        try {
            cls = Class.forName(name);
        } catch (Exception e) {
            System.out.println("No such class: " + name);
        }

        if (cls != null) {

            //Prints out public fields of the class along with its type
            Field[] fields = cls.getFields();
            for (Field f : fields)
                System.out.println("Found field: " + f.getName() + " Type: "+f.getType());

            //Prints method names
            Method[] methods = cls.getMethods();
            for (Method m : methods)
                System.out.println("Found method: " + m.getName());

            //Prints implemented interfaces
            Class[] interfaces = cls.getInterfaces();
            for (Class iface : interfaces)
                System.out.println("Interface name: " + iface.getName());

            //Prints superclasses
            cls=cls.getSuperclass();
            while(cls!=null){
                System.out.println("Superclass name: "+cls.getName());
                cls = cls.getSuperclass();
            }
        }
    }
}
