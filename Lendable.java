package br.com.biblioteca.model.interfaces;

public interface Lendable {
    boolean lend();
    boolean returnItem();
    boolean isAvailable();
}
