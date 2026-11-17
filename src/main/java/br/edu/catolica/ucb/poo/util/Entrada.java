package br.edu.catolica.ucb.poo.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Leitura segura do teclado: repete a pergunta até o usuário digitar algo válido.
 */
public final class Entrada {

    private static final Scanner TECLADO = new Scanner(System.in);
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Entrada() {
    }

    public static String texto(String pergunta) {
        System.out.print(pergunta);
        return TECLADO.nextLine().trim();
    }

    public static int inteiro(String pergunta) {
        while (true) {
            try {
                return Integer.parseInt(texto(pergunta));
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro válido.");
            }
        }
    }

    public static Long id(String pergunta) {
        return (long) inteiro(pergunta);
    }

    public static double decimal(String pergunta) {
        while (true) {
            try {
                return Double.parseDouble(texto(pergunta).replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Digite um valor numérico válido (ex.: 650,00).");
            }
        }
    }

    public static LocalDate data(String pergunta) {
        while (true) {
            try {
                return LocalDate.parse(texto(pergunta), FORMATO_DATA);
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida. Use o formato dd/MM/aaaa.");
            }
        }
    }

    public static boolean simNao(String pergunta) {
        while (true) {
            String resposta = texto(pergunta + " (s/n): ").toLowerCase();
            if (resposta.equals("s")) {
                return true;
            }
            if (resposta.equals("n")) {
                return false;
            }
            System.out.println("Responda com s ou n.");
        }
    }
}
