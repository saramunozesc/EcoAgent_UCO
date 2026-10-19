# EcoAgent UCO

Plataforma agentica capaz de generar aplicaciones Java completas a partir de requisitos en lenguaje natural, optimizando deliberadamente el consumo de tokens y contexto.

## Fase 0

Esta fase prepara una base limpia, versionada y reproducible. No incluye uso de IA.

La estructura inicial sigue los puntos 6.1 y 6.2 de la guia de trabajo:

- `forgeia-core`: dominio y orquestacion.
- `forgeia-llm`: adaptadores de modelos.
- `forgeia-analysis`: AST, grafo e indice.
- `forgeia-tools`: filesystem, Git, Maven y tests.
- `forgeia-server`: API REST del harness.
- `forgeia-ui`: interfaz opcional.
- `forgeia-evaluation`: benchmarks y experimentos.
- `docs`: documentacion del proyecto.
- `examples`: ejemplos y fixtures.

## Verificacion inicial

```powershell
mvn validate
```

## CLI minima

```powershell
.\forgeia.ps1 init demo
mvn -pl forgeia-core test
```

## Fase 1

```powershell
mvn -pl forgeia-tools test
```

## Fase 2

```powershell
mvn -pl forgeia-llm -am test
```

## Fase 3

```powershell
mvn -pl forgeia-core,forgeia-llm -am test
```

## Fase 4

```powershell
mvn -pl forgeia-core,forgeia-tools,forgeia-llm -am test
```

## Fase 5

```powershell
mvn -pl forgeia-analysis -am test
```

## Fase 6

```powershell
mvn -pl forgeia-core -am test
```

## Fase 7

```powershell
mvn -pl forgeia-analysis -am test
```

## Fase 8

```powershell
mvn -pl forgeia-server -am test
java -cp forgeia-server/target/classes es.uco.ecoagent.server.ForgeiaWebServer 8080
```

## Fase 9

```powershell
mvn -pl forgeia-evaluation -am test
```

## Modelo de datos

```powershell
mvn -pl forgeia-core,forgeia-analysis -am test
```

## Estructura del repositorio

```powershell
mvn test
```

## APIs internas

```powershell
mvn -pl forgeia-tools,forgeia-agents -am test
```

## Seguridad y control del agente

```powershell
mvn -pl forgeia-tools,forgeia-llm -am test
```

Las herramientas operan dentro de un workspace real, validan la invocacion
completa de Git y Maven, eliminan credenciales del entorno y de la auditoria,
y deniegan por defecto cualquier operacion destructiva que no tenga una
aprobacion explicita. El contenido recuperado de proyectos se delimita como
no confiable antes de enviarlo a un modelo.

## Estrategia de pruebas

```powershell
mvn clean test
```

La suite combina pruebas unitarias, integracion con fixtures Maven, contratos
JSON de agentes, un recorrido E2E desde la idea hasta el build en verde y la
matriz benchmark A/B/C. Los proyectos de prueba versionados cubren CRUD,
validacion, relacion entre entidades, endpoint y un error de compilacion
reparable.

## Roadmap

El [roadmap de 20 semanas](docs/roadmap-20-semanas.md) relaciona cada hito con
su entregable, evidencia verificable y estado actual. Los bloques pendientes se
concentran en integrar la UI con el motor, ejecutar el benchmark A/B/C con
resultados reales y preparar la demo final reproducible.

## Ejecucion integrada

`ForgeiaRunService` conecta en una unica ejecucion requisitos, planificacion,
Context Compiler, Code Agent, aplicacion del parche, Maven/tests y Repair Agent.
Cada `runId` conserva requisitos, trazas, contexto exacto, parche final,
metricas de tokens y resultado bajo `.forgeia/runs/`.

El servidor ofrece `GET /api/health` y una frontera inyectable
`POST /api/runs`. El arranque por defecto mantiene esta ultima deshabilitada
hasta configurar un `LlmClient`; de este modo no se simula un proveedor en
produccion ni se exponen credenciales desde la interfaz.

```powershell
mvn -pl forgeia-orchestrator,forgeia-server -am test
```
