/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//This class is used to parse the emails in emails.csv file
public class LanguageModelForCsv {
    //sentences arraylist stores the sentences of file.
     private ArrayList<String> sentences = new ArrayList<>();
     //the sentences of each email are stored into emails hashmap.
     //This hashmap is used to determine the number of training email and test email.
     private HashMap<Integer,ArrayList<String>>emails=new HashMap<>();
     private Unigram unigram=null;
     private Bigram bigram=null;
     private Trigram trigram=null;
      
     public LanguageModelForCsv(){
      unigram=new Unigram();
      bigram=new Bigram(unigram);
      trigram=new Trigram(bigram);
         
     }
     /*
      The following function deletes the irrelevant words from ArrayList.
      In other words , it generally takes the text after Subject: as the body of email. 
     */
     
    public void manipulateSentencesForCsvFile(int emailNumber,ArrayList<String> lines) {
        int control = 0;
        int firstIndex = 0;
        int secondIndex = 0;

        for (int i = 0; i < lines.size(); i++) {

            if (control == 0 && (lines.get(i).contains("-Original Message-") || lines.get(i).contains("--- Forwarded by"))) {
                firstIndex = i;
                control = 1;
            }
            if (lines.get(i).contains("Subject:") || lines.get(i).contains("To:") || lines.get(i).contains("cc:") || lines.get(i).contains("From:")) {

                secondIndex = i;
                removeElementsBetweenTwoPoints(lines, firstIndex, secondIndex);

                control = 0;
                i = firstIndex;
                i--;

            }

        }

        String output = "";
        for (int i = 0; i < lines.size(); i++) {
            output += lines.get(i) + " ";

        }

        output = output.replace('\"', ' ');
        output = seperatePunctutation(output);
        //the sentence and tail detection is made by regex. 
        Pattern re = Pattern.compile("[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)", Pattern.MULTILINE | Pattern.COMMENTS);
        Matcher reMatcher = re.matcher(output);
        while (reMatcher.find()) {
            sentences.add("<s> " + reMatcher.group() + " </s>");
        }
        //The sentences of each email are inserted into hashmap.
        emails.put(emailNumber,new ArrayList<String>(sentences));
        sentences.clear();

    }
  /*
    The punctuations in the sentences of each email  must be seperated by using white space.
    So the following function handles this operations.
    */
    public String seperatePunctutation(String line) {

        String startPunctutations = "[(";
        String finalPunctutations = "],:;)!?";
        String output = "";
        String[] s = line.trim().split("\\s+");
        for (int i = 0; i < s.length; i++) {
           
          if(s[i].length()>0){   
            if (finalPunctutations.contains(s[i].charAt(s[i].length() - 1) + "")) {
                s[i] = s[i].substring(0, s[i].length() - 1) + " " + s[i].charAt(s[i].length() - 1) + " ";
              

            }
            if (startPunctutations.contains(s[i].charAt(0) + "")) {
                s[i] = s[i].charAt(0) + " " + s[i].substring(1, s[i].length());

            }

            if (s[i].contains(".")) {

                if (s[i].contains("...")) {

                    s[i] = s[i].substring(0, s[i].length() - 3) + " " + "... ";
                } else if (s[i].charAt(s[i].length() - 1) == '.') {
                    s[i] = s[i].substring(0, s[i].length() - 1) + " " + ". ";

                }

            }
            output += s[i] + " ";
          }
        }

        return output;
    }
 //The following function removes the unreleated sentences between two indexes.
    public void removeElementsBetweenTwoPoints(ArrayList<String> lines, int firstIndex, int secondIndex) {

        for (int i = secondIndex; i >= firstIndex; i--) {

            lines.remove(i);

        }

    }
//The following function reads the sentences line by line .
    public void readFile(String inputFile,String outputFile) {
        try {
            
            File file = new File(inputFile);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            ArrayList<String> lines = new ArrayList<>();
            int xfileNameControl = 0;
            int emailUniqueNumber=0;
            
            while (line != null) {

                if (xfileNameControl == 1 && line.trim().length() > 0) {
                    if (!line.contains("\"Message-ID: <") && !line.equals("\"")) {

                        if (line.trim().length() > 0 && line.split("\\s+").length > 1) {
                            lines.add(line);
                        }
                    } else {
                        
                        manipulateSentencesForCsvFile(emailUniqueNumber,lines);
                        //print(lines);
                        lines.clear();                     
                        xfileNameControl = 0;
                    }
                }

                if (line.contains("X-FileName:")) {
                    emailUniqueNumber++;
                    xfileNameControl = 1;
                    
                   
                    
                }

                line = br.readLine();
            }
           // System.out.println("------------------------------------------------");
            if (lines.size() > 0) {
                manipulateSentencesForCsvFile(emailUniqueNumber,lines);
                //print(lines);
            }
         processTrainAndTestData(outputFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
/*
    After reading sentences from file,the language models must be created .
    So the following function handles this operation.
    */
 public void processTrainAndTestData(String outputfile){
    
   int trainSetEmailNumber=(int)Math.ceil(emails.size()*0.6);
  try{ 
    
    File outputFile=new File(outputfile);
    FileWriter fw=new FileWriter(outputFile,false);
    BufferedWriter bw=new BufferedWriter(fw);
    bw.write("The emails in the test set according to smoothed trigram probability:\n\n");
    //training data is processed and all models are created.
   for(int i=1;i<=trainSetEmailNumber;i++){
         ArrayList<String> values=emails.get(i);
         for(int j=0;j<values.size();j++){
          unigram.seperateUnigramWord(values.get(j));
          bigram.seperateBigramWord(values.get(j));
          trigram.seperateTrigramWord(values.get(j));
             
         }                 
       
   }
   //word types or unique word number is detected  by using unigram hashmap size. 
   unigram.setUniqueWordNumber(unigram.getUnigramCount().size());
  
   double sentenceProbability=0.0,bigramSentenceProb=0.0, trigramSentenceProb=0.0;
   //Test data is  processed and add-one smoothing is calculated for each models.
  for(int i=trainSetEmailNumber+1;i<=emails.size();i++){
        ArrayList<String> values=emails.get(i);
        sentenceProbability=0.0;
        bigramSentenceProb=0.0;
        trigramSentenceProb=0.0;
        bw.write(i+".Email :\n\n");
        for(int j=0;j<values.size();j++){
            unigram.seperateUnigramWordForTestSet(values.get(j));          
            bigram.seperateBigramWordForTestSet(values.get(j));
            sentenceProbability=bigram.calculateSmoothingProbabilityForSentence(values.get(j));
            /*
               The following condition controls the infinity situtation.Because if the parameter of 
             Math.log() is infinity , Math.log() returns the infinity result.So this situtation must be 
             eliminated.
            */
            if(sentenceProbability!=0.0){
              bigramSentenceProb+=(Math.log(sentenceProbability)/Math.log(2));     
            }
            
             trigram.seperateTrigramWordForTestSet(values.get(j));
             sentenceProbability=trigram.calculateSmoothingProbabilityForSentence(values.get(j));
            
            if(sentenceProbability!=0.0){
              trigramSentenceProb+=(Math.log(sentenceProbability)/Math.log(2));    
           }
             
             bw.write(values.get(j)+"\n");
         }   
        bw.write("\n\nThe smoothed trigram probability of email: "+trigramSentenceProb+" \n");
        
        double totalWordInEmail=totalWordTestSet(values);
        
        double perplexityBigram=bigramSentenceProb*(-1/totalWordInEmail); 
        perplexityBigram=Math.pow(2,perplexityBigram);
         
        double perplexityTrigram=trigramSentenceProb*(-1/totalWordInEmail);
        perplexityTrigram=Math.pow(2,perplexityTrigram);
   
        bw.write("Email perplexity bigram: "+perplexityBigram+"\n");
        bw.write("Email perplexity trigram: "+perplexityTrigram+"\n"); 
        bw.write("--------------------------------------------------\n\n\n");
  }
   
   
    genereateSentences(bw);
    bw.flush();
    bw.close();
  }
  catch(Exception e){
      e.printStackTrace();
  }
 }
 //The following function finds the total number words in the test set.
public int totalWordTestSet(ArrayList<String> values){
    int count=0;
    for(int i=0;i<values.size();i++){
        String []parts=values.get(i).split("\\s+");
        count+=parts.length;        
    }
    return count;
}
 //The following function triggers the starting 60 sentences according to smoothing and unsmoothing of each email. 
 public void genereateSentences(BufferedWriter bw){
     try{
     unigram.findTotalWordNumber();
     bw.write("GENERATING SENTENCE: \n\n");
     String sentence="";
     
       bw.write("*******UnSmoothing Sentences: ********\n\n");
    for(int i=0;i<10;i++){
        
         bw.write("Unigram: "+unigram.unSmoothingGenerateSentence()+"\n");
         bw.write("Probability: "+unigram.getSentenceProbability()+"\n\n");
         
         bw.write("Bigram: "+bigram.unSmoothingGenerateSentence()+"\n");
         bw.write("Probability: "+bigram.getSentenceProbability()+"\n\n");
         
         bw.write("Trigram: "+trigram.unSmoothingGenerateSentence()+"\n");
         bw.write("Probability: "+trigram.getSentenceProbability()+"\n\n");
   
     }
    
    
     bw.write("------------------------------------\n\n");
     bw.write(" ********Smoothing Sentences: ********\n\n");
      
     for(int i=0;i<10;i++){
         bw.write("Unigram: "+unigram.smoothingGenerateSentence()+"\n");
         bw.write("Probability: "+unigram.getSentenceProbability()+"\n\n");
         
         bw.write("Bigram : "+bigram.smoothingGenerateSentence()+"\n");
         bw.write("Probability: "+bigram.getSentenceProbability()+"\n\n");
         
         bw.write("Trigram : "+trigram.smoothingGenerateSentence()+"\n");
         bw.write("Probability: "+trigram.getSentenceProbability()+"\n\n");       
    }
  
  }
   catch(Exception e){
       e.printStackTrace();
   }  
     
 }

}
