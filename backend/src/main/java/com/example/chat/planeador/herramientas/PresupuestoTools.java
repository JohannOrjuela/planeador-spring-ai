package com.example.chat.planeador.herramientas;

import com.example.chat.planeador.datos.DatosPlaneador;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import static com.example.chat.planeador.datos.DatosPlaneador.pesos;

@Component
public class PresupuestoTools {
    private final DatosPlaneador datos;

    public PresupuestoTools(DatosPlaneador datos) { this.datos = datos; }

    @Tool(description = "Calcula el costo total de un destino para un número de personas.")
    public String estimarCosto(
            @ToolParam(description = "Nombre del destino") String destino,
            @ToolParam(description = "Número de personas") int personas) {
        String clave = datos.buscarDestino(destino);
        if (clave == null) {
            return "No conozco el destino '%s'. Destinos válidos: %s."
                    .formatted(destino, datos.destinosValidos());
        }
        if (personas <= 0) return "El número de personas debe ser mayor que cero.";
        DatosPlaneador.Destino c = datos.destino(clave);
        int total = c.costoPorPersona() * personas;
        return "%s: por persona transporte %s + entrada %s + alimentación %s = %s. "
                .concat("Costo total para %d personas: %s.")
                .formatted(c.nombre(), pesos(c.transporte()), pesos(c.entrada()),
                        pesos(c.alimentacion()), pesos(c.costoPorPersona()), personas, pesos(total));
    }

    @Tool(description = "Compara el presupuesto disponible con el costo total y dice si alcanza.")
    public String calcularSaldo(
            @ToolParam(description = "Presupuesto total del grupo en pesos, sin puntos") int presupuesto,
            @ToolParam(description = "Costo total del plan en pesos, sin puntos") int costoTotal) {
        int saldo = presupuesto - costoTotal;
        if (saldo >= 0) {
            return "Sí alcanza: presupuesto %s - costo %s = sobran %s."
                    .formatted(pesos(presupuesto), pesos(costoTotal), pesos(saldo));
        }
        return "No alcanza: presupuesto %s - costo %s = faltan %s."
                .formatted(pesos(presupuesto), pesos(costoTotal), pesos(-saldo));
    }
}

