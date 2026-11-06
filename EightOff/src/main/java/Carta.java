// Comparable permite comparar esta carta con otra usando compareTo.
public class Carta implements Comparable<Carta> {

    // El enum reúne los cuatro palos posibles.
    // El texto de cada palo es el símbolo que mostraremos en pantalla.
    public enum Palo {
        TREBOLES("♣"),
        DIAMANTES("♦"),
        PICAS("♠"),
        CORAZONES("♥");

        // Guardamos el símbolo asociado a este palo.
        private final String simbolo;

        // Este constructor se usa al definir cada palo arriba.
        Palo(String simbolo) {
            this.simbolo = simbolo;
        }

        // Permite obtener el símbolo del palo desde una carta.
        public String getSimbolo() {
            return simbolo;
        }
    }

    // Estos dos datos identifican a cada carta.
    private final Palo palo;
    private final int valor; // As = 1, Jota = 11, Reina = 12, Rey = 13

    // Al crear una carta, recibimos su palo y su valor.
    public Carta(Palo palo, int valor) {
        // Evitamos crear una carta sin palo.
        if (palo == null) {
            throw new IllegalArgumentException("El palo no puede ser nulo");
        }

        // Una carta solo puede tener un valor del 1 al 13.
        if (valor < 1 || valor > 13) {
            throw new IllegalArgumentException("El valor debe estar entre 1 y 13");
        }

        // Si los datos son válidos, los guardamos en esta carta.
        this.palo = palo;
        this.valor = valor;
    }

    // Devuelve el palo de esta carta.
    public Palo getPalo() {
        return palo;
    }

    // Devuelve el valor numérico de esta carta.
    public int getValor() {
        return valor;
    }

    // Compara primero el valor y, si empatan, usa el palo para desempatar.
    @Override
    public int compareTo(Carta otra) {
        int comparacionDeValor = Integer.compare(this.valor, otra.valor);

        // Si los valores son distintos, ya sabemos cuál carta va primero.
        if (comparacionDeValor != 0) {
            return comparacionDeValor;
        }

        // Si tienen el mismo valor, el orden del enum desempata por palo.
        return Integer.compare(this.palo.ordinal(), otra.palo.ordinal());
    }

    // Convierte la carta a texto, por ejemplo: "A♠", "7♦" o "K♣".
    @Override
    public String toString() {
        String nombre;

        // Usamos letras para las figuras y números para las demás cartas.
        switch (valor) {
            case 1 -> nombre = "A";
            case 11 -> nombre = "J";
            case 12 -> nombre = "Q";
            case 13 -> nombre = "K";
            default -> nombre = String.valueOf(valor);
        }

        // Juntamos el nombre de la carta con el símbolo de su palo.
        return nombre + palo.getSimbolo();
    }
}