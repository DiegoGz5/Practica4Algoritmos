// Guarda una copia del estado de las zonas de la partida.
public class EstadoPartida {

    // Copia de las ocho columnas.
    private final ListaSimple<ListaSimple<Carta>> columnas;

    // Copia de las cartas en las celdas libres.
    private final ListaSimple<Carta> celdasLibres;

    // Copia de las cuatro fundaciones.
    private final ListaSimple<ListaSimple<Carta>> fundaciones;

    // Recibe las zonas actuales y copia sus listas.
    public EstadoPartida(
            ListaSimple<ListaSimple<Carta>> columnasActuales,
            ListaSimple<Carta> celdasLibresActuales,
            ListaSimple<ListaSimple<Carta>> fundacionesActuales) {

        // Creamos una lista nueva para guardar las columnas copiadas.
        columnas = new ListaSimple<>();

        // Copiamos cada columna y sus cartas.
        for (int i = 0; i < columnasActuales.getCantidad(); i++) {
            ListaSimple<Carta> columnaOriginal = columnasActuales.obtener(i);
            columnas.agregarAlFinal(columnaOriginal.copiar());
        }

        // Las celdas libres son una sola lista, así que la copiamos directamente.
        celdasLibres = celdasLibresActuales.copiar();

        // Creamos una lista nueva para guardar las fundaciones copiadas.
        fundaciones = new ListaSimple<>();

        // Copiamos cada fundación y sus cartas.
        for (int i = 0; i < fundacionesActuales.getCantidad(); i++) {
            ListaSimple<Carta> fundacionOriginal =
                    fundacionesActuales.obtener(i);

            fundaciones.agregarAlFinal(fundacionOriginal.copiar());
        }
    }

    // Devuelve la copia de las columnas.
    public ListaSimple<ListaSimple<Carta>> getColumnas() {
        return columnas;
    }

    // Devuelve la copia de las celdas libres.
    public ListaSimple<Carta> getCeldasLibres() {
        return celdasLibres;
    }

    // Devuelve la copia de las fundaciones.
    public ListaSimple<ListaSimple<Carta>> getFundaciones() {
        return fundaciones;
    }
}