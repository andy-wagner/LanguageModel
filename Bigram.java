/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
//The Bigram class represents the bigram language model
public class Bigram {
    
       /*The data structure of bigram is hashmap.Hashmap stores the key and value.
       Key is double words in the sentence.The double words are seperated by using split function.
       Value is the count value of double words in the trainning set.
    */
       private HashMap<String, Integer> bimapCount = new HashMap<>();
       //Unigram class variable is used to access the important variables in the Unigram class such as unique word number...
       private Unigram unigram=null;
       /*
       SentenceProbability variable keeps the probability of generating sentence according to bigram model.
       */
       private double sentenceProbability=0.0;
       
       
       public Bigram(Unigram unigram){
           this.unigram=unigram;
       }

       public double getSentenceProbability(){
           return sentenceProbability;
       }
       public HashMap<String,Integer> getBimapCount(){
           return this.bimapCount;
       }
       public Unigram getUnigram(){
           
           return this.unigram;
       }
     
   /*
      The following functions calculates the probability of sentence according to smoothed bigram.
       */
      public double calculateSmoothingProbabilityForSentence(String line){
        
        String parts[]=line.split("\\s+");
        int count=0;
        double numerator=0.0,divider=0.0,prob=0.0,totalprob=0.0;
        Locale enLocale = Locale.forLanguageTag("en_US");
        for(int i=0;i<parts.length-1;i++){
            //The lower case is controlled by this function.
            String key=parts[i].trim().toLowerCase(enLocale)+" "+parts[i+1].trim().toLowerCase(enLocale);
           //standard bigram calculation is implemented.
            numerator = bimapCount.get(key) + 1;
            divider = unigram.getUnigramCount().get(parts[i].trim().toLowerCase(enLocale));
            prob = numerator / (divider + unigram.getUniqueWordNumber());
            //Math.log is used to avoid the underflow problem.
            totalprob+=(Math.log(prob)/Math.log(2));
           
        }
        //The log probability is converted to 2^.
        totalprob=Math.pow(2,totalprob);
        
        return totalprob; 
      }	
      //The following function is used to seperate the sentence as double word.
      public void seperateBigramWord(String line) {
      
        String parts[]=line.split("\\s+");
        int count=0;
        Locale enLocale = Locale.forLanguageTag("en_US");
        for(int i=0;i<parts.length-1;i++){
            count=0;
            String key=parts[i].trim()+" "+parts[i+1].trim();
            key=key.toLowerCase(enLocale);
            if(bimapCount.containsKey(key)){
               count=bimapCount.get(key);
            }
            bimapCount.put(key, count + 1);    
        }
     
    }
      /*
      The following function is used to seperate the sentence the double word for test set.
      Also the hashmap stores unseen words in the trainning, but these unseen words's count will be zero.
      So these words can be used to generate the sentences.
      */
      public void seperateBigramWordForTestSet(String line) {
       
        
        String parts[]=line.split("\\s+");   
        Locale enLocale = Locale.forLanguageTag("en_US");
        for(int i=0;i<parts.length-1;i++){
            String key=parts[i].trim()+" "+parts[i+1].trim();
            key=key.toLowerCase(enLocale);
            if(bimapCount.containsKey(key)==false){
               
               bimapCount.put(key,0);  
            
            }              
        }
     
    }  
 /*
      The following function is used to select the random word for generating sentences.
      
      
      */
	public String unSmoothingSelectRandomWord(String lastWord){

		String key="";
		double totalProbability = 0.0;
		double randomNumber = Math.random();
                double numerator=0.0,divider=0.0,prob=0.0;
                for (Map.Entry<String,Integer> entry : bimapCount.entrySet()) {
			 key = entry.getKey();
                         
                         if(key.split(" ")[0].equals(lastWord)){                            
                            numerator = bimapCount.get(key);
                            divider = unigram.getUnigramCount().get(key.split(" ")[0]);
                            /*
                             this condition is used to detect the unseen word in the trainning set.
                            The hashmap stores the unseen words with zero count that come from test set.
                            But because of unsmoothing ,these unseen words are not used to select random word.
                            */
                            if(numerator!=0.0&&divider!=0.0){
                                prob = numerator /divider;
			        totalProbability +=prob ;                              
			       if(randomNumber <= totalProbability){  
                                   /*
                                   Also the probability of sentence is calculated while generating sentence.
                                   */
                                   sentenceProbability+=(Math.log(prob)/Math.log(2));
			         return key;
			      }
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
                String lastWord=sentence;
                sentenceProbability=0.0;
                //This condition controls the number of words.
		while((sentence.split("\\s+").length+1)<=30){	
                    
                        String newWord =unSmoothingSelectRandomWord(lastWord).split(" ")[1];                                                         			
			if(newWord.equals("</s>")){ 
			     sentence = sentence + " " + newWord;
			     break;
			}
			
		        sentence = sentence + " " + newWord;			
                        lastWord=newWord;
                      
		}
                //White spaces,<s> and </s> characters are deleted from sentence.
		sentence = sentence.trim(); 
                sentence = sentence.replace("<s>", ""); 
		sentence = sentence.replace("</s>", "");
                //If sentence is created ,then log probablity is changed to 2^
                if(sentenceProbability!=0){
                    sentenceProbability=Math.pow(2,sentenceProbability);
                    
                }
		return sentence;
	}
	/*
        The following functions is used to select the random word acording to smoothing.
        */
	public String smoothingSelectRandomWord(String lastWord){

		String key="";
		double totalProbability = 0.0;
		double randomNumber = Math.random();
                 double numerator=0.0,divider=0.0,prob=0.0;
                for (Map.Entry<String,Integer> entry :bimapCount.entrySet()) {
			 key = entry.getKey();
			if(key.split(" ")[0].equals(lastWord)){
                            //Standard add-one smoothing operations.
                            numerator = bimapCount.get(key)+1;
		            divider = unigram.getUnigramCount().get(key.split(" ")[0]);    
                            
	                    prob = numerator / (divider+unigram.getUniqueWordNumber());                            
		            totalProbability += prob;                             
			    if(randomNumber <= totalProbability){    
                            //Also the probability of sentence is calculated while generating sentence according to smoothing.
			        sentenceProbability+=(Math.log(prob)/Math.log(2));
                                return key;
			     }
			}
		}
		return "";
	}
       /*
        The following function is used to generate the sentence according to smoothing 
        */ 
      public String smoothingGenerateSentence(){
		
          String sentence = "<s>";
          String lastWord = sentence;
          sentenceProbability=0.0;
          while ((sentence.split("\\s+").length+1)<=30) {   
              String s=smoothingSelectRandomWord(lastWord);
             
              if(s.trim().length()==0){
                 break;
              }
              String newWord = s.split(" ")[1];           
              if (newWord.equals("</s>")) {
                  sentence = sentence + " " + newWord;
                  break;
              }

              sentence = sentence + " " + newWord;
              lastWord = newWord;
            
          }
          sentence = sentence.trim();
          sentence = sentence.replace("<s>", "");
          sentence = sentence.replace("</s>", "");
          if(sentenceProbability!=0){
             sentenceProbability=Math.pow(2,sentenceProbability);
                    
          }
          return sentence;
	}
	
    
}
