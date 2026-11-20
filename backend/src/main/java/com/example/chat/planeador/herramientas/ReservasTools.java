package com.example.chat.planeador.herramientas;

import com.example.chat.planeador.datos.DatosPlaneador;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class ReservasTools {
    private final DatosPlaneador datos;

    public ReservasTools(DatosPlaneador datos) { this.datos = datos; }

    @Tool(description = "Consulta cuántos cupos quedan para un destino el sábado o el domingo.")
    public String consultarDisponibilidad(
            @ToolParam(description = "Nombre del destino") String destino,
            @ToolParam(description = "sabado o domingo") String dia) {
        String clave = datos.buscarDestino(destino);
        if (clave == null) {
            return "No conozco el destino '%s'. Destinos válidos: %s."
                    .formatted(destino, datos.destinosValidos());
        }
        String diaNormalizado = DatosPlaneador.normalizarDia(dia);
        if (diaNormalizado == null) return "Solo se puede reservar para 'sabado' o 'domingo'.";
        return "%s el %s: quedan %d cupos."
                .formatted(datos.destino(clave).nombre(), diaNormalizado, datos.cupos(clave, diaNormalizado));
    }

    @Tool(description = "Crea una reserva. Úsala SOLO si el usuario pidió reservar y dio responsable.")
    public String crearReserva(
            @ToolParam(description = "Nombre del destino") String destino,
            @ToolParam(description = "sabado o domingo") String dia,
            @ToolParam(description = "Número de personas") int personas,
            @ToolParam(description = "Nombre del responsable") String responsable) {
        String clave = datos.buscarDestino(destino);
        if (clave == null) {
            return "No conozco el destino '%s'. Destinos válidos: %s."
                    .formatted(destino, datos.destinosValidos());
        }
        String diaNormalizado = DatosPlaneador.normalizarDia(dia);
        if (diaNormalizado == null) return "Solo se puede reservar para 'sabado' o 'domingo'.";
        if (personas <= 0) return "El número de personas debe ser mayor que cero.";
        if (responsable == null || responsable.isBlank()) {
            return "Falta el nombre del responsable de la reserva. Pídeselo al usuario.";
        }
        int disponibles = datos.cupos(clave, diaNormalizado);
        if (personas > disponibles) {
            return "No hay cupo suficiente: se piden %d y quedan %d.".formatted(personas, disponibles);
        }
        DatosPlaneador.Reserva reserva = datos.reservar(
                clave, diaNormalizado, personas, responsable.trim());
        return "Reserva confirmada %s: %s, %s, %d personas, a nombre de %s."
                .formatted(reserva.codigo(), datos.destino(clave).nombre(), diaNormalizado,
                        personas, reserva.responsable());
    }
}

