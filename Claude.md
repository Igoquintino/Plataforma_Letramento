# Claude.md - Contexto Técnico e Diretrizes do Projeto de TCC

Este documento serve como a "Bússola de Contexto" para o desenvolvimento da plataforma de letramento digital. Deve ser lido por qualquer agente de I.A. ou desenvolvedor antes de iniciar a escrita de código.

---

## 1. Visão Geral do Projeto

- **Título:** Plataforma de Letramento Digital baseada em Engenharia Reversa
- **Objetivo:** Capacitar usuários leigos a identificar riscos cibernéticos através da desconstrução técnica (Simulador Raio-X)
- **Proposta:** Utilizar o "Modo Inspetor" para mostrar como a tecnologia funciona por trás de um golpe (Phishing, I.A. Fake, etc.)
- **Status:** MVP em desenvolvimento com autenticação Google OAuth2 e simulador Raio-X funcional
- **Escopo:** Plataforma web com backend Spring Boot + frontend React (separado)

---

## 2. Stack Tecnológica (Eficiência e Robustez)

### Core Backend
- **Java:** 17+ (com features modernos: Records, Sealed Classes, Stream API, Pattern Matching)
- **Spring Boot:** 4.0.6 (latest stable)
- **Build Tool:** Maven 3.x
- **ORM:** Hibernate 6 (suporte nativo a JSONB via `@JdbcTypeCode`)

### Banco de Dados
- **Principal:** PostgreSQL (armazenamento flexível via campos `JSONB`)
- **Configuração:** `ddl-auto=validate` (sem alterações automáticas)
- **SQL Logging:** Ativo em desenvolvimento (`show-sql=true`, `format_sql=true`)

### Segurança & Autenticação
- **Spring Security:** Framework de segurança padrão
- **OAuth2 Client:** Google OAuth2.0 (autenticação stateless)
- **JWT:** Para tokens de sessão (implementação esperada em `config/`)
- **CSRF Protection:** Habilitado por padrão via Spring Security

### Tratamento JSON & Mapeamento
- **Jackson:** Tree Model (JsonNode) para estruturas JSON flexíveis
- **MapStruct:** 1.5.5.Final (mapeamento Entity ↔ DTO com performance superior)
- **Lombok:** Geração automática de boilerplate (getters, setters, construtores)

[//]: # (### Linguagens Adicionais)

[//]: # (- **Kotlin:** 2.3.10 &#40;enums type-safe e validações em compile-time&#41;)

### Dependências Principais (pom.xml - Consolidadas)

#### Spring Boot Starters
```
✓ spring-boot-starter-data-jpa         → JPA/Hibernade ORM
✓ spring-boot-starter-security         → Autenticação/Autorização
✓ spring-boot-starter-security-oauth2-client  → OAuth2 Google
✓ spring-boot-starter-validation       → Bean Validation (@Valid, @NotNull, etc.)
✓ spring-boot-starter-webmvc            → REST Controllers e MVC
✓ spring-boot-devtools                  → Hot-reload em dev
```

#### Drivers de Banco
```
✓ postgresql                            → Driver nativo PostgreSQL
```

#### Processamento e Mapeamento
```
✓ org.mapstruct:mapstruct:1.5.5.Final
✓ org.mapstruct:mapstruct-processor:1.5.5.Final (compiler plugin)
✓ org.projectlombok:lombok              → Annotation processor
```

#### Suporte Kotlin
```
✓ kotlin-stdlib-jdk8:2.3.10
✓ kotlin-test:2.3.10                    → Testes em Kotlin
```

#### Dependências de Teste (escopo: test)
```
✓ spring-boot-starter-data-jpa-test
✓ spring-boot-starter-security-oauth2-client-test
✓ spring-boot-starter-security-test
✓ spring-boot-starter-validation-test
✓ spring-boot-starter-webmvc-test
```

---

## 3. Arquitetura e Organização de Pastas - COMPLETA

### Árvore de Diretórios - `src/main/java`

```
com/projeto/tcc/letramento/
│
├── LetramentoDigitalApplication.java      [Entry Point - @SpringBootApplication]
│                                          │ → Inicializa contexto Spring Boot
│
├── controller/                            [HTTP REST Layer - Endpoints]
│   ├── ScenarioController.java           │ ✅ IMPLEMENTADO
│   │  ├── GET  /api/scenarios/{id}/xray  │ → Retorna ScenarioDTO com Raio-X decodificado
│   │  ├── POST /api/scenarios/answer     │ → Valida resposta + Salva Progress com métricas
│   │  └── GET  /api/scenarios/{id}/quiz  │ → Retorna apenas dados do quiz
│   │
│   ├── UserController.java               │ ✅ IMPLEMENTADO
│   │  ├── GET    /api/users/{id}         │ → Retorna perfil do usuário
│   │  └── PATCH  /api/users/{id}/profile │ → Atualiza dados acadêmicos (curso, nível)
│   │
│   ├── TrailController.java              │ ✅ IMPLEMENTADO
│   │  ├── GET  /api/trails               │ → Lista todas as trilhas
│   │  ├── GET  /api/trails/{id}/scenarios │ → Cenários de uma trilha
│   │  └── GET  /api/trails/{id}/progress/{userId} │ → % conclusão para aluno
│   │
│   ├── progressController.java           │ ✅ IMPLEMENTADO
│   │  └── GET  /api/progress/dashboard/{userId} │ → Dashboard pessoal do aluno
│   │
│   └── AdminController.java              │ ✅ IMPLEMENTADO
│       ├── POST   /api/admin/tails       │ → Criar trilha (admin only)
│       ├── POST   /api/admin/scenarios   │ → Criar cenário (admin only)
│       └── DELETE /api/admin/trails/{id} │ → Deletar trilha (admin only)
│
├── dto/                                  [Data Transfer Objects]
│   │                                     │ → Responsável por input/output da API
│   │                                     │ → Isolam a estrutura interna do Domain
│   │
│   ├── ScenarioDTO.java                  │ ✅ Record: (id, title, pillar, xrayData)
│   │                                     │ → CRUCIAL: Carrega inteligência engenharia reversa
│   │
│   ├── AnswerDTO.java                    │ ✅ Record: (scenarioId, answers: JsonNode)
│   │                                     │ → Respostas do aluno para validação
│   │
│   ├── AuthResponseDTO.java              │ ✅ Record: (token, user: UserDTO)
│   │                                     │ → Response pós-autenticação OAuth
│   │
│   ├── UserDTO.java                      │ ✅ Record: (id, name, email, course, academicLevel)
│   │                                     │ → Perfil do usuário sem dados sensíveis
│   │
│   ├── UserUpdateDTO.java                │ ✅ Record: (course, academicLevel)
│   │                                     │ → Atualização de dados acadêmicos para TCC
│   │
│   ├── TrailRequestDTO.java              │ ✅ Record: (title, description)
│   │                                     │ → Criação/Atualização de trilhas
│   │
│   └── ScenarioRequestDTO.java           │ ✅ Record: (titleScenario, xrayData, quiz, pillar, trailId)
│                                         │ → Criação/Atualização de cenários (admin)
│
├── enums/                                [Type-Safe Enumerations - Kotlin]
│   │                                     │ → Usa Kotlin para garantir type-safety
│   │
│   ├── CidPillar.java                    │ ✅ IMPLEMENTADO
│   │                                     │ Pilares de Segurança CIA:
│   │                                     │  ├── CONFIDENCIALIDADE (C)
│   │                                     │  │   → Dados não devem ser acessíveis
│   │                                     │  ├── INTEGRIDADE (I)
│   │                                     │  │   → Dados não devem ser alterados
│   │                                     │  └── DISPONIBILIDADE (D)
│   │                                     │      → Dados devem estar acessíveis
│   │
│   ├── ProgressStatus.java               │ ✅ IMPLEMENTADO
│   │                                     │ Estados do Progresso:
│   │                                     │  ├── IN_PROGRESS   (iniciado)
│   │                                     │  ├── COMPLETED     (finalizado com sucesso)
│   │                                     │  └── FAILED        (fracassado)
│   │
│   └── UserRole.java                     │ ✅ IMPLEMENTADO
│                                         │ Papéis de Usuário:
│                                         │  ├── ALUNO  (papel padrão)
│                                         │  └── ADMIN  (administrador)
│
├── model/                                [JPA Entities - Domain Model]
│   │                                     │ → Representam tabelas do banco de dados
│   │                                     │ → Contêm lógica/relacionamentos de domínio
│   │
│   ├── User.java                         │ ✅ IMPLEMENTADO
│   │   ├── id (PK)
│   │   ├── name, email (unique)
│   │   ├── googleId (unique) ← Chave primária lógica OAuth
│   │   ├── course, academicLevel (para segmentação TCC)
│   │   ├── role: UserRole {ALUNO, ADMIN}
│   │   ├── createdAt: LocalDateTime ← CRUCIAL para TCC
│   │   └── [1:N com Progress]
│   │
│   ├── Scenario.java                     │ ✅ IMPLEMENTADO
│   │   ├── id (PK)
│   │   ├── titleScenarios: String
│   │   ├── xrayData: JsonNode (JSONB) ← Engenharia reversa
│   │   ├── quiz: JsonNode (JSONB) ← Dados do quiz
│   │   ├── pillar: CidPillar (Enum)
│   │   ├── trail: Trail (ManyToOne)
│   │   └── [1:N com Progress]
│   │
│   ├── Trail.java                        │ ✅ IMPLEMENTADO
│   │   ├── id (PK)
│   │   ├── title: String
│   │   ├── description: String
│   │   └── [1:N com Scenario]
│   │
│   └── Progress.java                     │ ✅ IMPLEMENTADO
│       ├── id (PK)
│       ├── status: ProgressStatus
│       ├── quizScore: BigDecimal ← Precisão para TCC
│       ├── completedAt: LocalDateTime
│       ├── timeSpent: Long (segundos) ← CRUCIAL para TCC
│       ├── userFeedback: String ← CRUCIAL para TCC
│       ├── user: User (ManyToOne)
│       └── scenario: Scenario (ManyToOne)
│
├── repository/                           [Spring Data JPA - Data Access Layer]
│   │                                     │ → Interfaces que herdam JpaRepository
│   │                                     │ → Sem implementação (gerada em compilação)
│   │
│   ├── UserRepository.java               │ ✅ IMPLEMENTADO
│   │   ├── CRUD padrão
│   │   ├── findByEmail(String) → Optional<User>
│   │   └── findByGoogleId(String) → Optional<User>
│   │
│   ├── ScenarioRepository.java           │ ✅ IMPLEMENTADO
│   │   ├── CRUD padrão
│   │   └── findByTrailId(Long) → List<Scenario>
│   │
│   ├── TrailRepository.java              │ ✅ IMPLEMENTADO
│   │   └── CRUD padrão
│   │
│   └── ProgressRepository.java           │ ✅ IMPLEMENTADO
│       ├── CRUD padrão
│       ├── findByUserId(Long) → List<Progress>
│       └── findByUserIdAndScenarioId() → Optional<Progress>
│
└── service/                              [Business Logic Layer - Orquestração]
│                                     │ → Lógica de negócio complexa
│                                     │ → Transformações e integrações
│
├── ScenarioService.java              │ ✅ IMPLEMENTADO
│   ├── getScenarioWithXray(id) → Scenario
│   │   └─ Busca cenário + decodifica xrayData
│   │
│   ├── compareUserResponse(scenarioId, answers) → Boolean
│   │   └─ Valida respostas contra quiz correto
│   │
│   └── getQuizData(id) → JsonNode
│       └─ Extrai apenas dados do quiz
│
├── ProgressService.java              │ ✅ IMPLEMENTADO
│   ├── getStudentDashboard(userId) → List<Progress>
│   │   └─ Retorna progresso geral do aluno
│   │
│   ├── startUserProgress(userId, scenarioId) → Progress
│   │   └─ Inicia novo progresso (lazy loading)
│   │
│   └── completeScenario(userId, scenarioId, score, feedback, timeSpent) → Progress
│       └─ Finaliza + Salva métricas (CRUCIAL)
│
├── UserService.java                  │ ✅ IMPLEMENTADO
│   ├── findById(id) → User
│   │
│   ├── processOAuthPostLogin(email, name, googleId) → User
│   │   └─ Cria/atualiza usuário após Google OAuth
│   │
│   └── updateProfile(userId, data) → User
│       └─ Atualiza curso/nível acadêmico para TCC
│
├── TrailService.java                 │ ✅ IMPLEMENTADO
│   ├── findAllActiveTrails() → List<Trail>
│   │
│   ├── getScenariosByTrail(trailId) → List<Scenario>
│   │
│   └── calculateTrailProgress(userId, trailId) → Double
│       └─ Retorna % conclusão da trilha
│
└── AdminService.java                 │ ✅ IMPLEMENTADO
├── createTrail(data) → Trail (@Transactional)
│
├── createScenario(data) → Scenario (@Transactional)
│   └─ Valida trailId, cria cenário com JSONB
│
└── deleteTrail(trailId) → void (@Transactional)
└─ Valida existência antes de deletar
```

### Árvore de Diretórios - `src/main/resources`

```
resources/
│
├── application.properties                [Configurações Principais]
│   ├── spring.application.name           → Nome da aplicação
│   ├── spring.datasource.*               → Conexão PostgreSQL
│   │   ├── url=jdbc:postgresql://localhost:5432/letramento_db
│   │   ├── username=${DB_USERNAME}       → Variável env
│   │   └── password=${DB_PASSWORD}       → Variável env
│   ├── spring.jpa.hibernate.ddl-auto     → validate (sem alterações)
│   ├── spring.jpa.show-sql               → true (logs SQL)
│   └── spring.jpa.properties.hibernate.format_sql → true (SQL formatado)
│
├── application-prod.properties           [Config para Produção - ESPERADO]
├── application-test.properties           [Config para Testes - ESPERADO]
│
├── static/                               [Assets Estáticos - NÃO UTILIZADOS]
│   └── [Vazio - Frontend em React separado com Vite]
│
└── templates/                            [Templates Thymeleaf - NÃO UTILIZADOS]
└── [Vazio - API REST pura, sem server-side render]
```

---

## 4. Descrição Detalhada de Cada Camada

### **Camada: Controller** - HTTP REST Layer

**Responsabilidade:**
- Exposição de endpoints HTTP REST
- Recepção e validação de requisições
- Delegação de lógica para serviços
- Construção de respostas HTTP apropriadas

**Padrão Aplicado:**
```java
@RestController                 // Indica que é um REST controller
@RequestMapping("/api/...")    // Prefixo da rota
@RequiredArgsConstructor       // Lombok: injeção via construtor
public class XyzController {
    private final XyzService xyzService;
    // endpoints
}
```

**Controllers Implementados:**

#### 1. **ScenarioController**
```
Responsabilidade: Simula cenários e gerencia quizzes do Raio-X

Endpoints:
├── GET /api/scenarios/{id}/xray
│   ├─ Input: Long id (path variable)
│   ├─ Output: ResponseEntity<ScenarioDTO>
│   └─ Fluxo:
│      1. Controller recebe requisição
│      2. Chama: scenarioService.getScenarioWithXray(id)
│      3. Service busca Entity no banco
│      4. MapStruct converte: Scenario Entity → ScenarioDTO
│      5. Controller retorna: ResponseEntity.ok(dto)
│
├── POST /api/scenarios/answer
│   ├─ Input: AnswerDTO { scenarioId, answers: JsonNode }
│   ├─ Output: ResponseEntity<String> (mensagem de feedback)
│   └─ Fluxo:
│      1. Valida: scenarioService.compareUserResponse()
│      2. Calcula score: BigDecimal (100 ou 0)
│      3. Salva progresso: progressService.completeScenario()
│      4. Retorna feedback ao aluno
│
└── GET /api/scenarios/{id}/quiz
    ├─ Input: Long id
    ├─ Output: ResponseEntity<JsonNode>
    └─ Fluxo: Retorna apenas dados estruturados do quiz
```

#### 2. **UserController**
```
Responsabilidade: Gerencia perfil do usuário e dados acadêmicos

Endpoints:
├── GET /api/users/{id}
│   ├─ Input: Long id
│   ├─ Output: ResponseEntity<UserDTO>
│   └─ Busca perfil do usuário sem expor googleId
│
└── PATCH /api/users/{id}/profile
    ├─ Input: UserUpdateDTO { course, academicLevel }
    ├─ Output: ResponseEntity<UserDTO>
    └─ Atualiza dados para segmentação do TCC
```

#### 3. **TrailController**
```
Responsabilidade: Listar trilhas e calcular progresso

Endpoints:
├── GET /api/trails
│   ├─ Output: ResponseEntity<List<Trail>>
│   └─ Lista todas as trilhas cadastradas
│
├── GET /api/trails/{id}/scenarios
│   ├─ Output: ResponseEntity<List<Scenario>>
│   └─ Retorna todos os cenários de uma trilha
│
└── GET /api/trails/{id}/progress/{userId}
    ├─ Input: Long id (trail), Long userId (aluno)
    ├─ Output: ResponseEntity<Double>
    └─ Calcula % de conclusão da trilha para o aluno
       Ex: 75.0 significa 75% concluído
```

#### 4. **progressController** (nota: snake_case não-padrão)
```
Responsabilidade: Dashboard de progresso do aluno

Endpoints:
└── GET /api/progress/dashboard/{userId}
    ├─ Input: Long userId
    ├─ Output: ResponseEntity<List<Progress>>
    └─ Retorna todo histórico de progresso do aluno
       (CRUCIAL para análise temporal do TCC)
```

#### 5. **AdminController**
```
Responsabilidade: CRUD de trilhas e cenários (admin only)

Endpoints:
├── POST /api/admin/tails (⚠️ typo: "tails" ao invés de "trails")
│   ├─ Input: TrailRequestDTO { title, description }
│   ├─ Output: ResponseEntity<Trail> (status 201 Created)
│   └─ Cria nova trilha de aprendizagem
│
├── POST /api/admin/scenarios
│   ├─ Input: ScenarioRequestDTO { titleScenario, xrayData, quiz, pillar, trailId }
│   ├─ Output: ResponseEntity<Scenario> (status 201 Created)
│   └─ Cria novo cenário com dados JSONB
│
└── DELETE /api/admin/trails/{id}
    ├─ Input: Long id
    ├─ Output: ResponseEntity<Void> (status 204 No Content)
    └─ Deleta trilha (valida existência antes)
```

---

### **Camada: DTO** - Data Transfer Objects

**Responsabilidade:**
- Transferência de dados entre API e Cliente (Frontend)
- Isolamento da estrutura interna do Domain
- Validação de entrada via `@Valid` + Bean Validation
- Serialização/Desserialização automática (Jackson)

**Padrão Aplicado:**
```java
// Java Record (imutável, type-safe, sem boilerplate)
public record XyzDTO(Long id, String name, @NotNull String description) {
}
```

**DTOs Implementados:**

1. **ScenarioDTO** ✅
   ```
   Record: (Long id, String title, CidPillar pillar, JsonNode xrayData)
   Propósito: Transportar dados de cenário + Raio-X decodificado
   JSON Output Exemplo:
   {
     "id": 1,
     "title": "Email Phishing Detection",
     "pillar": "CONFIDENCIALIDADE",
     "xrayData": {
       "type": "phishing_email",
       "sender": "fake@bank.com",
       "red_flags": ["misspelled domain", "urgency"]
     }
   }
   ```

2. **AnswerDTO** ✅
   ```
   Record: (Long scenarioId, JsonNode answers)
   Propósito: Receber respostas do aluno via POST
   JSON Input Exemplo:
   {
     "scenarioId": 1,
     "answers": { "answer": "phishing" }
   }
   ```

3. **AuthResponseDTO** ✅
   ```
   Record: (String token, UserDTO user)
   Propósito: Retorna após autenticação OAuth
   JSON Output Exemplo:
   {
     "token": "eyJhbGciOiJIUzI1NiIs...",
     "user": {
       "id": 1,
       "name": "João Silva",
       "email": "joao@example.com",
       "course": "Engenharia",
       "academicLevel": "Graduação"
     }
   }
   ```

4. **UserDTO** ✅
   ```
   Record: (Long id, String name, String email, String course, String academicLevel)
   Propósito: Perfil do usuário sem expor googleId
   Comentário: "DTO usado para devolver dados sem exposição sensível"
   ```

5. **UserUpdateDTO** ✅
   ```
   Record: (String course, String academicLevel)
   Propósito: Atualização de dados de segmentação acadêmica
   Comentário: "DTO utilizado para atualizar dados do aluno no TCC"
   ```

6. **TrailRequestDTO** ✅
   ```
   Record: (@NotBlank String title, @NotBlank String description)
   Propósito: Criação/Atualização de trilhas (admin)
   Validações: title e description não podem ser blank
   ```

7. **ScenarioRequestDTO** ✅
   ```
   Record: (
     @NotBlank String titleScenario,
     @NotNull JsonNode xrayData,
     @NotNull JsonNode quiz,
     @NotNull CidPillar pillar,
     @NotNull Long trailId
   )
   Propósito: Criação/Atualização de cenários (admin)
   Validações: Todos os campos obrigatórios
   ```

---

### **Camada: Enum** - Type-Safe Enumerations

**Responsabilidade:**
- Definir tipos seguros para valores fixos
- Garantir validação em compile-time
- Evitar strings soltas no código

**Tecnologia:** Escrito em **Kotlin** (2.3.10) para type-safety

**Enums Implementados:**

1. **CidPillar.kt** ✅
   ```kotlin
   enum class CidPillar {
       CONFIDENCIALIDADE,    // C: Dados não devem ser acessíveis
       INTEGRIDADE,          // I: Dados não devem ser alterados
       DISPONIBILIDADE       // D: Dados devem estar acessíveis
   }
   ```
   **Uso:** Cada Scenario mapeia obrigatoriamente um pilar CIA
   **JPA:** `@Enumerated(EnumType.STRING)` no Model

2. **ProgressStatus.kt** ✅
   ```kotlin
   enum class ProgressStatus {
       IN_PROGRESS,    // Aluno iniciou mas não finalizou
       COMPLETED,      // Aluno completou com sucesso
       FAILED          // Aluno falhou nas respostas
   }
   ```
   **Uso:** Rastreamento de estado em Progress

3. **UserRole.kt** ✅
   ```kotlin
   enum class UserRole {
       ALUNO,    // Papel padrão (estudante)
       ADMIN     // Administrador (cria conteúdo)
   }
   ```
   **Uso:** Controle de acesso e permissões

---

### **Camada: Model (Domain)** - JPA Entities

**Responsabilidade:**
- Representar tabelas do PostgreSQL
- Conter lógica e relacionamentos de domínio
- Ser gerenciados pelo Hibernate (ORM)

**Padrão Aplicado:**
```java
@Entity
@Table(name = "xyz")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Xyz {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
```

**Entidades Implementadas:**

1. **User.java** ✅
   ```
   Tabela: users
   
   Campos:
   ├── id (PK, Identity)
   ├── name: String
   ├── email: String (unique)
   ├── googleId: String (unique) ← Chave primária LÓGICA (OAuth)
   ├── course: String (opcional)
   ├── academicLevel: String
   ├── role: UserRole {ALUNO, ADMIN} (default: ALUNO)
   └── createdAt: LocalDateTime (default: NOW) ← CRUCIAL para TCC
   
   Relacionamentos:
   └── [1:N] com Progress (um usuário → múltiplos progressos)
   
   Autenticação Google OAuth:
   • googleId armazena o "sub" (subject) do token JWT de Google
   • Email é validado unicamente também
   ```

2. **Scenario.java** ✅
   ```
   Tabela: scenarios
   
   Campos:
   ├── id (PK, Identity)
   ├── titleScenarios: String
   ├── xrayData: JsonNode (JSONB) ← Engenharia reversa desconstruída
   ├── quiz: JsonNode (JSONB) ← Dados do quiz/atividade
   ├── pillar: CidPillar ← Mapeamento obrigatório CIA
   └── trail: Trail (ManyToOne)
   
   Anotações JSON:
   @JdbcTypeCode(SqlTypes.JSON)     ← Hibernate 6 suporta JSONB nativo
   @Column(name = "xray_data")
   private JsonNode xrayData;
   
   Justificativa JsonNode:
   • Permitir uso de Jackson Tree Model
   • Máxima flexibilidade para estruturas dinâmicas
   • Sem necessidade de criar classe Java para cada golpe
   • Ideal para comparações dinâmicas em ScenarioService
   
   Relacionamentos:
   ├── [N:1] com Trail
   └── [1:N] com Progress
   ```

3. **Trail.java** ✅
   ```
   Tabela: trails
   
   Campos:
   ├── id (PK, Identity)
   ├── title: String (não-nulo)
   └── description: String (não-nulo)
   
   Relacionamentos:
   └── [1:N] com Scenario (uma trilha → múltiplos cenários)
   
   Propósito:
   Agrupa múltiplos cenários em sequência de aprendizagem
   Ex: "Pilar Confidencialidade" contém 5 cenários diferentes
   ```

4. **Progress.java** ✅
   ```
   Tabela: progress
   
   Campos:
   ├── id (PK, Identity)
   ├── status: ProgressStatus ← Estado da conclusão
   ├── quizScore: BigDecimal ← CRUCIAL: Precisão numérica
   ├── completedAt: LocalDateTime
   ├── timeSpent: Long (segundos) ← CRUCIAL: Métricas para TCC
   ├── userFeedback: String ← CRUCIAL: Feedback qualitativo
   ├── user: User (ManyToOne)
   └── scenario: Scenario (ManyToOne)
   
   BigDecimal vs Float/Double:
   • Segurança: Sem erros de arredondamento
   • Precisão: Ideal para scores acadêmicos
   • Ex: BigDecimal("100.00") ao invés de 100.0f
   
   Métricas TCC:
   • timeSpent: Análise de engajamento temporal
   • quizScore: Pontuation com precisão
   • userFeedback: Dados qualitativos de experiência
   • createdAt/completedAt: Análise temporal
   ```

---

### **Camada: Repository** - Spring Data JPA

**Responsabilidade:**
- Acesso a dados via Spring Data JPA
- Herdam de `JpaRepository<Entity, ID_Type>`
- Geração automática de queries SQL

**Padrão Aplicado:**
```java
public interface XyzRepository extends JpaRepository<Xyz, Long> {
    // Query automática via naming convention
    List<Xyz> findByFieldName(String value);
}
```

**Repositories Implementados:**

1. **UserRepository** ✅
   ```java
   Métodos Gerados (JpaRepository):
   ├── save(User)
   ├── findById(Long) → Optional<User>
   ├── findAll() → List<User>
   ├── delete(User)
   └── count()
   
   Queries Customizadas:
   ├── findByEmail(String) → Optional<User>
   └── findByGoogleId(String) → Optional<User>
   
   Uso: Autenticação OAuth e busca de usuários
   ```

2. **ScenarioRepository** ✅
   ```java
   Métodos Gerados (JpaRepository):
   └── CRUD padrão
   
   Queries Customizadas:
   └── findByTrailId(Long) → List<Scenario>
   
   Uso: Busca cenários de uma trilha
   ```

3. **TrailRepository** ✅
   ```java
   Métodos Gerados (JpaRepository):
   └── CRUD padrão (sem queries customizadas no MVP)
   
   Uso: Busca e criação de trilhas
   ```

4. **ProgressRepository** ✅
   ```java
   Métodos Gerados (JpaRepository):
   └── CRUD padrão
   
   Queries Customizadas:
   ├── findByUserId(Long) → List<Progress>
   └── findByUserIdAndScenarioId(Long, Long) → Optional<Progress>
   
   Uso: Rastreamento de progresso do aluno
   ```

**Nota:** Nenhuma implementação manual - Spring Data JPA gera em tempo de compilação

---

### **Camada: Service** - Business Logic Layer

**Responsabilidade:**
- Implementar lógica de negócio complexa
- Transformações e integrações entre repositories
- Orquestração de múltiplas entidades
- Transações (`@Transactional`)

**Padrão Aplicado:**
```java
@Service
@RequiredArgsConstructor  // Lombok: injeção via construtor
public class XyzService {
    private final XyzRepository xyzRepository;
    
    @Transactional(readOnly = true)
    public Xyz getXyz(Long id) {
        return xyzRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Não encontrado"));
    }
}
```

**Services Implementados:**

#### 1. **ScenarioService** ✅
```
Gerencia Cenários + Validação de Respostas

Método: getScenarioWithXray(Long scenarioId)
├─ Busca cenário no banco via repository
├─ Valida existência (EntityNotFoundException)
├─ Retorna Scenario completa (com xrayData decodificado)
└─ Uso: Controller converte para DTO

Método: compareUserResponse(Long scenarioId, JsonNode input)
├─ Busca cenário
├─ Extrai campo "correct_answer" do quiz (JSONB)
├─ Compara com input.get("answer")
├─ Retorna: Boolean (true/false)
└─ Uso: Validar respostas do aluno

Método: getQuizData(Long scenarioId)
├─ Busca cenário
├─ Retorna apenas campo quiz: JsonNode
└─ Uso: Carregar dados do quiz no frontend

JACKSON TREE MODEL:
• JsonNode permite navegação dinâmica sem POJOs rígidos
• Ideal para estruturas flexíveis de golpes diferentes
```

#### 2. **ProgressService** ✅
```
Gerencia Evolução e Métricas do Aluno

Método: getStudentDashboard(Long userId)
├─ Busca todos os progressos do usuário via repository
├─ Retorna: List<Progress>
└─ Uso: Dashboard pessoal do aluno

Método: startUserProgress(Long userId, Long scenarioId)
├─ Verifica se já existe Progress
├─ Se não: Cria novo com status IN_PROGRESS
├─ Se sim: Retorna o existente
├─ Injeta User e Scenario via getReferenceById (lazy loading)
└─ Uso: Inicia um novo simulador Raio-X

Método: completeScenario(Long userId, Long scenarioId, 
                         BigDecimal score, String feedback, Long timeSpent)
├─ Busca Progress existente (deve ter sido iniciado)
├─ Se não encontrado: EntityNotFoundException
├─ Atualiza campos:
│  ├─ quizScore = score (BigDecimal)
│  ├─ userFeedback = feedback
│  ├─ timeSpent = timeSpent (segundos)
│  ├─ status = ProgressStatus.COMPLETED
│  └─ completedAt = LocalDateTime.now()
├─ Salva no banco
└─ Retorna Progress atualizado

CRUCIAL PARA TCC:
• timeSpent: Análise de engajamento temporal
• quizScore: Métricas de aprendizagem com precisão
• userFeedback: Dados qualitativos de experiência
```

#### 3. **UserService** ✅
```
Gerencia Usuários e Autenticação OAuth

Método: findById(Long id)
├─ Busca usuário por ID
├─ Se não encontrado: EntityNotFoundException
└─ Uso: Validação e busca de perfil

Método: processOAuthPostLogin(String email, String name, String googleId)
├─ Consulta se usuário com googleId já existe
├─ Se encontrado: Retorna usuário existente
├─ Se não encontrado:
│  ├─ Cria novo User
│  ├─ Seta: email, name, googleId
│  ├─ role padrão: ALUNO
│  ├─ createdAt: NOW
│  └─ Salva no banco
└─ Uso: Fluxo pós-autenticação do Google

Método: updateProfile(Long userId, UserUpdateDTO data)
├─ Busca usuário por ID
├─ Atualiza: course, academicLevel
├─ Salva no banco
└─ Uso: Segmentação de dados do TCC
```

#### 4. **TrailService** ✅
```
Gerencia Trilhas e Progresso

Método: findAllActiveTrails()
├─ Retorna: List<Trail> (todas cadastradas)
└─ Uso: Listar trilhas disponíveis

Método: getScenariosByTrail(Long trailId)
├─ Busca todos os cenários da trilha via repository
├─ Retorna: List<Scenario>
└─ Uso: Carregar cenários de uma trilha

Método: calculateTrailProgress(Long userId, Long trailId)
├─ Busca todos os cenários da trilha
├─ Se vazia: Retorna 0.0
├─ Conta quantos cenários o aluno completou
├─ Calcula: (completedCount / totalCount) * 100
├─ Retorna: Double (ex: 75.0 = 75%)
└─ Uso: Mostrar barra de progresso da trilha
```

#### 5. **AdminService** ✅
```
Gerencia Criação de Conteúdo (admin only)

Método: createTrail(@NonNull TrailRequestDTO data) [@Transactional]
├─ Cria novo Trail
├─ Seta: title, description
├─ Salva no banco
├─ Retorna: Trail criado
└─ Uso: Criar nova trilha (admin)

Método: createScenario(@NonNull ScenarioRequestDTO data) [@Transactional]
├─ Busca Trail por ID (data.trailId())
├─ Se não encontrada: EntityNotFoundException
├─ Cria novo Scenario
├─ Seta: titleScenarios, xrayData (JSONB), quiz (JSONB), pillar
├─ Associa à trilha
├─ Salva no banco
├─ Retorna: Scenario criado
└─ Uso: Criar novo cenário com Raio-X

Método: deleteTrail(Long trailId) [@Transactional]
├─ Valida se trilha existe
├─ Se não: EntityNotFoundException
├─ Deleta trilha do banco
└─ Uso: Remover trilha de aprendizagem
```

---

## 5. Fluxo Completo da Requisição (MVC - Layered Architecture)

### Fluxo 1: GET `/api/scenarios/{id}/xray` - Carregar Cenário

```
┌──────────────────────────────────────────────────────────────────────┐
│ 1. CLIENTE (Frontend React)                                           │
│    GET http://localhost:8080/api/scenarios/1/xray                    │
└──────────────────┬───────────────────────────────────────────────────┘
                   │
                   ↓
┌──────────────────────────────────────────────────────────────────────┐
│ 2. SPRING DISPATCHERSERVLET (Routing)                                 │
│    ├─ Mapeia rota para: ScenarioController.getXRay()                 │
│    ├─ Extrai pathVariable: Long id = 1                               │
│    └─ Invoca método da classe controladora                           │
└──────────────────┬───────────────────────────────────────────────────┘
                   │
                   ↓
┌──────────────────────────────────────────────────────────────────────┐
│ 3. SCENARIOCONTROLLER (HTTP Layer)                                    │
│    ├─ getXRay(@PathVariable Long id = 1)                             │
│    ├─ this.scenarioService.getScenarioWithXray(1)                    │
│    ├─ Recebe: Scenario(id=1, title="...", xrayData=[...])            │
│    ├─ Constrói DTO:                                                  │
│    │   new ScenarioDTO(id, title, pillar, xray)                     │
│    └─ Retorna: ResponseEntity.ok(dto)                                │
└──────────────────┬───────────────────────────────────────────────────┘
                   │
                   ↓
┌──────────────────────────────────────────────────────────────────────┐
│ 4. SCENARIOSERVICE (Business Logic)                                   │
│    ├─ getScenarioWithXray(Long id = 1)                               │
│    ├─ this.scenarioRepository.findById(1)                            │
│    ├─ Se não encontrado:                                             │
│    │   ├─ Lança: EntityNotFoundException                             │
│    │   └─ HTTP 404 retornado                                         │
│    └─ Retorna: Scenario entity (com xrayData JsonNode)               │
└──────────────────┬───────────────────────────────────────────────────┘
                   │
                   ↓
┌──────────────────────────────────────────────────────────────────────┐
│ 5. SCENARIOREPOSITORY (Data Access - Spring Data JPA)                │
│    ├─ findById(1)                                                    │
│    ├─ Gera automaticamente:                                          │
│    │   SELECT s FROM Scenario s WHERE s.id = 1                      │
│    ├─ Executa no PostgreSQL                                          │
│    ├─ Jackson desserializa JSONB → JsonNode                          │
│    └─ Retorna: Optional<Scenario>                                    │
└──────────────────┬───────────────────────────────────────────────────┘
                   │
                   ↓
┌──────────────────────────────────────────────────────────────────────┐
│ 6. POSTGRESQL DATABASE                                                │
│    ├─ Busca linha na tabela scenarios WHERE id = 1                   │
│    ├─ Retorna colunas incluindo xray_data (JSONB)                    │
│    └─ Exemplo xray_data armazenado:                                  │
│       {                                                              │
│         "type": "phishing_email",                                   │
│         "sender": "fake@bank.com",                                  │
│         "red_flags": ["misspelled domain", "urgency"],             │
│         "cid_pillars": ["CONFIDENCIALIDADE", "INTEGRIDADE"]       │
│       }                                                              │
└──────────────────┬───────────────────────────────────────────────────┘
                   │
              [Retorno ascendente]
                   ↓
┌──────────────────────────────────────────────────────────────────────┐
│ 7. HTTP Response Serialization (Jackson JSON)                         │
│    ├─ ScenarioDTO convertido para JSON                               │
│    ├─ JsonNode mantido como JSON estruturado                         │
│    └─ Response final:                                                │
│       {                                                              │
│         "id": 1,                                                    │
│         "title": "Email Phishing Detectar",                         │
│         "pillar": "CONFIDENCIALIDADE",                              │
│         "xrayData": {                                               │
│           "type": "phishing_email",                                 │
│           "sender": "fake@bank.com",                                │
│           "red_flags": ["misspelled domain", "urgency"]            │
│         }                                                            │
│       }                                                              │
└──────────────────┬───────────────────────────────────────────────────┘
                   │
                   ↓
┌──────────────────────────────────────────────────────────────────────┐
│ 8. HTTP RESPONSE → CLIENTE                                            │
│    ├─ Status: 200 OK                                                 │
│    ├─ Content-Type: application/json                                 │
│    └─ Body: {...JSON...}                                             │
└──────────────────┬───────────────────────────────────────────────────┘
                   │
                   ↓
┌──────────────────────────────────────────────────────────────────────┐
│ 9. FRONTEND REACT                                                     │
│    ├─ Recebe resposta 200 + JSON                                     │
│    ├─ TanStack Query cache/hydrate                                   │
│    ├─ Renderiza ScenarioDTO no "Modo Inspetor"                      │
│    └─ Exibe xrayData como componentes visuais interativos            │
└──────────────────────────────────────────────────────────────────────┘
```

### Fluxo 2: POST `/api/scenarios/answer` - Validar Resposta + Salvar Progresso

```
┌──────────────────────────────────────────────────────────────────────┐
│ 1. CLIENTE (Frontend React)                                           │
│    POST /api/scenarios/answer                                        │
│    Content-Type: application/json                                    │
│    Body: {                                                           │
│      "scenarioId": 1,                                               │
│      "answers": { "answer": "phishing" }                            │
│    }                                                                  │
└──────────────────┬───────────────────────────────────────────────────┘
                   │
                   ↓
┌──────────────────────────────────────────────────────────────────────┐
│ 2. SPRING DISPATCHERSERVLET (Routing + Desserialização)              │
│    ├─ Mapeia POST para: ScenarioController.postAnswer()              │
│    ├─ Jackson desserializa JSON → AnswerDTO                          │
│    └─ Bean Validation valida @RequestBody                            │
└──────────────────┬───────────────────────────────────────────────────┘
                   │
                   ↓
┌──────────────────────────────────────────────────────────────────────┐
│ 3. SCENARIOCONTROLLER (HTTP Layer)                                    │
│    ├─ postAnswer(@RequestBody AnswerDTO answerData)                  │
│    ├─ AnswerDTO: { scenarioId: 1, answers: {"answer": "phishing"} } │
│    ├─ Chama: scenarioService.compareUserResponse(1, answers)         │
│    ├─ Recebe: Boolean isCorrect = true                               │
│    │                                                                  │
│    ├─ Calcula Score:                                                 │
│    │   ├─ if (isCorrect):                                            │
│    │   │   score = new BigDecimal("100.00")                          │
│    │   └─ else:                                                      │
│    │       score = BigDecimal.ZERO                                   │
│    │                                                                  │
│    ├─ Chama: progressService.completeScenario(                       │
│    │     1,        // userId (fixo até token OAuth)                  │
│    │     1,        // scenarioId                                     │
│    │     score,    // BigDecimal("100.00")                           │
│    │     "Resposta enviada via simulador Raio-X",  // feedback       │
│    │     60L       // timeSpent em segundos                          │
│    │ )                                                               │
│    │                                                                  │
│    └─ Retorna: ResponseEntity.ok("Correto!")                         │
└──────────────────┬───────────────────────────────────────────────────┘
                   │
                   ↓
┌──────────────────────────────────────────────────────────────────────┐
│ 4. SCENARIOSERVICE (Validação de Resposta)                           │
│    ├─ compareUserResponse(1, answers)                                │
│    ├─ Busca Scenario (1) via repository.findById(1)                  │
│    ├─ Extrai: JsonNode quiz = scenario.getQuiz()                     │
│    ├─ Extrai string: quiz.get("correct_answer") → "phishing"         │
│    ├─ Extrai input: answers.get("answer") → "phishing"               │
│    ├─ Compara: "phishing".equalsIgnoreCase("phishing") → true         │
│    └─ Retorna: Boolean true                                          │
└──────────────────┬───────────────────────────────────────────────────┘
                   │
                   ↓
┌──────────────────────────────────────────────────────────────────────┐
│ 5. PROGRESSSERVICE (Salva Progresso com Métricas)                    │
│    ├─ completeScenario(1, 1, BigDecimal("100"), feedback, 60)        │
│    ├─ Busca Progress: progressRepository.findByUserIdAndScenarioId   │
│    │   (1, 1)                                                         │
│    ├─ Se não encontrado: EntityNotFoundException ("Progresso não..." ) │
│    │   └─ HTTP 404 retornado                                         │
│    │  (deve ter sido iniciado com startUserProgress antes!)          │
│    │                                                                  │
│    ├─ Atualiza Progress com métricas CRUCIAIS para TCC:              │
│    │   ├─ progress.setQuizScore(BigDecimal("100.00"))                │
│    │   ├─ progress.setUserFeedback("Resposta enviada...")            │
│    │   ├─ progress.setTimeSpent(60L)      // segundos                │
│    │   ├─ progress.setStatus(ProgressStatus.COMPLETED)               │
│    │   └─ progress.setCompletedAt(LocalDateTime.now())               │
│    │                                                                  │
│    ├─ Salva: progressRepository.save(progress)                       │
│    └─ Retorna: Progress atualizado                                   │
└──────────────────┬───────────────────────────────────────────────────┘
                   │
                   ↓
┌──────────────────────────────────────────────────────────────────────┐
│ 6. PROGRESSREPOSITORY & POSTGRESQL (UPDATE)                          │
│    ├─ Gera SQL UPDATE:                                               │
│    │   UPDATE progress SET                                           │
│    │     quiz_score = 100.00,                                        │
│    │     user_feedback = 'Resposta enviada via simulador Raio-X',   │
│    │     time_spent = 60,                                            │
│    │     status = 'COMPLETED',                                       │
│    │     completed_at = '2026-05-18T14:30:45.123'                   │
│    │   WHERE user_id = 1 AND scenario_id = 1;                        │
│    │                                                                  │
│    └─ Transaction committed no PostgreSQL                            │
└──────────────────┬───────────────────────────────────────────────────┘
                   │
                   ↓
┌──────────────────────────────────────────────────────────────────────┐
│ 7. HTTP RESPONSE → CLIENTE                                            │
│    ├─ Status: 200 OK                                                 │
│    ├─ Body: "Correto!"                                               │
│    │                                                                  │
│    │ OU se errado:                                                    │
│    │ Body: "Incorreto. Tente analisar o Raio-X novamente."           │
│    └─ Content-Type: text/plain                                       │
└──────────────────┬───────────────────────────────────────────────────┘
                   │
                   ↓
┌──────────────────────────────────────────────────────────────────────┐
│ 8. FRONTEND REACT (Real-time Feedback)                                │
│    ├─ Recebe resposta 200 + mensagem de feedback                     │
│    ├─ [Se "Correto"]: Mostra celebração/progresso                   │
│    ├─ [Se "Incorreto"]: Convida a revisar o Raio-X                 │
│    ├─ TanStack Query invalida cache (recarrega dashboard)           │
│    └─ Atualiza visualização do progresso da trilha                   │
└──────────────────────────────────────────────────────────────────────┘
```

---

## 6. Relacionamentos Entre Entidades (ER Conceitual)

```
┌──────────────────────────────────────────────────────────────────┐
│                                                                  │
│                     ┌──────────────┐                             │
│                     │     USER     │                             │
│                     ├──────────────┤                             │
│                     │ id (PK)      │                             │
│                     │ name         │                             │
│                     │ email        │                             │
│                     │ googleId     │ ← OAuth Google              │
│                     │ role         │ {ALUNO, ADMIN}              │
│                     │ createdAt    │ ← TCC Analysis             │
│                     └──────┬───────┘                             │
│                            │                                     │
│                 ┌──────────┴────────────┐                        │
│                 │ 1          (user_id)  │                        │
│                 │ User ────── Progress  │                        │
│                 │            (progress) │                        │
│                 └──────────┬────────────┘                        │
│                            │                                     │
│                            │ N                                   │
│                            │                                     │
│                 ┌──────────┴──────────────────┐                  │
│                 │ scenario_id                 │                  │
│                 │ Progress ────── Scenario    │                  │
│                 │              (scenarios)    │                  │
│                 └──────────┬──────────────────┘                  │
│                            │                                     │
│                            │ N                                   │
│                            │                                     │
│                 ┌──────────┴──────────────────┐                  │
│                 │ trail_id                    │                  │
│                 │ Scenario ────── Trail       │                  │
│                 │              (trails)       │                  │
│                 └──────────┬──────────────────┘                  │
│                            │                                     │
│                            │ 1                                   │
│                 ┌──────────┴────────────┐                        │
│                 │ PILLAR MAPPING ✓     │                        │
│                 └───────────────────────┘                        │
│                Cada Scenario:           │                       │
│         ├─ mapeia 1 CidPillar         │                       │
│         ├─ CONFIDENCIALIDADE          │                       │
│         ├─ INTEGRIDADE                │                       │
│         └─ DISPONIBILIDADE            │                       │
│                                                                  │
│ NOTA: A engenharia reversa ocorre DENTRO de cada Scenario      │
│       via campos JSONB (xrayData, quiz)                        │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

---

## 7. Fluxo de JSON / JSONB e Jackson (Detalhe Técnico)

### Armazenamento e Manipulação

**PostgreSQL JSONB Storage:**
```sql
-- Coluna xray_data armazena estruturas JSON complexas
CREATE TABLE scenarios (
    id BIGSERIAL PRIMARY KEY,
    title_scenario VARCHAR(255) NOT NULL,
    xray_data JSONB NOT NULL,  -- ← Coluna flexível para Raio-X
    quiz JSONB NOT NULL,         -- ← Dados do quiz
    pillar VARCHAR(50) NOT NULL,
    trail_id BIGINT NOT NULL REFERENCES trails(id)
);

-- Exemplo de inserção de cenário Phishing
INSERT INTO scenarios (title_scenario, xray_data, quiz, pillar, trail_id)
VALUES (
    'Email Phishing Detection',
    '{
      "type": "phishing_email",
      "sender": "fake@bank.com",
      "subject": "Urgent: Verify Your Account",
      "red_flags": ["misspelled domain", "urgency", "request password"],
      "cid_analysis": {
        "confidencialidade": "ataque direto - pede credenciais",
        "integridade": "pode alterar dados da conta",
        "disponibilidade": "sem impacto direto"
      }
    }'::jsonb,
    '{
      "question": "Como você classificaria este email?",
      "correct_answer": "phishing",
      "options": ["legitimate", "phishing", "spam"],
      "explanation": "Email suspeito pedindo para verificar conta"
    }'::jsonb,
    'CONFIDENCIALIDADE',
    1
);
```

**Java Object Mapping (Jackson Tree Model):**
```java
// Hibernate 6 + Jackson automático
@JdbcTypeCode(SqlTypes.JSON)
@Column(name = "xray_data", nullable = false)
private JsonNode xrayData;  // ← JsonNode do Jackson

// Na service, você navega facilmente:
ScenarioService.java:
  String senderEmail = scenario.getXrayData()
    .get("sender")
    .asString();  // "fake@bank.com"
  
  JsonNode redFlags = scenario.getXrayData()
    .get("red_flags");  // ["misspelled domain", "urgency"]
  
  String cidAnalysis = scenario.getXrayData()
    .get("cid_analysis")
    .get("confidencialidade")
    .asText();  // "ataque direto - pede credenciais"

// Comparações dinâmicas (sem classes Java rígidas):
if (userInput.get("answer").asString()
    .equalsIgnoreCase(quizData.get("correct_answer").asString())) {
    // Resposta correta!
}
```

**Vantagens do JsonNode:**
- ✅ Sem necessidade de criar `PhishingEmailDTO`, `AnomalyDTO`, `FakeVideoDTO`, etc.
- ✅ Uma mesma coluna suporta múltiplos formatos de golpes
- ✅ Extensível: Adiciona novos tipos sem ALTER TABLE
- ✅ Ideal para "engenharia reversa desconstruída"
- ✅ Flexibilidade máxima para diferentes estruturas de dados

---

## 8. Configuração do Banco de Dados

### application.properties
```ini
spring.application.name=letramento-digital

# Database configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/letramento_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# Hibernate configuration
spring.jpa.hibernate.ddl-auto=validate  # Não altera schema
spring.jpa.show-sql=true                # Log de queries
spring.jpa.properties.hibernate.format_sql=true  # SQL formatado
```

### Docker Compose (Para PostgreSQL - do projeto)
```yaml
# Ver docker-compose.yml do projeto
# Conecta em: localhost:5432
# Database: letramento_db
# Username: ${DB_USERNAME}
# Password: ${DB_PASSWORD}
```

---

## 9. Regras de Negócio (Consolidadas)

### Autenticação & Autorização
- ✅ Exclusivamente via **Google OAuth2.0**
- ✅ Campo `googleId` (sub) é chave primária lógica
- ✅ Email também é unique
- ✅ Papel padrão: `ALUNO`; pode ser elevado para `ADMIN`

### Simulador Raio-X
- ✅ Mapeia **obrigatoriamente** um pilar CIA:
  - `CONFIDENCIALIDADE` → Dados não devem ser acessíveis
  - `INTEGRIDADE` → Dados não devem ser alterados
  - `DISPONIBILIDADE` → Dados devem estar acessíveis
- ✅ Cada Scenario contém `xrayData` (JSONB) com estrutura dinâmica
- ✅ Cada Scenario contém `quiz` (JSONB) com perguntas

### Métricas Acadêmicas (TCC) - CRÍTICO
- ✅ Todo **Progress** registra:
  - `timeSpent` (segundos) → Análise de engajamento
  - `quizScore` (BigDecimal) → Pontuation com precisão
  - `userFeedback` (String) → Dados qualitativos
  - `createdAt` / `completedAt` → Timeline

### Validação de Respostas
- ✅ Via `ScenarioService.compareUserResponse()`
- ✅ Compara: resposta do aluno vs `quiz.correct_answer`
- ✅ Score: 100 se correto, 0 se errado (pode expandir)
- ✅ Feedback imediato salvo em `Progress.userFeedback`

---

## 10. Diretrizes de Codificação (Aplicadas)

### Idioma & Nomenclatura
- ✅ **Código:** Inglês camelCase (`getScenarioWithXray`, `userFeedback`)
- ✅ **SQL/Database:** snake_case (`user_id`, `quiz_score`, `xray_data`)
- ✅ **Comments/Docs:** Português (BR)

### Padrões Aplicados
- ✅ **Injeção de Dependência:** Constructor com `@RequiredArgsConstructor` ou `@AllArgsConstructor`
- ✅ **Mapeamento:** MapStruct (Entity ↔ DTO)
- ✅ **DTOs:** Java Records (imutáveis, type-safe)
- ✅ **Enums:** Kotlin (type-safety em compile-time)
- ✅ **BigDecimal:** Para scores e valores monetários
- ✅ **Validação:** Bean Validation (@Valid, @NotNull, @NotBlank)
- ✅ **Segurança:** Spring Security + OAuth2
- ✅ **Transações:** `@Transactional` em operations de escrita

### Estrutura de Pacotes
```
✅ controller/   → Endpoints REST (5 controllers)
✅ service/      → Lógica de negócio (5 services)
✅ repository/   → Data access (4 repositories)
✅ model/        → JPA entities (4 models)
✅ dto/          → Data transfer objects (7 DTOs)
✅ enums/        → Enumerações type-safe (3 enums Kotlin)
□ config/        → Configurações (Security, WebConfig, Jackson)
□ exception/     → Exception handlers customizados
□ util/          → Classes utilitárias
□ security/      → Filtros JWT, UserDetails
□ transformer/   → MapStruct interfaces (se necessário)
```

---

## 11. Próximas Expansões (Esperadas)

### 1. Configurações Centralizadas (`config/`)
- **SecurityConfig** - Spring Security + OAuth2 + JWT
- **WebConfig** - CORS, message converters
- **JacksonConfig** - Customização JSON serialization

### 2. Tratamento de Exceções (`exception/`)
- **GlobalExceptionHandler** - `@ControllerAdvice`
- **CustomExceptions** - Exceções específicas do domínio
- **ErrorResponse** - DTO padronizado de erros

### 3. Segurança (`security/`)
- **JwtTokenProvider** - Geração e validação de JWT
- **JwtAuthenticationFilter** - Filtro de autenticação
- **UserPrincipal** - Implementação de `UserDetails`

### 4. Mapeadores (`transformer/` ou `mapper/`)
- **MapStruct Interfaces** - Para conversões Entity ↔ DTO

### 5. Testes Automáticos (`test/`)
- **Service Tests** - `@DataJpaTest`
- **Controller Tests** - MockMvc
- **Integration Tests** - End-to-end

### 6. Documentação
- **Swagger/OpenAPI** - Documentação automática
- **README.md** - Setup, variáveis env, como rodar

---

## 12. Checklist de Implementação (MVP vs Esperado)

### ✅ Implementado
- [X] Estrutura de pastas em camadas
- [X] Models (User, Scenario, Trail, Progress)
- [X] Repositories (4 interfaces Spring Data JPA)
- [X] DTOs (7 records)
- [X] Enums (3 Kotlin type-safe)
- [X] Controllers (5 endpoints)
- [X] Services (5 services com lógica)
- [X] Scenario Simulator + Raio-X
- [X] Progress Tracking com métricas
- [X] Google OAuth Flow (até processOAuthPostLogin)
- [X] Trail Management

### ⏳ Esperado / Próximas Fases
- [ ] Security Config (JWT + OAuth2)
- [ ] JWT Token Generation & Validation
- [ ] Global Exception Handler
- [ ] CORS Configuration
- [ ] MapStruct Mappers (formais)
- [ ] Testes Unitários & Integração
- [ ] Swagger/OpenAPI Docs
- [ ] Docker - Nginx + Spring + PostgreSQL
- [ ] CI/CD Pipeline
- [ ] Admin Dashboard
- [ ] Analytics/Reports (TCC)

---

## 13. Stack Completo de Tecnologias (Consolidado)

| Camada | Tecnologia | Versão | Propósito |
|--------|-----------|--------|----------|
| **Java Runtime** | Java SE | 17+ | Runtime moderno com features avançadas |
| **Framework** | Spring Boot | 4.0.6 | Auto-configuration e simplicidade |
| **Build Tool** | Maven | 3.x | Gerenciamento de dependências |
| **ORM** | Hibernate | 6 | JPA nativo com JSONB |
| **Database** | PostgreSQL | Latest | JSONB nativo, integridade referencial |
| **Security** | Spring Security | 6.x | Autenticação e autorização |
| **OAuth** | Spring OAuth2 Client | 6.x | Google OAuth2.0 |
| **JSON** | Jackson | 2.x (embedded) | Tree Model para flexibilidade |
| **Mapeamento** | MapStruct | 1.5.5.Final | Entity ↔ DTO com performance |
| **Boilerplate** | Lombok | Latest | Getters, setters, construtores |
| **Type-Safety** | Kotlin | 2.3.10 | Enums e validações compile-time |
| **Validação** | Bean Validation | 3.x (embedded) | @Valid, @NotNull, etc. |
| **Testes** | JUnit 5 | Embedded | Framework de testes |
| **Mock** | MockMvc | Embedded | Testes de controllers |

---

## 14. Referência de Endpoints (API REST Consolidada)

### Scenarios (Simulador Raio-X)
```
GET    /api/scenarios/{id}/xray         → Carregar cenário com Raio-X
GET    /api/scenarios/{id}/quiz         → Dados do quiz
POST   /api/scenarios/answer            → Validar resposta + Salvar progresso
```

### Users (Perfil)
```
GET    /api/users/{id}                  → Buscar perfil do usuário
PATCH  /api/users/{id}/profile          → Atualizar curso/nível acadêmico
```

### Trails (Trilhas de Aprendizagem)
```
GET    /api/trails                      → Listar todas as trilhas
GET    /api/trails/{id}/scenarios       → Cenários de uma trilha
GET    /api/trails/{id}/progress/{userId} → % de conclusão
```

### Progress (Dashboard)
```
GET    /api/progress/dashboard/{userId} → Dashboard pessoal do aluno
```

### Admin (Gerenciamento de Conteúdo)
```
POST   /api/admin/tails                 → Criar trilha (⚠️ typo: tails)
POST   /api/admin/scenarios             → Criar cenário
DELETE /api/admin/trails/{id}           → Deletar trilha
```

---

## 15. Notas Finais para Desenvolvimento

### CRUCIAL para TCC
Os campos de progresso devem ser **sempre** registrados com precisão:
- `timeSpent` (Long em segundos) - base para análise temporal
- `quizScore` (BigDecimal) - scores acadêmicos sem arredondamento
- `userFeedback` (String) - dados qualitativos de experiência
- `createdAt` / `completedAt` - timeline de engajamento

### JsonNode Strategy
Use **Jackson Tree Model** para máxima flexibilidade. Estruturas diferentes de golpes (Phishing, I.A. Fake, Scams) compartilham a mesma coluna JSONB sem necessidade de criar múltiplas classes Java.

### Constructor Injection
Sempre prefira injeção via construtor com `@RequiredArgsConstructor` (Lombok) ou `@AllArgsConstructor`. Evita NullPointerException e torna testes mais fáceis.

### Transações
Use `@Transactional` em methods que alteram múltiplas entidades. Ex: AdminService.createScenario, ProgressService.completeScenario.

### Validação em DTOs
Sempre use Bean Validation (@Valid, @NotNull, @NotBlank) em @RequestBody. Deixa a validação centralizada e legível.

---

**Nota para I.A.:**
Este Claude.md é sua "Bússola de Contexto" completa. Refira-se a ele antes de implementar novas funcionalidades. Mantenha coesão arquitetural, priorize Java 17 features, MapStruct, BigDecimal, Records e Kotlin type-safety. O sistema deve permanecer leve, escalável e seguro.
```

---