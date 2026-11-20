package com.example.chat.planeador;

import com.example.chat.planeador.datos.DatosPlaneador;
import com.example.chat.planeador.herramientas.ActividadesTools;
import com.example.chat.planeador.herramientas.ClimaTools;
import com.example.chat.planeador.herramientas.PresupuestoTools;
import com.example.chat.planeador.herramientas.ReservasTools;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HerramientasTest {
    private final DatosPlaneador datos = new DatosPlaneador();

    @Test
    void pronosticoAceptaTildesYMayusculas() {
        String resultado = new ClimaTools(datos).consultarPronostico("Guatavita", "Domingo");
        assertTrue(resultado.contains("lluvioso"), resultado);
    }

    @Test
    void costoDeZipaquiraParaCuatro() {
        String resultado = new PresupuestoTools(datos).estimarCosto("Zipaquirá", 4);
        assertTrue(resultado.contains("$400.000"), resultado);
    }

    @Test
    void saldoNegativoDiceCuantoFalta() {
        String resultado = new PresupuestoTools(datos).calcularSaldo(350000, 400000);
        assertTrue(resultado.contains("faltan $50.000"), resultado);
    }

    @Test
    void noReservaSinCupos() {
        String resultado = new ReservasTools(datos).crearReserva("Chicaque", "sabado", 2, "Ana");
        assertTrue(resultado.startsWith("No hay cupo"), resultado);
    }

    @Test
    void destinoDesconocidoListaLosValidos() {
        String resultado = new ClimaTools(datos).consultarPronostico("Medellín", "sabado");
        assertTrue(resultado.contains("Destinos válidos"), resultado);
    }

    @Test
    void actividadesIncluyenLosPozosAzules() {
        String resultado = new ActividadesTools(datos).recomendarActividades("Villa de Leyva");
        assertTrue(resultado.contains("Pozos Azules"), resultado);
    }
}
