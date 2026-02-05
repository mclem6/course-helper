package com.coursehelper.services;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.coursehelper.api.QuoteAPIClient;
import com.coursehelper.model.Quote;


public class QuoteService {

    private QuoteAPIClient client = new QuoteAPIClient();
    private Queue<Quote> cache = new LinkedList<>();


    public Quote getRandomQuote(){
        try {

            return client.fetchRandomQuote();
            
        } catch (Exception e) {
            return new Quote("Error fetching quote", "System");
        }
    }


    public Quote getNextQuote(){

        if(cache.isEmpty()){
            try {
                List<Quote> quotes = client.fetchQuotes();
                cache.addAll(quotes);

            } catch (Exception e) {
                //DEFAULT STRRING FOR NOW BUT LATER SHOULD ARCHIVE QUOTES LOCALLY TO PULL FROM
                return new Quote("What the mind can conceive and believe, and the heart desire, you can achieve",  "Norman Vincent Peale");
            }

        }
        
        return cache.poll();
    
    }
    
}
