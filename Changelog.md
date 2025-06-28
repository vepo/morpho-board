# Changelog

## Vídeo 001 [Commit f9347db](https://github.com/vepo/morpho-board/commit/f9347db01d7bd9325d7807657d827c305e9cedc2)

1. Projeto inicial criado com controller para "Change"
2. Classe de teste criada

## Pós Vídeo 001

1. Mudança de pacote `com.seuprojeto` para `io.vepo.morphoboard`
2. Correção de import errado. O método `greaterThanOrEqualTo` fazia parte da classe `org.hamcrest.Matchers` e não `org.hamcrest.CoreMatchers`.
3. Adiciona `io.quarkus:quarkus-rest-jackson` como dependência.
5. Adiciona `@Transactional` ao método `ChangeController.create`
6. Substitui `"application/json"` por `MediaType.APPLICATION_JSON`
7. Comentando testes que não foram implementados.