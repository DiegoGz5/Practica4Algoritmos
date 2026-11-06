// Esta lista simple puede guardar objetos de cualquier tipo.
public class ListaSimple<T> {

    // Cada nodo guarda un dato y apunta al siguiente nodo.
    private static class Nodo<T> {
        private T dato;
        private Nodo<T> siguiente;

        private Nodo(T dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }

    // Referencia al primer nodo; null significa que la lista está vacía.
    private Nodo<T> primero;

    // Guardamos el tamaño para no tener que contar los nodos cada vez.
    private int cantidad;

    // Indica si la lista está vacía.
    public boolean estaVacia() {
        return cantidad == 0;
    }

    // Devuelve cuántos elementos hay guardados.
    public int getCantidad() {
        return cantidad;
    }

    // Agrega un elemento al final de la lista.
    public void agregarAlFinal(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException("El dato no puede ser nulo");
        }

        Nodo<T> nuevo = new Nodo<>(dato);

        // Si no hay elementos, el nuevo nodo se convierte en el primero.
        if (estaVacia()) {
            primero = nuevo;
        } else {
            // Buscamos el último nodo recorriendo los enlaces.
            Nodo<T> actual = primero;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }

            // El último nodo ahora apunta al nuevo.
            actual.siguiente = nuevo;
        }

        cantidad++;
    }

    // Devuelve el elemento que está en la posición indicada, empezando desde 0.
    public T obtener(int indice) {
        validarIndice(indice);

        Nodo<T> actual = primero;

        // Avanzamos hasta la posición que nos pidieron.
        for (int i = 0; i < indice; i++) {
            actual = actual.siguiente;
        }

        return actual.dato;
    }

    // Retira y devuelve el elemento de la posición indicada.
    public T quitar(int indice) {
        validarIndice(indice);

        Nodo<T> actual = primero;

        // Si piden la posición 0, cambiamos el primer nodo.
        if (indice == 0) {
            primero = primero.siguiente;
        } else {
            // Buscamos el nodo anterior al que queremos quitar.
            Nodo<T> anterior = primero;
            for (int i = 0; i < indice - 1; i++) {
                anterior = anterior.siguiente;
            }

            // Guardamos el nodo que se va a retirar.
            actual = anterior.siguiente;

            // El nodo anterior ahora salta sobre el nodo retirado.
            anterior.siguiente = actual.siguiente;
        }

        cantidad--;
        return actual.dato;
    }

    // Revisa que la posición exista antes de acceder a ella.
    private void validarIndice(int indice) {
        if (indice < 0 || indice >= cantidad) {
            throw new IndexOutOfBoundsException("Posición fuera de la lista: " + indice);
        }
    }
    // Crea otra lista con los mismos elementos y en el mismo orden.
    public ListaSimple<T> copiar() {
        ListaSimple<T> copia = new ListaSimple<>();

        // Empezamos en el primer nodo de esta lista.
        Nodo<T> actual = primero;

        // Recorremos los nodos hasta llegar al final.
        while (actual != null) {
            // Agregamos el dato a la nueva lista.
            copia.agregarAlFinal(actual.dato);

            // Avanzamos al nodo siguiente.
            actual = actual.siguiente;
        }

        return copia;
    }
}