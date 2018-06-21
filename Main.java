/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Umut Ozturk
 */
public class Main {
    public static void main(String[] args){
       
       if(args.length!=2){
           
           System.out.println("Invalid argument!");
       }
       if(args[0].contains(".csv")){
          //LanguageModelForCsv class is used to handle the email parsing operations.
          LanguageModelForCsv csv=new LanguageModelForCsv();
          csv.readFile(args[0],args[1]);     
           
       }
       else if(args[0].contains(".txt")){
            //LanguageModelForTxt class is used to handle the text operations without parsing.
        
            LanguageModelForTxt txt=new LanguageModelForTxt();
            txt.readFile(args[0],args[1]);
            
       } 
    }
}
