// Esta clase guarda las zonas y el estado inicial de una partida.
public class Partida {

    private ListaSimple<ListaSimple<Carta>> columnas;
    private ListaSimple<Carta> celdasLibres;
    private ListaSimple<ListaSimple<Carta>> fundaciones;
    // Guarda los estados anteriores para poder deshacer.
    private final ListaSimple<EstadoPartida> historial;

    // Al crear la partida, preparamos las zonas y repartimos las cartas.
    public Partida() {
        columnas = new ListaSimple<>();
        celdasLibres = new ListaSimple<>();
        fundaciones = new ListaSimple<>();
        historial = new ListaSimple<>();

        crearColumnas();
        crearFundaciones();
        repartirCartas();
    }

    // Crea las ocho columnas vacías del tableau.
    private void crearColumnas() {
        for (int i = 0; i < 8; i++) {
            columnas.agregarAlFinal(new ListaSimple<>());
        }
    }

    // Crea las cuatro fundaciones vacías, una por cada palo.
    private void crearFundaciones() {
        for (int i = 0; i < 4; i++) {
            fundaciones.agregarAlFinal(new ListaSimple<>());
        }
    }

    // Reparte cuatro cartas a celdas libres y seis a cada columna.
    private void repartirCartas() {
        Baraja baraja = new Baraja();

        // Las primeras cuatro cartas ocupan cuatro de las ocho celdas libres.
        for (int i = 0; i < 4; i++) {
            celdasLibres.agregarAlFinal(baraja.repartirCarta());
        }

        // Repartimos seis cartas a cada una de las ocho columnas.
        for (int indiceColumna = 0; indiceColumna < 8; indiceColumna++) {
            ListaSimple<Carta> columna = columnas.obtener(indiceColumna);

            for (int numeroCarta = 0; numeroCarta < 6; numeroCarta++) {
                columna.agregarAlFinal(baraja.repartirCarta());
            }
        }
    }

    // Da acceso a las ocho columnas de la partida.
    public ListaSimple<ListaSimple<Carta>> getColumnas() {
        return columnas;
    }

    // Da acceso a las cartas que ocupan celdas libres.
    public ListaSimple<Carta> getCeldasLibres() {
        return celdasLibres;
    }

    // Da acceso a las cuatro pilas de fundación.
    public ListaSimple<ListaSimple<Carta>> getFundaciones() {
        return fundaciones;
    }

    // Imprime las celdas, columnas y fundaciones para ver el reparto.
    public void mostrarEstado() {

        // Mostramos las ocho celdas libres.
        System.out.print("Celdas libres: ");

        for (int i = 0; i < 8; i++) {
            // Las cartas ocupadas están al principio de la lista.
            if (i < celdasLibres.getCantidad()) {
                System.out.print("[" + celdasLibres.obtener(i) + "] ");
            } else {
                System.out.print("[vacía] ");
            }
        }

        System.out.println();
        System.out.println();

        // Mostramos las cartas de cada columna, desde abajo hacia arriba.
        for (int i = 0; i < columnas.getCantidad(); i++) {
            ListaSimple<Carta> columna = columnas.obtener(i);

            System.out.print("Columna " + (i + 1) + " (abajo → arriba): ");

            for (int j = 0; j < columna.getCantidad(); j++) {
                System.out.print(columna.obtener(j) + " ");
            }

            System.out.println();
        }

        System.out.println();

        // Mostramos las cuatro fundaciones.
        for (int i = 0; i < fundaciones.getCantidad(); i++) {
            ListaSimple<Carta> fundacion = fundaciones.obtener(i);
            Carta.Palo palo = Carta.Palo.values()[i];

            System.out.print("Fundación de " + palo + ": ");

            if (fundacion.estaVacia()) {
                System.out.println("[vacía]");
            } else {
                for (int j = 0; j < fundacion.getCantidad(); j++) {
                    System.out.print(fundacion.obtener(j) + " ");
                }
                System.out.println();
            }
        }
    }
    // Revisa si una carta puede colocarse en su fundación.
    private boolean puedeAgregarAFundacion(Carta carta) {
        // La fundación que corresponde a esta carta depende de su palo.
        int indiceFundacion = carta.getPalo().ordinal();
        ListaSimple<Carta> fundacion = fundaciones.obtener(indiceFundacion);

        // Una fundación vacía solo acepta un As.
        if (fundacion.estaVacia()) {
            return carta.getValor() == 1;
        }

        // Revisamos cuál es la carta que está arriba de la fundación.
        Carta cartaSuperior =
                fundacion.obtener(fundacion.getCantidad() - 1);

        // La siguiente carta debe ser del mismo palo y tener un valor mayor en uno.
        return carta.getPalo() == cartaSuperior.getPalo()
                && carta.getValor() == cartaSuperior.getValor() + 1;
    }

    // Revisa si una carta de celda puede ir a una columna, sin moverla.
    public boolean puedeMoverCeldaAColumna(
            int indiceCelda,
            int indiceColumna) {

        if (indiceCelda < 0 || indiceCelda >= celdasLibres.getCantidad()) {
            return false;
        }

        if (indiceColumna < 0 || indiceColumna >= columnas.getCantidad()) {
            return false;
        }

        Carta carta = celdasLibres.obtener(indiceCelda);
        ListaSimple<Carta> columna = columnas.obtener(indiceColumna);
        return puedeColocarEnColumna(carta, columna);
    }

    // Revisa si una carta de celda puede ir a la fundación indicada.
    public boolean puedeMoverCeldaAFundacion(
            int indiceCelda,
            int indiceFundacion) {

        if (indiceCelda < 0 || indiceCelda >= celdasLibres.getCantidad()) {
            return false;
        }

        if (indiceFundacion < 0 || indiceFundacion >= fundaciones.getCantidad()) {
            return false;
        }

        Carta carta = celdasLibres.obtener(indiceCelda);
        return carta.getPalo().ordinal() == indiceFundacion
                && puedeAgregarAFundacion(carta);
    }

    // Revisa si la carta superior seleccionada puede ir a una celda libre.
    public boolean puedeMoverColumnaACelda(
            int indiceColumna,
            int indiceCartaInicio,
            int cantidadCartas) {

        if (indiceColumna < 0 || indiceColumna >= columnas.getCantidad()) {
            return false;
        }

        ListaSimple<Carta> columna = columnas.obtener(indiceColumna);

        // Una celda libre recibe una sola carta y debe ser la de arriba.
        if (celdasLibres.getCantidad() >= 8
                || cantidadCartas != 1
                || indiceCartaInicio != columna.getCantidad() - 1) {
            return false;
        }

        return !columna.estaVacia();
    }

    // Revisa si la carta superior seleccionada puede ir a esa fundación.
    public boolean puedeMoverColumnaAFundacion(
            int indiceColumna,
            int indiceCartaInicio,
            int cantidadCartas,
            int indiceFundacion) {

        if (indiceColumna < 0 || indiceColumna >= columnas.getCantidad()) {
            return false;
        }

        if (indiceFundacion < 0 || indiceFundacion >= fundaciones.getCantidad()) {
            return false;
        }

        ListaSimple<Carta> columna = columnas.obtener(indiceColumna);

        // A una fundación solo se mueve la carta superior, nunca una secuencia.
        if (columna.estaVacia()
                || cantidadCartas != 1
                || indiceCartaInicio != columna.getCantidad() - 1) {
            return false;
        }

        Carta carta = columna.obtener(indiceCartaInicio);
        return carta.getPalo().ordinal() == indiceFundacion
                && puedeAgregarAFundacion(carta);
    }

    // Intenta mover la carta de arriba de una columna a su fundación.
    public boolean moverColumnaAFundacion(int indiceColumna) {
        // El índice debe corresponder a una de las columnas.
        if (indiceColumna < 0 || indiceColumna >= columnas.getCantidad()) {
            return false;
        }

        // Obtenemos la columna elegida.
        ListaSimple<Carta> columna = columnas.obtener(indiceColumna);

        // Si está vacía, no hay carta para mover.
        if (columna.estaVacia()) {
            return false;
        }

        // La última carta de la lista es la que está arriba de la columna.
        int indiceSuperior = columna.getCantidad() - 1;
        Carta carta = columna.obtener(indiceSuperior);

        // Si no cumple la regla, dejamos las listas como están.
        if (!puedeAgregarAFundacion(carta)) {
            return false;
        }

        // Guardamos el estado anterior antes de modificar las listas.
        guardarEstado();

        // Retiramos la carta de la columna y la ponemos en su fundación.
        columna.quitar(indiceSuperior);
        fundaciones.obtener(carta.getPalo().ordinal()).agregarAlFinal(carta);

        return true;
    }

    // Intenta mover una carta de una celda libre a su fundación.
    public boolean moverCeldaAFundacion(int indiceCelda) {
        // Solo podemos elegir una celda que tenga una carta.
        if (indiceCelda < 0 || indiceCelda >= celdasLibres.getCantidad()) {
            return false;
        }

        // Obtenemos la carta que ocupa esa celda.
        Carta carta = celdasLibres.obtener(indiceCelda);

        // Si no puede ir a la fundación, no modificamos ninguna lista.
        if (!puedeAgregarAFundacion(carta)) {
            return false;
        }

        // Guardamos el estado anterior antes de modificar las listas.
        guardarEstado();

        // Retiramos la carta de las celdas libres.
        celdasLibres.quitar(indiceCelda);

        // La agregamos al final de la fundación de su palo.
        fundaciones.obtener(carta.getPalo().ordinal()).agregarAlFinal(carta);

        return true;
    }
    // Intenta mover la carta de arriba de una columna a una celda libre.
    public boolean moverColumnaACelda(int indiceColumna) {

        // Revisamos que el índice corresponda a una de las ocho columnas.
        if (indiceColumna < 0 || indiceColumna >= columnas.getCantidad()) {
            return false;
        }

        // Si ya hay ocho cartas en las celdas, no queda ninguna disponible.
        if (celdasLibres.getCantidad() == 8) {
            return false;
        }

        // Obtenemos la columna desde la que queremos mover la carta.
        ListaSimple<Carta> columna = columnas.obtener(indiceColumna);

        // Una columna vacía no tiene carta para mover.
        if (columna.estaVacia()) {
            return false;
        }

        // La carta de arriba está al final de la lista.
        int indiceSuperior = columna.getCantidad() - 1;

        // Guardamos el estado anterior antes de modificar las listas.
        guardarEstado();

        // La quitamos de la columna y guardamos cuál era.
        Carta carta = columna.quitar(indiceSuperior);

        // La agregamos a las celdas libres.
        celdasLibres.agregarAlFinal(carta);

        // Indicamos que el movimiento sí se realizó.
        return true;
    }
    // Revisa si la carta puede colocarse en esta columna.
    private boolean puedeColocarEnColumna(
            Carta carta,
            ListaSimple<Carta> columna) {

        // Una columna vacía solo acepta un Rey.
        if (columna.estaVacia()) {
            return carta.getValor() == 13;
        }

        // Obtenemos la carta que está arriba de la columna.
        Carta cartaSuperior = columna.obtener(columna.getCantidad() - 1);

        // La carta debe ser del mismo palo y tener un valor menor en uno.
        return carta.getPalo() == cartaSuperior.getPalo()
                && cartaSuperior.getValor() == carta.getValor() + 1;
    }

    // Intenta mover una carta de una celda libre a una columna.
    public boolean moverCeldaAColumna(int indiceCelda, int indiceColumna) {

        // Revisamos que la celda indicada tenga una carta.
        if (indiceCelda < 0 || indiceCelda >= celdasLibres.getCantidad()) {
            return false;
        }

        // Revisamos que la columna indicada exista.
        if (indiceColumna < 0 || indiceColumna >= columnas.getCantidad()) {
            return false;
        }

        // Obtenemos la carta de la celda y la columna destino.
        Carta carta = celdasLibres.obtener(indiceCelda);
        ListaSimple<Carta> columna = columnas.obtener(indiceColumna);

        // Si la carta no cabe según las reglas, no hacemos cambios.
        if (!puedeColocarEnColumna(carta, columna)) {
            return false;
        }

        // Guardamos el estado anterior antes de modificar las listas.
        guardarEstado();

        // La quitamos de la celda libre y la agregamos a la columna.
        celdasLibres.quitar(indiceCelda);
        columna.agregarAlFinal(carta);

        // Indicamos que el movimiento se realizó.
        return true;
    }
    // Mover una carta equivale a mover una secuencia de tamaño 1.
    public boolean moverColumnaAColumna(
            int indiceOrigen,
            int indiceDestino) {

        return moverSecuenciaColumnaAColumna(
                indiceOrigen,
                indiceDestino,
                1
        );
    }

    // Revisa si una secuencia mantiene el mismo palo y baja de valor de uno en uno.
    public boolean esSecuenciaConectada(int indiceColumna, int indiceInicio) {
        // La columna debe existir.
        if (indiceColumna < 0 || indiceColumna >= columnas.getCantidad()) {
            return false;
        }

        ListaSimple<Carta> columna = columnas.obtener(indiceColumna);

        // El índice inicial debe señalar una carta real de la columna.
        if (indiceInicio < 0 || indiceInicio >= columna.getCantidad()) {
            return false;
        }

        // Comparamos cada carta con la que está inmediatamente encima.
        for (int i = indiceInicio; i < columna.getCantidad() - 1; i++) {
            Carta cartaDeAbajo = columna.obtener(i);
            Carta cartaDeArriba = columna.obtener(i + 1);

            // Las dos cartas deben ser del mismo palo.
            if (cartaDeAbajo.getPalo() != cartaDeArriba.getPalo()) {
                return false;
            }

            // La carta de arriba debe tener un valor exactamente menor en uno.
            if (cartaDeArriba.getValor() != cartaDeAbajo.getValor() - 1) {
                return false;
            }
        }

        // Si no encontramos un corte, toda la secuencia está conectada.
        return true;
    }

    // Revisa si una secuencia se puede mover, pero no cambia las listas.
    public boolean puedeMoverSecuenciaColumnaAColumna(
            int indiceOrigen,
            int indiceDestino,
            int cantidadCartas) {

        // Los índices deben apuntar a columnas existentes.
        if (indiceOrigen < 0 || indiceOrigen >= columnas.getCantidad()) {
            return false;
        }

        if (indiceDestino < 0 || indiceDestino >= columnas.getCantidad()) {
            return false;
        }

        // El origen y el destino deben ser columnas distintas.
        if (indiceOrigen == indiceDestino) {
            return false;
        }

        // Obtenemos las columnas involucradas.
        ListaSimple<Carta> origen = columnas.obtener(indiceOrigen);
        ListaSimple<Carta> destino = columnas.obtener(indiceDestino);

        // La secuencia debe tener cartas y caber dentro del origen.
        if (cantidadCartas <= 0 || cantidadCartas > origen.getCantidad()) {
            return false;
        }

        // Eight Off permite mover celdas vacías más una carta.
        int celdasVacias = 8 - celdasLibres.getCantidad();
        int maximoMovible = celdasVacias + 1;

        if (cantidadCartas > maximoMovible) {
            return false;
        }

        // Calculamos dónde comienza la secuencia dentro de la columna.
        int indiceInicio = origen.getCantidad() - cantidadCartas;

        // La secuencia debe ser del mismo palo y descender de uno en uno.
        if (!esSecuenciaConectada(indiceOrigen, indiceInicio)) {
            return false;
        }

        // La carta de abajo de la secuencia toca la columna destino.
        Carta baseDeLaSecuencia = origen.obtener(indiceInicio);

        return puedeColocarEnColumna(baseDeLaSecuencia, destino);
    }

    // Mueve la secuencia si la validación previa dice que es legal.
    public boolean moverSecuenciaColumnaAColumna(
            int indiceOrigen,
            int indiceDestino,
            int cantidadCartas) {

        // Si la jugada no es legal, no modificamos ninguna lista.
        if (!puedeMoverSecuenciaColumnaAColumna(
                indiceOrigen,
                indiceDestino,
                cantidadCartas)) {
            return false;
        }

        // Guardamos el estado anterior antes de mover cualquier carta.
        guardarEstado();

        // Recuperamos el origen y calculamos dónde empieza la secuencia.
        ListaSimple<Carta> origen = columnas.obtener(indiceOrigen);
        ListaSimple<Carta> destino = columnas.obtener(indiceDestino);
        int indiceInicio = origen.getCantidad() - cantidadCartas;

        // Retiramos desde el mismo índice para conservar el orden.
        for (int i = 0; i < cantidadCartas; i++) {
            Carta carta = origen.quitar(indiceInicio);
            destino.agregarAlFinal(carta);
        }

        return true;
    }

    // Indica si las cuatro fundaciones ya están completas.
    public boolean haGanado() {
        // Revisamos que cada fundación tenga sus trece cartas.
        for (int i = 0; i < fundaciones.getCantidad(); i++) {
            ListaSimple<Carta> fundacion = fundaciones.obtener(i);

            if (fundacion.getCantidad() != 13) {
                return false;
            }
        }

        return true;
    }

    // Indica si queda al menos una jugada legal.

    public boolean hayMovimientosPosibles() {
        return buscarPista() != null;
    }

    // Busca una jugada legal y devuelve la información para mostrarla como pista.
    public Pista buscarPista() {

        // Primero revisamos si alguna carta de una celda libre va a su fundación.
        for (int i = 0; i < celdasLibres.getCantidad(); i++) {
            Carta carta = celdasLibres.obtener(i);

            if (puedeAgregarAFundacion(carta)) {
                String mensaje = "Mueve " + carta
                        + " de la celda libre " + (i + 1)
                        + " a su fundación";

                return new Pista(
                        Pista.Zona.CELDA_LIBRE,
                        i,
                        -1,
                        1,
                        mensaje
                );
            }
        }

        // Revisamos si la carta de arriba de una columna va a su fundación.
        for (int i = 0; i < columnas.getCantidad(); i++) {
            ListaSimple<Carta> columna = columnas.obtener(i);

            if (columna.estaVacia()) {
                continue;
            }

            int indiceSuperior = columna.getCantidad() - 1;
            Carta carta = columna.obtener(indiceSuperior);

            if (puedeAgregarAFundacion(carta)) {
                String mensaje = "Mueve " + carta
                        + " de la columna " + (i + 1)
                        + " a su fundación";

                return new Pista(
                        Pista.Zona.COLUMNA,
                        i,
                        indiceSuperior,
                        1,
                        mensaje
                );
            }
        }

        // Revisamos si una carta de celda libre puede volver al tableau.
        for (int i = 0; i < celdasLibres.getCantidad(); i++) {
            Carta carta = celdasLibres.obtener(i);

            for (int j = 0; j < columnas.getCantidad(); j++) {
                ListaSimple<Carta> columna = columnas.obtener(j);

                if (puedeColocarEnColumna(carta, columna)) {
                    String mensaje = "Mueve " + carta
                            + " de la celda libre " + (i + 1)
                            + " a la columna " + (j + 1);

                    return new Pista(
                            Pista.Zona.CELDA_LIBRE,
                            i,
                            -1,
                            1,
                            mensaje
                    );
                }
            }
        }

        // Calculamos el límite de cartas para las secuencias.
        int celdasVacias = 8 - celdasLibres.getCantidad();
        int maximoMovible = celdasVacias + 1;

        // Buscamos una secuencia que pueda moverse entre columnas.
        for (int i = 0; i < columnas.getCantidad(); i++) {
            ListaSimple<Carta> origen = columnas.obtener(i);

            if (origen.estaVacia()) {
                continue;
            }

            int maximoDesdeEsteOrigen =
                    Math.min(origen.getCantidad(), maximoMovible);

            for (int j = 0; j < columnas.getCantidad(); j++) {
                if (i == j) {
                    continue;
                }

                for (int cantidad = 1;
                     cantidad <= maximoDesdeEsteOrigen;
                     cantidad++) {

                    if (puedeMoverSecuenciaColumnaAColumna(i, j, cantidad)) {
                        int indiceInicio = origen.getCantidad() - cantidad;
                        Carta primeraCarta = origen.obtener(indiceInicio);

                        String mensaje = "Mueve una secuencia de "
                                + cantidad + " carta(s), empezando con "
                                + primeraCarta + ", de la columna " + (i + 1)
                                + " a la columna " + (j + 1);

                        return new Pista(
                                Pista.Zona.COLUMNA,
                                i,
                                indiceInicio,
                                cantidad,
                                mensaje
                        );
                    }
                }
            }
        }

        // Por último, revisamos si alguna carta superior puede ir a una celda libre.
        if (celdasLibres.getCantidad() < 8) {
            for (int i = 0; i < columnas.getCantidad(); i++) {
                ListaSimple<Carta> columna = columnas.obtener(i);

                if (!columna.estaVacia()) {
                    int indiceSuperior = columna.getCantidad() - 1;
                    Carta carta = columna.obtener(indiceSuperior);
                    int numeroCelda = celdasLibres.getCantidad() + 1;

                    String mensaje = "Mueve " + carta
                            + " de la columna " + (i + 1)
                            + " a la celda libre " + numeroCelda;

                    return new Pista(
                            Pista.Zona.COLUMNA,
                            i,
                            indiceSuperior,
                            1,
                            mensaje
                    );
                }
            }
        }

        // Si llegamos aquí, no encontramos una jugada legal.
        return null;
    }
    // Guarda una copia del estado actual antes de hacer una jugada.
    private void guardarEstado() {
        historial.agregarAlFinal(
                new EstadoPartida(columnas, celdasLibres, fundaciones)
        );
    }

    // Restaura el estado anterior, si hay uno guardado.
    public boolean deshacer() {
        // Si no hay estados anteriores, no hay nada que deshacer.
        if (historial.estaVacia()) {
            return false;
        }

        // Tomamos el estado más reciente y lo quitamos del historial.
        int indiceAnterior = historial.getCantidad() - 1;
        EstadoPartida estadoAnterior = historial.quitar(indiceAnterior);

        // Reemplazamos las listas actuales por las copias guardadas.
        columnas = estadoAnterior.getColumnas();
        celdasLibres = estadoAnterior.getCeldasLibres();
        fundaciones = estadoAnterior.getFundaciones();

        return true;
    }
}
