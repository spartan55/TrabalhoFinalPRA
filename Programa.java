
import java.io.IOException;

/**
 * Classe que contém o main do servidor; Inicia o servidor
 *
 * @author Lucas Gutierrez
 * @author Marcos Balatka
 */
public class Programa {

    public static void main(String args[]) {
        Servidor servidor;

        try {
            servidor = new Servidor();
            servidor.loopEspera();
        } catch (IOException e) {
            System.out.println("Erro");
        }
    }
}
