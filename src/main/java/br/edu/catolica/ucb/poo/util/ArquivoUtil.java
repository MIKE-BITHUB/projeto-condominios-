package br.edu.catolica.ucb.poo.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Entrada e saída de arquivos (I/O): exportação de CSV e registro de log.
 * (Tópico: I/O)
 */
public final class ArquivoUtil {

    private static final Path PASTA_EXPORTACAO = Paths.get("exportacoes");
    private static final Path ARQUIVO_LOG = Paths.get("condominio.log");

    private ArquivoUtil() {
    }

    /** Grava as linhas em exportacoes/<nome> e devolve o caminho do arquivo. */
    public static Path salvar(String nomeArquivo, List<String> linhas) throws IOException {
        Files.createDirectories(PASTA_EXPORTACAO);
        Path destino = PASTA_EXPORTACAO.resolve(nomeArquivo);
        Files.write(destino, linhas, StandardCharsets.UTF_8);
        return destino;
    }

    /** Acrescenta uma linha com data e hora ao arquivo de log. */
    public static void registrarLog(String mensagem) {
        String hora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        try (BufferedWriter escritor = Files.newBufferedWriter(ARQUIVO_LOG, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            escritor.write(hora + " - " + mensagem);
            escritor.newLine();
        } catch (IOException e) {
            System.out.println("Não foi possível gravar o log: " + e.getMessage());
        }
    }

    /** Lê o arquivo de log (lista vazia se ele ainda não existir). */
    public static List<String> lerLog() throws IOException {
        if (!Files.exists(ARQUIVO_LOG)) {
            return new ArrayList<>();
        }
        return Files.readAllLines(ARQUIVO_LOG, StandardCharsets.UTF_8);
    }
}
