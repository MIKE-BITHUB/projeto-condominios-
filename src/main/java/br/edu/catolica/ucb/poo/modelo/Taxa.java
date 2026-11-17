package br.edu.catolica.ucb.poo.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "taxa")
public class Taxa implements Exportavel {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "unidade_id")
    private Unidade unidade;

    @Column(nullable = false)
    private String competencia; // formato MM/yyyy

    @Column(nullable = false)
    private double valor;

    @Column(nullable = false)
    private LocalDate vencimento;

    private boolean pago;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    protected Taxa() {
    }

    public Taxa(Unidade unidade, String competencia, double valor, LocalDate vencimento) {
        this.unidade = unidade;
        this.competencia = competencia;
        this.valor = valor;
        this.vencimento = vencimento;
    }

    public void pagar(LocalDate data) {
        this.pago = true;
        this.dataPagamento = data;
    }

    public boolean isVencida() {
        return !pago && vencimento.isBefore(LocalDate.now());
    }

    public String getSituacao() {
        if (pago) {
            return "PAGA";
        }
        return isVencida() ? "VENCIDA" : "EM ABERTO";
    }

    @Override
    public String paraCsv() {
        return id + ";" + unidade.getIdentificacao() + ";" + competencia + ";"
                + String.format("%.2f", valor) + ";" + vencimento.format(FORMATO) + ";" + getSituacao();
    }

    public Long getId() {
        return id;
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public String getCompetencia() {
        return competencia;
    }

    public double getValor() {
        return valor;
    }

    public LocalDate getVencimento() {
        return vencimento;
    }

    public boolean isPago() {
        return pago;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s | %s | R$ %.2f | vence %s | %s",
                id, unidade.getIdentificacao(), competencia, valor, vencimento.format(FORMATO), getSituacao());
    }
}
