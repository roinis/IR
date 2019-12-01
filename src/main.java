import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class main {

    public static void main(String args[]) throws IOException {
        ReadFile rf = new ReadFile("c");
        Parse pr = new Parse(rf.getStopWords());
        HashSet<String> check =rf.getStopWords();
        HashMap<String,String> checkDoc = rf.getDocuments();
        HashMap<String,String> terms = pr.parse(checkDoc);
        for(String s : terms.keySet())
            System.out.println(s);


    }
}
