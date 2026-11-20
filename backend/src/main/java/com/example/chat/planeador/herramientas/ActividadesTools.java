package com.example.chat.planeador.herramientas;

import com.example.chat.planeador.datos.DatosPlaneador;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ActividadesTools {
    private final DatosPlaneador datos;

    public ActividadesTools(DatosPlaneador datos) { this.datos = datos; }

    @Tool(description = "Recomienda actividades para hacer en un destino de fin de semana.")
    public String recomendarActividades(
            @ToolParam(description = "Nombre del destino") String destino) {
        String clave = datos.buscarDestino(destino);
        if (clave == null) {
            return "No conozco el destino '%s'. Destinos válidos: %s."
                    .formatted(destino, datos.destinosValidos());
        }
        String lista = datos.actividades(clave).stream()
                .map(a -> "%s (%s, %d h)".formatted(a.nombre(), a.tipo(), a.horas()))
                .collect(Collectors.joining("; "));
        return "Actividades en %s: %s.".formatted(datos.destino(clave).nombre(), lista);
    }
}

