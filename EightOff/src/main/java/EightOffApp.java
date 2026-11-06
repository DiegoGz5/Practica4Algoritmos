import javax.swing.SwingUtilities;

// Esta clase inicia la aplicación.
public class EightOffApp {

    // Java comienza la ejecución aquí.
    public static void main(String[] args) {

        // Inicia la ventana en el hilo de eventos de Swing.
        SwingUtilities.invokeLater(() -> {
            VentanaJuego ventana = new VentanaJuego();
            ventana.setVisible(true);
        });
    }
}