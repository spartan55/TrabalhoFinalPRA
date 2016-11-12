
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Classe que atua no arquivo de índices.
 *
 * @author Lucas Gutierrez
 * @author Marcos Balatka
 */
public class ManipuladorIndice {

    private int campoChave;
    private String arquivoIndice;
    private String arquivoOriginal;
    private int maiores[];
    private int inicioChave;
    private int fimChave;

    /**
     * Método que retorna um ArrayList dos índices devidamente carregados para
     * uso no servidor.
     *
     * @param ind Nome do arquivo de índices
     * @param ori Nome do arquivo formatado
     * @param maior Vetor de maiores de cada coluna
     * @param chave IdChave
     * @return ArrayList com os índices devidamente carregados
     * @throws IOException Erro de IO
     */
    public ArrayList<Integer> carregaIndice(String ind, String ori, int[] maior, int chave) throws IOException {
        this.campoChave = chave;
        this.arquivoIndice = ind;
        this.arquivoOriginal = ori;
        this.maiores = maior;

        try {
            BufferedReader buffRead = new BufferedReader(new FileReader(arquivoIndice));
            System.out.println("Arquivo  de indices encontrado, carregar indices!!!");
            buffRead.close();
        } catch (Exception e) {
            System.out.println("Arquivo nao encontrado, criar arquivo de indices!!!");
            calculaPosicao();
            criaIndice();
        }

        BufferedReader buffRead = new BufferedReader(new FileReader(arquivoIndice));
        ArrayList<Integer> indices = new ArrayList<Integer>();
        String linha;

        indices.add(-2);
        indices.add(-2);

        linha = buffRead.readLine();
        linha = buffRead.readLine();

        while (true) {
            if (linha != null) {
                indices.add(pegaInteiro(linha));
            } else {
                break;
            }
            linha = buffRead.readLine();
        }
        buffRead.close();

        return indices;
    }

    // TODO comentar
    private void calculaPosicao() {
        int inicio = 0;

        for (int i = 0; i < campoChave - 1; i++) {
            inicio += maiores[i] + 1;
        }
        this.inicioChave = inicio;
        this.fimChave = inicio + maiores[campoChave - 1] - 1;
    }

    // TODO comentar
    private void criaIndice() throws IOException {
        BufferedReader buffRead = new BufferedReader(new FileReader(this.arquivoOriginal));
        BufferedWriter buffWrite = new BufferedWriter(new FileWriter(this.arquivoIndice));
        int i = 1;
        String linha;

        linha = buffRead.readLine();
        linha = buffRead.readLine();
        buffWrite.append(-2 + "\n");
        while (true) {
            if (linha != null) {
                buffWrite.append(pegaInteiro(linha.substring(this.inicioChave, this.fimChave)) + "\n");
            } else {
                break;
            }
            linha = buffRead.readLine();
        }

        buffRead.close();
        buffWrite.close();
    }

    /**
     * Método que abre o arquivo de índice e gera a árvore
     *
     * @return TreeMap devidamente criada
     *
     * @throws IOException Erro IO
     */
    public TreeMap criaArvore() throws IOException {
        TreeMap tm = new TreeMap();
        BufferedReader buffRead = new BufferedReader(new FileReader(arquivoIndice));
        String linha;

        linha = buffRead.readLine();
        int i = 1;
        while (true) {
            if (linha != null) {
                if (pegaInteiro(linha) != -1 && pegaInteiro(linha) != -2) {
                    tm.put(pegaInteiro(linha), i);
                }
                i++;
            } else {
                break;
            }
            linha = buffRead.readLine();
        }

        buffRead.close();

        return tm;
    }

    // TODO comentar
    private static int encontraMeio(String linha) {
        int i = 0;
        char letra = linha.charAt(i);

        while (letra != ';') {
            i++;
            letra = linha.charAt(i);
        }

        return i;
    }

    // TODO comentar
    private static int pegaInteiro(String linha) {
        StringBuffer sBuffer = new StringBuffer("");

        for (int i = 0; i < linha.length(); i++) {
            if (linha.charAt(i) == '\n' || linha.charAt(i) == ' ') {
                break;
            }
            sBuffer.append(linha.charAt(i));
        }
        return Integer.parseInt(sBuffer.toString());
    }

    /**
     * Método que altera os índices quando necessário
     *
     * @param indices ArrayList de índices
     *
     * @throws IOException Erro de IO
     */
    public void atualizaIndice(ArrayList<Integer> indices) throws IOException {
        BufferedWriter buffWrite = new BufferedWriter(new FileWriter(this.arquivoIndice));

        buffWrite.append(-2 + "\n");
        for (int i = 2; i < indices.size(); i++) {
            buffWrite.append(indices.get(i) + "\n");
        }

        buffWrite.close();
    }

    /**
     * Método de retorna o ID da linha
     *
     * @param linha Linha do arquivo
     *
     * @return Retorna o ID da linha
     */
    public int recuperaID(String linha) {
        calculaPosicao();
        return pegaInteiro(linha.substring(this.inicioChave, this.fimChave));
    }
}
