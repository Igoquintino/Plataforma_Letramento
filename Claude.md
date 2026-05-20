# CLAUDE.md — Guia operacional (enxuto e factual)

Propósito
---------
Guia conciso e factual para agentes de IA e desenvolvedores mantenedores do backend "letramento-digital". Contém: estrutura real (branch `main`), responsabilidades das camadas, dependências, convenções, endpoints-chave, instruções de execução e problemas conhecidos com localizações.

1 — Resumo rápido
-----------------
- Backend: Spring Boot (Java 17).
- Banco: PostgreSQL (uso de JSONB para dados dinâmicos).
- Autenticação: Google OAuth2 (cliente presente no POM; configuração completa de segurança ausente).
- Entidades principais: `Scenario` (xrayData JSONB, quiz JSONB), `Trail`, `User`, `Progress`.

2 — Estrutura real (src/main/java)
----------------------------------
````
com/projeto/tcc/letramento/
├── LetramentoDigitalApplication.java
├── controller/
│   ├── AdminController.java
│   ├── ScenarioController.java
│   ├── TrailController.java
│   ├── UserController.java
│   └── progressController.java
├── dto/
│   ├── AnswerDTO.java
│   ├── AuthResponseDTO.java
│   ├── ScenarioDTO.java
│   ├── ScenarioRequestDTO.java
│   ├── TrailRequestDTO.java
│   ├── UserDTO.java
│   └── UserUpdateDTO.java
├── enums/
│   ├── CidPillar.java
│   ├── ProgressStatus.java
│   └── UserRole.java
├── model/
│   ├── Scenario.java
│   ├── Trail.java
│   ├── User.java
│   └── Progress.java
├── repository/
│   ├── ScenarioRepository.java
│   ├── TrailRepository.java
│   ├── UserRepository.java
│   └── ProgressRepository.java
└── service/
├── AdminService.java
├── ScenarioService.java
├── TrailService.java
├── UserService.java
└── ProgressService.java


`src/main/resources/`
- `application.properties`
````
3 — O que cada pacote/camada faz (breve)
----------------------------------------
- `controller/` — camada REST: recebe requests, valida (DTOs), delega para services e retorna respostas.
- `service/` — lógica de negócio: orquestra repositórios, valida regras e manipula `JsonNode`.
- `repository/` — Spring Data JPA: acesso a dados por convenção.
- `model/` — entidades JPA; `Scenario` persiste `JsonNode` em JSONB via Hibernate 6.
- `dto/` — payloads entre frontend e backend.
- `enums/` — enums do domínio (java).

4 — Fluxo de requisição (exemplo)
---------------------------------
GET /api/scenarios/{id}/xray:
Frontend → `ScenarioController.getXRay` → `ScenarioService.getScenarioWithXray` → `ScenarioRepository.findById` (Postgres retorna JSONB) → Service monta `ScenarioDTO` → Controller retorna JSON.

5 — Endpoints chave (resumo)
----------------------------
- Scenarios: GET `/api/scenarios/{id}/xray`, GET `/api/scenarios/{id}/quiz`, POST `/api/scenarios/answer`
- Trails: GET `/api/trails`, GET `/api/trails/{id}/scenarios`, GET `/api/trails/{id}/progress/{userId}`
- Users: GET `/api/users/{id}`, PATCH `/api/users/{id}/profile`
- Progress: GET `/api/progress/dashboard/{userId}`
- Admin: POST `/api/admin/tails` (nota: typo), POST `/api/admin/scenarios`, DELETE `/api/admin/trails/{id}`

6 — Dependências principais (do `pom.xml`)
------------------------------------------
- Spring Boot parent 4.0.6 (Java 17)
- Spring starters:
    - `spring-boot-starter-data-jpa`
    - `spring-boot-starter-security`
    - `spring-boot-starter-security-oauth2-client`
    - `spring-boot-starter-validation`
    - `spring-boot-starter-webmvc`
    - `spring-boot-devtools` (runtime, optional)
- DB driver: `org.postgresql:postgresql` (runtime)
- MapStruct: `org.mapstruct:mapstruct:1.5.5.Final` + `mapstruct-processor` (scope provided)
- Lombok
- Kotlin artifacts presentes no POM, porém não há código Kotlin em `main` (revisar)

7 — Convenções essenciais
-------------------------
- Java: `camelCase` para campos, `PascalCase` para classes.
- SQL: `snake_case` nas colunas (mapeadas explicitamente).
- JSONB: usar `com.fasterxml.jackson.databind.JsonNode` em campos dinâmicos e `@JdbcTypeCode(SqlTypes.JSON)`.
- Score: `BigDecimal` (não float/double).
- DI: constructor injection (`@RequiredArgsConstructor`).
- MapStruct: usar para mapear Entity ↔ DTO quando implementado.

8 — Important implementation notes
---------------------------------
- `Scenario.xrayData` e `Scenario.quiz` são JSONB; use `JsonNode` (Jackson Tree Model) para ler e comparar estruturas dinâmicas.
- Services que modificam múltiplas entidades devem usar `@Transactional`.
- OAuth2 client está no POM, mas falta `SecurityConfig` custom (proteção, roles, JWT se necessário).

9 — Known issues / Urgent fixes (prioridade)
--------------------------------------------
Lista priorizada com caminhos e ação recomendada.

1) Imports Jackson errados (ALTAMENTE PRIORITÁRIO)
- Problema: arquivos usam `tools.jackson.databind.JsonNode`.
- Arquivos afetados:
    - `controller/ScenarioController.java`
    - `dto/ScenarioDTO.java`
    - `dto/AnswerDTO.java`
    - `dto/ScenarioRequestDTO.java`
    - `model/Scenario.java`
    - `service/ScenarioService.java`
- Ação: substituir por
  ```java
  import com.fasterxml.jackson.databind.JsonNode;
  ```