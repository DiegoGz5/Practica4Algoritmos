// Este nodo guarda una carta y sus enlaces dentro de la lista.
public class NodoDoble {

    // La carta que contiene este nodo.
    private Carta carta;

    // Enlace al nodo que está antes de este.
    private NodoDoble anterior;

    // Enlace al nodo que está después de este.
    private NodoDoble siguiente;

    // Al crear el nodo, recibimos la carta que va a guardar.
    public NodoDoble(Carta carta) {
        this.carta = carta;

        // Al inicio, el nodo se apunta a sí mismo.
        // Esto permite que también forme un círculo si es el único nodo.
        this.anterior = this;
        this.siguiente = this;
    }

    // Devuelve la carta guardada en este nodo.
    public Carta getCarta() {
        return carta;
    }

    // Devuelve el nodo anterior.
    public NodoDoble getAnterior() {
        return anterior;
    }

    // Cambia el nodo anterior.
    public void setAnterior(NodoDoble anterior) {
        this.anterior = anterior;
    }

    // Devuelve el nodo siguiente.
    public NodoDoble getSiguiente() {
        return siguiente;
    }

    // Cambia el nodo siguiente.
    public void setSiguiente(NodoDoble siguiente) {
        this.siguiente = siguiente;
    }
}