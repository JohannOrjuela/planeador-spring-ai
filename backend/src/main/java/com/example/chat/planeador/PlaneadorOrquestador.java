package com.example.chat.planeador;

import com.example.chat.planeador.Decision.Agente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.example.chat.planeador.datos.DatosPlaneador.normalizar;

/** Orquestador multiagente con patrón supervisor / orchestrator-workers. */
@Service
public class PlaneadorOrquestador {
    private static final Logger log = LoggerFactory.getLogger(PlaneadorOrquestador.class);
    private static final int MAX_PASOS = 3;

    private final ChatClient supervisor;
    private final ChatClient sintetizador;
    private final Map<Agente, ChatClient> especialistas = new EnumMap<>(Agente.class);

    public PlaneadorOrquestador(
            @Qualifier("supervisor") ChatClient supervisor,
            @Qualifier("sintetizador") ChatClient sintetizador,
            @Qualifier("agenteClima") ChatClient agenteClima,
            @Qualifier("agentePresupuesto") ChatClient agentePresupuesto,
            @Qualifier("agenteActividades") ChatClient agenteActividades,
            @Qualifier("agenteReservas") ChatClient agenteReservas) {
        this.supervisor = supervisor;
        this.sintetizador = sintetizador;
        especialistas.put(Agente.CLIMA, agenteClima);
        especialistas.put(Agente.PRESUPUESTO, agentePresupuesto);
        especialistas.put(Agente.ACTIVIDADES, agenteActividades);
        especialistas.put(Agente.RESERVAS, agenteReservas);
    }

    public String planear(String pregunta) {
        List<String> resultados = new ArrayList<>();
        List<Agente> necesarios = analizarNecesidades(pregunta);

        for (Agente agente : necesarios.stream().limit(MAX_PASOS).toList()) {
            ChatClient especialista = especialistas.get(agente);
            if (especialista == null) break;
            String respuesta = especialista.prompt()
                    .user(contexto(pregunta, resultados))
                    .call()
                    .content();
            log.info("[{}] {}", agente, respuesta);
            resultados.add("[" + agente + "] " + respuesta);
        }

        String respuestaFinal = sintetizador.prompt()
                .user(contexto(pregunta, resultados))
                .call()
                .content();
        return respuestaFinal == null ? "" : respuestaFinal;
    }

    private List<Agente> analizarNecesidades(String pregunta) {
        Set<Agente> porPalabrasClave = detectarPorPalabrasClave(pregunta);
        Set<Agente> necesarios = new LinkedHashSet<>();
        try {
            Decision decision = supervisor.prompt()
                    .user("Pregunta del usuario: " + pregunta)
                    .call()
                    .entity(Decision.class);
            if (decision != null && porPalabrasClave.isEmpty()) {
                if (decision.necesitaClima()) necesarios.add(Agente.CLIMA);
                if (decision.necesitaPresupuesto()) necesarios.add(Agente.PRESUPUESTO);
                if (decision.necesitaActividades()) necesarios.add(Agente.ACTIVIDADES);
                if (decision.necesitaReservas()) necesarios.add(Agente.RESERVAS);
            }
        } catch (RuntimeException exception) {
            log.warn("[SUPERVISOR] decisión inválida ({}); se usa la red de seguridad",
                    exception.getMessage());
        }
        if (!porPalabrasClave.isEmpty()) necesarios = porPalabrasClave;
        log.info("[SUPERVISOR] necesita: {}", necesarios.isEmpty() ? "ninguno" : necesarios);
        return List.copyOf(necesarios);
    }

    private static Set<Agente> detectarPorPalabrasClave(String pregunta) {
        String texto = normalizar(pregunta);
        Set<Agente> agentes = new LinkedHashSet<>();
        if (contiene(texto, "clima", "llueve", "lluvia", "llover", "soleado", "temperatura", "pronostico")) {
            agentes.add(Agente.CLIMA);
        }
        if (contiene(texto, "alcanza", "cuesta", "costo", "precio", "presupuesto", "pesos")) {
            agentes.add(Agente.PRESUPUESTO);
        }
        if (contiene(texto, "actividad", "que hacer", "que podemos hacer", "planes", "recomienda")) {
            agentes.add(Agente.ACTIVIDADES);
        }
        if (contiene(texto, "reserv", "cupo")) agentes.add(Agente.RESERVAS);
        return agentes;
    }

    private static boolean contiene(String texto, String... palabras) {
        for (String palabra : palabras) {
            if (texto.contains(palabra)) return true;
        }
        return false;
    }

    private static String contexto(String pregunta, List<String> resultados) {
        String previos = resultados.isEmpty()
                ? "(todavía no hay resultados)"
                : String.join("\n", resultados);
        return "Pregunta del usuario: " + pregunta
                + "\n\nResultados de otros agentes:\n" + previos;
    }
}

