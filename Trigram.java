/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

//The Trigram class represents the trigram language model
public class Trigram {
    
     /*The data structure of trigram is hashmap.Hashmap stores the key and value.
       Key is triple words in the sentence.The triple words are seperated by using split function.
       Value is the count value of triple words in the trainning set.
       For example, key can be  "umut ozturk go".Also white spaces are between the words.
       So these words are seperated according to white space by using split function in Java.
    */
      private HashMap<String, Integer> trigramCount=new HashMap<>();  
      private Bigram bigram=null;
      
       /*
       SentenceProbability variable keeps the probability of generating sentence according to trigram model.
       */
      private double sentenceProbability=0.0;
      public Trigram(Bigram bigram){
          this.bigram=bigram;
      }
      
      public HashMap<String,Integer> getTrigramCount(){
           return this.trigramCount;
       }
     public double getSentenceProbability(){
           return sentenceProbability;
     }
    /*
      
      The following functions calculates the probability of sentence according to smoothed trigram.
      
     */
    public double calculateSmoothingProbabilityForSentence(String line){
        String part[] = line.split("\\s+");
        double totalProbability=0.0;
         double numerator=0.0,divide=0.0,probability=0.0;
         Locale enLocale = Locale.forLanguageTag("en_US");
        for (int i = 0; i < part.length - 2; i++) {
            
            String key = part[i].trim() + " " + part[i + 1].trim() + " " + part[i + 2].trim();
            key=key.toLowerCase(enLocale);
            /*
            this condition controls the sentence boundary,double sentence boundary is handled for trigram.
            But the sentence boundary probability is implemented like bigram model.
            For example, P(the|s s) can be calculated such as P(the|s) 
            */
            if(part[i].trim().equals("<s>")){
               numerator=bigram.getBimapCount().get(part[i].trim().toLowerCase(enLocale)+" "+part[i+1].trim().toLowerCase(enLocale))+1.0;
               divide= bigram.getUnigram().getUnigramCount().get(part[i].trim().toLowerCase(enLocale))+bigram.getUnigram().getUniqueWordNumber();
               probability=numerator/divide;
               totalProbability+=(Math.log(probability)/Math.log(2));
            }
             //Standard add-one smoothing calculation for trigram.
             numerator = trigramCount.get(key) + 1.0;
             divide = bigram.getBimapCount().get(part[i].trim().toLowerCase(enLocale) + " " + part[i+1].trim().toLowerCase(enLocale)) + bigram.getUnigram().getUniqueWordNumber();
             probability = numerator / (divide);

            totalProbability+=(Math.log(probability)/Math.log(2));                       
            
        }
      
         
        totalProbability=Math.pow(2,totalProbability);
      
        return totalProbability;
    }  
    //The following function seperates the words as triple
    public void seperateTrigramWord(String line) {
        int count = 0;
        String part[] = line.split("\\s+");
        Locale enLocale = Locale.forLanguageTag("en_US");
        for (int i = 0; i < part.length - 2; i++) {
            count = 0;
            String key = part[i].trim() + " " + part[i + 1].trim() + " " + part[i + 2].trim();
            key=key.toLowerCase(enLocale);
            if (trigramCount.containsKey(key)) {
                count = trigramCount.get(key);

            }
            trigramCount.put(key, count + 1);
        }

    }
      /*
      The following function is used to seperate the sentence as  the triple word for test set.
      Also the hashmap stores unseen words in the trainning, but these unseen words's count will be zero.
      So these words can be used to generate the sentences.
      */
     public void seperateTrigramWordForTestSet(String line) {
   
        String part[] = line.split("\\s+");
        Locale enLocale = Locale.forLanguageTag("en_US");
        for (int i = 0; i < part.length - 2; i++) {
          
            String key = part[i].trim() + " " + part[i + 1].trim() + " " + part[i + 2].trim();
            key=key.toLowerCase(enLocale);
            if (trigramCount.containsKey(key)==false) {
                 trigramCount.put(key,0);
            }
          
        }

    }
     /*
      The following function is used to select the random words for generating sentences.     
      */
	public String unSmoothingSelectRandomWord(String lastWord){
                double totalProbability = 0.0;
		double randomNumber = Math.random();   
                double numerator=0.0,divide=0.0,probability=0.0;
                
		for (Map.Entry<String,Integer> entry : trigramCount.entrySet()) {                   
			String key = entry.getKey();
                        
                        if(key.split(" ")[0].equals(lastWord)){
                                                                                 
                            numerator = trigramCount.get(key);  
                            String []parts=key.split(" ");
                            divide = bigram.getBimapCount().get(parts[0] + " " + parts[1]);
                            /*
                             this condition is used to detect the unseen word in the trainning set.
                            The hashmap stores the unseen words with zero count that come from test set.
                            But because of unsmoothing ,these unseen words are not used to select random word.
                            */
                            if(numerator!=0.0 && divide!=0.0){
                               probability = numerator / divide;
                               totalProbability += probability;
                               if (randomNumber <= totalProbability) {
                                   if(lastWord.equals("<s>")){
                                       numerator=bigram.getBimapCount().get(parts[0]+" "+parts[1]);
                                       divide= bigram.getUnigram().getUnigramCount().get(parts[0]);
                                       double  sentenceBoundaryProb=numerator/divide;
                                       sentenceProbability=(Math.log(sentenceBoundaryProb)/Math.log(2));
                                      
                                  }
                                  /*
                                  Also the probability of sentence is calculated while generating sentence.
                                  */  
                                 sentenceProbability+=(Math.log(probability)/Math.log(2));
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
		String []parts;
                sentenceProbability=0.0;
                while((sentence.split("\\s+").length+2)<=30){			
                        String newWord = unSmoothingSelectRandomWord(lastWord);                       
			if(newWord.equals("")){
                           
                            break;
                        }
                        parts=newWord.split(" ");
			if(parts[2].equals("</s>")){  
				sentence = sentence + " " + parts[1]+" "+parts[2];
				break;
			}
			
		       sentence = sentence + " " +parts[1]+" "+parts[2];
                        lastWord=parts[2];
                      
                       
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
                double totalProbability = 0.0;
		double randomNumber = Math.random();  
                double numerator=0.0,divide=0.0,probability=0.0;
		for (Map.Entry<String, Integer> entry : trigramCount.entrySet()) {                   
			String key = entry.getKey();
                        
                        if(key.split(" ")[0].equals(lastWord)){
                             //Standard add-one smoothing operations.
                                numerator = trigramCount.get(key)+1.0;
                                String []parts=key.split(" ");
	                        divide = bigram.getBimapCount().get(parts[0]+" "+parts[1])+bigram.getUnigram().getUniqueWordNumber();                      
		                probability= numerator / (divide);
                                
				totalProbability += probability;                                                            				 
                                if(randomNumber <= totalProbability){
                                    /*
                                    this condition controls the sentence boundary,double sentence boundary is handled for trigram.
                                    But the sentence boundary probability is implemented like bigram model.
                                    For example, P(the|s s) can be calculated such as P(the|s) .
                                 */
                                   if(lastWord.equals("<s>")){
                                       numerator=bigram.getBimapCount().get(parts[0]+" "+parts[1])+1.0;
                                       divide= bigram.getUnigram().getUnigramCount().get(parts[0])+bigram.getUnigram().getUniqueWordNumber();
                                       double  sentenceBoundaryProb=numerator/divide;
                                       sentenceProbability=(Math.log(sentenceBoundaryProb)/Math.log(2));
                                      
                                  } 
                                    /*
                                  Also the smoothed probability of sentence is calculated while generating sentence.
                                  */  
                                  sentenceProbability+=(Math.log(probability)/Math.log(2)); 
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
                String lastWord=sentence;
		String []parts;
                sentenceProbability=0.0;
                while((sentence.split("\\s+").length+2)<=30){			
                        String newWord = smoothingSelectRandomWord(lastWord);                       
			if(newWord.equals("")){
                           
                            break;
                        }
                        parts=newWord.split(" ");
			if(parts[2].equals("</s>")){  
				sentence = sentence + " " + parts[1]+" "+parts[2];
				break;
			}
			
		      sentence = sentence + " " +parts[1]+" "+parts[2];			                                             
                     
                       lastWord=parts[2];
                      
                       
		}
                
		sentence = sentence.trim();
                sentence = sentence.replace("<s>", ""); 
		sentence = sentence.replace("</s>", "");
                /*
                This condition is used to check whether sentence is created or not.
                If sentence is not created, then probability of sentence is not converted to 2^.
                */
                if(sentenceProbability!=0){
                  
                  sentenceProbability=Math.pow(2,sentenceProbability);
                    
                }
                
                
		return sentence;
        }
}
