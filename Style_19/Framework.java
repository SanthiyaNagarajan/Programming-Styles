import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * The program executes two term frequency functions extract words and top 25.
 * The version of functions chosen by the program is dictated dynamically with the help
 * of config.ini file.
 */
public class Framework {
    public static void main(String args[]) throws IOException {
        Properties p = new Properties();
        p.load(new FileInputStream("config.ini"));
        String appVersion = p.getProperty("term_frequency_plugin");
        System.out.println("Using app version: "+appVersion);

        URL classUrl = null;
        try {
            classUrl = new URL("file://"+appVersion+".jar");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Class cls = null;

        URL[] classUrls = {classUrl};
        URLClassLoader cloader = new URLClassLoader(classUrls);
        try {
            cls = cloader.loadClass(appVersion);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HashMap<String,Integer> wordFreqs = new HashMap<>();
        if (cls != null) {
            try {
                ITermFrequency termFrequency = (ITermFrequency) cls.newInstance();
                wordFreqs = termFrequency.top25(termFrequency.extractWords(args[0]));
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<HashMap.Entry<String, Integer>> sortedWordsFreqList = new ArrayList<>(wordFreqs.entrySet());
            for (HashMap.Entry<String, Integer> entry : sortedWordsFreqList.subList(0, 25)) {
                System.out.println(entry.getKey() + " - " + entry.getValue());
            }
        }
    }
}

