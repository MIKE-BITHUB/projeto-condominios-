package br.edu.catolica.ucb.poo.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "unidade", uniqueConstraints = @UniqueConstraint(columnNames = {"bloco", "numero"}))
public class Unidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bloco;

    @Column(nullable = false)
    private String numero;

    @Column(name = "valor_taxa", nullable = false)
    private double valorTaxa;

    protected Unidade() {
    }

    public Unidade(String bloco, String numero, double valorTaxa) {
        this.bloco = bloco;
        this.numero = numero;
        this.valorTaxa = valorTaxa;
    }

    public String getIdentificacao() {
        return "Bloco " + bloco + " - Apto " + numero;
    }

    public Long getId() {
        return id;
    }

    public String getBloco() {
        return bloco;
    }

    public void setBloco(String bloco) {
        this.bloco = bloco;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public double getValorTaxa() {
        return valorTaxa;
    }

    public void setValorTaxa(double valorTaxa) {
        this.valorTaxa = valorTaxa;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s - taxa R$ %.2f", id, getIdentificacao(), valorTaxa);
    }
}
