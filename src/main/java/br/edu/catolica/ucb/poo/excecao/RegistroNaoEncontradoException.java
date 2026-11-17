package br.edu.catolica.ucb.poo.excecao;

public class RegistroNaoEncontradoException extends RegraNegocioException {

    public RegistroNaoEncontradoException(String entidade, Long id) {
        super(entidade + " com ID " + id + " não encontrado(a).");
    }
}
