package org.example;

public class Loan {
    private int loanId;
    private int userId;
    private String bookTitle;
    private String loanDate;
    private String returnDate;

    public Loan(int loanId, int userId, String bookTitle, String loanDate, String returnDate) {
        this.loanId = loanId;
        this.userId = userId;
        this.bookTitle = bookTitle;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
    }

}
