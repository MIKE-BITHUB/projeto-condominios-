package br.edu.catolica.ucb.poo.servico;

import br.edu.catolica.ucb.poo.excecao.ConflitoReservaException;
import br.edu.catolica.ucb.poo.excecao.RegistroNaoEncontradoException;
import br.edu.catolica.ucb.poo.excecao.RegraNegocioException;
import br.edu.catolica.ucb.poo.modelo.AreaComum;
import br.edu.catolica.ucb.poo.modelo.Aviso;
import br.edu.catolica.ucb.poo.modelo.Exportavel;
import br.edu.catolica.ucb.poo.modelo.Funcionario;
import br.edu.catolica.ucb.poo.modelo.Morador;
import br.edu.catolica.ucb.poo.modelo.Pessoa;
import br.edu.catolica.ucb.poo.modelo.Reserva;
import br.edu.catolica.ucb.poo.modelo.StatusReserva;
import br.edu.catolica.ucb.poo.modelo.Taxa;
import br.edu.catolica.ucb.poo.modelo.Unidade;
import br.edu.catolica.ucb.poo.persistencia.Repositorio;
import br.edu.catolica.ucb.poo.util.ArquivoUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Regras de negócio do condomínio. Os menus (Main) só chamam esta classe.
 */
public class CondominioService {

    private static final DateTimeFormatter FORMATO_COMPETENCIA = DateTimeFormatter.ofPattern("MM/yyyy");
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Repositorio<Unidade> unidades = new Repositorio<>(Unidade.class);
    private final Repositorio<Morador> moradores = new Repositorio<>(Morador.class);
    private final Repositorio<Funcionario> funcionarios = new Repositorio<>(Funcionario.class);
    private final Repositorio<Taxa> taxas = new Repositorio<>(Taxa.class);
    private final Repositorio<AreaComum> areas = new Repositorio<>(AreaComum.class);
    private final Repositorio<Reserva> reservas = new Repositorio<>(Reserva.class);
    private final Repositorio<Aviso> avisos = new Repositorio<>(Aviso.class);

    // ------------------------------------------------------------------
    // Cadastros
    // ------------------------------------------------------------------

    public Unidade cadastrarUnidade(String bloco, String numero, double valorTaxa) throws RegraNegocioException {
        if (bloco.isBlank() || numero.isBlank()) {
            throw new RegraNegocioException("Bloco e número são obrigatórios.");
        }
        if (valorTaxa <= 0) {
            throw new RegraNegocioException("O valor da taxa deve ser maior que zero.");
        }
        List<Unidade> existentes = unidades.consultar(
                "select u from Unidade u where u.bloco = ?1 and u.numero = ?2", Unidade.class, bloco, numero);
        if (!existentes.isEmpty()) {
            throw new RegraNegocioException("Já existe a unidade Bloco " + bloco + " - Apto " + numero + ".");
        }
        Unidade unidade = unidades.salvar(new Unidade(bloco, numero, valorTaxa));
        ArquivoUtil.registrarLog("Unidade cadastrada: " + unidade.getIdentificacao());
        return unidade;
    }

    public Morador cadastrarMorador(String nome, String cpf, String email, Long unidadeId, boolean proprietario)
            throws RegraNegocioException {
        validarPessoa(nome, cpf);
        Unidade unidade = buscar(unidades, unidadeId, "Unidade");
        Morador morador = moradores.salvar(new Morador(nome, cpf, email, unidade, proprietario));
        ArquivoUtil.registrarLog("Morador cadastrado: " + morador.getNome());
        return morador;
    }

    public Funcionario cadastrarFuncionario(String nome, String cpf, String email, String cargo)
            throws RegraNegocioException {
        validarPessoa(nome, cpf);
        if (cargo.isBlank()) {
            throw new RegraNegocioException("O cargo é obrigatório.");
        }
        Funcionario funcionario = funcionarios.salvar(new Funcionario(nome, cpf, email, cargo));
        ArquivoUtil.registrarLog("Funcionário cadastrado: " + funcionario.getNome());
        return funcionario;
    }

    public AreaComum cadastrarAreaComum(String nome, int capacidade) throws RegraNegocioException {
        if (nome.isBlank() || capacidade <= 0) {
            throw new RegraNegocioException("Informe um nome e uma capacidade maior que zero.");
        }
        AreaComum area = areas.salvar(new AreaComum(nome, capacidade));
        ArquivoUtil.registrarLog("Área comum cadastrada: " + area.getNome());
        return area;
    }

    private void validarPessoa(String nome, String cpf) throws RegraNegocioException {
        if (nome.isBlank()) {
            throw new RegraNegocioException("O nome é obrigatório.");
        }
        String somenteNumeros = cpf.replaceAll("\\D", "");
        if (somenteNumeros.length() != 11) {
            throw new RegraNegocioException("O CPF deve ter 11 dígitos.");
        }
        boolean cpfJaUsado = !moradores.consultar("select m from Morador m where m.cpf = ?1", Morador.class, cpf).isEmpty()
                || !funcionarios.consultar("select f from Funcionario f where f.cpf = ?1", Funcionario.class, cpf).isEmpty();
        if (cpfJaUsado) {
            throw new RegraNegocioException("Já existe uma pessoa cadastrada com esse CPF.");
        }
    }

    // ------------------------------------------------------------------
    // Taxas
    // ------------------------------------------------------------------

    // Sobrecarga: usa o valor padrão da unidade
    public Taxa gerarTaxa(Unidade unidade, String competencia) throws RegraNegocioException {
        return gerarTaxa(unidade, competencia, unidade.getValorTaxa());
    }

    // Sobrecarga: valor informado manualmente
    public Taxa gerarTaxa(Unidade unidade, String competencia, double valor) throws RegraNegocioException {
        YearMonth mes = lerCompetencia(competencia);
        if (valor <= 0) {
            throw new RegraNegocioException("O valor da taxa deve ser maior que zero.");
        }
        List<Taxa> existentes = taxas.consultar(
                "select t from Taxa t where t.unidade.id = ?1 and t.competencia = ?2",
                Taxa.class, unidade.getId(), competencia);
        if (!existentes.isEmpty()) {
            throw new RegraNegocioException("A taxa " + competencia + " da " + unidade.getIdentificacao() + " já foi gerada.");
        }
        Taxa taxa = taxas.salvar(new Taxa(unidade, competencia, valor, mes.atDay(10)));
        ArquivoUtil.registrarLog("Taxa gerada: " + unidade.getIdentificacao() + " - " + competencia);
        return taxa;
    }

    /** Gera a taxa do mês para todas as unidades que ainda não a possuem. */
    public List<Taxa> gerarTaxasDoMes(String competencia) throws RegraNegocioException {
        lerCompetencia(competencia);
        List<Taxa> geradas = new ArrayList<>();
        for (Unidade unidade : unidades.listarTodos()) {
            try {
                geradas.add(gerarTaxa(unidade, competencia));
            } catch (RegraNegocioException e) {
                // unidade já tinha a taxa desse mês: apenas ignora e segue para a próxima
            }
        }
        if (geradas.isEmpty()) {
            throw new RegraNegocioException("Nenhuma taxa nova para gerar em " + competencia + ".");
        }
        return geradas;
    }

    public Taxa pagarTaxa(Long id) throws RegraNegocioException {
        Taxa taxa = buscar(taxas, id, "Taxa");
        if (taxa.isPago()) {
            throw new RegraNegocioException("Essa taxa já está paga.");
        }
        taxa.pagar(LocalDate.now());
        Taxa paga = taxas.atualizar(taxa);
        ArquivoUtil.registrarLog("Taxa paga: ID " + id);
        return paga;
    }

    public List<Taxa> listarTaxasEmAberto() {
        return taxas.consultar("select t from Taxa t where t.pago = false order by t.id", Taxa.class);
    }

    /** Soma, por unidade, o valor das taxas vencidas e não pagas. */
    public Map<String, Double> inadimplenciaPorUnidade() {
        Map<String, Double> total = new TreeMap<>();
        for (Taxa taxa : listarTaxasEmAberto()) {
            if (taxa.isVencida()) {
                total.merge(taxa.getUnidade().getIdentificacao(), taxa.getValor(), Double::sum);
            }
        }
        return total;
    }

    private YearMonth lerCompetencia(String competencia) throws RegraNegocioException {
        try {
            return YearMonth.parse(competencia, FORMATO_COMPETENCIA);
        } catch (DateTimeParseException e) {
            throw new RegraNegocioException("Competência inválida. Use o formato MM/aaaa (ex.: 11/2026).");
        }
    }

    // ------------------------------------------------------------------
    // Reservas
    // ------------------------------------------------------------------

    public Reserva reservar(Long areaId, Long moradorId, LocalDate data) throws RegraNegocioException {
        if (data.isBefore(LocalDate.now())) {
            throw new RegraNegocioException("A data da reserva não pode estar no passado.");
        }
        AreaComum area = buscar(areas, areaId, "Área comum");
        Morador morador = buscar(moradores, moradorId, "Morador");

        List<Reserva> daArea = reservas.consultar(
                "select r from Reserva r where r.area.id = ?1", Reserva.class, areaId);
        for (Reserva existente : daArea) {
            if (existente.getStatus() == StatusReserva.CONFIRMADA && existente.getDataReserva().equals(data)) {
                throw new ConflitoReservaException(area.getNome(), data.format(FORMATO_DATA));
            }
        }
        Reserva reserva = reservas.salvar(new Reserva(area, morador, data));
        ArquivoUtil.registrarLog("Reserva criada: " + area.getNome() + " em " + data.format(FORMATO_DATA));
        return reserva;
    }

    public Reserva cancelarReserva(Long id) throws RegraNegocioException {
        Reserva reserva = buscar(reservas, id, "Reserva");
        if (reserva.getStatus() == StatusReserva.CANCELADA) {
            throw new RegraNegocioException("Essa reserva já está cancelada.");
        }
        reserva.cancelar();
        Reserva cancelada = reservas.atualizar(reserva);
        ArquivoUtil.registrarLog("Reserva cancelada: ID " + id);
        return cancelada;
    }

    // ------------------------------------------------------------------
    // Avisos
    // ------------------------------------------------------------------

    public Aviso publicarAviso(String titulo, String texto, String autor) throws RegraNegocioException {
        if (titulo.isBlank() || texto.isBlank()) {
            throw new RegraNegocioException("Título e texto são obrigatórios.");
        }
        Aviso aviso = avisos.salvar(new Aviso(titulo, texto, autor.isBlank() ? "Síndico" : autor));
        ArquivoUtil.registrarLog("Aviso publicado: " + titulo);
        return aviso;
    }

    public void removerAviso(Long id) throws RegraNegocioException {
        if (!avisos.remover(id)) {
            throw new RegistroNaoEncontradoException("Aviso", id);
        }
        ArquivoUtil.registrarLog("Aviso removido: ID " + id);
    }

    // ------------------------------------------------------------------
    // Consultas
    // ------------------------------------------------------------------

    public List<Unidade> listarUnidades() {
        return unidades.listarTodos();
    }

    public List<Morador> listarMoradores() {
        return moradores.listarTodos();
    }

    public List<Funcionario> listarFuncionarios() {
        return funcionarios.listarTodos();
    }

    public List<Taxa> listarTaxas() {
        return taxas.listarTodos();
    }

    public List<AreaComum> listarAreas() {
        return areas.listarTodos();
    }

    public List<Reserva> listarReservas() {
        return reservas.listarTodos();
    }

    public List<Aviso> listarAvisos() {
        return avisos.listarTodos();
    }

    /**
     * Polimorfismo: a lista guarda Pessoa, mas cada elemento é um Morador ou um
     * Funcionario e responde à chamada de descricao() do seu próprio jeito.
     */
    public List<Pessoa> listarPessoas() {
        List<Pessoa> pessoas = new ArrayList<>();
        pessoas.addAll(moradores.listarTodos());
        pessoas.addAll(funcionarios.listarTodos());
        pessoas.sort(Comparator.comparing(Pessoa::getNome));
        return pessoas;
    }

    /** Set: blocos distintos, em ordem alfabética, sem repetição. */
    public Set<String> listarBlocos() {
        Set<String> blocos = new TreeSet<>();
        for (Unidade unidade : unidades.listarTodos()) {
            blocos.add(unidade.getBloco());
        }
        return blocos;
    }

    /** Set de CPFs distintos (HashSet) para conferir quantas pessoas únicas há no condomínio. */
    public int contarPessoasUnicas() {
        Set<String> cpfs = new HashSet<>();
        for (Pessoa pessoa : listarPessoas()) {
            cpfs.add(pessoa.getCpf());
        }
        return cpfs.size();
    }

    /** Map: quantidade de moradores por unidade. */
    public Map<String, Integer> moradoresPorUnidade() {
        Map<String, Integer> contagem = new TreeMap<>();
        for (Morador morador : moradores.listarTodos()) {
            contagem.merge(morador.getUnidade().getIdentificacao(), 1, Integer::sum);
        }
        return contagem;
    }

    // ------------------------------------------------------------------
    // Arquivos (I/O)
    // ------------------------------------------------------------------

    public Path exportarMoradores() throws IOException {
        return exportar("moradores.csv", "id;nome;cpf;email;unidade;perfil", listarMoradores());
    }

    public Path exportarTaxas() throws IOException {
        return exportar("taxas.csv", "id;unidade;competencia;valor;vencimento;situacao", listarTaxas());
    }

    public Path exportarReservas() throws IOException {
        return exportar("reservas.csv", "id;area;morador;data;status", listarReservas());
    }

    private <T extends Exportavel> Path exportar(String arquivo, String cabecalho, List<T> itens) throws IOException {
        List<String> linhas = new ArrayList<>();
        linhas.add(cabecalho);
        for (T item : itens) {
            linhas.add(item.paraCsv());
        }
        Path caminho = ArquivoUtil.salvar(arquivo, linhas);
        ArquivoUtil.registrarLog("Arquivo exportado: " + caminho);
        return caminho;
    }

    // ------------------------------------------------------------------
    // Dados de exemplo (para demonstração)
    // ------------------------------------------------------------------

    public void popularDadosExemplo() throws RegraNegocioException {
        if (!unidades.listarTodos().isEmpty()) {
            throw new RegraNegocioException("Já existem dados cadastrados. Os exemplos só entram em um banco vazio.");
        }
        Unidade a101 = cadastrarUnidade("A", "101", 650.00);
        Unidade a102 = cadastrarUnidade("A", "102", 650.00);
        Unidade b201 = cadastrarUnidade("B", "201", 820.50);

        cadastrarMorador("Ana Souza", "11111111111", "ana@email.com", a101.getId(), true);
        cadastrarMorador("Bruno Lima", "22222222222", "bruno@email.com", a102.getId(), false);
        cadastrarMorador("Carla Mendes", "33333333333", "carla@email.com", b201.getId(), true);

        cadastrarFuncionario("Diego Alves", "44444444444", "diego@email.com", "Porteiro");
        cadastrarFuncionario("Elisa Prado", "55555555555", "elisa@email.com", "Zeladora");

        cadastrarAreaComum("Salão de festas", 80);
        cadastrarAreaComum("Churrasqueira", 25);

        publicarAviso("Manutenção do elevador", "O elevador do bloco A ficará parado na quinta-feira.", "Síndico");
    }

    // ------------------------------------------------------------------

    private <T> T buscar(Repositorio<T> repositorio, Long id, String nomeEntidade)
            throws RegistroNaoEncontradoException {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException(nomeEntidade, id));
    }
}
