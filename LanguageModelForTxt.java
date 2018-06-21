/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Umut Öztürk
 */

//This class is used to handle the sentences form dataset.txt file.
public class LanguageModelForTxt {
     //This arraylist is used to determine the number of training email and test email. 
    private ArrayList<String> sentences = new ArrayList<>();
     private Unigram unigram=null;
     private Bigram bigram=null;
     private Trigram trigram=null;
     
     public LanguageModelForTxt(){
         
         this.unigram=new Unigram();
         this.bigram=new Bigram(unigram);
         this.trigram=new Trigram(bigram);
     }
     
   //The following function reads the sentences line by line .
    public void readFile(String inputfile,String outputfile) {
        try {
            
            File file = new File(inputfile);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            
            String line = br.readLine();
            while (line != null) {

                
                line="<s> "+line+" </s>";
                if(line.trim().length()>0){
                  sentences.add(line);    
                }
                
                line = br.readLine();
            }
         
          
         processTrainAndTestData(outputfile);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
/*
    After reading sentences from file,the language models must be created .
    So the following function handles this operation.
  */
    
 public void processTrainAndTestData(String outputfile){
   int trainSetNumber=(int)Math.ceil(sentences.size()*0.6);
   double perplexityBigram=0.0,perplexityTrigram=0.0;
   try{
    
    File outputFile=new File(outputfile);
    FileWriter fw=new FileWriter(outputFile,false);
    BufferedWriter bw=new BufferedWriter(fw);
    bw.write("The sentences in the test set according to smoothed trigram probability:\n\n");
    //training data is processed and all models are created.
    for(int i=0;i<sentences.size();i++){
      
       if(i<trainSetNumber){
            unigram.seperateUnigramWord(sentences.get(i));
            bigram.seperateBigramWord(sentences.get(i));
            trigram.seperateTrigramWord(sentences.get(i));
           
       }
       else{
           //word types or unique word number is detected  by using unigram hashmap size. 
           if(i==trainSetNumber){
               unigram.setUniqueWordNumber(unigram.getUnigramCount().size());
               
           }
            //Test data is  processed and add-one smoothing is calculated for each models.
           unigram.seperateUnigramWordForTestSet(sentences.get(i));
           
           bigram.seperateBigramWordForTestSet(sentences.get(i));
           double s=bigram.calculateSmoothingProbabilityForSentence(sentences.get(i));
           /*
               The following condition controls the infinity situtation.Because if the parameter of 
             Math.log() is infinity , Math.log() returns the infinity result.So this situtation must be 
             eliminated.
            */
           if(s!=0.0){
              perplexityBigram+=(Math.log(s)/Math.log(2));     
           }
           
           trigram.seperateTrigramWordForTestSet(sentences.get(i));
           s=trigram.calculateSmoothingProbabilityForSentence(sentences.get(i));
           bw.write("Sentence : "+sentences.get(i)+"\n");
           bw.write("Smoothed trigram probability :  "+s+"\n\n\n");
           
          
           if(s!=0.0){
              perplexityTrigram+=(Math.log(s)/Math.log(2));    
           }
                     
       }
     
     }
 
   double totalWordTestSet=totalWordTestSet(trainSetNumber);
  
   perplexityBigram=perplexityBigram*(-1/totalWordTestSet); 
   perplexityBigram=Math.pow(2,perplexityBigram);
         
   perplexityTrigram=perplexityTrigram*(-1/totalWordTestSet);
   perplexityTrigram=Math.pow(2,perplexityTrigram);
   
   bw.write("PERPLEXITY of test set according to smoothed bigram : "+perplexityBigram+"\n");
   bw.write("PERPLEXITY of test set according to smoothed trigram : "+perplexityTrigram+"\n");

   genereateSentences(bw);
   sentences.clear();
    bw.flush();
    bw.close();
    } 
   catch(Exception e){
       e.printStackTrace();
   }
   
}
 //The following function finds the total number words in the test set.
  public int totalWordTestSet(int trainSetNumber){
     int count=0;
     for(int i=trainSetNumber;i<sentences.size();i++){
         String[]parts=sentences.get(i).split("\\s+");      
         count+=parts.length;
         
     }
     
    return count;
     
 }
 //The following function triggers the starting 60 sentences according to smoothing and unsmoothing of each email. 
 public void genereateSentences(BufferedWriter bw){
   try{ 
    unigram.findTotalWordNumber();
    // System.out.println("Unique Word: "+unigram.getUniqueWordNumber()+" Unigram TotalWordNumber: "+unigram.getTotalWordNumber());
    bw.write("\n\nGENERATING SENTENCE : \n\n");
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