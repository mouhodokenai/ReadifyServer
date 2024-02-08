package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

public class Database {
    protected static String dbHost = "localhost";
    protected static String dbPort = "3306";
    protected static String dbUser = "root";
    protected static String dbPass = " ";
    protected static String dbName = "readify";

    String answerdb = null;
    static Connection dbConnection;

    static {
        try {
            dbConnection = getDbConnection();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    String request;

    public static Connection getDbConnection()
            throws ClassNotFoundException, SQLException {
        String connectionString = "jdbc:mysql://" + dbHost + ":"
                + dbPort + "/" + dbName;
        Class.forName("com.mysql.cj.jdbc.Driver");
        dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPass);
        return dbConnection;
    }

    //метод для входа пользователя
    public User loginUser(String email, String enteredPassword) {
        User user = null;
        request = "SELECT password, id, name FROM user WHERE email = ?";

        try (PreparedStatement prSt = dbConnection.prepareStatement(request)) {
            prSt.setString(1, email);
            ResultSet resultSet = prSt.executeQuery();

            while (resultSet.next()) {
                String hashedPasswordFromDB = resultSet.getString("password");
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");

                // Проверка пароля
                if (BCrypt.checkpw(enteredPassword, hashedPasswordFromDB)) {
                    user = new User(id, name, email, enteredPassword);
                }
            }
            return user;

        } catch (SQLException e) {
            System.out.println("Неверный логин или пароль");
            throw new RuntimeException(e);
        }
    }

    //метод для регистрации пользователя
    public String registerUser(String name, String email, String plainPassword) {
        String salt = BCrypt.gensalt();
        String hashedPassword = BCrypt.hashpw(plainPassword, salt);
        request = "INSERT INTO user (name, email, password) VALUES (?, ?, ?)";

        try (PreparedStatement prSt = dbConnection.prepareStatement(request)) {
            prSt.setString(1, name);
            prSt.setString(2, email);
            prSt.setString(3, hashedPassword);

            prSt.executeUpdate();
            answerdb = "1";
            System.out.println(answerdb);
        } catch (SQLException e) {
            answerdb = "0";
            throw new RuntimeException(e);
        }
        return answerdb;
    }


    public List<Book> showBooks() {
        List<Book> books = new ArrayList<>();
        String request = "SELECT * FROM book";

        try (PreparedStatement prSt = dbConnection.prepareStatement(request)) {
            ResultSet resultSet = prSt.executeQuery();

            while (resultSet.next()) {
                int article = resultSet.getInt("article");
                String releaseDate = resultSet.getString("release_date");
                String title = resultSet.getString("title");
                String publication = resultSet.getString("publication");
                String author = resultSet.getString("author");
                String genre = resultSet.getString("genre");
                boolean isAvailable = resultSet.getBoolean("available");
                String description = resultSet.getString("description");

                Book book = new Book(article, releaseDate, title, publication, author, genre, isAvailable, description);
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return books;
    }

    public List<Loan> showLoans() {
        List<Loan> loans = new ArrayList<>();
        String request = "SELECT loans.loan_id, loans.user_id, book.title, loans.loan_date, loans.return_date FROM loans JOIN book ON book.article = loans.book_id";

        try (PreparedStatement prSt = dbConnection.prepareStatement(request)) {
            ResultSet resultSet = prSt.executeQuery();

            while (resultSet.next()) {
                int loanId = resultSet.getInt("loan_id");
                int userId = resultSet.getInt("user_id");
                String bookTitle = resultSet.getString("title");
                String loanDate = resultSet.getString("loan_date");
                String returnDate = resultSet.getString("return_date");

                Loan loan = new Loan(loanId, userId, bookTitle, loanDate, returnDate);
                loans.add(loan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    public String TakeABook(String userId, String bookId, String loanDate, String returnDate) {
        String requestInsert = "INSERT INTO loans (user_id, book_id, loan_date, return_date) VALUES (?, ?, ?, ?)";
        String requestUpdate = "UPDATE book SET isAvailable = FALSE WHERE article = ?";

        try (PreparedStatement prStInsert = dbConnection.prepareStatement(requestInsert);
             PreparedStatement prStUpdate = dbConnection.prepareStatement(requestUpdate)) {

            // Вставка новой записи о займе книги
            prStInsert.setInt(1, Integer.parseInt(userId));
            prStInsert.setInt(2, Integer.parseInt(bookId));
            prStInsert.setString(3, loanDate);
            prStInsert.setString(4, returnDate);
            prStInsert.executeUpdate();

            // Установка isAvailable в false для книги, которая была взята в займ
            prStUpdate.setInt(1, Integer.parseInt(bookId));
            prStUpdate.executeUpdate();

            answerdb = "1";
            System.out.println(answerdb);

        } catch (SQLException e) {
            answerdb = "0";
            System.err.println(e);
            throw new RuntimeException(e);
        }
        return answerdb;
    }

    public Book showBook(String bookId) {
        Book book = null;
        String request = "SELECT * FROM book where article = ?";

        try (PreparedStatement prSt = dbConnection.prepareStatement(request)) {
            prSt.setInt(1, Integer.parseInt(bookId));
            ResultSet resultSet = prSt.executeQuery();

            while (resultSet.next()) {
                int article = resultSet.getInt("article");
                String releaseDate = resultSet.getString("release_date");
                String title = resultSet.getString("title");
                String publication = resultSet.getString("publication");
                String author = resultSet.getString("author");
                String genre = resultSet.getString("genre");
                boolean isAvailable = resultSet.getBoolean("available");
                String description = resultSet.getString("description");

                book = new Book(article, releaseDate, title, publication, author, genre, isAvailable, description);

            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return book;
    }
}

