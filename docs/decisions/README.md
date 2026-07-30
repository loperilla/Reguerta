---
id: ADR-0000
title: <Decisión breve y concreta>
status: proposed | accepted | superseded | rejected | deprecated
date: 2025-10-21
authors: [Jesús Franco]
platforms: [shared] # shared | android | ios
scope: [domain | data | presentation | build | ci-cd | ux]
version_range:
  android: [min: "-", max: "-"]
  ios: [min: "-", max: "-"]
links:
  supersedes: []
  superseded_by: []
  related: []
---

## Contexto
¿Qué problema intentamos resolver? ¿Qué fuerzas/limitaciones hay (técnicas, de negocio, UX, plazos)?

## Decisión
La decisión concreta, en presente imperativo.
Ej.: “Introducir `ClockProvider` en domain y proveerlo vía DI; `presentation` usa `BuildConfig.DISPLAY_VERSION` para rotular versión.”

## Consecuencias
- Positivas: …
- Negativas / trade-offs: …
- Costes de migración: …

## Alternativas consideradas
- Alternativa A — pros/contras
- Alternativa B — pros/contras

## Detalles de implementación
- Diagrama rápido / módulos afectados
- Migraciones (nombres de clases/archivos, rutas)
- Feature flags si aplica

## Seguridad / Privacidad (si aplica)
Riesgos, mitigaciones.

## Preguntas abiertas
- …

## Changelog
- 2025-10-21: propuesta inicial.
