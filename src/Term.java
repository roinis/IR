import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Term {
    private List<Document> documents;
    private String term;
    private int counter;
    public Term(String term) {
        this.term = term;
        documents = new ArrayList<>();
    }
    public void addDocument(Document doc){
        if(!documents.contains(doc))
            documents.add(doc);
    }

    public void incrementCounter(){
        counter++;
    }

    @Override
    public String toString() {
        return "Term{" +
                "term='" + term + '\'' +
                '}';
    }

    public String getTerm() {
        return term;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Term term1 = (Term) o;
        return term.equals(term1.term);
    }

    @Override
    public int hashCode() {
        return Objects.hash(term);
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
