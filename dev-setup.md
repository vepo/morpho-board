# Configuração do Desenvolvimento

## Para fazer o SPA fallback funcionar corretamente:

### 1. Build do Frontend Angular
```bash
cd src/main/webui
npm install
npm run build
```

### 2. Build do Backend Quarkus
```bash
./mvnw clean package
```

### 3. Executar em modo desenvolvimento
```bash
./mvnw quarkus:dev
```

### 4. Ou executar o JAR
```bash
java -jar target/quarkus-app/quarkus-run.jar
```

## URLs de acesso:
- **Home**: http://localhost:8080/
- **Kanban**: http://localhost:8080/kanban
- **API Docs**: http://localhost:8080/docs/openapi

## Notas importantes:
- O SPA fallback só funciona após o build do Angular
- Em desenvolvimento, use o `NotFoundExceptionMapper` criado
- Em produção, o Quinoa cuida automaticamente do fallback 