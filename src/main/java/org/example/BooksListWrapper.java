package org.example;

import java.util.List;

public class BooksListWrapper {
    private List<Book> books;

    public BooksListWrapper(List<Book> books) {
        this.books = books;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
