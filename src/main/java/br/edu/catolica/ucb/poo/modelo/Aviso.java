package br.edu.catolica.ucb.poo.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "aviso")
public class Aviso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String texto;

    @Column(name = "data_publicacao", nullable = false)
    private LocalDate dataPublicacao;

    private String autor;

    protected Aviso() {
    }

    public Aviso(String titulo, String texto, String autor) {
        this.titulo = titulo;
        this.texto = texto;
        this.autor = autor;
        this.dataPublicacao = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getTexto() {
        return texto;
    }

    public LocalDate getDataPublicacao() {
        return dataPublicacao;
    }

    public String getAutor() {
        return autor;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + dataPublicacao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                + " - " + titulo + " (por " + autor + ")\n      " + texto;
    }
}
