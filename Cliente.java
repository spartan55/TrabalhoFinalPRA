
import java.net.*;
import java.io.*;
import java.util.Scanner;

/**
 * Classe que faz o acesso ao servidor
 *
 * @author Lucas Gutierrez
 * @author Marcos Balatka
 */
public class Cliente {

    public static void main(String[] args) {

        try {
            Socket cliente = new Socket("10.20.129.146", 12345);
            System.out.println("O cliente se conectou ao servidor!");

            Scanner teclado = new Scanner(System.in);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            PrintStream saida = new PrintStream(cliente.getOutputStream());

            String linha;
            System.out.println("1 : Busca\n2 : Remoção\n3 : Adicionar\n4 : Enviar arquivo para inserção\n-1: Sair");

            while (teclado.hasNextLine()) {
                linha = teclado.nextLine();
                if (linha.equals("-1")) {
                    System.out.println("Fechando conexao com o servidor");
                    break;
                } else if (linha.equals("4")) {
                    System.out.println("Nome do arquivo");
                    saida.println(linha);
                    linha = teclado.nextLine();
                    try {
                        BufferedReader buffRead = new BufferedReader(new FileReader(linha));

                        linha = buffRead.readLine();
                        while (linha != null) {
                            saida.println(linha);
                            System.out.println(entrada.readLine());
                            linha = buffRead.readLine();
                        }
                    } catch (Exception e) {
                        saida.println("EOF");
                    }
                    System.out.println("Concluido!");
                }
                saida.println(linha);
                System.out.println(entrada.readLine());

            }
            saida.close();
            entrada.close();
            teclado.close();
        } catch (Exception e) {
            System.out.println("Erro ao conectar ao servidor");
        }
    }
}
