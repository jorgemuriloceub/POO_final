package br.com.biblioteca.dao;

import br.com.biblioteca.model.entities.Book;
import br.com.biblioteca.model.enums.Genre;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public Book save(Book book) throws SQLException {
        String sql = "INSERT INTO books(title, author, year, genre, status) VALUES(?,?,?,?,?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setInt(3, book.getYear());
            ps.setString(4, book.getGenre().name());
            ps.setString(5, book.isAvailable() ? "AVAILABLE" : "LOANED");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) book.setId(rs.getInt(1));
            }
        }
        return book;
    }

    public Book findById(int id) throws SQLException {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Book b = new Book();
                    b.setId(rs.getInt("id"));
                    b.setTitle(rs.getString("title"));
                    b.setAuthor(rs.getString("author"));
                    b.setYear(rs.getInt("year"));
                    b.setGenre(Genre.valueOf(rs.getString("genre")));
                    if ("LOANED".equals(rs.getString("status"))) b.lend();
                    return b;
                }
            }
        }
        return null;
    }

    public List<Book> findAll() throws SQLException {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Book b = new Book();
                b.setId(rs.getInt("id"));
                b.setTitle(rs.getString("title"));
                b.setAuthor(rs.getString("author"));
                b.setYear(rs.getInt("year"));
                b.setGenre(Genre.valueOf(rs.getString("genre")));
                if ("LOANED".equals(rs.getString("status"))) b.lend();
                list.add(b);
            }
        }
        return list;
    }

    public void updateStatus(int bookId, boolean available) throws SQLException {
        String sql = "UPDATE books SET status = ? WHERE id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, available ? "AVAILABLE" : "LOANED");
            ps.setInt(2, bookId);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
