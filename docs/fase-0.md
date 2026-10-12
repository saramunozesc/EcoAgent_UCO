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

