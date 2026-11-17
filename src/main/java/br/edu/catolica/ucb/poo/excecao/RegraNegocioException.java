package br.edu.catolica.ucb.poo.excecao;

/**
 * Exceção verificada (checked) para violações das regras do condomínio.
 * (Tópico: Exceções)
 */
public class RegraNegocioException extends Exception {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
