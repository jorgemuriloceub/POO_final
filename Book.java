package br.com.biblioteca.model.entities;

import br.com.biblioteca.model.abstractions.CatalogItem;
import br.com.biblioteca.model.enums.Genre;
import br.com.biblioteca.model.interfaces.Lendable;

public class Book extends CatalogItem implements Lendable {
    private String author;
    private Genre genre;
    private Status status;

    public enum Status { AVAILABLE, LOANED }

    public Book() {}

    public Book(int id, String title, String author, int year, Genre genre) {
        super(id, title, year);
        this.author = author;
        this.genre = genre;
        this.status = Status.AVAILABLE;
    }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public Genre getGenre() { return genre; }
    public void setGenre(Genre genre) { this.genre = genre; }

    private void setStatus(Status status) { this.status = status; }
    public Status getStatus() { return status; }

    @Override
    public boolean lend() {
        if (status == Status.AVAILABLE) {
            setStatus(Status.LOANED); return true;
        }
        return false;
    }

    @Override
    public boolean returnItem() {
        if (status == Status.LOANED) {
            setStatus(Status.AVAILABLE); return true;
        }
        return false;
    }

    @Override
    public boolean isAvailable() { return status == Status.AVAILABLE; }

    @Override
    public String toString() {
        return String.format("%s — %s (%d) [%s] - %s",
                getTitle(), author, getYear(), genre, status);
    }
}
