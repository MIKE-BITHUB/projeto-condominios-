package br.edu.catolica.ucb.poo.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "funcionario")
public class Funcionario extends Pessoa implements Exportavel {

    private String cargo;

    protected Funcionario() {
    }

    public Funcionario(String nome, String cpf, String email, String cargo) {
        super(nome, cpf, email);
        this.cargo = cargo;
    }

    @Override
    public String getPerfil() {
        return "Funcionário";
    }

    @Override
    public String descricao() {
        return super.descricao() + " - " + cargo;
    }

    @Override
    public String paraCsv() {
        return getId() + ";" + getNome() + ";" + getCpf() + ";" + getEmail() + ";" + cargo;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
}
