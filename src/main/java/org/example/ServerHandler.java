package org.example;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


import com.google.gson.Gson;
import org.snf4j.core.handler.AbstractStreamHandler;
import org.snf4j.core.handler.SessionEvent;

public class ServerHandler extends AbstractStreamHandler {
    Gson gson = new Gson();
    Database database = new Database();
    Answer AnswerSer;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void read(Object msg) {
        //получение запроса
        String jsonStringRequest = new String((byte[]) msg);
        System.out.println("Пришедший запрос: " + jsonStringRequest);

        try {
            Request request = gson.fromJson(jsonStringRequest, Request.class);

            System.out.println("Request: " + request.getRequest());

            Map<String, String> attributes = request.getMapAttributes();
            if (attributes != null && !attributes.isEmpty()) {
                for (String attribut : attributes.keySet()) {
                    System.out.println("Ключ " + attribut + " - Значение " + attributes.get(attribut));
                }
            }
            switch (request.getRequest()) {
                case "RegisterRequest" -> RegUser(request);
                case "LoginRequest" -> LogUser(request);
                case "ShowBooks" -> ShowBooks(request);
                case "ShowLoans" -> ShowLoans(request);
                case "TakeABook" -> TakeABook(request);
                case "ShowBook" -> ShowABook(request);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // событие (добавление, удаление клиента)
    @SuppressWarnings("incomplete-switch")
    @Override
    public void event(SessionEvent event) {
        switch (event) {
            case CREATED -> {
                System.out.println(getSession().getId() + "{created}" + getSession());
            }
            case OPENED -> {
                System.out.println(getSession().getId() + "{connected}" + getSession());
            }
            case CLOSED -> {
                System.out.println(getSession().getId() + "{disconnected}" + getSession());
            }
        }
    }

    private void RegUser(Request request) {
        String answerdb = database.registerUser(request.getMapAttributes().get("name"), request.getMapAttributes().get("email"), request.getMapAttributes().get("password"));
        AnswerSer = new Answer(Map.of("answer", answerdb));

        answer(AnswerSer);
    }

    private void TakeABook(Request request) {
        String answerdb = database.TakeABook(request.getMapAttributes().get("user"), request.getMapAttributes().get("book"), request.getMapAttributes().get("dateNow"), request.getMapAttributes().get("dateReturn"));
        AnswerSer = new Answer(Map.of("answer", answerdb));

        answer(AnswerSer);
    }


    private void LogUser(Request request) {
        String email = request.getMapAttributes().get("email");
        String password = request.getMapAttributes().get("password");

        System.out.println("Received login request for email: " + email);

        // Проверка, не является ли email или password null
        if (email != null && password != null) {
            User answerdb = database.loginUser(email, password);

            // Проверка, не является ли answerdb null
            if (answerdb != null) {
                userReturn(answerdb);
            } else {
                System.out.println("Error: loginUser returned null");
            }
        } else {
            System.out.println("Error: Email or password is null");
        }
    }

    private void ShowBooks(Request request) {
        List<Book> answerdb = database.showBooks();
        // Проверка, не является ли answerdb null
        if (answerdb != null) {
            System.out.println("Server response: " + answerdb);
            booksReturn(answerdb);
        }
    }

    private void ShowLoans(Request request) {
        List<Loan> answerdb = database.showLoans();
        // Проверка, не является ли answerdb null
        if (answerdb != null) {
            System.out.println("Server response: " + answerdb);
            loanReturn(answerdb);
        }
    }

    // Отправка ответа
    private void answer(Answer answer) {
        String jsonStringAnswer = gson.toJson(answer);
        getSession().write(("%s\n".formatted(jsonStringAnswer)).getBytes(StandardCharsets.UTF_8));
    }

    private void booksReturn(List<Book> books) {
        BooksListWrapper booksListWrapper = new BooksListWrapper(books);
        String jsonStringAnswer = gson.toJson(booksListWrapper);
        getSession().write(("%s\n".formatted(jsonStringAnswer)).getBytes(StandardCharsets.UTF_8));
    }

    private void bookReturn(Book book) {
        String jsonStringAnswer = gson.toJson(book);
        getSession().write(("%s\n".formatted(jsonStringAnswer)).getBytes(StandardCharsets.UTF_8));
    }

    private void userReturn(User user) {
        String jsonStringAnswer = gson.toJson(user);
        getSession().write(("%s\n".formatted(jsonStringAnswer)).getBytes(StandardCharsets.UTF_8));
    }

    private void loanReturn(List<Loan> loans) {
        LoansListWrapper loansListWrapper = new LoansListWrapper(loans);
        String jsonStringAnswer = gson.toJson(loansListWrapper);
        getSession().write(("%s\n".formatted(jsonStringAnswer)).getBytes(StandardCharsets.UTF_8));
    }

    private void ShowABook(Request request) {
        String id = request.getMapAttributes().get("id");
        Book answerdb = database.showBook(id);
        bookReturn(answerdb);
    }
}