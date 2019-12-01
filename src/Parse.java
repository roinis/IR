import java.util.*;


public class Parse {
    private HashMap<String, Term> terms;
    private int currentWord;
    private int currentDocument;
    private Hashtable<String, String> months;


    public Parse() {
        terms = new HashMap<>();
        currentWord = 0;
        createMonthsHash();
    }

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

    private void createMonthsHash() {
        months = new Hashtable<>();
        months.put("january", "01");
        months.put("february", "02");
        months.put("march", "03");
        months.put("april", "04");
        months.put("may", "05");
        months.put("june", "06");
        months.put("july", "07");
        months.put("august", "08");
        months.put("september", "09");
        months.put("october", "10");
        months.put("november", "11");
        months.put("december", "12");
    }

    private String[] splitDocumentText(String text) {
        String[] words = text.split(" ");
        return words;
    }

    public void mainParser(Document document) {
        String[] words;
        words = document.getDocText().split("\\s+");
    }

    private boolean generateWord(String[] words, Document document){





        return true;
    }

    private boolean generateNumber(String[] words, Document document) {
        String firstWord = words[currentWord];
        String nextWord = "";
        String previousWord = "";
        if (currentWord > 0) {
            previousWord = words[currentWord - 1].toLowerCase();
        }
        if (currentWord < words.length)
            nextWord = words[currentWord + 1].toLowerCase();
        if (firstWord.matches("\\d[\\d,.]*\\%")) {
            addPercentNumber(words[currentWord], document);
            currentWord++;
        } else if (firstWord.matches("\\d[\\d,.]*")) {
            numberIsTypeOne(words, firstWord, nextWord, previousWord, document);
        } else if (firstWord.matches("\\b\\$\\d[\\d,.]*")) {
            addPriceWithDollarSign(firstWord,nextWord,document);
        } else if (firstWord.matches("\\d[\\d,.]*bn")&& nextWord.toLowerCase().equals("dollars")) {
            addPriceWithBnSuffix(words[currentWord].toLowerCase(),document);
        }
        else if(firstWord.matches("\\d[\\d,.]*m")&& nextWord.toLowerCase().equals("dollars"))
            addPriceWithMSuffix(words[currentWord],document);
        else{
            return false;
        }
        return true;
    }

    private void addPriceWithMSuffix(String word,Document document){
        float floatNumber;
        int intNumber;
        String priceTerm=word.replace("[,bn]*","");
        floatNumber = Float.parseFloat(priceTerm);
        priceTerm = String.valueOf((int)(floatNumber*1000));
        priceTerm = priceTerm + " M Dollars";
        currentWord = currentWord+2;
    }

    private void addPriceWithBnSuffix(String word,Document document){
        float floatNumber;
        int intNumber;
        String priceTerm=word.replace("[,m]*","");
        if(word.contains(".")){
            floatNumber = Float.parseFloat(priceTerm);
            priceTerm = String.valueOf(floatNumber);
        }else{
            intNumber = Integer.valueOf(priceTerm);
            priceTerm = String.valueOf(intNumber);
        }
        priceTerm = priceTerm + " M Dollars";
        currentWord = currentWord+2;
    }

    private void addPriceWithUSandDollar(String word,String nextWord,Document document){
        float floatNumber;
        int intNumber;
        String priceTerm=word.replace("[,]*","");
        if(word.contains(".")){
            floatNumber = Float.parseFloat(priceTerm);
            priceTerm = String.valueOf(floatNumber);
        }
        else{
            intNumber = Integer.valueOf(priceTerm);
            priceTerm = String.valueOf(intNumber);
        }
        if(nextWord.equals("million")){
            priceTerm = priceTerm + " M Dollars";
        }
        else{
            priceTerm = priceTerm+ "000 M Dollars";
        }
        addNumericTermToHash(priceTerm,document);
    }

    private void numberIsTypeOne(String[] words, String firstWord, String nextWord, String previousWord, Document document) {
        if (words[currentWord + 1].toLowerCase().equals("percent") || words[currentWord + 1].toLowerCase().equals("percentage")) {
            addPercentNumber(words[currentWord], document);
            currentWord = currentWord + 2;
        }
        else if (words[currentWord + 1].toLowerCase().equals("thousand") || words[currentWord + 1].toLowerCase().equals("million") || words[currentWord + 1].toLowerCase().equals("billion")) {
            String checkIfDollar ="";
            String checkIfUs = "";
            if(currentWord+3<words.length){
                checkIfDollar = words[currentWord+3].toLowerCase();
                checkIfUs = words[currentWord+2].toLowerCase();
            }
            if(checkIfUs.equals("u.s.") && checkIfDollar.equals("dollars")){
                addPriceWithUSandDollar(words[currentWord].toLowerCase(),words[currentWord+1].toLowerCase(),document);
                currentWord = currentWord+4;
            }
            else{
                addLargeNumbers(words[currentWord], words[currentWord + 1], document);
                currentWord = currentWord + 2;
            }
        } else if (nextWord.matches("\\d+/\\d")) {
            if (currentWord + 2 < words.length) {
                if (words[currentWord + 2].toLowerCase().equals("dollars")) {
                    addDollarWithFraction(words[currentWord], words[currentWord + 1], document);
                    currentWord = currentWord + 3;
                } else {
                    String numberWithFraction = words[currentWord] + " " + nextWord;
                    addNumericTermToHash(numberWithFraction, document);
                }
            }
        } else if (nextWord.toLowerCase().matches("dollars")) {
            addPriceWithDollar(words[currentWord], document);
            currentWord = currentWord + 2;
        } else if (firstWord.matches("\\d+]")) {
            if (months.contains(nextWord.toLowerCase()) || months.contains(previousWord.toLowerCase())) {
                if (months.contains(nextWord.toLowerCase())) {
                    addDate(firstWord, nextWord.toLowerCase(), document);
                    currentWord = currentWord + 2;
                } else {
                    addDate(firstWord, previousWord.toLowerCase(), document);
                    currentWord++;
                }
            } else {
                addPlainNumber(words[currentWord], document);
                currentWord++;
            }
        } else {
            addPlainNumber(words[currentWord], document);
            currentWord++;
        }
    }

    private void addPriceWithDollarSign(String word, String nextWord, Document document) {
        String priceTerm = "";
        float floatNumber;
        int intNumber;
        priceTerm = word.replace("\\$*", "");
        if (nextWord.toLowerCase().equals("million")) {
            priceTerm = word.replace("\\,*", "");
            priceTerm = priceTerm + " M Dollars";
            currentWord = currentWord+2;
        } else if (nextWord.toLowerCase().equals("billion")) {
            priceTerm = word.replace("\\,*", "");
            priceTerm = priceTerm + "000" + " M Dollars";
            currentWord = currentWord+2;
        } else {
            if (priceTerm.contains(".")) {
                floatNumber = Float.parseFloat(priceTerm.replace("\\,*", ""));
                if (floatNumber >= 1000000) {
                    priceTerm = String.valueOf(floatNumber / 1000000) + " M Dollars";
                } else
                    priceTerm = priceTerm + " Dollars";
            } else {
                intNumber = Integer.parseInt(priceTerm.replace("\\,*", ""));
                if (intNumber >= 1000000)
                    priceTerm = String.valueOf(intNumber / 1000000) + " M Dollars";
                else
                    priceTerm = priceTerm + " Dollars";
            }
            currentWord++;
        }
        addNumericTermToHash(priceTerm, document);
    }

    private void addPriceWithDollar(String number, Document document) {
        int price = Integer.parseInt(number);
        String priceWithDollar = "";
        if (price <1000000) {
            priceWithDollar = number + " Dollars";
        } else {
            priceWithDollar = String.valueOf(price/1000000) + " M Dollars";
        }
        addNumericTermToHash(priceWithDollar, document);
    }


    private void addDollarWithFraction(String number, String fraction, Document document) {
        String fractionTerm = number + " " + fraction + " Dollars";
        addNumericTermToHash(fractionTerm, document);
    }


    private void addPercentNumber(String word, Document document) {
        if (word.contains("%"))
            addNumericTermToHash(word, document);
        else
            addNumericTermToHash(word + "%", document);
    }

    private void addLargeNumbers(String word, String nextWord, Document document) {
        String termName;
        if (word.contains(".")) {
            float tempoFloat = Float.parseFloat(word);
            termName = String.format("%.3f", tempoFloat);
        } else {
            termName = word;
        }
        if (nextWord.toLowerCase().equals("thousand")) {
            termName = termName + "K";
            addNumericTermToHash(termName, document);
        } else if (nextWord.toLowerCase().equals("million")) {
            termName = termName + "M";
            addNumericTermToHash(termName, document);
        } else if (nextWord.toLowerCase().equals("billion")) {
            termName = termName + "B";
            addNumericTermToHash(termName, document);
        }
    }

    private void addPlainNumber(String number, Document document) {
        String formattedNumber = number.replaceAll(",*", "");
        String numberTerm = "";
        int nativeNum;
        if (formattedNumber.contains(".")) {
            if (Float.parseFloat(formattedNumber) < 1000)
                numberTerm = String.valueOf(Float.parseFloat(formattedNumber));
            else if (Float.parseFloat(formattedNumber) < 1000000)
                numberTerm = String.valueOf(Float.parseFloat(formattedNumber) / 1000) + "K";
        } else {
            nativeNum = Integer.parseInt(formattedNumber);
            if (nativeNum < 1000)
                numberTerm = String.valueOf(nativeNum);
            else if (nativeNum < 1000000)
                numberTerm = String.valueOf(nativeNum / 1000) + "K";
            else if (nativeNum < 1000000000)
                numberTerm = String.valueOf(nativeNum / 1000000) + "M";
            else
                numberTerm = String.valueOf(nativeNum / 1000000000) + "B";

        }
        addNumericTermToHash(numberTerm, document);
    }


    private void addDate(String number, String month, Document document) {
        Term date;
        String dateText = "";
        if (number.matches("\\d{1}")) {
            dateText = months.get(month) + "-0" + number;
        } else if (number.matches("\\d{2}")) {
            dateText = months.get(month) + "-" + number;
        } else if (number.matches("\\d{4}")) {
            dateText = number + "-" + months.get(month);
        }
        addNumericTermToHash(dateText, document);
    }


}
