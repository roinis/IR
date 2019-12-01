import java.util.*;


public class Parse {
    private HashMap<String, String> terms;
    private int currentWord;
    private String currentDocument;
    private Hashtable<String, String> months;
    private String[] words;
    HashSet<String> stopWords;

    public Parse(HashSet<String> stopWords) {
        terms = new HashMap<>();
        currentWord = 0;
        createMonthsHash();
        this.stopWords = stopWords;
    }

    public HashMap<String,String> parse(HashMap<String,String> documentsToParse){
        for (String s: documentsToParse.keySet()) {
            currentDocument = s;
            words = documentsToParse.get(s).split("\\s+");
            mainParser();
        }
        return terms;
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


    public void mainParser() {

        while(currentWord<words.length){
            if(generateNumber())
                continue;
            else if(generatePhrases())
                continue;
            else {
                generateWord();
            }
        }

    }

    private boolean generateWord(){
        String word = words[currentWord].replaceAll("[([,.]#)]*","");
        if(months.containsKey(words[currentWord].toLowerCase()) ){
            if(words[currentWord+1].matches("\\d+"))
                addDate(words[currentWord+1],words[currentWord].toLowerCase());
                currentWord = currentWord+2;
        }
        else if(stopWords.contains(word.toLowerCase())){
            currentWord++;
            return false;
        }else {
            terms.put(word, currentDocument);
            currentWord++;
            return true;
        }
        return false;
    }

    private boolean generatePhrases(){
        if(currentWord + 3 < words.length){
            if(words[currentWord].toLowerCase().equals("between") &&
                    words[currentWord + 1].matches("\\d+") &&
                    words[currentWord + 2].toLowerCase().equals("and") &&
                    words[currentWord + 3].matches("\\d+")){
                terms.put(words[currentWord + 1] + "-" + words[currentWord + 3],currentDocument);
                currentWord+=4;
                return true;
            }
        }
        return false;

    }

    private boolean generateNumber() {
        String firstWord = words[currentWord];
        String nextWord = "";
        String previousWord = "";
        if (currentWord > 0) {
            previousWord = words[currentWord - 1].toLowerCase();
        }
        if (currentWord +1< words.length)
            nextWord = words[currentWord + 1].toLowerCase();

        if (firstWord.matches("\\d[\\d,.]*\\%")) {
            addPercentNumber(words[currentWord]);
            currentWord++;
        } else if (firstWord.matches("\\d[\\d,.]*")) {
            numberIsTypeOne(firstWord, nextWord, previousWord);
        } else if (firstWord.matches("\\$\\d[\\d,.]*")) {
            addPriceWithDollarSign(firstWord,nextWord);
        } else if (firstWord.matches("\\d[\\d,.]*bn")&& nextWord.toLowerCase().equals("dollars")) {
            addPriceWithBnSuffix(words[currentWord].toLowerCase());
        }
        else if(firstWord.matches("\\d[\\d,.]*m")&& nextWord.toLowerCase().equals("dollars"))
            addPriceWithMSuffix(words[currentWord]);
        else{
            return false;
        }
        return true;
    }

    private void addPriceWithMSuffix(String word){
        float floatNumber;
        int intNumber;
        String priceTerm=word.replaceAll("[,m]","");
        floatNumber = Float.parseFloat(priceTerm);
        priceTerm = priceTerm + " M Dollars";
        terms.put(priceTerm,currentDocument);
        currentWord = currentWord+2;
    }

    private void addPriceWithBnSuffix(String word){
        float floatNumber;
        long intNumber;
        String priceTerm=word.replaceAll("[,bn]*","");
        if(word.contains(".")){
            floatNumber = Float.parseFloat(priceTerm);
            priceTerm = String.valueOf((float)(floatNumber*1000));
            priceTerm = String.valueOf(floatNumber);
        }else{
            intNumber = Integer.valueOf(priceTerm);
            priceTerm = String.valueOf((long)(intNumber*1000));
            priceTerm = String.valueOf(intNumber);
        }
        priceTerm = priceTerm + " M Dollars";
        terms.put(priceTerm,currentDocument);
        currentWord = currentWord+2;
    }

    private void addPriceWithUSandDollar(String word,String nextWord){
        float floatNumber;
        int intNumber;
        String priceTerm=word.replaceAll("[,]*","");
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
        terms.put(priceTerm,currentDocument);
    }

    private void numberIsTypeOne(String firstWord, String nextWord, String previousWord) {
        if (nextWord.toLowerCase().equals("percent") || nextWord.toLowerCase().equals("percentage")) {
            addPercentNumber(words[currentWord]);
            currentWord = currentWord + 2;
        }
        else if (nextWord.toLowerCase().equals("thousand") || nextWord.toLowerCase().equals("million") || nextWord.toLowerCase().equals("billion")) {
            String checkIfDollar ="";
            String checkIfUs = "";
            if(currentWord+3<words.length){
                checkIfDollar = words[currentWord+3].toLowerCase();
                checkIfUs = words[currentWord+2].toLowerCase();
            }
            if(checkIfUs.equals("u.s.") && checkIfDollar.equals("dollars")){
                addPriceWithUSandDollar(words[currentWord].toLowerCase(),words[currentWord+1].toLowerCase());
                currentWord = currentWord+4;
            }
            else{
                addLargeNumbers(words[currentWord], words[currentWord + 1]);
                currentWord = currentWord + 2;
            }
        } else if (nextWord.matches("\\d+/\\d")) {
            if (currentWord + 2 < words.length) {
                if (words[currentWord + 2].toLowerCase().equals("dollars")) {
                    addDollarWithFraction(words[currentWord], words[currentWord + 1]);
                    currentWord = currentWord + 3;
                } else {
                    String numberWithFraction = words[currentWord] + " " + nextWord;
                    terms.put(numberWithFraction,currentDocument);
                }
            }
        } else if (nextWord.toLowerCase().matches("dollars")) {
            addPriceWithDollar(words[currentWord]);
            currentWord = currentWord + 2;
        } else if (firstWord.matches("\\d+")) {
            if (months.containsKey(nextWord.toLowerCase()) || months.containsKey(previousWord.toLowerCase())) {
                if (months.containsKey(nextWord.toLowerCase())) {
                    addDate(firstWord, nextWord.toLowerCase());
                    currentWord = currentWord + 2;
                } else {
                    addDate(firstWord, previousWord.toLowerCase());
                    currentWord++;
                }
            } else {
                addPlainNumber(words[currentWord]);
                currentWord++;
            }
        } else {
            addPlainNumber(words[currentWord]);
            currentWord++;
        }
    }

    private void addPriceWithDollarSign(String word, String nextWord) {
        String priceTerm = "";
        float floatNumber;
        int intNumber;
        priceTerm = word.replaceAll("\\$*", "");
        if (nextWord.toLowerCase().equals("million")) {
            priceTerm = priceTerm.replaceAll("\\,*", "");
            priceTerm = priceTerm + " M Dollars";
            currentWord = currentWord+2;
        } else if (nextWord.toLowerCase().equals("billion")) {
            priceTerm = priceTerm.replaceAll("\\,*", "");
            priceTerm = priceTerm + "000" + " M Dollars";
            currentWord = currentWord+2;
        } else {
            if (priceTerm.contains(".")) {
                floatNumber = Float.parseFloat(priceTerm.replaceAll("\\,*", ""));
                if (floatNumber >= 1000000) {
                    priceTerm = String.valueOf(floatNumber / 1000000) + " M Dollars";
                } else
                    priceTerm = priceTerm + " Dollars";
            } else {
                intNumber = Integer.parseInt(priceTerm.replaceAll("\\,*", ""));
                if (intNumber >= 1000000)
                    priceTerm = String.valueOf(intNumber / 1000000) + " M Dollars";
                else
                    priceTerm = priceTerm + " Dollars";
            }
            currentWord++;
        }
        terms.put(priceTerm,currentDocument);
    }

    private void addPriceWithDollar(String number) {
        number = number.replaceAll("[,]","");
        int price = Integer.parseInt(number);
        String priceWithDollar = "";
        if (price <1000000) {
            priceWithDollar = number + " Dollars";
        } else {
            priceWithDollar = String.valueOf(price/1000000) + " M Dollars";
        }
        terms.put(priceWithDollar,currentDocument);
    }


    private void addDollarWithFraction(String number, String fraction) {
        String fractionTerm = number + " " + fraction + " Dollars";
        terms.put(fractionTerm,currentDocument);
    }


    private void addPercentNumber(String word) {
        if (word.contains("%"))
            terms.put(word,currentDocument);
        else
            terms.put(word + "%", currentDocument);
    }

    private void addLargeNumbers(String word, String nextWord) {
        String termName;
        word = word.replaceAll(",","");
        if (word.contains(".")) {
            float tempoFloat = Float.parseFloat(word);
            termName = String.format("%.3f", tempoFloat);
        } else {
            termName = word;
        }
        if (nextWord.toLowerCase().equals("thousand")) {
            termName = termName + "K";
            terms.put(termName,currentDocument);
        } else if (nextWord.toLowerCase().equals("million")) {
            termName = termName + "M";
            terms.put(termName,currentDocument);
        } else if (nextWord.toLowerCase().equals("billion")) {
            termName = termName + "B";
            terms.put(termName,currentDocument);
        }
    }

    private void addPlainNumber(String number) {
        String formattedNumber = number.replaceAll(",*", "");
        String numberTerm = "";
        long nativeNum;
        if (formattedNumber.contains(".")) {
            if (Float.parseFloat(formattedNumber) < 1000)
                numberTerm = String.valueOf(Float.parseFloat(formattedNumber));
            else if (Float.parseFloat(formattedNumber) < 1000000)
                numberTerm = String.valueOf(Float.parseFloat(formattedNumber) / 1000) + "K";
        } else {
            nativeNum = Long.parseLong(formattedNumber);
            if (nativeNum < 1000)
                numberTerm = String.valueOf(nativeNum);
            else if (nativeNum < 1000000)
                numberTerm = String.valueOf((float)nativeNum / 1000) + "K";
            else if (nativeNum < 1000000000)
                numberTerm = String.valueOf((float)nativeNum / 1000000) + "M";
            else
                numberTerm = String.valueOf((float)nativeNum / 1000000000) + "B";

        }
        terms.put(numberTerm,currentDocument);
    }


    private void addDate(String number, String month) {
        Term date;
        String dateText = "";
        if (number.matches("\\d{1}")) {
            dateText = months.get(month) + "-0" + number;
        } else if (number.matches("\\d{2}")) {
            dateText = months.get(month) + "-" + number;
        } else if (number.matches("\\d{4}")) {
            dateText = number + "-" + months.get(month);
        }
        terms.put(dateText,currentDocument);
    }


}
