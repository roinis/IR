import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class main {

    public static void main(String args[]) throws IOException {
        ReadFile rf = new ReadFile("corpus");
        //HashSet<String> check =rf.getStopWords();
        HashMap<String,String> checkDoc = rf.getDocuments();
    }
}
