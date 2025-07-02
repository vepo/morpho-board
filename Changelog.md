# Changelog

## Vídeo 002 [Commit f9347db](https://github.com/vepo/morpho-board/commit/f9347db01d7bd9325d7807657d827c305e9cedc2)

1. Projeto inicial criado com controller para "Change"
2. Classe de teste criada

## Pós Vídeo 002 [Commit b11afe3](https://github.com/vepo/morpho-board/commit/b11afe37d7ec7e2d308c4c091a6566d5524b8fa3)

1. Mudança de pacote `com.seuprojeto` para `io.vepo.morphoboard`
2. Distribuição das classes nos pacotes por dominio e não por funcionalidade
3. Correção de import errado. O método `greaterThanOrEqualTo` fazia parte da classe `org.hamcrest.Matchers` e não `org.hamcrest.CoreMatchers`.
4. Adiciona `io.quarkus:quarkus-rest-jackson` como dependência.
5. Adiciona `@Transactional` ao método `ChangeController.create`
6. Substitui `"application/json"` por `MediaType.APPLICATION_JSON`
7. Comentando testes que não foram implementados.

## Video 003 [Commit a246846](https://github.com/vepo/morpho-board/commit/a24684693c2d11a06ce368eb3a0fbd1737004e18)

1. Adicionando projeto Angular com Quinoa para build.

## Video 004 [Commit 5ec6761](https://github.com/vepo/morpho-board/commit/5ec67614da2ab79a305203710c0b0aac7ca28fe0)

1. Modelando tabelas usando JPA (`User` e `Ticket`)

## Video 005

2. Criando APIs para criação de Tickets