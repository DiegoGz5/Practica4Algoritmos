import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// Esta ventana muestra la partida y sus controles.
public class VentanaJuego extends JFrame {

    // Verde tenue para marcar destinos donde la jugada seleccionada es válida.
    private static final Color COLOR_DESTINO = new Color(190, 235, 190);

    // El estado que estamos mostrando y modificando.
    private final Partida partida;

    // Paneles cuyo contenido se vuelve a dibujar después de un cambio.
    private JPanel panelCeldas;
    private JPanel panelFundaciones;
    private JPanel panelColumnas;
    // Guarda la pista actual para poder resaltar su carta en el tablero.
    private Pista pistaActual;
    // Guarda qué carta o secuencia seleccionó el jugador.
    private Pista.Zona zonaSeleccionada;
    private int indiceOrigenSeleccionado = -1;
    private int indiceCartaInicioSeleccionada = -1;
    private int cantidadSeleccionada;
    // Aquí mostraremos mensajes y pistas.
    private JLabel mensaje;

    public VentanaJuego() {
        partida = new Partida();

        setTitle("Eight Off");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        construirInterfaz();
        refrescarTablero();
    }

    // Construye los paneles y botones de la ventana.
    private void construirInterfaz() {
        setLayout(new BorderLayout(10, 10));

        // El área de arriba contiene celdas libres y fundaciones.
        JPanel zonasSuperiores = new JPanel(new GridLayout(1, 2, 20, 0));

        panelCeldas = new JPanel(new GridLayout(1, 8, 5, 5));
        panelCeldas.setBorder(
                BorderFactory.createTitledBorder("Celdas libres")
        );

        panelFundaciones = new JPanel(new GridLayout(1, 4, 5, 5));
        panelFundaciones.setBorder(
                BorderFactory.createTitledBorder("Fundaciones")
        );

        zonasSuperiores.add(panelCeldas);
        zonasSuperiores.add(panelFundaciones);

        // El área central contiene las ocho columnas.
        panelColumnas = new JPanel(new GridLayout(1, 8, 8, 0));
        panelColumnas.setBorder(
                BorderFactory.createTitledBorder("Tableau")
        );

        JPanel tablero = new JPanel(new BorderLayout(10, 10));
        tablero.add(zonasSuperiores, BorderLayout.NORTH);
        tablero.add(panelColumnas, BorderLayout.CENTER);

        // Creamos el botón para pedir una pista.
        JButton botonPista = new JButton("Pista");
        botonPista.addActionListener(evento -> mostrarPista());

        // Creamos el botón para deshacer el último movimiento.
        JButton botonDeshacer = new JButton("Deshacer");
        botonDeshacer.addActionListener(evento -> deshacerMovimiento());

        // Permite quitar la selección sin intentar un movimiento.
        JButton botonCancelarSeleccion = new JButton("Cancelar selección");
        botonCancelarSeleccion.addActionListener(
                evento -> cancelarSeleccion()
        );

        // El mensaje aparece abajo, junto a los botones.
        mensaje = new JLabel("Partida iniciada");

        JPanel controles = new JPanel(new FlowLayout());
        controles.add(botonPista);
        controles.add(botonDeshacer);
        controles.add(botonCancelarSeleccion);
        controles.add(mensaje);

        add(tablero, BorderLayout.CENTER);
        add(controles, BorderLayout.SOUTH);
    }
    // Vuelve a dibujar las zonas según las cartas que contienen.
    private void refrescarTablero() {
        panelCeldas.removeAll();
        panelFundaciones.removeAll();
        panelColumnas.removeAll();

        // Dibujamos las ocho celdas libres.
        for (int i = 0; i < 8; i++) {
            if (i < partida.getCeldasLibres().getCantidad()) {
                Carta carta = partida.getCeldasLibres().obtener(i);

                // Revisamos si esta celda es el origen de la pista actual.
                boolean resaltada = pistaActual != null
                        && pistaActual.getZonaOrigen() == Pista.Zona.CELDA_LIBRE
                        && pistaActual.getIndiceOrigen() == i;

                boolean seleccionada = zonaSeleccionada == Pista.Zona.CELDA_LIBRE
                        && indiceOrigenSeleccionado == i;

                int indiceCelda = i;
                panelCeldas.add(crearCarta(
                        carta,
                        resaltada || seleccionada,
                        () -> seleccionarCelda(indiceCelda)
                ));
            } else {
                // Una celda vacía puede recibir una carta seleccionada.
                JLabel celdaVacia = crearEtiqueta(
                        "[vacía]",
                        () -> manejarClickCeldaVacia()
                );

                if (esDestinoCeldaLibreValido()) {
                    celdaVacia.setBorder(
                            BorderFactory.createLineBorder(COLOR_DESTINO, 3)
                    );
                }

                panelCeldas.add(celdaVacia);
            }
        }

        // Dibujamos las cuatro fundaciones.
        for (int i = 0; i < 4; i++) {
            ListaSimple<Carta> fundacion =
                    partida.getFundaciones().obtener(i);
            Carta.Palo palo = Carta.Palo.values()[i];
            int indiceFundacion = i;
            JPanel pilaFundacion = new JPanel(new BorderLayout());
            pilaFundacion.setBorder(
                    BorderFactory.createTitledBorder(
                            BorderFactory.createLineBorder(
                                    esDestinoFundacionValido(indiceFundacion)
                                            ? COLOR_DESTINO
                                            : Color.LIGHT_GRAY,
                                    esDestinoFundacionValido(indiceFundacion) ? 3 : 1
                            ),
                            palo.getSimbolo()
                    )
            );

            if (fundacion.estaVacia()) {
                pilaFundacion.add(crearEtiqueta(
                        "[vacía]",
                        () -> manejarClickFundacion(indiceFundacion)
                ), BorderLayout.CENTER);
            } else {
                Carta cartaSuperior =
                        fundacion.obtener(fundacion.getCantidad() - 1);
                pilaFundacion.add(crearCarta(
                        cartaSuperior,
                        false,
                        () -> manejarClickFundacion(indiceFundacion)
                ), BorderLayout.CENTER);
            }

            panelFundaciones.add(pilaFundacion);
        }

        // Usamos estas medidas para todas las cartas del tableau.
        int anchoCarta = 75;
        int altoCarta = 100;
        int separacionVertical = 28;

        // Dibujamos cada columna como una pila de cartas encimadas.
        for (int i = 0; i < 8; i++) {
            ListaSimple<Carta> columna =
                    partida.getColumnas().obtener(i);

            JPanel marcoColumna = new JPanel(new BorderLayout());
            marcoColumna.setBorder(
                    BorderFactory.createTitledBorder(
                            BorderFactory.createLineBorder(
                                    esDestinoColumnaValido(i)
                                            ? COLOR_DESTINO
                                            : Color.LIGHT_GRAY,
                                    esDestinoColumnaValido(i) ? 3 : 1
                            ),
                            "Columna " + (i + 1)
                    )
            );

            JLayeredPane cartasEncimadas = new JLayeredPane();
            cartasEncimadas.setLayout(null);

            int cantidad = columna.getCantidad();

            // Calculamos la altura necesaria para que no se corte la última carta.
            int altoPila = cantidad == 0
                    ? altoCarta
                    : altoCarta + (cantidad - 1) * separacionVertical;

            cartasEncimadas.setPreferredSize(
                    new Dimension(anchoCarta, altoPila)
            );

            if (cantidad == 0) {
                int indiceColumna = i;
                JLabel vacia = crearEtiqueta(
                        "[vacía]",
                        () -> manejarClickColumna(indiceColumna, -1)
                );
                vacia.setBounds(0, 0, anchoCarta, altoCarta);
                cartasEncimadas.add(vacia, Integer.valueOf(0));
            } else {
                // La lista está guardada de abajo hacia arriba.
                for (int j = 0; j < cantidad; j++) {
                    Carta carta = columna.obtener(j);

                    // Revisamos si esta carta forma parte de la secuencia sugerida.
                    boolean resaltada = pistaActual != null
                            && pistaActual.getZonaOrigen() == Pista.Zona.COLUMNA
                            && pistaActual.getIndiceOrigen() == i
                            && j >= pistaActual.getIndiceCartaInicio()
                            && j < pistaActual.getIndiceCartaInicio()
                                    + pistaActual.getCantidadCartas();

                    boolean seleccionada = zonaSeleccionada == Pista.Zona.COLUMNA
                            && indiceOrigenSeleccionado == i
                            && j >= indiceCartaInicioSeleccionada
                            && j < indiceCartaInicioSeleccionada
                                    + cantidadSeleccionada;

                    int indiceColumna = i;
                    int indiceCarta = j;
                    JPanel vistaCarta = crearCarta(
                            carta,
                            resaltada || seleccionada,
                            () -> manejarClickColumna(
                                    indiceColumna,
                                    indiceCarta
                            )
                    );

                    // Cada carta empieza un poco más abajo que la anterior.
                    int posicionY = j * separacionVertical;
                    vistaCarta.setBounds(
                            0,
                            posicionY,
                            anchoCarta,
                            altoCarta
                    );

                    // Las cartas añadidas después quedan encima en las zonas solapadas.
                    cartasEncimadas.add(vistaCarta, Integer.valueOf(j));
                }
            }

            marcoColumna.add(cartasEncimadas, BorderLayout.NORTH);
            panelColumnas.add(marcoColumna);
        }

        // Le avisamos a Swing que cambió el contenido de los paneles.
        revalidate();
        repaint();
    }

    // Crea una carta normal, sin resaltarla ni asignarle una acción de clic.
    private JPanel crearCarta(Carta carta) {
        return crearCarta(carta, false, null);
    }

    // Crea una carta resaltada o normal, sin asignarle una acción de clic.
    private JPanel crearCarta(Carta carta, boolean resaltada) {
        return crearCarta(carta, resaltada, null);
    }

    // Crea una carta con o sin resaltado y, si se indica, responde al clic.
    private JPanel crearCarta(
            Carta carta,
            boolean resaltada,
            Runnable accionAlHacerClic) {

        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setOpaque(true);

        // El borde naranja permite distinguir la carta sugerida.
        if (resaltada) {
            tarjeta.setBorder(
                    BorderFactory.createLineBorder(Color.ORANGE, 3)
            );
        } else {
            tarjeta.setBorder(
                    BorderFactory.createLineBorder(Color.BLACK)
            );
        }

        // El rango y el símbolo van juntos en una línea para que no se tapen.
        String textoEsquina = "<html><b>"
                + obtenerTextoValor(carta.getValor())
                + "&nbsp;"
                + carta.getPalo().getSimbolo()
                + "</b></html>";

        JLabel esquina = new JLabel(textoEsquina);
        esquina.setVerticalAlignment(SwingConstants.TOP);
        esquina.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        esquina.setBorder(
                BorderFactory.createEmptyBorder(4, 5, 0, 0)
        );
        esquina.setForeground(obtenerColorPalo(carta.getPalo()));

        tarjeta.add(esquina, BorderLayout.NORTH);
        tarjeta.setPreferredSize(new Dimension(75, 100));

        // El clic sobre la tarjeta ejecuta la acción que recibimos.
        if (accionAlHacerClic != null) {
            MouseAdapter listenerDeClic = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evento) {
                    accionAlHacerClic.run();
                }
            };

            // Añadimos el listener a ambos para que también funcione al tocar el texto.
            tarjeta.addMouseListener(listenerDeClic);
            esquina.addMouseListener(listenerDeClic);
        }

        return tarjeta;
    }

    // Selecciona una carta que está en una celda libre.
    private void seleccionarCelda(int indiceCelda) {
        // Si hacemos clic otra vez en la misma carta, cancelamos la selección.
        if (zonaSeleccionada == Pista.Zona.CELDA_LIBRE
                && indiceOrigenSeleccionado == indiceCelda) {
            cancelarSeleccion();
            return;
        }

        zonaSeleccionada = Pista.Zona.CELDA_LIBRE;
        indiceOrigenSeleccionado = indiceCelda;
        indiceCartaInicioSeleccionada = -1;
        cantidadSeleccionada = 1;

        // Quitamos una pista anterior para dejar visible una sola selección.
        pistaActual = null;

        mensaje.setText(
                "Seleccionaste la celda libre " + (indiceCelda + 1)
                        + ". Los destinos válidos están en verde."
        );
        refrescarTablero();
    }

    // Selecciona una carta y la secuencia que continúa hasta arriba de la columna.
    private void seleccionarCartaColumna(int indiceColumna, int indiceCarta) {
        ListaSimple<Carta> columna =
                partida.getColumnas().obtener(indiceColumna);

        // La secuencia seleccionada no puede exceder las celdas vacías más una.
        int cantidadQueSeQuiereMover = columna.getCantidad() - indiceCarta;
        int celdasVacias = 8 - partida.getCeldasLibres().getCantidad();
        int maximoSeleccionable = celdasVacias + 1;

        if (cantidadQueSeQuiereMover > maximoSeleccionable) {
            mensaje.setText(
                    "Puedes seleccionar hasta " + maximoSeleccionable
                            + " carta(s): hay " + celdasVacias
                            + " celda(s) libre(s) + 1"
            );
            return;
        }

        // Si se seleccionan varias, deben formar una secuencia conectada.
        if (!partida.esSecuenciaConectada(indiceColumna, indiceCarta)) {
            mensaje.setText(
                    "Solo puedes mover juntas cartas consecutivas del mismo palo"
            );
            return;
        }

        zonaSeleccionada = Pista.Zona.COLUMNA;
        indiceOrigenSeleccionado = indiceColumna;
        indiceCartaInicioSeleccionada = indiceCarta;
        cantidadSeleccionada = cantidadQueSeQuiereMover;
        pistaActual = null;

        Carta cartaElegida = columna.obtener(indiceCarta);
        mensaje.setText(
                "Seleccionaste " + cantidadSeleccionada
                        + " carta(s) desde " + cartaElegida
                        + " en la columna " + (indiceColumna + 1)
                        + ". Los destinos válidos están en verde."
        );

        refrescarTablero();
    }

    // Usa el clic en una columna como selección o como destino del movimiento.
    private void manejarClickColumna(int indiceColumna, int indiceCarta) {
        // Si todavía no hay origen, una carta seleccionada será el origen.
        if (zonaSeleccionada == null) {
            if (indiceCarta >= 0) {
                seleccionarCartaColumna(indiceColumna, indiceCarta);
            } else {
                mensaje.setText("Selecciona primero una carta");
            }
            return;
        }

        // Si se vuelve a hacer clic en la columna de origen, se cambia la selección.
        if (zonaSeleccionada == Pista.Zona.COLUMNA
                && indiceOrigenSeleccionado == indiceColumna) {
            if (indiceCarta >= 0) {
                boolean clicEnLaSeleccionActual =
                        indiceCarta >= indiceCartaInicioSeleccionada
                                && indiceCarta < indiceCartaInicioSeleccionada
                                        + cantidadSeleccionada;

                if (clicEnLaSeleccionActual) {
                    cancelarSeleccion();
                } else {
                    seleccionarCartaColumna(indiceColumna, indiceCarta);
                }
            } else {
                mensaje.setText("Elige una columna distinta como destino");
            }
            return;
        }

        boolean seMovio;

        // Desde una celda libre movemos una sola carta a la columna elegida.
        if (zonaSeleccionada == Pista.Zona.CELDA_LIBRE) {
            seMovio = partida.moverCeldaAColumna(
                    indiceOrigenSeleccionado,
                    indiceColumna
            );
        } else {
            // Desde otra columna movemos la secuencia que seleccionamos.
            seMovio = partida.moverSecuenciaColumnaAColumna(
                    indiceOrigenSeleccionado,
                    indiceColumna,
                    cantidadSeleccionada
            );
        }

        terminarMovimiento(seMovio);
    }

    // Intenta mover una sola carta seleccionada a una celda libre vacía.
    private void manejarClickCeldaVacia() {
        // Las cartas de una celda libre no se pueden mover a otra celda libre.
        if (zonaSeleccionada != Pista.Zona.COLUMNA) {
            mensaje.setText("Selecciona una carta de una columna primero");
            return;
        }

        // Cada celda libre guarda una carta, así que no acepta secuencias.
        if (cantidadSeleccionada != 1) {
            mensaje.setText("A una celda libre solo se mueve una carta");
            return;
        }

        boolean seMovio = partida.moverColumnaACelda(
                indiceOrigenSeleccionado
        );

        terminarMovimiento(seMovio);
    }

    // Intenta mover una carta seleccionada a la fundación que se pulsó.
    private void manejarClickFundacion(int indiceFundacion) {
        // Primero debe haber una carta seleccionada como origen.
        if (zonaSeleccionada == null) {
            mensaje.setText("Selecciona primero una carta para la fundación");
            return;
        }

        Carta carta;
        boolean seMovio;

        // Una carta de una celda libre puede ir a su fundación.
        if (zonaSeleccionada == Pista.Zona.CELDA_LIBRE) {
            carta = partida.getCeldasLibres().obtener(
                    indiceOrigenSeleccionado
            );

            // La fundación que pulsamos debe corresponder al palo.
            if (carta.getPalo().ordinal() != indiceFundacion) {
                mensaje.setText(
                        "Esa carta va en la fundación "
                                + carta.getPalo().getSimbolo()
                );
                return;
            }

            seMovio = partida.moverCeldaAFundacion(
                    indiceOrigenSeleccionado
            );
        } else {
            ListaSimple<Carta> columna =
                    partida.getColumnas().obtener(indiceOrigenSeleccionado);
            int indiceSuperior = columna.getCantidad() - 1;

            // A una fundación solo se mueve una carta, y debe ser la de arriba.
            if (cantidadSeleccionada != 1
                    || indiceCartaInicioSeleccionada != indiceSuperior) {
                mensaje.setText(
                        "A una fundación solo se mueve la carta de arriba"
                );
                return;
            }

            carta = columna.obtener(indiceSuperior);

            if (carta.getPalo().ordinal() != indiceFundacion) {
                mensaje.setText(
                        "Esa carta va en la fundación "
                                + carta.getPalo().getSimbolo()
                );
                return;
            }

            seMovio = partida.moverColumnaAFundacion(
                    indiceOrigenSeleccionado
            );
        }

        // El modelo comprueba que empiece con As y siga en orden ascendente.
        terminarMovimiento(seMovio);
    }

    // Actualiza el tablero y el mensaje después de intentar una jugada.
    private void terminarMovimiento(boolean seMovio) {
        if (!seMovio) {
            mensaje.setText("Ese movimiento no está permitido");
            return;
        }

        limpiarSeleccion();
        pistaActual = null;
        refrescarTablero();

        if (partida.haGanado()) {
            mensaje.setText("¡Ganaste!");
        } else if (!partida.hayMovimientosPosibles()) {
            mensaje.setText("No quedan movimientos posibles. Fin del juego.");
        } else {
            mensaje.setText("Movimiento realizado");
        }
    }

    // Revisa si la selección actual puede ir a una columna concreta.
    private boolean esDestinoColumnaValido(int indiceColumna) {
        if (zonaSeleccionada == null) {
            return false;
        }

        if (zonaSeleccionada == Pista.Zona.CELDA_LIBRE) {
            return partida.puedeMoverCeldaAColumna(
                    indiceOrigenSeleccionado,
                    indiceColumna
            );
        }

        return partida.puedeMoverSecuenciaColumnaAColumna(
                indiceOrigenSeleccionado,
                indiceColumna,
                cantidadSeleccionada
        );
    }

    // Revisa si la selección actual puede ir a la fundación indicada.
    private boolean esDestinoFundacionValido(int indiceFundacion) {
        if (zonaSeleccionada == null) {
            return false;
        }

        if (zonaSeleccionada == Pista.Zona.CELDA_LIBRE) {
            return partida.puedeMoverCeldaAFundacion(
                    indiceOrigenSeleccionado,
                    indiceFundacion
            );
        }

        return partida.puedeMoverColumnaAFundacion(
                indiceOrigenSeleccionado,
                indiceCartaInicioSeleccionada,
                cantidadSeleccionada,
                indiceFundacion
        );
    }

    // Una celda libre vacía es destino válido si tenemos una carta superior seleccionada.
    private boolean esDestinoCeldaLibreValido() {
        return zonaSeleccionada == Pista.Zona.COLUMNA
                && partida.puedeMoverColumnaACelda(
                        indiceOrigenSeleccionado,
                        indiceCartaInicioSeleccionada,
                        cantidadSeleccionada
                );
    }

    // Convierte los valores especiales en letras.
    private String obtenerTextoValor(int valor) {
        switch (valor) {
            case 1:
                return "A";
            case 11:
                return "J";
            case 12:
                return "Q";
            case 13:
                return "K";
            default:
                return String.valueOf(valor);
        }
    }

    // Devuelve rojo para corazones y diamantes, negro para los otros palos.
    private Color obtenerColorPalo(Carta.Palo palo) {
        if (palo == Carta.Palo.CORAZONES
                || palo == Carta.Palo.DIAMANTES) {
            return Color.RED;
        }

        return Color.BLACK;
    }

    // Crea una etiqueta para los espacios vacíos.
    private JLabel crearEtiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto, SwingConstants.CENTER);
        etiqueta.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        etiqueta.setOpaque(true);
        etiqueta.setBackground(Color.WHITE);
        etiqueta.setPreferredSize(new Dimension(75, 100));
        return etiqueta;
    }

    // Crea una etiqueta vacía que también responde al clic.
    private JLabel crearEtiqueta(String texto, Runnable accionAlHacerClic) {
        JLabel etiqueta = crearEtiqueta(texto);

        etiqueta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evento) {
                accionAlHacerClic.run();
            }
        });

        return etiqueta;
    }

    // Busca una jugada, resalta su origen y muestra el mensaje.
    private void mostrarPista() {
        limpiarSeleccion();
        pistaActual = partida.buscarPista();

        if (pistaActual == null) {
            mensaje.setText("No hay movimientos posibles");
        } else {
            mensaje.setText(pistaActual.getMensaje());
        }

        refrescarTablero();
    }

    // Quita la selección actual y deja sus índices en valores iniciales.
    private void limpiarSeleccion() {
        zonaSeleccionada = null;
        indiceOrigenSeleccionado = -1;
        indiceCartaInicioSeleccionada = -1;
        cantidadSeleccionada = 0;
    }

    // Cancela la selección actual y quita su resaltado naranja.
    private void cancelarSeleccion() {
        if (zonaSeleccionada == null) {
            mensaje.setText("No hay una carta seleccionada");
            return;
        }

        limpiarSeleccion();
        pistaActual = null;
        mensaje.setText("Selección cancelada");
        refrescarTablero();
    }

    // Intenta deshacer el último movimiento y vuelve a dibujar el tablero.
    private void deshacerMovimiento() {
        if (partida.deshacer()) {
            mensaje.setText("Se deshizo el último movimiento");
            pistaActual = null;
            limpiarSeleccion();
            refrescarTablero();
        } else {
            mensaje.setText("Todavía no hay movimientos para deshacer");
        }
    }
}
