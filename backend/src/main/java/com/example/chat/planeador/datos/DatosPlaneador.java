package com.example.chat.planeador.datos;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/** Datos ficticios en memoria para que las respuestas se puedan verificar a mano. */
@Component
public class DatosPlaneador {
    public record Destino(String nombre, int transporte, int entrada, int alimentacion) {
        public int costoPorPersona() { return transporte + entrada + alimentacion; }
    }

    public record Pronostico(String cielo, int min, int max, int lluvia) {}
    public record Reserva(String codigo, String destino, String dia, int personas, String responsable) {}
    public record Actividad(String nombre, String tipo, int horas) {}

    private final Map<String, Destino> destinos = new LinkedHashMap<>();
    private final Map<String, String> alias = new LinkedHashMap<>();
    private final Map<String, Pronostico> pronosticos = new LinkedHashMap<>();
    private final Map<String, Integer> cupos = new ConcurrentHashMap<>();
    private final Map<String, List<Actividad>> actividades = new LinkedHashMap<>();
    private final List<Reserva> reservas = new ArrayList<>();

    public DatosPlaneador() {
        destinos.put("guatavita", new Destino("Laguna de Guatavita", 30000, 25000, 35000));
        destinos.put("zipaquira", new Destino("Catedral de Sal de Zipaquirá", 20000, 50000, 30000));
        destinos.put("villa de leyva", new Destino("Villa de Leyva", 70000, 0, 45000));
        destinos.put("suesca", new Destino("Rocas de Suesca", 25000, 15000, 30000));
        destinos.put("chicaque", new Destino("Parque Natural Chicaque", 20000, 35000, 35000));

        alias.put("laguna", "guatavita");
        alias.put("catedral", "zipaquira");
        alias.put("zipa", "zipaquira");
        alias.put("leiva", "villa de leyva");
        alias.put("leyva", "villa de leyva");
        alias.put("rocas", "suesca");

        pronosticos.put("guatavita|sabado", new Pronostico("parcialmente nublado", 8, 18, 30));
        pronosticos.put("guatavita|domingo", new Pronostico("lluvioso", 7, 15, 80));
        pronosticos.put("zipaquira|sabado", new Pronostico("nublado", 9, 19, 40));
        pronosticos.put("zipaquira|domingo", new Pronostico("soleado", 8, 20, 10));
        pronosticos.put("villa de leyva|sabado", new Pronostico("soleado", 12, 25, 5));
        pronosticos.put("villa de leyva|domingo", new Pronostico("soleado", 11, 24, 10));
        pronosticos.put("suesca|sabado", new Pronostico("soleado", 7, 21, 15));
        pronosticos.put("suesca|domingo", new Pronostico("tormentas en la tarde", 8, 17, 70));
        pronosticos.put("chicaque|sabado", new Pronostico("neblina en la mañana", 10, 19, 50));
        pronosticos.put("chicaque|domingo", new Pronostico("parcialmente nublado", 10, 20, 35));

        cupos.put("guatavita|sabado", 12);
        cupos.put("guatavita|domingo", 4);
        cupos.put("zipaquira|sabado", 30);
        cupos.put("zipaquira|domingo", 25);
        cupos.put("villa de leyva|sabado", 2);
        cupos.put("villa de leyva|domingo", 10);
        cupos.put("suesca|sabado", 8);
        cupos.put("suesca|domingo", 6);
        cupos.put("chicaque|sabado", 0);
        cupos.put("chicaque|domingo", 15);

        actividades.put("guatavita", List.of(
                new Actividad("Sendero guiado a la laguna sagrada", "senderismo", 2),
                new Actividad("Recorrido por el pueblo y su plaza de artesanías", "cultura", 1),
                new Actividad("Almuerzo de trucha frente al embalse de Tominé", "gastronomía", 1)));
        actividades.put("zipaquira", List.of(
                new Actividad("Recorrido por la Catedral de Sal y la Ruta del Minero", "cultura", 3),
                new Actividad("Museo de la Salmuera", "museos", 1),
                new Actividad("Parrillada en la plaza de los Comuneros", "gastronomía", 1)));
        actividades.put("villa de leyva", List.of(
                new Actividad("Museo El Fósil y Centro de Investigaciones Paleontológicas", "museos", 2),
                new Actividad("Caminata a los Pozos Azules", "senderismo", 2),
                new Actividad("Degustación de cocina boyacense en la Plaza Mayor", "gastronomía", 1)));
        actividades.put("suesca", List.of(
                new Actividad("Escalada en roca con instructor", "aventura", 3),
                new Actividad("Caminata por la vía del tren junto a las rocas", "senderismo", 2),
                new Actividad("Picada y postres en el pueblo", "gastronomía", 1)));
        actividades.put("chicaque", List.of(
                new Actividad("Sendero al Pico del Águila en el bosque de niebla", "senderismo", 3),
                new Actividad("Canopy entre robles", "aventura", 1),
                new Actividad("Avistamiento de aves", "naturaleza", 2)));
    }

    public static String normalizar(String texto) {
        if (texto == null) return "";
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinTildes.toLowerCase(Locale.ROOT).trim().replaceAll("\\s+", " ");
    }

    public String buscarDestino(String texto) {
        String t = normalizar(texto);
        if (t.isEmpty()) return null;
        for (String clave : destinos.keySet()) {
            if (t.contains(clave) || (t.length() >= 4 && clave.contains(t))) return clave;
        }
        for (Map.Entry<String, String> entrada : alias.entrySet()) {
            if (t.contains(entrada.getKey())) return entrada.getValue();
        }
        return null;
    }

    public static String normalizarDia(String texto) {
        String t = normalizar(texto);
        if (t.startsWith("sab")) return "sabado";
        if (t.startsWith("dom")) return "domingo";
        return null;
    }

    public static String pesos(int valor) {
        return "$" + String.format(Locale.ROOT, "%,d", valor).replace(',', '.');
    }

    public String destinosValidos() {
        return destinos.values().stream().map(Destino::nombre).collect(Collectors.joining(", "));
    }

    public Destino destino(String clave) { return destinos.get(clave); }
    public Pronostico pronostico(String clave, String dia) { return pronosticos.get(clave + "|" + dia); }
    public int cupos(String clave, String dia) { return cupos.getOrDefault(clave + "|" + dia, 0); }
    public List<Actividad> actividades(String clave) { return actividades.getOrDefault(clave, List.of()); }

    public synchronized Reserva reservar(String clave, String dia, int personas, String responsable) {
        cupos.merge(clave + "|" + dia, -personas, Integer::sum);
        Reserva reserva = new Reserva(
                "RES-%03d".formatted(reservas.size() + 1), clave, dia, personas, responsable);
        reservas.add(reserva);
        return reserva;
    }
}

