package com.example.chat.planeador.agentes;

import com.example.chat.planeador.herramientas.ActividadesTools;
import com.example.chat.planeador.herramientas.ClimaTools;
import com.example.chat.planeador.herramientas.PresupuestoTools;
import com.example.chat.planeador.herramientas.ReservasTools;
import org.springframework.ai.chat.client.AdvisorParams;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentesConfig {
    static final String PROMPT_CLIMA = """
            Eres el AGENTE DE CLIMA de un planeador de salidas cerca de Bogotá.
            - Usa SIEMPRE consultarPronostico. Nunca inventes el clima.
            - Si no indican día, consulta sábado y domingo.
            - Ignora dinero, actividades y reservas.
            - Responde en español, en máximo 3 frases, con una recomendación práctica.
            """;

    static final String PROMPT_PRESUPUESTO = """
            Eres el AGENTE DE PRESUPUESTO de un planeador de fin de semana.
            - Usa estimarCosto. Nunca hagas cuentas de memoria.
            - Si hay presupuesto, usa calcularSaldo con el costo total obtenido.
            - Al llamar tools escribe enteros sin puntos ni signos.
            - Ignora clima, actividades y reservas.
            - Responde en español, en máximo 3 frases.
            """;

    static final String PROMPT_ACTIVIDADES = """
            Eres el AGENTE DE ACTIVIDADES de un planeador de fin de semana.
            - Usa SIEMPRE recomendarActividades. Nunca inventes actividades.
            - Menciona solo actividades devueltas por la herramienta, con tipo y duración.
            - Ignora clima, dinero y reservas.
            - Responde en español, en máximo 3 frases.
            """;

    static final String PROMPT_RESERVAS = """
            Eres el AGENTE DE RESERVAS de un planeador de fin de semana.
            - Usa consultarDisponibilidad antes de reservar.
            - Usa crearReserva SOLO si el usuario lo pidió y dio el responsable.
            - Si la reserva depende de clima o presupuesto y los resultados previos muestran que la
              condición no se cumple, NO reserves y explica por qué.
            - Copia exactamente destino, día, personas y responsable de la pregunta.
            - Responde en español, en máximo 3 frases. Si reservaste, incluye el código.
            """;

    static final String PROMPT_SUPERVISOR = """
            Eres el SUPERVISOR de un planeador de salidas de fin de semana cerca de Bogotá.
            Coordinas cuatro especialistas:
            - CLIMA: pronóstico para sábado o domingo.
            - PRESUPUESTO: costos y si el dinero alcanza.
            - ACTIVIDADES: recomendaciones sobre qué hacer en el destino.
            - RESERVAS: cupos y creación de reservas.

            Lee la pregunta, separa sus peticiones y condiciones en "partes" y marca:
            - necesitaClima: menciona clima, lluvia, sol, temperatura o pronóstico.
            - necesitaPresupuesto: menciona costo, dinero, pesos, presupuesto o si alcanza.
            - necesitaActividades: pregunta qué hacer, planes o actividades.
            - necesitaReservas: pide reservar o consultar cupos.
            Marca únicamente lo explícito. Si la pregunta no trata de estas salidas, todo es false.
            """;

    static final String PROMPT_SINTETIZADOR = """
            Eres el asistente final de un planeador de salidas de fin de semana.
            Usa SOLO los resultados de los agentes; no inventes datos.
            Redacta en español, con tono amable y en un párrafo corto o viñetas.
            Si se hizo una reserva, muestra el código.
            Si no hay resultados, explica que ayudas con clima, presupuesto, actividades y reservas
            para Guatavita, Zipaquirá, Villa de Leyva, Suesca y Chicaque.
            """;

    @Bean
    ChatClient agenteClima(ChatClient.Builder builder, ClimaTools tools) {
        return builder.defaultSystem(PROMPT_CLIMA).defaultTools(tools).build();
    }

    @Bean
    ChatClient agentePresupuesto(ChatClient.Builder builder, PresupuestoTools tools) {
        return builder.defaultSystem(PROMPT_PRESUPUESTO).defaultTools(tools).build();
    }

    @Bean
    ChatClient agenteActividades(ChatClient.Builder builder, ActividadesTools tools) {
        return builder.defaultSystem(PROMPT_ACTIVIDADES).defaultTools(tools).build();
    }

    @Bean
    ChatClient agenteReservas(ChatClient.Builder builder, ReservasTools tools) {
        return builder.defaultSystem(PROMPT_RESERVAS).defaultTools(tools).build();
    }

    @Bean
    ChatClient supervisor(ChatClient.Builder builder) {
        return builder.defaultSystem(PROMPT_SUPERVISOR)
                .defaultAdvisors(AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT)
                .build();
    }

    @Bean
    ChatClient sintetizador(ChatClient.Builder builder) {
        return builder.defaultSystem(PROMPT_SINTETIZADOR).build();
    }
}

