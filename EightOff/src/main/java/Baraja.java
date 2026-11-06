// Estas clases de Java nos ayudarán a mezclar las cartas temporalmente.
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Esta clase representa la baraja que usaremos para repartir.
public class Baraja {

    // Aquí guardamos la baraja en la lista doble circular que hicimos.
    private ListaDobleCircular cartas;

    // Al crear una baraja, la llenamos y la mezclamos.
    public Baraja() {
        cartas = new ListaDobleCircular();
        crearYMezclar();
    }

    // Crea las 52 cartas y las deja en orden aleatorio.
    private void crearYMezclar() {
        // Esta lista de Java solo se usa mientras mezclamos.
        List<Carta> cartasTemporales = new ArrayList<>();

        // Recorremos cada uno de los cuatro palos.
        for (Carta.Palo palo : Carta.Palo.values()) {

            // Para cada palo, creamos los valores del As al Rey.
            for (int valor = 1; valor <= 13; valor++) {
                cartasTemporales.add(new Carta(palo, valor));
            }
        }

        // Reordena al azar las cartas temporales.
        Collections.shuffle(cartasTemporales);

        // Pasamos las cartas ya mezcladas a nuestra lista circular.
        for (Carta carta : cartasTemporales) {
            cartas.agregarAlFinal(carta);
        }
    }

    // Retira y entrega la siguiente carta de la baraja.
    public Carta repartirCarta() {
        return cartas.quitarPrimera();
    }

    // Indica cuántas cartas quedan por repartir.
    public int getCantidad() {
        return cartas.getCantidad();
    }

    // Permite saber si ya se repartieron todas las cartas.
    public boolean estaVacia() {
        return cartas.estaVacia();
    }
}