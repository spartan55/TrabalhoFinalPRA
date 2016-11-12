
import java.util.*;
import java.net.*;
import java.io.*;

/**
 * Servidor que processa e armazena os dados dos arquivos.
 *
 * @author Lucas Gutierrez
 * @author Marcos Balatka
 */
public class Servidor extends IOException {

    private final String arquivoOriginal = "sample.csv";
    private final String arquivoFormatado = "sample.pra";
    private final String arquivoIndice = "indice.pra";
    private final int colunaChave = 2;
    private final int porta = 12345;
    private TreeMap tm;
    private Set set;
    private Iterator ite;
    private final ManipuladorArquivo ma = new ManipuladorArquivo();
    private final ManipuladorIndice mi = new ManipuladorIndice();
    private ServerSocket servidor;
    private final int limite;
    private Socket cliente;
    private final ArrayList<Integer> indices;

    /**
     * Construtor formata os arquivos, carrega a TreeMap e abre o servidor
     *
     * @throws IOException Erros de IO.
     */
    public Servidor() throws IOException {
        limite = 0;
        ma.formataArquivo(arquivoOriginal, arquivoFormatado);
        indices = mi.carregaIndice(arquivoIndice, arquivoFormatado, ma.getMaiores(), colunaChave); //nome do arquivo do indice, nome do arquivo formatado, vetor de maiores, numero da coluna com campo chave
        carregaArvore();
        abreServidor();
    }

    /**
     * Método que cria uma thread que recebe entradas do servidor a fim de
     * atualizar o arquivo de índice
     *
     * @throws IOException Erros de IO.
     */
    public void atualizarArquivo() throws IOException {
        new Thread(() -> {
            Scanner teclado = new Scanner(System.in);
            String cmd;
            while (true) {
                cmd = teclado.nextLine();
                if (cmd.equals("Atualizar")) {
                    synchronized (this) {
                        try {
                            mi.atualizaIndice(indices);
                            System.out.println("Arquivo atualizado");
                        } catch (Exception e) {
                            System.out.println("Problemas na atualização");
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * Abre o servidor de dados e começa a espera por clientes.
     *
     * @throws IOException Erros de IO.
     */
    private void abreServidor() throws IOException {
        servidor = new ServerSocket(porta);
        System.out.println("Porta " + porta + " aberta!");
    }

    /**
     * Cria a TreeMap usando o arquivo de índices devidamente criado.
     *
     * @throws IOException Erros de IO.
     */
    private void carregaArvore() throws IOException {
        tm = mi.criaArvore();
        set = tm.entrySet();
        ite = set.iterator();
    }

    /**
     * Cria-se a Thread que espera entradas de teclado do servidor e em seguida
     * entra-se em um loop de espera de cliente; Ao chegar um novo cliente,
     * cria-se uma thread que espera solicitações do referido cliente.
     *
     * @throws IOException Erros de IO.
     */
    public void loopEspera() throws IOException {
        atualizarArquivo();

        while (true) {
            cliente = servidor.accept();
            new Thread(() -> {
                Socket c = cliente;
                System.out.println("Nova conexão com o cliente " + c.getInetAddress().getHostAddress());
                try {
                    Scanner s = new Scanner(c.getInputStream());
                    PrintStream saida = new PrintStream(c.getOutputStream());
                    int nReg, menu;
                    String resultado;
                    String linha;

                    while (s.hasNextLine()) {
                        menu = Integer.parseInt(s.nextLine());
                        if (menu == 1) {
                            saida.println("Número do registro: ");
                            nReg = Integer.parseInt(s.nextLine());
                            try {
                                resultado = buscaRegistro(nReg);
                                saida.println(resultado);
                                System.out.println(c.getInetAddress().getHostAddress() + " realizou busca");
                            } catch (Exception e) {
                            }
                        } else if (menu == 2) {
                            saida.println("Número do registro: ");
                            nReg = Integer.parseInt(s.nextLine());
                            try {
                                resultado = removeRegistro(nReg);
                                saida.println(resultado);
                                System.out.println(c.getInetAddress().getHostAddress() + " realizou remoção");
                            } catch (Exception e) {
                            }
                        } else if (menu == 3) {
                            saida.println("Digite as informações a serem inseridas:");
                            linha = s.nextLine();
                            try {
                                resultado = insereRegistro(linha);
                                saida.println(resultado);
                                System.out.println(c.getInetAddress().getHostAddress() + " realizou inserção");
                            } catch (Exception e) {
                            }
                        } else if (menu == 4) {
                            saida.println("Nome do arquivo a ser usado:");
                            linha = s.nextLine();
                            try {
                                while (!linha.equals("EOF")) {
                                    resultado = insereRegistro(linha);
                                    saida.println(resultado);
                                    System.out.println(c.getInetAddress().getHostAddress() + " realizou inserção");
                                    linha = s.nextLine();
                                }
                            } catch (Exception e) {
                            }
                        } else if (menu == -1) {
                            break;
                        } else {
                            saida.println("Valor inválido!");
                        }
                    }
                    System.out.println("Conexão " + c.getInetAddress().getHostAddress() + " encerrada");
                    mi.atualizaIndice(indices);
                } catch (IOException e) {
                }
            }).start();
        }
    }

    /**
     * Método que recebe um id e retorna o registro correspondente ao id ao
     * cliente que solicitou.
     *
     * @param nReg Id do registro desejado.
     *
     * @return Linha respectiva, ou mensagem de erro.
     *
     * @throws IOException Erros de IO.
     */
    private String buscaRegistro(int nReg) throws IOException {
        if (tm.get(nReg) != null) {
            String resultBusca;
            int linhaRegistro = Integer.parseInt(tm.get(nReg).toString());
            resultBusca = ma.buscaLinha(linhaRegistro);
            return resultBusca;
        }

        return "Resultado não encontrado!";

    }

    /**
     * Dado um id lançado pelo usuário, o método exclui o registro respectivo; A
     * exclusão acontece apenas no arquivo de índices inicialmente e, ou
     * solicitado pelo servidor, ou ao ter um usuário desconectado os arquivos
     * são devidamente atualizados; O método garante exclusividade de execução.
     *
     * @param nReg Id do registro desejado.
     *
     * @return Mensagem de execução.
     *
     * @throws IOException Erro de IO.
     */
    private synchronized String removeRegistro(int nReg) throws IOException {
        if (tm.get(nReg) != null) {
            String resultBusca;
            int linhaRegistro = Integer.parseInt(tm.get(nReg).toString());
            System.out.println("Linha Registro:" + linhaRegistro);
            tm.remove(nReg);
            indices.set(linhaRegistro, -1);
            resultBusca = "Registro Excluido!!!";
            System.out.println(resultBusca);

            return resultBusca;
        }

        return "Registro nao encontrado";
    }

    /**
     * Dado um id lançado pelo usuário, o método adiciona o registro respectivo;
     * O método garante exclusividade de execução.
     *
     * @param novoRegistro Linha a ser inserida.
     *
     * @return Mensagem de execução.
     *
     * @throws IOException Erro de IO.
     */
    private synchronized String insereRegistro(String novoRegistro) throws IOException {
        String regFormatado = ma.separaLinha(novoRegistro);
        System.out.println(regFormatado);
        int novoID = mi.recuperaID(regFormatado);
        if (tm.get(novoID) != null) {
            return "Nao foi possivel adicionar o novo registro pois ja existe um registro com esse ID";
        }

        indices.add(novoID);
        tm.put(novoID, indices.size() - 1);
        ma.insereLinha(regFormatado);
        ma.insereLinhaArqOriginal(novoRegistro);

        return "O registro foi inserido com sucesso";
    }
}
