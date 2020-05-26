import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Program follows the spreadsheet style, with columns of data and formulas.
 * Some data depends on other data. When the formula changes or with execution,
 * we can notice the change in the related columns, just like in a spreadsheet.
 */
public class TwentySix {
    /**
     * IFormulas models the formula object with a method execute
     */
    @FunctionalInterface
    private interface IFormulas{
        Object execute();
    }

    /**
     * Column models the spreadsheet columns that have data and formula associated
     */
    private static class Column{
        private Object data;
        private IFormulas formula;
        public Column(Object data,IFormulas formula){
            this.data = data;
            this.formula = formula;
        }
    }

    /**
     * Will be filled out with words from the input file
     */
    private static Column allWords = new Column(new ArrayList<String>(),null);

    /**
     * Will be filled with words from the stop word file
     */
    private static Column stopWords = new Column(new ArrayList<String>(),null);

    /**
     * Will be filled with non stop words which are derived from the formula.
     * The formula uses the first column and second column to achieve this.
     */
    private static Column nonStopWords = new Column(new ArrayList<String>(), ()->{
        ArrayList<String> nonStopWords = new ArrayList<>();

        for(String word:(ArrayList<String>)allWords.data){
            if(!((ArrayList<String>)stopWords.data).contains(word)){
                nonStopWords.add(word);
            }
        }
        return nonStopWords;
    });

    /**
     * Will be filled with unique words from the non stop words list.
     * The formula uses the third column to achieve this.
     */
    private static Column uniqueWords = new Column(new ArrayList<String>(), ()->{
        ArrayList<String> uniqueWords = new ArrayList<>();

        for(String word:(ArrayList<String>)nonStopWords.data){
            if(!uniqueWords.contains(word)){
                uniqueWords.add(word);
            }
        }
        return uniqueWords;
    });

    /**
     * Will be filled with integer counts of the corresponding unique words' in the list.
     * The formula uses the third and fourth column to achieve this.
     */
    private static Column counts = new Column(new ArrayList<Integer>(), ()->{
        ArrayList<Integer> counts = new ArrayList<>();
        for(int i=0; i<((ArrayList<String>)uniqueWords.data).size();i++)
            counts.add(0);

        for(String word:(ArrayList<String>)nonStopWords.data){
            counts.set((((ArrayList<String>)uniqueWords.data).indexOf(word)),
                    counts.get((((ArrayList<String>)uniqueWords.data).indexOf(word)))+1);
        }
        return counts;
    });

    /**
     * Will be filled with sorted hashmap of unique words and their counts.
     * The formula uses the fourth and fifth column to achieve this.
     */
    private static Column sortedData = new Column(new HashMap<String,Integer>(), ()->{
        HashMap<String, Integer> sortedWordFreqs = new HashMap<>();
        int i=0;
        for(String word:((ArrayList<String>)uniqueWords.data)){
            sortedWordFreqs.put(word, ((ArrayList<Integer>)counts.data).get(i));
            i++;
        }

        sortedWordFreqs = (sortedWordFreqs).entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
        return sortedWordFreqs;
    });

    /**
     * Models a spreadsheet with all the columns defined previously.
     */
    static Column[] allColumns = new Column[]{allWords,stopWords,nonStopWords,uniqueWords,counts,sortedData};

    /**
     * Updates all the columns by executing the formula in the column object.
     */
    private static void update(){
        for (Column c: allColumns){
            if(c.formula!=null){
                c.data = c.formula.execute();
            }
        }
    }

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        String filePath;
        Character choice;
        boolean stop = false;
        while (!stop){
            System.out.println("Enter file path: ");
            filePath = in.nextLine();

            File indexFile = new File(filePath);
            if (indexFile.exists()){
                allWords.data = loadAllWords(filePath);
                stopWords.data = loadstopWords();
                update();

                List<HashMap.Entry<String, Integer>> sortedWordsFreqList =
                        new ArrayList<>(((HashMap<String,Integer>)sortedData.data).entrySet());
                for (HashMap.Entry<String, Integer> entry : sortedWordsFreqList.subList(0, 25)) {
                    System.out.println(entry.getKey() + " - " + entry.getValue());
                }
            }
            else {
                System.out.println("File argument invalid!");
            }

            System.out.println("-------------------------------------------------------------------");
            /**
             * Ex 26.2 Interactive program that accepts new arguments for file
             */
            System.out.println("Do you want to try again?(y/n) ");
            choice = in.nextLine().charAt(0);
            if((Character.toLowerCase(choice))=='n'){
                stop = true;
            }
        }
    }

    /**
     * Helper function to load all words from the given file path
     * @param filePath
     * @return all words in the file
     */
    private static ArrayList<String> loadAllWords(String filePath){
        File indexFile = new File(filePath);
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

    /**
     * Reads stop words file and updates list of stop words
     * @return list of stop words
     */
    private static ArrayList<String> loadstopWords(){
        ArrayList<String> stopWords = new ArrayList<>();

        final File stopWordsfile = new File("../stop_words.txt");
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

        return stopWords;
    }
}
