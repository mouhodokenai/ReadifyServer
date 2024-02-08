package org.example;

public class Book {
    private int article;
    private String realiseDate;
    private String title;
    private String publication;
    private String author;
    private String genre;
    private boolean isAvailable;
    private String description;

    public Book(int article, String realiseDate, String title, String publication, String author, String genre, boolean isAvailable, String description) {
        this.article = article;
        this.realiseDate = realiseDate;
        this.title = title;
        this.publication = publication;
        this.author = author;
        this.genre = genre;
        this.isAvailable = isAvailable;
        this.description = description;
    }

}
