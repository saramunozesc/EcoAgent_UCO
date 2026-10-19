# Fase 0 Preparacion del proyecto

## 6.1 Objetivo

El objetivo de esta fase es disponer de una base limpia, versionada y reproducible para el desarrollo del harness. Esta fase no utiliza IA: solo prepara la organizacion del repositorio y los limites iniciales del proyecto.

## 6.2 Repositorio

La estructura base queda organizada asi:

```text
EcoAgent_UCO/
|-- forgeia-core/        # dominio y orquestacion
|-- forgeia-llm/         # adaptadores de modelos
|-- forgeia-analysis/    # AST, grafo e indice
|-- forgeia-tools/       # filesystem, git, Maven y tests
|-- forgeia-server/      # API REST del harness
|-- forgeia-ui/          # interfaz React/Electron opcional
|-- forgeia-evaluation/  # benchmarks y experimentos
|-- docs/
`-- examples/
```

Los modulos Java se declaran desde el `pom.xml` raiz para poder ejecutar validaciones Maven de forma conjunta.

## 6.3 Primeras tareas

Estado actual:

- Repositorio Git creado y enlazado con GitHub.
- Modulos Maven declarados desde el `pom.xml` raiz.
- Configuracion externa en `config/forgeia.properties`.
- Sandbox de escritura configurado en `workspace/`.
- Logging estructurado JSONL en `logs/`, con un `runId` por ejecucion.
- Modelos de dominio basicos en `forgeia-core`.
- CLI minima disponible con `forgeia init <nombre>` mediante el script `forgeia.ps1`.

Ejemplo:

```powershell
.\forgeia.ps1 init demo
```

## 6.4 Criterio de salida

La fase se considera cerrada cuando:

- El comando `forgeia init <nombre>` crea un workspace vacio dentro del sandbox.
- La ejecucion queda registrada en un fichero JSONL con `runId`.
- Los tests de `forgeia-core` pasan sin usar IA.

Verificacion:

```powershell
mvn -pl forgeia-core test
```

# Fase 1 Nucleo determinista

## 7.1 Tool API minima

La primera version de la API de herramientas esta en `forgeia-tools` e incluye:

- `listFiles`: lista ficheros dentro del workspace.
- `readFile`: lee un fichero completo o un rango de lineas.
- `writeFile`: escribe contenido UTF-8 y devuelve hash SHA-256.
- `applyPatch`: aplica un diff unificado mediante `git apply`.
- `runMaven`: ejecuta objetivos Maven permitidos.
- `runTests`: ejecuta tests Maven con selector opcional.
- `gitDiff`: devuelve un diff acotado del workspace.
- `searchText`: busca texto en ficheros del workspace.

## 7.2 Restricciones iniciales

- Todas las rutas pasan por una politica de workspace autorizado.
- Los comandos se ejecutan con `ProcessBuilder`, no con shell libre.
- Solo se permiten comandos en allowlist: `git`, `mvn` y `mvn.cmd`.
- La salida de comandos se resume para evitar logs enormes.
- Maven, tests y patches tienen timeout.

## 7.3 Generador de plantilla

`SpringBootTemplateGenerator` crea una plantilla Spring Boot conocida con:

- `pom.xml`
- endpoint `/api/health`
- entidad, repositorio y servicio `Task`
- configuracion de base de datos
- test del endpoint de health

Verificacion:

```powershell
mvn -pl forgeia-tools test
```

# Fase 2 Primer LLM y generacion de requisitos

## 8.1 Interfaz de modelo

`forgeia-llm` define la interfaz `LlmClient` con DTOs `LlmRequest` y `LlmResponse`. El resto del sistema consume esa abstraccion y no conoce SDKs concretas de proveedores.

## 8.2 Requirements Agent

`RequirementsAgent` convierte una idea en una especificacion JSON validada. En esta fase no genera codigo.

## 8.3 Validacion

- Valida que el JSON tenga `projectName`, `actors` y `requirements`.
- Normaliza prioridades a `MUST`, `SHOULD` o `COULD`.
- Ignora IDs propuestos por el LLM y asigna `REQ-001`, `REQ-002`, etc.
- Reintenta una vez si la primera respuesta no es parseable.
- Persiste `requirements.json` y `requirements-trace.jsonl` con prompt, modelo, tokens y respuesta.

## 8.4 Criterio de salida

Verificacion:

```powershell
mvn -pl forgeia-llm -am test
```

# Fase 3 Ejecutor de requisitos

## 9.1 Planner

`RequirementPlanner` divide un requisito aprobado en tareas pequenas y validables: entidad, repositorio, servicio, endpoint y tests. Cada tarea incluye ID determinista, dependencias y archivos objetivo probables.

## 9.2 Maquina de estados

`TaskStateMachine` aplica el flujo:

```text
PENDING -> PLANNED -> READY -> RUNNING -> VERIFYING -> DONE
                                     |             |
                                     +-> FAILED <--+
```

Cada transicion produce un `TaskTransition` auditable con estado anterior, estado nuevo, motivo y fecha.

## 9.3 Primer Code Agent

`CodeAgent` recibe una `PlannedTask`, instrucciones de arquitectura y contexto minimo. Su salida es un `PatchProposal` estructurado; no escribe archivos ni aplica cambios.

Verificacion:

```powershell
mvn -pl forgeia-core,forgeia-llm -am test
```

# Fase 4 Build tests y reparacion

## 10.1 Verificacion determinista

`PatchVerifier` aplica un patch con `git apply` y ejecuta `mvn test` mediante las herramientas deterministas. El resultado queda en `DONE` solo cuando Maven devuelve exit code 0; si falla, queda en `FAILED`.

## 10.2 Normalizador de errores

`ErrorNormalizer` compacta la salida de Maven en `NormalizedError`, extrayendo tipo, fichero, linea, mensaje, simbolo relacionado y test afectado cuando se puede inferir.

## 10.3 Reparacion limitada

`RepairPolicy` limita los intentos de reparacion. `RepairAgent` recibe solo el diff previo y los errores normalizados, y devuelve un `PatchProposal`; no aplica cambios ni ejecuta comandos. Si se agotan intentos, la maquina de estados puede pasar a `NEEDS_REVIEW`.

Verificacion:

```powershell
mvn -pl forgeia-core,forgeia-tools,forgeia-llm -am test
```

# Fase 5 Context Compiler y ahorro de tokens

## 11.1 Problema

`ContextCompiler` transforma una tarea y un proyecto Java en un `ContextSlice` pequeno, evitando enviar el proyecto completo al agente.

## 11.2 Indice AST de Java

`JavaAstIndexer` construye resumenes por fichero sin LLM: paquete, tipo principal, interfaces, imports/dependencias, anotaciones y metodos. Es un indexador ligero para el MVP y puede reemplazarse por JavaParser cuando el entorno use Java 21.

## 11.3 Grafo de dependencias

`DependencyGraphBuilder` crea un grafo en memoria con nodos de fichero/simbolo y aristas `declares`, `uses` e `implements`.

## 11.4 Seleccion inicial

`ContextRanker` aplica la formula:

```text
score = 0.40 semanticSimilarity
      + 0.25 dependencyProximity
      + 0.20 requirementRelation
      + 0.15 recentModification
```

## 11.5 Niveles

El compilador selecciona contexto en niveles:

- `L0_METADATA`
- `L1_SUMMARY`
- `L2_SNIPPET`
- `L3_FULL_FILE`

Si no cabe el fichero completo, baja a snippet, summary o metadata.

## 11.6 Presupuesto

El presupuesto se toma de `TaskBudget.maxContextTokens`. Los candidatos se ordenan por score y se incorporan hasta agotar presupuesto.

Verificacion:

```powershell
mvn -pl forgeia-analysis -am test
```

# Fase 6 Multiagente

## Agentes

`AgentCatalog` registra los roles especializados:

- `REQUIREMENTS`: produce `RequirementSpec`.
- `ARCHITECT`: produce `ArchitectureDecision`.
- `BACKEND`: produce `PatchProposal`.
- `FRONTEND`: produce `PatchProposal`.
- `TEST`: produce `TestPatch`.
- `REVIEWER`: produce `ReviewReport`.
- `REPAIR`: produce `RepairPatch`.

## 12.1 Blackboard

`Blackboard` almacena artefactos estructurados en `blackboard/`:

- `requirements.json`
- `architecture.json`
- `decisions.json`
- `api-contract.yaml`
- `project-graph.json`
- `task-status.json`
- `metrics.jsonl`

Los agentes comparten estos artefactos en lugar de mantener conversaciones largas.

## 12.2 Orquestacion

`MultiAgentOrchestrator` coordina el flujo con una maquina de estados explicita. Las transiciones se registran con `OrchestrationTransition` y apuntan al artefacto producido o consumido.

Verificacion:

```powershell
mvn -pl forgeia-core -am test
```

# Fase 7 Proyecto existente y evolucion

## 13.1 Importacion

`ExistingProjectImporter` valida que el directorio contiene `pom.xml`, crea una rama snapshot `forgeia/import-...`, escanea ficheros, construye indice Java, grafo de dependencias y ejecuta `mvn -q test` antes de tocar codigo.

El resumen persistente se guarda en:

```text
.forgeia/project-summary.json
```

Incluye modulos Maven detectados, endpoints, entidades, dependencias y resultado del build base.

## 13.2 Nuevo requisito

`ImpactAnalyzer` recibe un `ProjectSummary` y el texto del nuevo requisito. Devuelve un `ImpactAnalysis` con componentes afectados y `requiresApproval=true`, de forma que ForgeIA pueda mostrar el impacto antes de aplicar parches.

## 13.3 Trazabilidad

`TraceStore` escribe enlaces JSONL entre artefactos:

- `Requirement` -> `Task`, `Commit`, `Diff`, `TestResult`.
- `Task` -> `ContextSlice`, `LlmUsage`, `File`.
- `Patch` -> agente, modelo y validacion.
- `TestResult` -> requisito y build.
- `LlmUsage` -> task, run y modelo.

Verificacion:

```powershell
mvn -pl forgeia-analysis -am test
```

# Fase 8 Interfaz visual

## 14.1 Pantallas minimas

La interfaz vive en `forgeia-server/src/main/resources/static` y se sirve con `ForgeiaWebServer`.

Pantallas incluidas:

- Dashboard de proyectos.
- Crear proyecto desde una idea.
- Lista de requisitos con estados.
- Detalle de requisito con plan, agentes, contexto, tokens y resultados.
- Vista de diff antes/despues.
- Build/tests.
- Metricas de consumo y presupuesto.
- Nuevo requisito sobre proyecto existente con impacto previo.

## 14.2 Pantalla de defensa

La pantalla `Detalle REQ` muestra el caso `REQ-014 Sistema de logros` con:

- Task actual `TASK-014-3 Implementar AchievementService`.
- Agente activo `BackendAgent`.
- Context Slice `4,120 / 8,000 tokens`.
- Build `PASS`, tests `18/18`, reparaciones `0`.
- Tokens totales de proyecto `63,842` frente a presupuesto `180,000`.

Ejecucion local:

```powershell
java -cp forgeia-server/target/classes es.uco.ecoagent.server.ForgeiaWebServer 8080
```

Verificacion:

```powershell
mvn -pl forgeia-server -am test
```

# Fase 9 Metricas y evaluacion experimental

## 15.1 Registro de metricas

`MetricsRecorder` escribe metricas JSONL en `.forgeia/metrics.jsonl`.

Metricas cubiertas:

- `input_tokens` y `output_tokens` por llamada LLM.
- `context_tokens` y `context_items_used` por tarea.
- numero de llamadas por tarea, requisito y proyecto.
- latencia y coste estimado por llamada.
- `build_success`, `tests_passed`, `tests_failed`.
- `repair_attempts`, `requirements_completed`.
- criterios de aceptacion cumplidos y cambios no relacionados.

`MetricsAggregator` resume los registros en `ProjectMetricsSummary` para calcular totales y ratio de build correcto.

## 15.2 Experimento recomendado

`ExperimentPlanner` crea un plan A/B/C para cada requisito:

- `FULL_CONTEXT`: contexto amplio.
- `BASIC_RETRIEVAL`: busqueda textual o semantica top-k.
- `FORGEIA_CONTEXT_COMPILER`: AST, dependencias, relevancia y presupuesto.

## 15.3 Metricas de calidad

La evaluacion puede comparar build correcto, tests superados, criterios cumplidos, reparaciones, cambios no relacionados, tokens, latencia y coste estimado.

## 15.4 Dataset benchmark

`BenchmarkDatasetFactory` genera un dataset inicial controlado con 5 aplicaciones y 10 requisitos, cada uno con criterios de aceptacion automatizables.

Verificacion:

```powershell
mvn -pl forgeia-evaluation -am test
```

# Modelo de datos y artefactos internos

## 16.1 Entidades principales

El modelo interno queda repartido entre `forgeia-core` y `forgeia-analysis`:

- `Project`: `id`, `name`, `workspace`, `stack`, `status`, `createdAt`.
- `Requirement`: `id`, `title`, `description`, `acceptanceCriteria`, `priority`, `status`.
- `Task`: `id`, `requirementId`, `type`, `status`, `dependencies`, `budget`.
- `ContextSlice`: `taskId`, `items`, `tokenCount`, `strategy`, `score`.
- `AgentRun`: `id`, `agentType`, `taskId`, `startedAt`, `result`.
- `LlmUsage`: `provider`, `model`, `inputTokens`, `outputTokens`, `latencyMillis`, `estimatedCost`.
- `Patch`: `taskId`, `diff`, `files`, `hash`, `applied`.
- `ValidationRun`: `buildStatus`, `testsPassed`, `testsFailed`, `errors`, `durationMillis`.

## 16.2 Estados de Requirement

`RequirementStateMachine` controla el ciclo:

```text
DRAFT -> APPROVED -> PLANNED -> IN_PROGRESS -> VERIFYING -> DONE
                                           |
                                           +-> BLOCKED / NEEDS_REVIEW
```

Las transiciones quedan registradas como `RequirementTransition`.

Verificacion:

```powershell
mvn -pl forgeia-core,forgeia-analysis -am test
```

# Estructura recomendada del repositorio

El reactor Maven queda preparado con estos modulos:

```text
forgeia/
├── pom.xml
├── forgeia-domain/
├── forgeia-core/
├── forgeia-orchestrator/
├── forgeia-llm/
├── forgeia-analysis/
├── forgeia-tools/
├── forgeia-agents/
├── forgeia-server/
├── forgeia-ui/
├── forgeia-evaluation/
└── docs/
```

## 17.1 Dependencias entre modulos

Reglas actuales:

- `forgeia-domain` contiene las entidades principales y no depende de SDKs, herramientas ni LLMs.
- `forgeia-core` depende de `forgeia-domain` para configuracion, workspace, logging, blackboard y contratos centrales.
- `forgeia-tools` depende de `forgeia-core`; encapsula Git, Maven, filesystem y patch.
- `forgeia-llm` depende de interfaces y modelos internos, pero no obliga al resto del sistema a conocer una SDK concreta.
- `forgeia-analysis` usa `core` y `tools` para importacion, AST, grafo, busqueda y contexto.
- `forgeia-orchestrator`, `forgeia-agents` y `forgeia-ui` quedan como modulos frontera para extraer responsabilidades desde `core`, `llm` y `server` sin ciclos.

Compatibilidad:

El paquete Java `es.uco.ecoagent.core.domain` se conserva aunque viva en `forgeia-domain`, para evitar reescrituras masivas y mantener estable el codigo ya implementado.

Verificacion:

```powershell
mvn test
```

# APIs y contratos internos

## 18.1 Contrato de agente

`Agent<I, O>` vive en `forgeia-agents`:

```java
interface Agent<I, O> {
    O execute(AgentContext context, I input);
}
```

`AgentContext` contiene `projectId`, `taskId`, `tokenBudget`, `toolPermissions`, `contextSlice` y `traceId`.

## 18.2 Contrato de herramienta

`Tool<I, O>` ya se mantiene en `forgeia-tools`:

```java
interface Tool<I, O> {
    String name();
    O execute(ToolExecutionContext ctx, I input);
}
```

`AuditedTool` envuelve cualquier herramienta para registrar:

- entrada sanitizada.
- duracion.
- resultado.
- errores.
- archivos afectados.

## 18.3 Salidas estructuradas

`StructuredOutputValidator<T>` permite validar DTOs o salidas con esquema antes de aplicar efectos en el workspace. Si la validacion devuelve `ValidationOutcome.invalid(...)`, el executor debe rechazar la propuesta.

Verificacion:

```powershell
mvn -pl forgeia-tools,forgeia-agents -am test
```

# Seguridad y control del agente

## 19.1 Workspace aislado

`WorkspacePathPolicy` rechaza rutas vacias, absolutas y recorridos con `..`.
Tambien resuelve el ancestro existente mas cercano para impedir que enlaces
simbolicos o junctions redirijan una lectura o escritura fuera del workspace.

## 19.2 Comandos y entorno

`CommandAllowlist` valida la invocacion completa, no solo el ejecutable:

- Git queda limitado a `diff`, aplicacion de parches por stdin y creacion de la
  rama de snapshot usada al importar un proyecto.
- Maven queda limitado a fases conocidas y a selectores y modulos con formato
  seguro. No se permiten plugins arbitrarios ni propiedades adicionales.
- `CommandRunner` utiliza `ProcessBuilder` sin shell, mantiene timeout y salida
  acotada, y elimina del proceso hijo variables de entorno que puedan contener
  tokens, contrasenas, secretos o claves.

## 19.3 Auditoria sin secretos

`InputSanitizer` oculta credenciales habituales en entradas, resultados y
errores auditados. Cubre pares clave-valor, bearer tokens, tokens de GitHub,
claves OpenAI, identificadores AWS y bloques de clave privada. El texto
registrado continua estando limitado en longitud.

## 19.4 Operaciones destructivas

`DestructiveOperationPolicy` deniega todas las operaciones por defecto. La capa
de orquestacion debe obtener confirmacion humana y crear una aprobacion para el
identificador exacto de la operacion. Aprobar una operacion no autoriza otras.
Los futuros tools de borrado, sobrescritura irreversible o actuacion fuera del
workspace deben llamar a `ToolExecutionContext.requireDestructiveApproval`
inmediatamente antes de ejecutar el efecto.

## 19.5 Prompt injection en proyectos importados

`UntrustedProjectContent` separa la politica del agente del texto recuperado.
`CodeAgent` y `RepairAgent` incluyen una instruccion inmutable que obliga a
tratar README, comentarios, diffs, errores y codigo como evidencia tecnica, y
envuelven ese contenido entre delimitadores de datos no confiables. Una orden
incluida en el repositorio no puede convertirse en instruccion del sistema.

## 19.6 Criterio de salida

El apartado queda verificable cuando:

- ninguna ruta puede escapar lexicalmente ni mediante un enlace;
- cada proceso coincide con una invocacion autorizada y recibe un entorno sin
  credenciales;
- los registros de tool calls no contienen secretos comunes;
- una operacion destructiva sin aprobacion exacta falla de forma segura;
- el contexto procedente del proyecto llega delimitado al LLM;
- toda la suite Maven permanece en verde.

Verificacion:

```powershell
mvn -pl forgeia-tools,forgeia-llm -am test
mvn test
```

# Estrategia de pruebas

## 20.1 Piramide del propio ForgeIA

La suite se organiza en cinco niveles ejecutables con `mvn test`:

| Nivel | Cobertura implementada |
| --- | --- |
| Unitarias | presupuestos y niveles de contexto, ranking, maquinas de estados, rutas, allowlist, parsers y sanitizacion |
| Integracion | `MavenTool` compila un proyecto fixture real; `JavaAstIndexer` extrae sus simbolos; `ApplyPatchTool` aplica un diff real mediante Git |
| Contract | `AgentOutputContractTest` acepta JSON valido y rechaza requisitos o parches que incumplen el esquema |
| E2E | `ForgeiaEndToEndTest` recorre idea, requisitos, plan, propuesta de codigo, parche, build y tests |
| Benchmark | `ContextStrategyBenchmarkTest` garantiza que cada caso se ejecuta con `FULL_CONTEXT`, `BASIC_RETRIEVAL` y `FORGEIA_CONTEXT_COMPILER` |

Las pruebas que invocan Maven trabajan sobre copias temporales de los fixtures,
por lo que no dejan artefactos de build dentro de `src/test/resources`.

## 20.2 Fixtures

Los fixtures versionados son:

- `forgeia-analysis/src/test/resources/fixtures/task-crud`: proyecto Java
  compilable con entidad `Task`, validacion de titulo, relacion `ManyToOne` con
  `User`, repositorio, servicio CRUD y controlador con endpoints.
- `forgeia-llm/src/test/resources/fixtures/priority-e2e`: proyecto Maven con un
  error conocido porque falta `Priority`. El flujo E2E normaliza el fallo,
  genera y aplica el parche que incorpora el enum y confirma que el test de
  aceptacion termina en verde.

Las anotaciones del fixture CRUD son locales y deliberadamente pequenas. Esto
permite probar AST, grafo y Maven sin descargar Spring Boot o una base de datos.

## 20.3 Tests del Context Compiler

`ContextCompilerTest` verifica explicitamente que:

- el simbolo o archivo objetivo aparece primero cuando es conocido;
- `tokenCount` nunca supera `maxContextTokens`;
- un archivo solo puede aparecer una vez en el `ContextSlice`;
- la seleccion degrada `L3_FULL_FILE` -> `L2_SNIPPET` -> `L1_SUMMARY` ->
  `L0_METADATA` al reducir el presupuesto;
- dos compilaciones con la misma entrada producen las mismas rutas, niveles,
  contenidos, tokens y puntuaciones.

Para garantizar el ultimo punto, los empates de ranking se ordenan por la ruta
normalizada del archivo en vez de depender del orden devuelto por el sistema de
ficheros.

## 20.4 Criterio de salida

El apartado queda terminado cuando una compilacion limpia supera los cinco
niveles de la piramide y el E2E demuestra tanto el fallo inicial como la
reparacion posterior:

```powershell
mvn clean test
```

# Roadmap de 20 semanas

## 21.1 Planificacion y seguimiento

El calendario completo, sus dependencias, criterios de salida y el estado
contrastado con la implementacion se mantienen en
[`roadmap-20-semanas.md`](roadmap-20-semanas.md).

El diagnostico actual situa como completados los hitos MVP-0 a MVP-5 y el modo
de evolucion. La UI y la infraestructura experimental existen, pero aun deben
conectarse a ejecuciones reales y producir resultados A/B/C reproducibles. Por
ello, la continuacion prioritaria corresponde a las semanas 17 a 20, no a
rehacer los hitos tecnicos anteriores.

## 21.2 Criterio de salida

El roadmap se considera operativo cuando cada franja de semanas dispone de un
entregable, evidencia en el repositorio, comando o prueba de aceptacion y estado
explicito. El estado solo cambia a `Completado` despues de validar esa evidencia.

