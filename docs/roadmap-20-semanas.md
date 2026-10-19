# Roadmap de 20 semanas

Este roadmap convierte la planificacion del TFG en hitos verificables. Las
semanas indican esfuerzo relativo y no fechas de calendario. El estado refleja
la evidencia disponible actualmente en el repositorio.

## Leyenda de estado

- **Completado**: existe implementacion y una comprobacion automatizada.
- **En curso**: existe una base funcional, pero falta parte del entregable.
- **Pendiente**: todavia no existe evidencia suficiente para dar el hito por
  cerrado.

## Plan de trabajo y estado actual

| Semanas | Objetivo | Entregable | Estado | Evidencia y criterio de salida |
| --- | --- | --- | --- | --- |
| 1-2 | Diseno, repositorio, dominio, CLI y sandbox | MVP-0 | Completado | Reactor Maven multimodulo, entidades de dominio, `ForgeiaCli` y workspace aislado. `mvn validate` y `mvn -pl forgeia-core -am test` deben pasar. |
| 3-4 | Tools de filesystem, Git y Maven; plantilla Java | nucleo determinista | Completado | Herramientas con allowlist, auditoria y tests; generador de plantilla compilable. `mvn -pl forgeia-tools -am test` debe pasar. |
| 5-6 | `LlmClient`, Requirements Agent y contratos JSON | MVP-1 | Completado | Abstraccion de proveedor, requisitos estructurados, validacion y trazas de uso. `mvn -pl forgeia-llm -am test` debe pasar. |
| 7-8 | Planner, Code Agent y motor de parches | MVP-2 | Completado | Requisito transformado en tareas, propuesta de diff validada y aplicacion segura mediante Git. Las pruebas de contrato y parche deben pasar. |
| 9-10 | Build, tests y Repair Agent | MVP-3 | Completado | `MavenTool`, `TestTool`, normalizacion de errores y reparacion acotada. El E2E debe demostrar un fallo inicial y un build final correcto. |
| 11-13 | AST Java, grafo, Context Compiler y presupuestos | MVP-4 | Completado | Indice de simbolos, dependencias, ranking y degradacion de contexto bajo budget. `ContextCompilerTest` debe verificar seleccion, limite, unicidad y determinismo. |
| 14-15 | Separacion multiagente y blackboard | MVP-5 | Completado | Catalogo de agentes, contratos compartidos, orquestacion y artefactos persistidos en blackboard con transiciones probadas. |
| 16 | Importar y evolucionar un proyecto existente | modo evolucion | Completado | `ExistingProjectImporter` crea un snapshot, obtiene inventario, grafo, build base y resumen; `ImpactAnalyzer` identifica componentes afectados antes de aprobar un cambio. Sus pruebas deben pasar. |
| 17 | UI y visualizacion de tokens y diffs | demo usable | En curso | La consola comprueba la salud real del servidor y existe una frontera `POST /api/runs`. `ForgeiaRunService` ya produce contexto, diff y metricas por `runId`; falta configurar un proveedor y enlazar el formulario con el handler de ejecucion. |
| 18-19 | Benchmarks A/B/C y analisis | resultados TFG | En curso | Existen dataset, planificador, metricas y matriz para `FULL_CONTEXT`, `BASIC_RETRIEVAL` y `FORGEIA_CONTEXT_COMPILER`. Falta ejecutar un lote reproducible y publicar resultados agregados reales. |
| 20 | Pulido, documentacion y demo reproducible | version final | Pendiente | Requiere guia de instalacion limpia, guion de demo ejecutado, resultados de benchmark, evidencias y paquete final etiquetado. |

## Dependencias entre entregables

El camino critico es:

```text
MVP-0 -> nucleo determinista -> MVP-1 -> MVP-2 -> MVP-3
      -> MVP-4 -> MVP-5 -> evolucion -> demo -> evaluacion -> version final
```

Un hito solo se considera cerrado cuando su prueba o comando de aceptacion
termina correctamente. La interfaz puede avanzar en paralelo desde MVP-3, pero
los resultados experimentales dependen del E2E, del Context Compiler y del
registro de metricas.

## Trabajo pendiente priorizado

1. Configurar un adaptador real de `LlmClient` y conectarlo al handler
   `POST /api/runs`; despues enlazar el formulario para ejecutar una tarea y
   consultar contexto, diff, build y tokens.
2. Incorporar un ejecutor de benchmark que materialice cada corrida A/B/C y
   exporte configuracion, resultado, tokens, latencia y estado de tests a JSON
   o CSV.
3. Ejecutar varias repeticiones del dataset, agregar los resultados y generar
   las tablas o graficas empleadas en la memoria del TFG.
4. Preparar una demo reproducible de MathKids que permita crear el proyecto,
   anadir el requisito de medallas y verificar nuevamente build y tests.
5. Probar desde un clon limpio y documentar JDK, Maven, configuracion del
   proveedor LLM, comandos de arranque y limitaciones conocidas.

Si el calendario se retrasa, se reduce primero el alcance visual y el numero de
agentes. Se conservan el flujo E2E, la evaluacion A/B/C y las pruebas
automatizadas, ya que sostienen la aportacion academica.

## Revision semanal

Al final de cada semana se debe registrar:

- hito trabajado y estado (`Pendiente`, `En curso` o `Completado`);
- commit o etiqueta que contiene el entregable;
- comando de aceptacion y resultado;
- riesgos o decisiones abiertas;
- objetivo verificable de la semana siguiente.

La comprobacion global del producto es:

```powershell
mvn clean test
```
