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

