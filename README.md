# Planeador de fin de semana multiagente con Spring AI y Ollama

Implementación en Java del taller del Módulo 3 de Aplicaciones basadas en grandes modelos de lenguaje. Un supervisor coordina agentes de clima, presupuesto, actividades y reservas; después, un sintetizador redacta la respuesta final usando exclusivamente los resultados de los especialistas.

## Arquitectura

```text
pregunta
   |
supervisor
   +--> clima ---------+
   +--> presupuesto ---+--> supervisor --> sintetizador --> respuesta
   +--> actividades ---+
   +--> reservas ------+
```

El supervisor usa salida estructurada (`Decision`), evita repetir especialistas y limita la autonomía a tres agentes por solicitud. Cada especialista es un `ChatClient` con instrucciones y herramientas propias. Los costos, pronósticos, actividades, cupos y reservas se calculan en Java sobre datos ficticios en memoria.

## Requisitos

- Docker Desktop, o Java 21 + Gradle 9 + Ollama.
- El modelo `qwen2.5:3b`, que soporta tool calling.

## Ejecución con Docker

```bash
docker compose -f docker/docker-compose.yml up --build
```

La primera ejecución descarga `qwen2.5:3b` (aproximadamente 1,9 GB). Después estarán disponibles:

- Interfaz: http://localhost:4200
- API: http://localhost:8080/api/v1/chat
- Salud de Ollama: http://localhost:8080/health/llm

Para detener los servicios sin borrar el modelo:

```bash
docker compose -f docker/docker-compose.yml down
```

## Desarrollo local

Con Ollama ejecutándose en `http://localhost:11434`:

```bash
ollama pull qwen2.5:3b
cd backend
gradle bootRun
```

En otra terminal:

```bash
cd frontend
npm ci
npm start
```

El proxy de Angular envía `/api` y `/health` al backend en el puerto 8080.

## Pruebas

Las herramientas se prueban sin modelo y sin iniciar Spring:

```bash
cd backend
gradle test
```

El controlador de depuración permite hablar con un especialista aislado:

```text
GET /api/v1/debug/clima?q=¿Cómo estará Guatavita el domingo?
GET /api/v1/debug/presupuesto?q=Somos 4 con 350000, ¿alcanza para Zipaquirá?
GET /api/v1/debug/actividades?q=¿Qué hacer en Villa de Leyva?
GET /api/v1/debug/reservas?q=Consulta cupos en Suesca el sábado
```

## Contrato del chat

```http
POST /api/v1/chat
Content-Type: application/json

{
  "question": "¿Cómo estará el clima en Guatavita el domingo?",
  "sessionId": "demo-1"
}
```

La respuesta conserva el contrato del frontend:

```json
{
  "requestId": "uuid",
  "answer": "respuesta del planeador",
  "elapsedMs": 1234,
  "status": "ok"
}
```

## Reto elegido

Se implementó el nuevo especialista de actividades. `ActividadesTools.recomendarActividades` consulta tres planes ficticios por destino; el agente, el enum `Decision.Agente`, el supervisor, el controlador de depuración y el orquestador incluyen la nueva capacidad.

## Estructura principal

```text
backend/src/main/java/com/example/chat/
├── ChatController.java
├── ModelService.java
├── impl/
│   ├── SpringAiModelService.java
│   └── AgentesModelService.java
└── planeador/
    ├── datos/DatosPlaneador.java
    ├── herramientas/ClimaTools.java
    ├── herramientas/PresupuestoTools.java
    ├── herramientas/ActividadesTools.java
    ├── herramientas/ReservasTools.java
    ├── agentes/AgentesConfig.java
    ├── AgentesDebugController.java
    ├── Decision.java
    └── PlaneadorOrquestador.java
```

La bitácora editable se encuentra en `docs/Bitacora_Planeador_SpringAI_Editable.docx`.
