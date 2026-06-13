package com.coursehelper.frontend.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import com.coursehelper.frontend.model.Quote;

public class QuoteService {

    private static final List<Quote> ALL_QUOTES = List.of(
        new Quote("The beautiful thing about learning is that no one can take it away from you.", "B.B. King"),
        new Quote("Education is the most powerful weapon which you can use to change the world.", "Nelson Mandela"),
        new Quote("The more that you read, the more things you will know.", "Dr. Seuss"),
        new Quote("Live as if you were to die tomorrow. Learn as if you were to live forever.", "Mahatma Gandhi"),
        new Quote("An investment in knowledge pays the best interest.", "Benjamin Franklin"),
        new Quote("The expert in anything was once a beginner.", "Helen Hayes"),
        new Quote("Success is the sum of small efforts repeated day in and day out.", "Robert Collier"),
        new Quote("Believe you can and you're halfway there.", "Theodore Roosevelt"),
        new Quote("It does not matter how slowly you go as long as you do not stop.", "Confucius"),
        new Quote("You don't have to be great to start, but you have to start to be great.", "Zig Ziglar"),
        new Quote("The secret of getting ahead is getting started.", "Mark Twain"),
        new Quote("Don't watch the clock; do what it does. Keep going.", "Sam Levenson"),
        new Quote("Quality is not an act, it is a habit.", "Aristotle"),
        new Quote("Learning never exhausts the mind.", "Leonardo da Vinci"),
        new Quote("The capacity to learn is a gift; the ability to learn is a skill.", "Brian Herbert")
    );

    private final List<Quote> cache = new ArrayList<>(ALL_QUOTES);
    Random random = new Random();

    public Quote getNextQuote() {
        if (cache.isEmpty()) {
            cache.addAll(ALL_QUOTES); // refill when exhausted
        }
        return cache.get(random.nextInt(cache.size()));
    }
}