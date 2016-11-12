
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Classe responsável por manipular e formatar o arquivo original.
 *
 * @author Lucas Gutierrez
 * @author Marcos Balatka
 */
public class ManipuladorArquivo extends IOException {

    private int maiores[];
    private String pathEntrada, pathSaida;
    private int quantCol;

    /**
     * Método que recebe o nome do arquivo de entrada e de saída, formatando
     * assim o arquivo de entrada no de saída; O arquivo de entrada é mantido
     * sem alterações e o de saída é reescrito
     *
     * @param entrada Arquivo original.
     *
     * @param saida Arquivo a formatar.
     *
     * @throws IOException Erro de IO.
     */
    public void formataArquivo(String entrada, String saida) throws IOException {
        this.pathEntrada = entrada;
        this.pathSaida = saida;
        geraMaiores();
        BufferedReader buffRead = new BufferedReader(new FileReader(this.pathEntrada));
        BufferedWriter buffWrite = new BufferedWriter(new FileWriter(this.pathSaida));
        String linha;
        linha = buffRead.readLine();
        String linhaFormatada;
        int i = 0;

        while (true) {
            if (linha != null) {
                linhaFormatada = separaLinha(linha);
                buffWrite.append(linhaFormatada + "\n");
                i++;
            } else {
                break;
            }
            linha = buffRead.readLine();
        }
        buffRead.close();
        buffWrite.close();
    }

    /**
     * Método que calcula o tamanho que cada coluna deverá ter, armazenando em
     * um vetor
     *
     * @throws IOException Erro de IO
     */
    private void geraMaiores() throws IOException {
        BufferedReader buffRead = new BufferedReader(new FileReader(this.pathEntrada));
        String linha = buffRead.readLine();

        this.quantCol = quantidadeColunas(linha);
        this.maiores = new int[quantCol];
        for (int i = 0; i < this.quantCol; i++) {
            maiores[i] = 0;
        }
        while (true) {
            if (linha != null) {
                maioresLinha(linha);
            } else {
                break;
            }
            linha = buffRead.readLine();
        }
        buffRead.close();
    }

    /**
     * Recebe uma linha e calcula o tamanho da coluna, caso seja maior que algum
     * anteriormente calculado, substitui no vetor.
     *
     * @param linha Linha do arquivo.
     */
    private void maioresLinha(String linha) {
        boolean aspas = false;
        Integer contador = 0;
        Integer index = 0;

        for (int i = 0; i < linha.length(); i++) {
            if (linha.charAt(i) == ';' && !aspas) {
                if (contador > maiores[index]) {
                    maiores[index] = contador;
                }
                contador = 0;
                index++;
            } else {
                contador++;
                if (linha.charAt(i) == '"') {
                    aspas = !aspas;
                }
            }
        }
        if (contador > maiores[index]) {
            maiores[index] = contador;
        }
    }

    /**
     * Retorna a quantidade de colunas na linha respectiva.
     *
     * @param linha Linha do arquivo
     *
     * @return Quantidade de colunas da linha
     */
    private int quantidadeColunas(String linha) {
        boolean aspas = false;
        int quantidade = 1;

        for (int i = 0; i < linha.length(); i++) {
            if (linha.charAt(i) == ';' && !aspas) {
                quantidade++;
            } else if (linha.charAt(i) == '"') {
                aspas = !aspas;
            }
        }

        return quantidade;
    }

    /**
     * Separa a linha considerando os tamanhos de coluna anteriormente
     * calculados.
     *
     * @param linha Linha do arquivo
     *
     * @return Linha com os espaços de coluna corretos.
     */
    public String separaLinha(String linha) {
        Integer contador = 0;
        Integer index = 0;
        boolean aspas = false;
        String linhaFormatada = new String();

        for (int i = 0; i < linha.length(); i++) {
            if (linha.charAt(i) == ';' && !aspas) {
                for (int j = 0; j < maiores[index] - contador; j++) {
                    linhaFormatada = linhaFormatada.concat(" ");
                }
                linhaFormatada = linhaFormatada.concat(linha.substring(i, i + 1));
                index++;
                contador = 0;
            } else {
                linhaFormatada = linhaFormatada.concat(linha.substring(i, i + 1));
                contador++;
                if (linha.charAt(i) == '"') {
                    aspas = !aspas;
                }
            }
        }

        return linhaFormatada;
    }

    public int[] getMaiores() {
        return maiores;
    }

    /**
     * Dado o número da linha, retornar a linha respectiva.
     *
     * @param nLinha Número da linha.
     *
     * @return A linha desejada.
     *
     * @throws IOException Erros de IO.
     */
    public String buscaLinha(int nLinha) throws IOException {
        BufferedReader buffRead = new BufferedReader(new FileReader(this.pathSaida));
        String registro = "";

        for (int i = 0; i < nLinha; i++) {
            registro = buffRead.readLine();
        }
        buffRead.close();

        return registro;
    }

    /**
     * Recebe uma nova linha e a insere no arquivo formatado.
     *
     * @param registro Linha a ser inserida.
     *
     * @throws IOException Erros de IO.
     */
    public void insereLinha(String registro) throws IOException {
        BufferedWriter buffWrite = new BufferedWriter(new FileWriter(this.pathSaida, true));
        buffWrite.append(registro + "\n");
        buffWrite.close();
    }

    /**
     * Recebe uma nova linha e a insere no arquivo original.
     *
     * @param registro Linha a ser inserida.
     *
     * @throws IOException Erros de IO.
     */
    public void insereLinhaArqOriginal(String registro) throws IOException {
        BufferedWriter buffWrite = new BufferedWriter(new FileWriter(this.pathEntrada, true));
        buffWrite.append(registro + "\n");
        buffWrite.close();
    }
}
