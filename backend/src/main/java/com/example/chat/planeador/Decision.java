package com.example.chat.planeador;

import java.util.List;

/**
 * Lista de verificación estructurada del supervisor.
 *
 * Un modelo pequeño es más estable marcando necesidades independientes que
 * eligiendo repetidamente un único agente siguiente. El orden se aplica en Java.
 */
public record Decision(
        List<String> partes,
        boolean necesitaClima,
        boolean necesitaPresupuesto,
        boolean necesitaActividades,
        boolean necesitaReservas) {
    public enum Agente { CLIMA, PRESUPUESTO, ACTIVIDADES, RESERVAS }
}

