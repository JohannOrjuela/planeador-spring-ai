package com.example.chat.planeador.herramientas;

import com.example.chat.planeador.datos.DatosPlaneador;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class ClimaTools {
    private final DatosPlaneador datos;

    public ClimaTools(DatosPlaneador datos) { this.datos = datos; }

    @Tool(description = "Lista los destinos de fin de semana disponibles cerca de Bogotá.")
    public String listarDestinos() {
        return "Destinos disponibles: " + datos.destinosValidos();
    }

    @Tool(description = "Consulta el pronóstico de un destino para el sábado o el domingo.")
    public String consultarPronostico(
            @ToolParam(description = "Nombre del destino") String destino,
            @ToolParam(description = "sabado o domingo") String dia) {
        String clave = datos.buscarDestino(destino);
        if (clave == null) {
            return "No conozco el destino '%s'. Destinos válidos: %s."
                    .formatted(destino, datos.destinosValidos());
        }
        String diaNormalizado = DatosPlaneador.normalizarDia(dia);
        if (diaNormalizado == null) return "Solo tengo pronóstico para 'sabado' o 'domingo'.";
        DatosPlaneador.Pronostico p = datos.pronostico(clave, diaNormalizado);
        return "%s el %s: %s, entre %d °C y %d °C, probabilidad de lluvia %d %%."
                .formatted(datos.destino(clave).nombre(), diaNormalizado,
                        p.cielo(), p.min(), p.max(), p.lluvia());
    }
}

