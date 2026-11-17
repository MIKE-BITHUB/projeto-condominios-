# Projeto Condomínios

Sistema de gestão de condomínio em Java 21, com menu no terminal, JPA/Hibernate e SQLite.
Disciplina: Programação Orientada a Objetos (GPE17N40158).

## Como executar
1. Abra o projeto no IntelliJ (File > Open > selecione o `pom.xml` > Open as Project).
2. Use o JDK 21 (Microsoft OpenJDK 21) e aguarde o Maven baixar as dependências.
3. Execute a classe `br.edu.catolica.ucb.poo.Main`.
4. No menu, a opção 9 carrega dados de exemplo.

O banco `condominio.db` (SQLite) é criado automaticamente na pasta do projeto.

## Fluxos
Unidades, moradores, funcionários, taxas (gerar, pagar, inadimplência), áreas comuns e reservas,
avisos e exportação de arquivos CSV.
