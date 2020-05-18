import java.util.HashMap;
import java.util.List;

public interface ITermFrequency {
    List<String> extractWords(String path);
    HashMap<String,Integer> top25(List<String> words);
}
