package com.coursehelper.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import com.coursehelper.model.Quote;
import com.google.gson.Gson; 

public class QuoteAPIClient {

    private static final String API_URL = "https://zenquotes.io/api/";

    public List<Quote> fetchQuotes() throws IOException {

        URL url = new URL(API_URL + "quotes/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        StringBuilder response = stringBuilder(conn);

        return parseQuote_s(response.toString());

    }


    public Quote fetchRandomQuote() throws IOException {

        URL url = new URL(API_URL + "random/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        StringBuilder response = stringBuilder(conn);

        return parseQuote(response.toString());

    }

    private StringBuilder stringBuilder(HttpURLConnection conn) throws IOException{

        BufferedReader reader = new BufferedReader(
            new InputStreamReader(conn.getInputStream())
        );

        StringBuilder response = new StringBuilder();
        String line;

        while((line = reader.readLine()) != null){
            response.append(line);
        }

        reader.close();

        return response;

    }


    private Quote parseQuote(String json){

        Gson gson = new Gson();
        Quote quote = gson.fromJson(json, Quote.class);
        return quote;
    }

    private List<Quote> parseQuote_s(String json) {
        Gson gson = new Gson();
        Quote[] quotesArray = gson.fromJson(json, Quote[].class);
        return Arrays.asList(quotesArray);
    }

    
}
