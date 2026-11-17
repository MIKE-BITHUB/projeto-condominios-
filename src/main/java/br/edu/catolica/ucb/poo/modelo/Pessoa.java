package br.edu.catolica.ucb.poo.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * Classe abstrata que reúne o que é comum a todas as pessoas do condomínio.
 * (Tópicos: Classe e abstração, Herança, Encapsulamento, Sobrecarga)
 */
@MappedSuperclass
public abstract class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cpf;

    private String email;

    protected Pessoa() {
        // exigido pelo JPA
    }

    protected Pessoa(String nome, String cpf, String email) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
    }

    // Método abstrato: cada tipo de pessoa define o seu perfil
    public abstract String getPerfil();

    // Sobrecarga: mesmo nome, parâmetros diferentes
    public String descricao() {
        return nome + " (" + getPerfil() + ")";
    }

    public String descricao(boolean completa) {
        if (completa) {
            return descricao() + " - CPF " + cpf + " - " + email;
        }
        return descricao();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + descricao();
    }
}
