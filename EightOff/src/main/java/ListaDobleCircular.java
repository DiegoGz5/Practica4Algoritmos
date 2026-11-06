// Esta clase administra una lista circular de cartas con enlaces dobles.
public class ListaDobleCircular {

    // Guarda el primer nodo de la lista; si está vacía, será null.
    private NodoDoble primero;

    // Lleva la cuenta de cuántas cartas hay en la lista.
    private int cantidad;

    // Indica si la lista no tiene cartas.
    public boolean estaVacia() {
        return cantidad == 0;
    }

    // Devuelve cuántas cartas hay en la lista.
    public int getCantidad() {
        return cantidad;
    }

    // Agrega una carta al final de la lista.
    public void agregarAlFinal(Carta carta) {
        // No permitimos guardar una referencia vacía en lugar de una carta.
        if (carta == null) {
            throw new IllegalArgumentException("La carta no puede ser nula");
        }

        // Creamos el nodo que va a guardar la carta nueva.
        NodoDoble nuevo = new NodoDoble(carta);

        // Si no hay nodos, el nuevo será el primero y se apuntará a sí mismo.
        if (estaVacia()) {
            primero = nuevo;
        } else {
            // En una lista circular, el anterior del primero es el último.
            NodoDoble ultimo = primero.getAnterior();

            // Conectamos el último nodo con el nuevo.
            ultimo.setSiguiente(nuevo);
            nuevo.setAnterior(ultimo);

            // Cerramos el círculo: el nuevo apunta al primero.
            nuevo.setSiguiente(primero);
            primero.setAnterior(nuevo);
        }

        // La lista ahora tiene una carta más.
        cantidad++;
    }

    // Retira y devuelve la primera carta de la lista.
    public Carta quitarPrimera() {
        // No se puede retirar una carta si la lista está vacía.
        if (estaVacia()) {
            throw new IllegalStateException("La lista está vacía");
        }

        // Guardamos la carta antes de desconectar su nodo.
        Carta cartaRetirada = primero.getCarta();

        // Si era el único nodo, al quitarlo la lista queda vacía.
        if (cantidad == 1) {
            primero = null;
        } else {
            // El anterior del primero actual es el último nodo.
            NodoDoble ultimo = primero.getAnterior();

            // El siguiente del primero actual será el nuevo primero.
            primero = primero.getSiguiente();

            // Reconectamos el último con el nuevo primero en ambos sentidos.
            primero.setAnterior(ultimo);
            ultimo.setSiguiente(primero);
        }

        // La lista ahora tiene una carta menos.
        cantidad--;

        // Devolvemos la carta que retiramos.
        return cartaRetirada;
    }
}