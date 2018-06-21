/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

//The Unigram class represents the unigram language model
public class Unigram {
    /*The data structure of trigram is hashmap.Hashmap stores the key and value.
       Key is one word in the sentence.The word is seperated by using split function.
       Value is the count value of word in the trainning set.
       Words are seperated according to white space by using split function in Java.
    */
    
    private HashMap<String, Integer> unigramCount = new HashMap<>();
    private int totalWordNumber=0;
    //The following variable is the unique word number of trainning set (unigram hashmap size)
    private int uniqueWordNumber=0;
     /*
      SentenceProbability variable keeps the probability of generating sentence according to unigram model.
     */
    private double sentenceProbability=0.0;
    
    public int getUniqueWordNumber(){
       return this.uniqueWordNumber;
       
    }
    public void setUniqueWordNumber(int uniqueWordNumber){
        this.uniqueWordNumber=uniqueWordNumber;
        
    }
    public int getTotalWordNumber(){
        return this.totalWordNumber;
    }
    
    public double getSentenceProbability(){
        return sentenceProbability;
        
    }
    
    public HashMap<String,Integer> getUnigramCount(){
           return this.unigramCount;
     }
    
    //The following function seperates the words as single
     public void seperateUnigramWord(String line) {

     
        String part[]=line.split("\\s+");
        int count=0;
        Locale enLocale = Locale.forLanguageTag("en_US");
        for (int i = 0; i <part.length; i++) {
            count=0;
            String key=part[i].trim().toLowerCase(enLocale);
	    if(unigramCount.containsKey(key)){
               count=unigramCount.get(key);
            }
            unigramCount.put(key, count + 1); 
        }
    }
     
      /*
      The following function is used to seperate the sentence as the single word for test set.
      Also the hashmap stores unseen words in the trainning, but these unseen words's count will be zero.
      So these words can be used to generate the sentences.
      */
     
     public void seperateUnigramWordForTestSet(String line) {

        String part[]=line.split("\\s+");
          Locale enLocale = Locale.forLanguageTag("en_US");
        for (int i = 0; i <part.length; i++) {
            
	    if(unigramCount.containsKey(part[i].trim().toLowerCase(enLocale))==false){
                unigramCount.put(part[i].trim().toLowerCase(enLocale),0);    
            }
           
        }
    }
    //The following function returns the total word number of trainning set.
     public void findTotalWordNumber(){
         int count=0;
         for (HashMap.Entry<String, Integer> entry : unigramCount.entrySet()) {
             String key=entry.getKey();
             count+=unigramCount.get(key);   
         }  
         this.totalWordNumber=count;
          
     }
     /*
      The following function is used to select the random words for generating sentences.     
      */
	public String unSmoothingSelectRandomWord(){

	       double totalProbability = 0.0;
               double randomNumber = Math.random();
               String key="";
               double numerator=0.0,probability=0.0;
		for (Map.Entry<String, Integer> entry :unigramCount.entrySet()) {
			key = entry.getKey();
                        numerator = unigramCount.get(key);
                         /*
                            This condition is used to detect the unseen word in the trainning set.
                            The hashmap stores the unseen words with zero count that come from test set.
                            But because of unsmoothing ,these unseen words are not used to select random word.
                          */
                        
                        if(numerator!=0.0){			  
                          probability = numerator / totalWordNumber;
			  totalProbability += probability;
			  if(randomNumber <= totalProbability){
                               /*
                                 Also the probability of sentence is calculated while generating sentence.
                                */  
                                sentenceProbability+=(Math.log(probability)/Math.log(2));
				return key;
			  }
                        }
		}
		return "";
	} 
        /*
        
        The following function is used to generate the sentence according to unsmoothing.
        <s> is the first word of generating sentence.
        */
	public String unSmoothingGenerateSentence(){
		
	    String sentence = "<s>";
            sentenceProbability=0.0;
            while ((sentence.split("\\s+").length+1)<=30) {
                String selectedWord = unSmoothingSelectRandomWord();

                if (selectedWord.equals("</s>")) {
                    sentence = sentence + " " + selectedWord;
                    break;
                }

                sentence = sentence + " " + selectedWord;
             

            }
             //White spaces,<s> and </s> characters are deleted from sentence.
            sentence = sentence.trim();
            sentence = sentence.replace("<s>", "");
            sentence = sentence.replace("</s>", "");
            //If sentence is created ,then log probablity is changed to 2^
            if(sentence.split("\\s+").length>0){
                sentenceProbability=Math.pow(2,sentenceProbability);
            }
            else{
              /*
                The generating sentence can be "<s> </s>",but these tokens must be deleted from sentence.
                Also these tokens effect the probability of sentence, so sentence probability must be zero.
                */
                sentenceProbability=0.0;
            }
            return sentence;
      }
        /*
        The following functions is used to select the random word acording to smoothing.
        */
        public String smoothingSelectRandomWord(){
            
	       double totalProbability = 0.0;
               double randomNumber = Math.random();
               String key="";
               double numerator=0.0,probability=0.0;
		for (Map.Entry<String,Integer> entry : unigramCount.entrySet()) {
			key = entry.getKey();
                        numerator = unigramCount.get(key)+1;
			probability = numerator / (getTotalWordNumber()+getUniqueWordNumber());
                        
			totalProbability += probability;
			if(randomNumber <= totalProbability){
                               /*
                               Also the smoothed probability of sentence is calculated while generating sentence.
                               */  
                                sentenceProbability+=(Math.log(probability)/Math.log(2));
				return key;
			}
		}
		return "";
	}
          /*
        The following function is used to generate the sentence according to smoothing 
        */ 
        public String smoothingGenerateSentence(){
		
	    String sentence = "<s>";
            sentenceProbability=0.0;
            while ((sentence.split("\\s+").length+1)<=30) {
                String selectedWord = smoothingSelectRandomWord();
               
                if (selectedWord.equals("</s>")) {
                    sentence = sentence + " " + selectedWord;
                    break;
                }

                sentence = sentence + " " + selectedWord;
               
            }
            sentence = sentence.trim();
            sentence = sentence.replace("<s>", "");
            sentence = sentence.replace("</s>", "");
              /*
                This condition is used to check whether sentence is created or not.
                If sentence is not created, then probability of sentence is not converted to 2^.
                */
            if(sentence.split("\\s+").length>0){
                sentenceProbability=Math.pow(2,sentenceProbability);
            }
            else{
                 /*
                The generating sentence can be "<s> </s>",but these tokens must be deleted from sentence.
                Also these tokens effect the probability of sentence, so sentence probability must be zero.
                */
                sentenceProbability=0.0;
            }
            
            
            return sentence;
      }
        
        
        
}
