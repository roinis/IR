import javafx.scene.shape.Path;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;


public class ReadFile {

    private String path;
    private HashSet<String> stopWords = new HashSet<>();
    private HashMap<String,String> documents = new HashMap();

    public ReadFile(String path) throws IOException {
        this.path = path;
        readStopWords();
        getDocumentsPath();
    }

    public HashMap<String, String> getDocuments() {
        return documents;
    }

    public void setDocuments(HashMap<String, String> documents) {
        this.documents = documents;
    }

    public HashSet<String> getStopWords() {
        return stopWords;
    }

    public void setStopWords(HashSet<String> stopWords) {
        this.stopWords = stopWords;
    }

    private void readStopWords() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path+"\\stopwords.txt"));
        String word;
        while((word = br.readLine()) != null){
            stopWords.add(word);
        }
    }

    private void readDocument(String docPath) throws IOException {
        String openText = "<TEXT>";
        String closeText = "</TEXT>";
        String docId = "<DOCNO>";
        String newDoc = "</DOC>";
        String documentText= "";
        String line;
        String key="";
        boolean textCheck = false;
        BufferedReader br = new BufferedReader(new FileReader(docPath));
        while((line = br.readLine()) != null){
            if(line.contains(docId)){
                key = line.replaceAll("<.*?>","");
            }
            if(line.contains(openText)){
                textCheck = true;
                continue;
            }
            if(line.contains(closeText)){
                textCheck =false;
                documents.put(key,documentText);
                documentText = "";
            }
            if(textCheck){
                documentText+=line;
            }
        }
    }

    private void getDocumentsPath() throws IOException {
        File[] directories = new File(path).listFiles(new FileFilter(){
            @Override
            public boolean accept(File file){
                return file.isDirectory();
            }
        });
        for(File f : directories){
            readDocument(f.getPath()+"\\"+f.getName());
        }
    }
}
