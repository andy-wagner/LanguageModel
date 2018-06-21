UMUT OZTURK - 21328394

This application implements the language models such as trigram,bigram and unigram. Also the hashmap is used to store the count of each word.
There are the hash maps for each model and  these hash maps store the count values.
The important two parts are in this application. The one of these parts is parsing emails. This application deletes the unrelated word/sentence from email.
In other words , it generally takes the sentences after Subject: as body of email. But in the some statuses  there is no text after Subject: .
After gathering the body of emails ,the all models must be implemented and trained. This application creates the all models  by using hashmap.
The hashmap of each model is created and it must store only the count of words. After training models , test set is used to determine how successful these models are implemented or not.
So the perplexity is calculated for smoothing models. The perplexity of each emails is calculated according to smoothing bigram and trigram.
The probability of each sentence in one email is calculated by using Math.log() function  for avoiding underflow problem. Also Math.pow(2,"sentence_probability") is calculated.
"sentence_probability"  generally can be negative value and this value is used in the Math.log() again for perplexity of all sentences in the email .If the parameter of Math.log() is negative or zero ,then 
result will be isNaN. So Math.pow(2,"sentence_probability") must be calculated.This application does not calculate the perplexity of each sentence for second data set.
But one perplexity is calculated for all test data in the second dataset according to smoothing bigram and trigram. 
Finally , 60 sentences with their probability  are generated according to unsmoothed and smoothed models.
The other part of application creates the all models without parsing text.The above steps are reimplemented.


This application has important points:

1-The probability of each sentence in one email is calculated by using Math.log() function  for avoiding underflow problem.
 Also Math.pow(2,"sentence_probability") is calculated after calculating probability of sentence for calculating perplexity.
"sentence_probability"  generally can be negative value and this value is used in the Math.log() again for perplexity of email .
If the parameter of Math.log() is negative or zero ,then  result will be isNaN. isNaN can cause great distress in probability calculation .
(For example the probability of one sentence is  6.876343560518264E-123 by using Math.pow(2,"sentence_probability") )

2-If the email count is bigger than 50000 ,this application gives run times error. This error is regex stackoverflow error.
Because the regex is compiled into a recursive call, which results in a StackOverflow error when used on a very large string.
But if the number of emails is not bigger than 50000, any errors don't take place.(https://stackoverflow.com/questions/7509905/java-lang-stackoverflowerror-while-using-a-regex-to-parse-big-strings)

3-This application is developed for each dataset.The perplexity of each email is calculated according to smoothing models.
But in the second dataset , the perplexity of all test set is calculated according to smoothing models.So there is one perplexity result for second dataset.

4-In the first dataset ,firstly the emails of test set are printed and their smoothed trigram probabilities are calculated.Then the perplexity of each email is calculated.
Then 60 sentences with probability are created according to unsmoothed and smoothed.

5-In second dataset, firstly the sentence of test set printed and their  smoothed trigram probabilities are calculated.Then the perplexity of all test set is calculated and printed.
There is one perplexity calculation.(The perplexity of each test set sentence are not calculated.)
Finally , 60 sentences with probability are created according to smoothing and unsmoothing.

6-The run time of application generally is at most 1 minute 33 seconds for second data.