package br.edu.catolica.ucb.poo.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "morador")
public class Morador extends Pessoa implements Exportavel {

    @ManyToOne(optional = false)
    @JoinColumn(name = "unidade_id")
    private Unidade unidade;

    private boolean proprietario;

    protected Morador() {
    }

    public Morador(String nome, String cpf, String email, Unidade unidade, boolean proprietario) {
        super(nome, cpf, email);
        this.unidade = unidade;
        this.proprietario = proprietario;
    }

    @Override
    public String getPerfil() {
        return proprietario ? "Proprietário" : "Inquilino";
    }

    // Sobrescrita: refina o comportamento herdado de Pessoa
    @Override
    public String descricao() {
        return super.descricao() + " - " + unidade.getIdentificacao();
    }

    @Override
    public String paraCsv() {
        return getId() + ";" + getNome() + ";" + getCpf() + ";" + getEmail() + ";"
                + unidade.getIdentificacao() + ";" + getPerfil();
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public void setUnidade(Unidade unidade) {
        this.unidade = unidade;
    }

    public boolean isProprietario() {
        return proprietario;
    }

    public void setProprietario(boolean proprietario) {
        this.proprietario = proprietario;
    }
}
