package br.com.biblioteca.dao;

import br.com.biblioteca.model.entities.Loan;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO {

    public Loan createLoan(Loan loan) throws SQLException {
        String sql = "INSERT INTO loans(book_id, member_id, loan_date, due_date) VALUES(?,?,?,?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, loan.getBookId());
            ps.setInt(2, loan.getMemberId());
            ps.setString(3, loan.getLoanDate().toString());
            ps.setString(4, loan.getDueDate().toString());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) loan.setId(rs.getInt(1));
            }
        }
        return loan;
    }

    public void markReturn(int loanId, LocalDate returnDate) throws SQLException {
        String sql = "UPDATE loans SET return_date = ? WHERE id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, returnDate.toString());
            ps.setInt(2, loanId);
            ps.executeUpdate();
        }
    }

    public List<Loan> findAll() throws SQLException {
        List<Loan> list = new ArrayList<>();
        String sql = "SELECT * FROM loans";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Loan l = new Loan();
                l.setId(rs.getInt("id"));
                l.setBookId(rs.getInt("book_id"));
                l.setMemberId(rs.getInt("member_id"));
                l.setLoanDate(LocalDate.parse(rs.getString("loan_date")));
                l.setDueDate(LocalDate.parse(rs.getString("due_date")));
                String r = rs.getString("return_date");
                if (r != null) l.setReturnDate(LocalDate.parse(r));
                list.add(l);
            }
        }
        return list;
    }

    public Loan findActiveLoanByBookId(int bookId) throws SQLException {
        String sql = "SELECT * FROM loans WHERE book_id = ? AND return_date IS NULL";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Loan l = new Loan();
                    l.setId(rs.getInt("id"));
                    l.setBookId(rs.getInt("book_id"));
                    l.setMemberId(rs.getInt("member_id"));
                    l.setLoanDate(LocalDate.parse(rs.getString("loan_date")));
                    l.setDueDate(LocalDate.parse(rs.getString("due_date")));
                    return l;
                }
            }
        }
        return null;
    }
}
