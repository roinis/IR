import java.util.List;

public class Index {
    private List<String> dictionary;
    private final String postingFilePath = "";

    public Index(List<String> dictionary) {
        this.dictionary = dictionary;
    }

/**
    private void addWordToHash(String termName,Document document){
        if (!terms.containsKey(termName.toLowerCase())){
            if(Character.isUpperCase(termName.charAt(0))){
                Term newTerm = new Term(termName.toUpperCase());
                newTerm.addDocument(document);
                newTerm.incrementCounter();
                terms.put(termName.toLowerCase(),newTerm);
            }
            else{
                Term newTerm = new Term(termName);
                newTerm.addDocument(document);
                newTerm.incrementCounter();
                terms.put(termName.toLowerCase(),newTerm);
            }
        }else{
            Term tempoTerm = terms.remove(termName.toLowerCase());
            if(Character.isLowerCase(tempoTerm.getTerm().charAt(0))){
                tempoTerm.incrementCounter();
                tempoTerm.addDocument(document);
                terms.put(termName.toLowerCase(),tempoTerm);
            }
            else{
                if(Character.isUpperCase(termName.charAt(0))){
                    tempoTerm.addDocument(document);
                    tempoTerm.incrementCounter();
                    terms.put(termName.toLowerCase(),tempoTerm);
                }
                else{
                    tempoTerm.setTerm(termName.toLowerCase());
                    tempoTerm.incrementCounter();
                    tempoTerm.addDocument(document);
                    terms.put(termName.toLowerCase(),tempoTerm);
                }
            }
        }
    }

    private void addNumericTermToHash(String termName, Document document) {
        if (!terms.containsKey(termName)) {
            Term newTerm = new Term(termName);
            newTerm.addDocument(document);
            newTerm.incrementCounter();
            terms.put(termName, newTerm);
        } else {
            Term tempoTerm = terms.remove(termName);
            tempoTerm.incrementCounter();
            tempoTerm.addDocument(document);
            terms.put(termName, tempoTerm);
        }
    }

**/
}
