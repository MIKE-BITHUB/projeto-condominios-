import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Atividade 001 - Item 05 - Collections
 * Tema: Projeto Condomínios
 * Demonstra ArrayList, HashSet e HashMap em um único programa.
 */
public class ColecoesCondominio {

    // Classe simples para representar um morador
    static class Morador {
        private final String nome;
        private final String apartamento;

        Morador(String nome, String apartamento) {
            this.nome = nome;
            this.apartamento = apartamento;
        }

        String getNome() {
            return nome;
        }

        String getApartamento() {
            return apartamento;
        }

        @Override
        public String toString() {
            return nome + " (Apto " + apartamento + ")";
        }
    }

    public static void main(String[] args) {

        // ==================================================
        // 1) ArrayList: lista ordenada, aceita duplicados
        //    Uso: cadastro de moradores na ordem de chegada
        // ==================================================
        System.out.println("=== ArrayList: Moradores cadastrados ===");
        ArrayList<Morador> moradores = new ArrayList<>();
        moradores.add(new Morador("Ana Souza", "101"));
        moradores.add(new Morador("Bruno Lima", "102"));
        moradores.add(new Morador("Carla Mendes", "201"));
        moradores.add(new Morador("Diego Alves", "202"));

        for (Morador m : moradores) {
            System.out.println(m);
        }
        System.out.println("Total: " + moradores.size());
        System.out.println("Primeiro da lista: " + moradores.get(0));

        // Remove um morador que se mudou
        moradores.remove(1);
        System.out.println("Após remover o índice 1, total: " + moradores.size());

        // ==================================================
        // 2) HashSet: não aceita duplicados, sem ordem garantida
        //    Uso: placas de veículos autorizados (únicas)
        // ==================================================
        System.out.println("\n=== HashSet: Placas autorizadas ===");
        Set<String> placas = new HashSet<>();
        placas.add("ABC-1234");
        placas.add("DEF-5678");
        placas.add("GHI-9012");

        boolean adicionou = placas.add("ABC-1234"); // duplicada
        System.out.println("Tentou adicionar ABC-1234 de novo? " + adicionou);
        System.out.println("Total de placas únicas: " + placas.size());
        System.out.println("Placa DEF-5678 autorizada? " + placas.contains("DEF-5678"));
        System.out.println("Placa XYZ-0000 autorizada? " + placas.contains("XYZ-0000"));

        for (String placa : placas) {
            System.out.println("Placa: " + placa);
        }

        // ==================================================
        // 3) HashMap: pares chave -> valor
        //    Uso: apartamento -> valor da taxa de condomínio
        // ==================================================
        System.out.println("\n=== HashMap: Taxa de condomínio por apartamento ===");
        Map<String, Double> taxas = new HashMap<>();
        taxas.put("101", 650.00);
        taxas.put("201", 650.00);
        taxas.put("202", 820.50);

        // put com chave existente substitui o valor
        taxas.put("101", 700.00);

        System.out.println("Taxa do apto 101: R$ " + taxas.get("101"));
        System.out.println("Existe apto 303? " + taxas.containsKey("303"));

        for (Map.Entry<String, Double> entrada : taxas.entrySet()) {
            System.out.println("Apto " + entrada.getKey() + " -> R$ " + entrada.getValue());
        }

        // ==================================================
        // 4) Combinando as três coleções
        //    Mapa apartamento -> morador, montado a partir da lista
        // ==================================================
        System.out.println("\n=== Combinação: Apartamento -> Morador ===");
        Map<String, Morador> porApartamento = new HashMap<>();
        for (Morador m : moradores) {
            porApartamento.put(m.getApartamento(), m);
        }

        Set<String> apartamentosOcupados = porApartamento.keySet();
        System.out.println("Apartamentos ocupados: " + apartamentosOcupados);

        double totalArrecadado = 0;
        for (String apto : apartamentosOcupados) {
            double taxa = taxas.getOrDefault(apto, 0.0);
            totalArrecadado += taxa;
            System.out.println(porApartamento.get(apto) + " paga R$ " + taxa);
        }
        System.out.println("Total previsto de arrecadação: R$ " + totalArrecadado);
    }
}
