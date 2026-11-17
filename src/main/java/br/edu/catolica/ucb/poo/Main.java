package br.edu.catolica.ucb.poo;

import br.edu.catolica.ucb.poo.excecao.RegraNegocioException;
import br.edu.catolica.ucb.poo.modelo.Pessoa;
import br.edu.catolica.ucb.poo.modelo.Taxa;
import br.edu.catolica.ucb.poo.persistencia.JpaUtil;
import br.edu.catolica.ucb.poo.servico.CondominioService;
import br.edu.catolica.ucb.poo.util.ArquivoUtil;
import br.edu.catolica.ucb.poo.util.Entrada;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Sistema de Gestão de Condomínio (menu no terminal).
 */
public class Main {

    private static final CondominioService servico = new CondominioService();

    public static void main(String[] args) {
        int opcao;
        do {
            exibirMenuPrincipal();
            opcao = Entrada.inteiro("Opção: ");
            try {
                switch (opcao) {
                    case 1 -> menuUnidades();
                    case 2 -> menuMoradores();
                    case 3 -> menuFuncionarios();
                    case 4 -> menuTaxas();
                    case 5 -> menuReservas();
                    case 6 -> menuAvisos();
                    case 7 -> menuArquivos();
                    case 8 -> listarPessoas();
                    case 9 -> {
                        servico.popularDadosExemplo();
                        System.out.println("Dados de exemplo cadastrados com sucesso!");
                    }
                    case 0 -> System.out.println("Encerrando o sistema. Até logo!");
                    default -> System.out.println("Opção inválida.");
                }
            } catch (RegraNegocioException e) {
                System.out.println("Atenção: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Erro ao ler/gravar arquivo: " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("Erro inesperado: " + e.getMessage());
                ArquivoUtil.registrarLog("ERRO: " + e);
            }
        } while (opcao != 0);
        JpaUtil.fechar();
    }

    private static void exibirMenuPrincipal() {
        System.out.println();
        System.out.println("===== GESTÃO DE CONDOMÍNIO =====");
        System.out.println("1 - Unidades");
        System.out.println("2 - Moradores");
        System.out.println("3 - Funcionários");
        System.out.println("4 - Taxas de condomínio");
        System.out.println("5 - Áreas comuns e reservas");
        System.out.println("6 - Avisos");
        System.out.println("7 - Arquivos (exportar CSV / ver log)");
        System.out.println("8 - Todas as pessoas (moradores + funcionários)");
        System.out.println("9 - Carregar dados de exemplo");
        System.out.println("0 - Sair");
    }

    // ---------------------------- Unidades ----------------------------

    private static void menuUnidades() throws RegraNegocioException {
        System.out.println("\n--- UNIDADES ---");
        System.out.println("1 - Cadastrar | 2 - Listar | 3 - Blocos e moradores por unidade");
        switch (Entrada.inteiro("Opção: ")) {
            case 1 -> {
                String bloco = Entrada.texto("Bloco: ").toUpperCase();
                String numero = Entrada.texto("Número do apartamento: ");
                double taxa = Entrada.decimal("Valor da taxa mensal (R$): ");
                System.out.println("Cadastrada: " + servico.cadastrarUnidade(bloco, numero, taxa));
            }
            case 2 -> imprimir(servico.listarUnidades());
            case 3 -> {
                System.out.println("Blocos: " + servico.listarBlocos());
                Map<String, Integer> contagem = servico.moradoresPorUnidade();
                contagem.forEach((unidade, total) -> System.out.println(unidade + ": " + total + " morador(es)"));
            }
            default -> System.out.println("Opção inválida.");
        }
    }

    // ---------------------------- Moradores ----------------------------

    private static void menuMoradores() throws RegraNegocioException {
        System.out.println("\n--- MORADORES ---");
        System.out.println("1 - Cadastrar | 2 - Listar");
        switch (Entrada.inteiro("Opção: ")) {
            case 1 -> {
                imprimir(servico.listarUnidades());
                Long unidadeId = Entrada.id("ID da unidade: ");
                String nome = Entrada.texto("Nome: ");
                String cpf = Entrada.texto("CPF (11 dígitos): ");
                String email = Entrada.texto("E-mail: ");
                boolean proprietario = Entrada.simNao("É proprietário?");
                System.out.println("Cadastrado: " + servico.cadastrarMorador(nome, cpf, email, unidadeId, proprietario));
            }
            case 2 -> imprimir(servico.listarMoradores());
            default -> System.out.println("Opção inválida.");
        }
    }

    // ---------------------------- Funcionários ----------------------------

    private static void menuFuncionarios() throws RegraNegocioException {
        System.out.println("\n--- FUNCIONÁRIOS ---");
        System.out.println("1 - Cadastrar | 2 - Listar");
        switch (Entrada.inteiro("Opção: ")) {
            case 1 -> {
                String nome = Entrada.texto("Nome: ");
                String cpf = Entrada.texto("CPF (11 dígitos): ");
                String email = Entrada.texto("E-mail: ");
                String cargo = Entrada.texto("Cargo: ");
                System.out.println("Cadastrado: " + servico.cadastrarFuncionario(nome, cpf, email, cargo));
            }
            case 2 -> imprimir(servico.listarFuncionarios());
            default -> System.out.println("Opção inválida.");
        }
    }

    // ---------------------------- Taxas ----------------------------

    private static void menuTaxas() throws RegraNegocioException {
        System.out.println("\n--- TAXAS ---");
        System.out.println("1 - Gerar taxas do mês | 2 - Listar todas | 3 - Listar em aberto");
        System.out.println("4 - Registrar pagamento | 5 - Inadimplência por unidade");
        switch (Entrada.inteiro("Opção: ")) {
            case 1 -> {
                String competencia = Entrada.texto("Competência (MM/aaaa): ");
                List<Taxa> geradas = servico.gerarTaxasDoMes(competencia);
                System.out.println(geradas.size() + " taxa(s) gerada(s):");
                imprimir(geradas);
            }
            case 2 -> imprimir(servico.listarTaxas());
            case 3 -> imprimir(servico.listarTaxasEmAberto());
            case 4 -> {
                imprimir(servico.listarTaxasEmAberto());
                Long id = Entrada.id("ID da taxa paga: ");
                System.out.println("Pagamento registrado: " + servico.pagarTaxa(id));
            }
            case 5 -> {
                Map<String, Double> inadimplencia = servico.inadimplenciaPorUnidade();
                if (inadimplencia.isEmpty()) {
                    System.out.println("Nenhuma unidade inadimplente.");
                } else {
                    inadimplencia.forEach((unidade, valor) ->
                            System.out.printf("%s deve R$ %.2f%n", unidade, valor));
                }
            }
            default -> System.out.println("Opção inválida.");
        }
    }

    // ---------------------------- Reservas ----------------------------

    private static void menuReservas() throws RegraNegocioException {
        System.out.println("\n--- ÁREAS COMUNS E RESERVAS ---");
        System.out.println("1 - Cadastrar área | 2 - Listar áreas | 3 - Reservar");
        System.out.println("4 - Cancelar reserva | 5 - Listar reservas");
        switch (Entrada.inteiro("Opção: ")) {
            case 1 -> {
                String nome = Entrada.texto("Nome da área: ");
                int capacidade = Entrada.inteiro("Capacidade (pessoas): ");
                System.out.println("Cadastrada: " + servico.cadastrarAreaComum(nome, capacidade));
            }
            case 2 -> imprimir(servico.listarAreas());
            case 3 -> {
                imprimir(servico.listarAreas());
                Long areaId = Entrada.id("ID da área: ");
                imprimir(servico.listarMoradores());
                Long moradorId = Entrada.id("ID do morador: ");
                var data = Entrada.data("Data da reserva (dd/MM/aaaa): ");
                System.out.println("Reserva confirmada: " + servico.reservar(areaId, moradorId, data));
            }
            case 4 -> {
                imprimir(servico.listarReservas());
                Long id = Entrada.id("ID da reserva a cancelar: ");
                System.out.println("Cancelada: " + servico.cancelarReserva(id));
            }
            case 5 -> imprimir(servico.listarReservas());
            default -> System.out.println("Opção inválida.");
        }
    }

    // ---------------------------- Avisos ----------------------------

    private static void menuAvisos() throws RegraNegocioException {
        System.out.println("\n--- AVISOS ---");
        System.out.println("1 - Publicar | 2 - Listar | 3 - Remover");
        switch (Entrada.inteiro("Opção: ")) {
            case 1 -> {
                String titulo = Entrada.texto("Título: ");
                String texto = Entrada.texto("Texto: ");
                String autor = Entrada.texto("Autor (Enter para Síndico): ");
                servico.publicarAviso(titulo, texto, autor);
                System.out.println("Aviso publicado.");
            }
            case 2 -> imprimir(servico.listarAvisos());
            case 3 -> {
                imprimir(servico.listarAvisos());
                servico.removerAviso(Entrada.id("ID do aviso a remover: "));
                System.out.println("Aviso removido.");
            }
            default -> System.out.println("Opção inválida.");
        }
    }

    // ---------------------------- Arquivos ----------------------------

    private static void menuArquivos() throws IOException {
        System.out.println("\n--- ARQUIVOS ---");
        System.out.println("1 - Exportar moradores | 2 - Exportar taxas | 3 - Exportar reservas | 4 - Ver log");
        switch (Entrada.inteiro("Opção: ")) {
            case 1 -> System.out.println("Arquivo gerado: " + servico.exportarMoradores());
            case 2 -> System.out.println("Arquivo gerado: " + servico.exportarTaxas());
            case 3 -> System.out.println("Arquivo gerado: " + servico.exportarReservas());
            case 4 -> imprimir(ArquivoUtil.lerLog());
            default -> System.out.println("Opção inválida.");
        }
    }

    // ---------------------------- Polimorfismo ----------------------------

    private static void listarPessoas() {
        System.out.println("\n--- PESSOAS DO CONDOMÍNIO ---");
        for (Pessoa pessoa : servico.listarPessoas()) {
            // chamada polimórfica: cada subclasse monta a descrição do seu jeito
            System.out.println(pessoa.descricao(true));
        }
        System.out.println("Total de pessoas únicas (por CPF): " + servico.contarPessoasUnicas());
    }

    // ---------------------------- Auxiliar ----------------------------

    private static void imprimir(List<?> itens) {
        if (itens.isEmpty()) {
            System.out.println("Nenhum registro encontrado.");
            return;
        }
        itens.forEach(System.out::println);
    }
}
