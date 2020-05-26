import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Program follows the lazy river style.
 * Data is available in streams rather than as a complete bunch.
 */
public class TwentySeven {
    private static ArrayList<String> stopWords = new ArrayList<>();

    /**
     * Reads all the characters given the file path
     */
    static class Characters implements Iterator<Character>{

        BufferedReader characterReader;

        public Characters(String filePath) throws FileNotFoundException {
            this.characterReader = new BufferedReader(new FileReader(filePath));
        }

        @Override
        public boolean hasNext() {
            try {
                //returns true as long as there is stream to be read
                return characterReader.ready();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public Character next() {
            try {
                //read will return -1 if end of stream is reached
                return (char) characterReader.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void remove() {
        }
    }

    /**
     * Forms words from the characters returned.
     */
    static class AllWords implements Iterator<String>{
        private Iterator<Character> characterIterator;

        public AllWords(Iterator<Character> characterIterator){
            this.characterIterator = characterIterator;
        }

        @Override
        public boolean hasNext() {
            return characterIterator.hasNext();
        }

        @Override
        public String next() {
            StringBuilder stringBuilder = new StringBuilder();;
            while(characterIterator.hasNext()){
                char character = characterIterator.next();
                //Add to word only alphanumeric characters
                if(Character.isLetterOrDigit(character)){
                    stringBuilder.append(Character.toLowerCase(character));
                }else{
                    break;
                }
            }
            return stringBuilder.toString();
        }

        @Override
        public void remove() {
        }
    }

    /**
     * Adds non stop words that are greater than a single character length.
     */
    static class NonStopWords implements Iterator<String>{
        private Iterator<String> wordIterator;

        public NonStopWords(Iterator<String> wordIterator){
            this.wordIterator = wordIterator;
        }

        @Override
        public boolean hasNext() {
            return wordIterator.hasNext();
        }

        @Override
        public String next() {
            while(wordIterator.hasNext()){
                String currentWord = wordIterator.next();
                if(currentWord!=null && currentWord.length()>1){
                    if(!stopWords.contains(currentWord)){
                        return currentWord;
                    }
                }
            }
            return null;
        }

        @Override
        public void remove() {
        }
    }

    /**
     * Adds count to the corresponding word and sorts the hashmap
     */
    static class CountAndSort implements Iterator<HashMap<String,Integer>>{
        private HashMap<String,Integer> wordFreqs;
        private Iterator<String> nonStopWordIterator;

        public CountAndSort(Iterator<String> nonStopWordIterator){
            this.nonStopWordIterator = nonStopWordIterator;
            wordFreqs = new HashMap<>();
        }

        @Override
        public boolean hasNext() {
            return nonStopWordIterator.hasNext();
        }

        @Override
        public HashMap<String, Integer> next() {
            int i = 1;
            while(nonStopWordIterator.hasNext()){
                String nonStopWord = nonStopWordIterator.next();
                if(nonStopWord != null){
                    wordFreqs.put(nonStopWord,wordFreqs.getOrDefault(nonStopWord,0)+1);
                }

                if(i%5000 == 0){
                    HashMap<String, Integer> sortedWordFreqs;
                    sortedWordFreqs = wordFreqs.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));
                    return sortedWordFreqs;
                }
                i++;
            }

            HashMap<String, Integer> sortedWordFreqs;
            sortedWordFreqs = wordFreqs.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (e1, e2) -> e1, LinkedHashMap::new));
            return sortedWordFreqs;
        }

        @Override
        public void remove() {
        }
    }

    public static void main(String args[]) throws FileNotFoundException {

        if(args.length == 1 ){
            File indexFile = new File(args[0]);
            if (indexFile.exists()){
                loadstopWords();
                Characters characters = new Characters(args[0]);
                AllWords allWords = new AllWords(characters);
                NonStopWords nonStopWords = new NonStopWords(allWords);
                CountAndSort countAndSort = new CountAndSort(nonStopWords);

                while (countAndSort.hasNext()){
                    List<HashMap.Entry<String, Integer>> sortedWordsFreqList = new ArrayList<>(countAndSort.next().entrySet());
                    for (HashMap.Entry<String, Integer> entry : sortedWordsFreqList.subList(0, 25)) {
                        System.out.println(entry.getKey() + " - " + entry.getValue());
                    }
                    System.out.println("-------------------------------------------------------------");
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

    /**
     * Reads stop words file and updates list of stop words
     * @return list of stop words
     */
    private static ArrayList<String> loadstopWords(){
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
