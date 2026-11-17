package br.edu.catolica.ucb.poo.excecao;

public class ConflitoReservaException extends RegraNegocioException {

    public ConflitoReservaException(String area, String data) {
        super("A área \"" + area + "\" já está reservada em " + data + ".");
    }
}
