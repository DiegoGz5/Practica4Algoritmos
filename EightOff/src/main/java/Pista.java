// Guarda la información necesaria para mostrar una pista.
public class Pista {

    // Indica si la carta sugerida está en una columna o celda libre.
    public enum Zona {
        COLUMNA,
        CELDA_LIBRE
    }

    // Zona donde se encuentra la carta sugerida.
    private final Zona zonaOrigen;

    // Número de la columna o posición de la celda, empezando desde 0.
    private final int indiceOrigen;

    // Posición inicial de la secuencia en la columna.
    // Para una celda libre no se usa y tendrá el valor -1.
    private final int indiceCartaInicio;

    // Cuántas cartas se moverían. Para una celda libre normalmente será 1.
    private final int cantidadCartas;

    // Texto que explica la jugada sugerida.
    private final String mensaje;

    // Guardamos toda la información de esta pista.
    public Pista(
            Zona zonaOrigen,
            int indiceOrigen,
            int indiceCartaInicio,
            int cantidadCartas,
            String mensaje) {

        this.zonaOrigen = zonaOrigen;
        this.indiceOrigen = indiceOrigen;
        this.indiceCartaInicio = indiceCartaInicio;
        this.cantidadCartas = cantidadCartas;
        this.mensaje = mensaje;
    }

    // Devuelve la zona donde está la carta sugerida.
    public Zona getZonaOrigen() {
        return zonaOrigen;
    }

    // Devuelve la columna o celda donde está la carta sugerida.
    public int getIndiceOrigen() {
        return indiceOrigen;
    }

    // Devuelve dónde empieza la secuencia dentro de una columna.
    public int getIndiceCartaInicio() {
        return indiceCartaInicio;
    }

    // Devuelve cuántas cartas incluye la jugada.
    public int getCantidadCartas() {
        return cantidadCartas;
    }

    // Devuelve el texto que explicará la pista al jugador.
    public String getMensaje() {
        return mensaje;
    }

}