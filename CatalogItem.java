package br.com.biblioteca.model.abstractions;

/**
 * Classe abstrata representando item do catálogo.
 */
public abstract class CatalogItem {
    protected int id;
    protected String title;
    protected int year;

    public CatalogItem() {}

    public CatalogItem(int id, String title, int year) {
        this.id = id; this.title = title; this.year = year;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
}
