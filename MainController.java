package br.com.biblioteca.ui.controllers;

import br.com.biblioteca.dao.BookDAO;
import br.com.biblioteca.dao.LoanDAO;
import br.com.biblioteca.dao.MemberDAO;
import br.com.biblioteca.model.entities.Book;
import br.com.biblioteca.model.entities.Loan;
import br.com.biblioteca.model.entities.Member;
import br.com.biblioteca.model.enums.Genre;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class MainController {

    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private ComboBox<Genre> genreBox;
    @FXML private TextField memberNameField;
    @FXML private TextField memberEmailField;

    @FXML private ListView<Book> bookListView;
    @FXML private ListView<Member> memberListView;
    @FXML private ListView<Loan> loanListView;

    private final BookDAO bookDAO = new BookDAO();
    private final MemberDAO memberDAO = new MemberDAO();
    private final LoanDAO loanDAO = new LoanDAO();

    @FXML
    public void initialize() {
        genreBox.getItems().setAll(Genre.values());
        genreBox.getSelectionModel().select(Genre.FICTION);
        refreshAll();
    }

    @FXML
    public void onAddBook() {
        String t = titleField.getText(), a = authorField.getText();
        Genre g = genreBox.getValue();
        if (t == null || t.isBlank() || a == null || a.isBlank()) {
            showAlert("Preencha título e autor.");
            return;
        }
        Book b = new Book(0, t, a, LocalDate.now().getYear(), g);
        try {
            bookDAO.save(b);
            titleField.clear(); authorField.clear();
            refreshBooks();
        } catch (SQLException e) { showError(e); }
    }

    @FXML
    public void onAddMember() {
        String name = memberNameField.getText(), email = memberEmailField.getText();
        if (name == null || name.isBlank()) { showAlert("Preencha o nome do membro."); return; }
        Member m = new Member(0, name, email, null);
        try {
            memberDAO.save(m);
            memberNameField.clear(); memberEmailField.clear();
            refreshMembers();
        } catch (SQLException e) { showError(e); }
    }

    @FXML
    public void onLendBook() {
        Book selected = bookListView.getSelectionModel().getSelectedItem();
        Member member = memberListView.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Selecione um livro."); return; }
        if (member == null) { showAlert("Selecione um membro."); return; }
        if (!selected.isAvailable()) { showAlert("Livro já emprestado."); return; }
        try {
            // criar loan
            Loan loan = new Loan();
            loan.setBookId(selected.getId());
            loan.setMemberId(member.getId());
            loan.setLoanDate(LocalDate.now());
            loan.setDueDate(LocalDate.now().plusWeeks(2));
            loanDAO.createLoan(loan);
            bookDAO.updateStatus(selected.getId(), false);
            refreshAll();
        } catch (SQLException e) { showError(e); }
    }

    @FXML
    public void onReturnBook() {
        Book selected = bookListView.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Selecione um livro."); return; }
        try {
            Loan active = loanDAO.findActiveLoanByBookId(selected.getId());
            if (active == null) { showAlert("Este livro não possui empréstimo ativo."); return; }
            loanDAO.markReturn(active.getId(), LocalDate.now());
            bookDAO.updateStatus(selected.getId(), true);
            refreshAll();
        } catch (SQLException e) { showError(e); }
    }

    @FXML
    public void onRefresh() { refreshAll(); }

    private void refreshAll() {
        refreshBooks(); refreshMembers(); refreshLoans();
    }

    private void refreshBooks() {
        Platform.runLater(() -> {
            try {
                List<Book> books = bookDAO.findAll();
                bookListView.getItems().setAll(books);
            } catch (SQLException e) { showError(e); }
        });
    }

    private void refreshMembers() {
        Platform.runLater(() -> {
            try {
                List<Member> ms = memberDAO.findAll();
                memberListView.getItems().setAll(ms);
            } catch (SQLException e) { showError(e); }
        });
    }

    private void refreshLoans() {
        Platform.runLater(() -> {
            try {
                List<Loan> loans = loanDAO.findAll();
                loanListView.getItems().setAll(loans);
            } catch (SQLException e) { showError(e); }
        });
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.showAndWait();
    }

    private void showError(Exception e) {
        e.printStackTrace();
        Alert a = new Alert(Alert.AlertType.ERROR, e.getMessage() == null ? e.toString() : e.getMessage(), ButtonType.OK);
        a.showAndWait();
    }
}
