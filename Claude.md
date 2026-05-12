---
# Claude.md - Contexto Técnico e Diretrizes do Projeto de TCC

Este documento serve como a "Bússola de Contexto" para o desenvolvimento da plataforma de letramento digital. Deve ser lido por qualquer agente de I.A. ou desenvolvedor antes de iniciar a escrita de código.

---

## 1. Visão Geral do Projeto
- **Título:** Plataforma de Letramento Digital baseada em Engenharia Reversa.
- **Objetivo:** Capacitar usuários leigos a identificar riscos cibernéticos através da desconstrução técnica (Simulador Raio-X).
- **Proposta:** Utilizar o "Modo Inspetor" para mostrar como a tecnologia funciona por trás de um golpe (Phishing, I.A. Fake, etc.).

## 2. Stack Tecnológica (Eficiência e Robustez)
- **Backend:** Java 17 + Spring Boot 4.0.6.
- **ORM:** Hibernate 6 (Suporte nativo a JSONB via `@JdbcTypeCode`).
- **Banco de Dados:** PostgreSQL (armazenamento de lógica flexível via campos `JSONB`).
- **Build Tool:** Maven 3.x.
- **Segurança:** Spring Security + Spring Security OAuth2 Client (Autenticação Stateless via Google OAuth2.0).
- **Frontend:** React (Vite) + Tailwind CSS + TanStack Query.
- **Linguagens Adicionais:** Kotlin 2.3.10 (para enums type-safe).
- **Auxiliares:** MapStruct 1.5.5 (mapeamento de objetos), Lombok (boilerplate), Jackson (manipulação JSON), Docker (ambiente de BD).

### Dependências Principais (pom.xml)
- `spring-boot-starter-data-jpa` - Persistência com Hibernate/JPA
- `spring-boot-starter-security` - Framework de segurança
- `spring-boot-starter-security-oauth2-client` - Integração OAuth2 Google
- `spring-boot-starter-validation` - Validação de DTOs com Bean Validation
- `spring-boot-starter-webmvc` - REST Controllers e MVC
- `spring-boot-devtools` - Reload automático em desenvolvimento
- `postgresql` - Driver PostgreSQL
- `org.projectlombok:lombok` - Gerador de boilerplate (getters, setters, constructores)
- `org.mapstruct:mapstruct` - Mapeamento automático Entity ↔ DTO
- `org.jetbrains.kotlin:kotlin-stdlib-jdk8` - Suporte Kotlin no projeto
- **Dependências de Teste:** spring-boot-starter-*-test (Data JPA, Security, Validation, WebMVC)

---

## 3. Arquitetura e Organização de Pastas

### Árvore de Diretórios Completa - `src/main/java`

```
com/projeto/tcc/letramento/
│
├── LetramentoDigitalApplication.java          [Classe Principal - Spring Boot Entry Point]
│
├── controller/                                 [Camada de API REST - Endpoints]
│   └── ScenarioController.java                [Endpoints para Cenários e Raio-X]
│
├── dto/                                       [Data Transfer Objects - Serialização/Comunicação]
│   ├── AnswerDTO.java                         [DTO para Respostas de Quizzes]
│   ├── AuthResponseDTO.java                   [DTO para Respostas de Autenticação]
│   ├── ScenarioDTO.java                       [DTO para Dados de Cenários]
│   └── UserDTO.java                           [DTO para Dados de Usuário]
│
├── enums/                                     [Enumerações Type-Safe]
│   ├── CidPillar.kt                          [Enum Kotlin: {CONFIDENCIALIDADE, INTEGRIDADE, DISPONIBILIDADE}]
│   ├── ProgressStatus.kt                     [Enum Kotlin: Estados de Progresso]
│   └── UserRole.kt                           [Enum Kotlin: {ALUNO, PROFESSOR, ADMIN}]
│
├── model/                                    [Entidades JPA - Domain Model]
│   ├── User.java                             [Usuário com autenticação Google OAuth]
│   ├── Scenario.java                         [Cenário com dados JSON para Raio-X]
│   ├── Trail.java                            [Trilha/Sequência de Aprendizagem]
│   └── Progress.java                         [Rastreamento de Progresso do Usuário]
│
├── repository/                               [Interfaces Spring Data JPA - Data Access]
│   ├── UserRepository.java                   [CRUD + queries customizadas para User]
│   ├── ScenarioRepository.java               [CRUD + queries para Scenarios]
│   ├── TrailRepository.java                  [CRUD + queries para Trails]
│   └── ProgressRepository.java               [CRUD + queries para Progress]
│
└── service/                                  [Camada de Lógica de Negócio]
└── ScenarioService.java                  [Serviço de Scenarios - Raio-X, comparações, validações]
```

### Árvore de Diretórios - `src/main/resources`

```
resources/
│
├── application.properties                    [Configurações do Spring Boot]
│   ├── spring.datasource.url               [Conexão PostgreSQL]
│   ├── spring.jpa.hibernate.ddl-auto       [Modo validate - sem alterações auto]
│   ├── spring.jpa.show-sql                 [Log SQL em console]
│   └── OAuth2 + Security configs           [Será expandido]
│
├── static/                                  [Arquivos estáticos - CSS, JS, imagens]
│   └── [Vazio - Frontend em React separado]
│
└── templates/                               [Templates Thymeleaf - se necessário]
└── [Vazio - API REST pura, sem server-side render]
```

---

## 4. Descrição Detalhada de Cada Camada

### **Camada: Controller** (`controller/`)
**Responsabilidade:** Exposição de endpoints REST, recepção de requisições HTTP, delegação para serviços.
- **ScenarioController.java:**
  - `GET /api/scenarios/{id}/xray` - Retorna um cenário com seus dados de Raio-X decodificados
  - Recebe `Long id` como parâmetro de caminho
  - Retorna `ResponseEntity<ScenarioDTO>` com status HTTP 200

**Padrão Aplicado:** Injeção via construtor com `@RequiredArgsConstructor` (Lombok)

---

### **Camada: DTO** (`dto/`)
**Responsabilidade:** Objetos que representam dados trafegados entre API e cliente, isolando a estrutura interna do Domain.
- **AnswerDTO.java** - Transporta respostas de quizzes do usuário
- **AuthResponseDTO.java** - Transporta informações retornadas após autenticação OAuth
- **ScenarioDTO.java** - Transporta dados de um cenário (id, título, pilar CID, dados Raio-X)
- **UserDTO.java** - Transporta dados do usuário (perfil, nome, email, role)

**Padrão Aplicado:** Objetos imutáveis (idealmente Java Records, conforme diretiva no Claude.md original)

**Mapeamento:** MapStruct converte Entity → DTO automaticamente (performance superior vs reflexão)

---

### **Camada: Enum** (`enums/`)
**Responsabilidade:** Tipos seguros e validados para valores fixos e reusáveis.
- **CidPillar.kt** - Define os três pilares de segurança CIA:
  - `CONFIDENCIALIDADE` - Dados não devem ser acessíveis sem autorização
  - `INTEGRIDADE` - Dados não devem ser alterados sem autorização
  - `DISPONIBILIDADE` - Dados devem estar acessíveis quando necessário
- **ProgressStatus.kt** - Estados do progresso: `NÃOINICIADO`, `EMANDAMENTO`, `CONCLUIDO`, `REVISÃO`
- **UserRole.kt** - Papéis de usuário: `ALUNO`, `PROFESSOR`, `ADMIN`

**Padrão Aplicado:** Escrito em Kotlin para maior type-safety e legibilidade

---

### **Camada: Model/Domain** (`model/`)
**Responsabilidade:** Entidades JPA que representam o esquema de banco de dados e a lógica de domínio.
- **User.java**
  - Campos: `id`, `name`, `email`, `googleId` (chave primária lógica), `course`, `academicLevel`, `role`, `createdAt`
  - Anotações: `@Entity`, `@Table(name = "users")`, `@Enumerated(EnumType.STRING)`
  - **Padrão Google OAuth:** Campo `googleId` armazena o `sub` (subject) do token JWT do Google

- **Scenario.java**
  - Campos: `id`, `titleScenarios`, `xrayData` (JSON), `quiz` (JSON), `pillar`, `trail`
  - Anotações: `@JdbcTypeCode(SqlTypes.JSON)`, `@Column`, `@ManyToOne`
  - **Tipo JsonNode:** Utiliza Jackson Tree Model para manipular estruturas JSON dinâmicas
  - **Relação:** Muitos-para-Um com `Trail`

- **Trail.java**
  - Representa uma trilha de aprendizagem (sequência de cenários)
  - Relaciona-se Um-para-Muitos com `Scenario`

- **Progress.java**
  - Rastreia o progresso do usuário em cada trilha
  - Campos esperados: `userId`, `trailId`, `status`, `timeSpent`, `quizScore` (BigDecimal), `userFeedback`, `createdAt`, `updatedAt`

**Padrão Aplicado:** Lombok com `@Getter @Setter @NoArgsConstructor @AllArgsConstructor`

**Auditoria:** Campo `createdAt` com `LocalDateTime` (essencial para análise estatística do TCC)

---

### **Camada: Repository** (`repository/`)
**Responsabilidade:** Acesso a dados via Spring Data JPA. Herdam de `JpaRepository<Entity, ID>`.
- **UserRepository.java**
  - Métodos built-in: `save()`, `findById()`, `findAll()`, `delete()`
  - Queries customizadas esperadas: `findByGoogleId()`, `findByEmail()`

- **ScenarioRepository.java**
  - Consultas filtradas por `trailId`, `pillar`
  - Método customizado: `findByPillar(CidPillar)`

- **TrailRepository.java**
  - CRUD padrão + queries por status e usuário

- **ProgressRepository.java**
  - Queries agregadas: `findByUserId()`, `findByUserIdAndTrailId()`
  - Cálculos estatísticos para dashboard de progresso

**Padrão Aplicado:** Sem implementação (gerada automaticamente pelo Spring Data)

---

### **Camada: Service** (`service/`)
**Responsabilidade:** Lógica de negócio complexa, transformações, integrações entre repositories.
- **ScenarioService.java**
  - Método chave: `getScenarioWithXRay(Long id)` - Recupera cenário e decodifica dados Raio-X
  - Comparações dinâmicas: Navega estrutura JsonNode para extrair e comparar metadados
  - Validações: Verifica se cenário mapeia a pilares CID válidos
  - Cálculos: Pontuation de quiz baseado em respostas do usuário

**Padrão Aplicado:** Injeção via construtor; transações com `@Transactional`

---

### **Classe Principal: LetramentoDigitalApplication**
**Responsabilidade:** Entry point da aplicação Spring Boot.
- Anotação `@SpringBootApplication` - Habilita auto-configuration, component scanning e desabilita Spring MVC
- Método `main()` - Inicializa o contexto Spring

---

## 5. Fluxo Típico da Requisição (MVC)

```
1. Cliente (Frontend React)
   ↓
2. HTTP GET /api/scenarios/{id}/xray
   ↓
3. ScenarioController.getXRay(id)
   ↓
4. ScenarioService.getScenarioWithXRay(id)
   ↓
5. ScenarioRepository.findById(id)
   ↓
6. Banco de Dados PostgreSQL → JsonNode (JSONB)
   ↓
7. Service processa xrayData (Jackson Tree Model)
   ↓
8. MapStruct: Scenario Entity → ScenarioDTO
   ↓
9. ResponseEntity.ok(dto) → JSON
   ↓
10. Cliente recebe resposta HTTP 200 + JSON
```

---

## 6. Configuração do Banco de Dados

### application.properties
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/letramento_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate  # Não alterado automaticamente
spring.jpa.show-sql=true                # Log de queries SQL
spring.jpa.properties.hibernate.format_sql=true
```

### Configurações Esperadas (não visíveis ainda)
- **Google OAuth2 Credentials** - `client-id`, `client-secret` em `application-*.properties`
- **JWT Secret** - Para tokens de sessão
- **CORS** - Configuração para aceitar requisições do Frontend React

---

## 7. Regras de Negócio e Implementação

- **Autenticação:** Exclusivamente via Google OAuth2.0. O campo `google_id` (sub) é a chave primária lógica do usuário.
- **Simulador Raio-X:** A lógica de desconstrução mapeia obrigatoriamente um ou mais pilares **CID** (Confidencialidade, Integridade, Disponibilidade).
- **Métricas Acadêmicas:** Todo progresso registra `time_spent`, `quiz_score` (BigDecimal) e `user_feedback`.
- **Campos JSON (JSONB):** Utilizados em `Scenario.xrayData` e `Scenario.quiz` para máxima flexibilidade na modelagem de diferentes tipos de golpes.

---

## 8. Diretrizes de Codificação

- **Idioma:** Código e variáveis em **Inglês**; Comentários e documentação em **Português (BR)**.
- **Nomenclatura:**
  - SQL: `snake_case` (ex: `xray_data`, `google_id`)
  - Java/Kotlin: `camelCase` (ex: `xrayData`, `getScenarioWithXRay()`)
- **Segurança Nativa:** Validar entradas via DTOs com `@Valid` + Spring Validation; utilizar proteções padrão do Spring Security contra CSRF, XSS e SQL Injection.
- **Injeção de Dependência:** Sempre via construtor com `@RequiredArgsConstructor` (Lombok).
- **Mapeamento:** Priorizar **MapStruct** para transformações Entity ↔ DTO (performance superior).
- **Modelos:** Utilizar Java 17+ Records para DTOs imutáveis e type-safe.

---

## 9. Próximas Expansões Esperadas

Com base na estrutura identificada, as seguintes camadas/funcionalidades serão implementadas:

- **`config/`** - Configurações de Spring Security, OAuth2, CORS, Jackson
- **`transformer/`** ou **`mapper/`** - MapStruct Interfaces para conversões Entity ↔ DTO
- **`exception/`** - Handlers customizados para GlobalExceptionHandler (@ControllerAdvice)
- **`util/`** - Classes utilitárias (validators, parsers JSON, helpers)
- **`security/`** - Filtros JWT, implementação de UserDetails, entidades de autenticação
- Novos **Controllers** - UserController, ProgressController, TrailController, AuthController
- Novos **Services** - ProgressService, TrailService, AuthService
- **`test/`** - Testes unitários (JUnit 5) e integração (MockMvc, @DataJpaTest)

---

## 10. Stack de Teste

O projeto está configurado com as seguintes dependências de teste:
- `spring-boot-starter-data-jpa-test` - Testes de JPA com `@DataJpaTest`
- `spring-boot-starter-security-test` - Testes de segurança com `@WithMockUser`
- `spring-boot-starter-webmvc-test` - Testes de controllers com `MockMvc`
- `spring-boot-starter-validation-test` - Validação em testes
- `kotlin-test` - Suporte a testes em Kotlin (para enums)

---

**Nota para a I.A.:** Ao sugerir implementações, mantenha coesão com a arquitetura em camadas descrita, priorize o uso de recursos modernos do **Java 17** (Records, Sealed Classes, Pattern Matching, Stream API) e **Kotlin** para type-safety, e garanta que o sistema permaneça leve, escalável e seguro. Sempre prefira **MapStruct** para mapeamentos e **Spring Data JPA** para queries complexas.
