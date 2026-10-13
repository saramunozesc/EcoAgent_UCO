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
