/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lapr1_analiseredessociais;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Formatter;
import java.util.Scanner;
import org.la4j.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.decomposition.EigenDecompositor;
import static lapr1_analiseredessociais.Utilitarios.*;
import static lapr1_analiseredessociais.UtilitariosOutput.*;

/**
 *
 * @author UTeam
 */
public class LAPR1_AnaliseRedesSociais {

    /**
     * @param args the command line arguments
     */
    static int NODE_FIELDS = 5;
    static int BRANCH_FIELDS = 3;
    static int MAX_N_NODES = 200; //Número máximo de nós definido pelo cliente. Nota: Valores superiores a 200 poderão desformatar o output
    static String INFO_SPLIT = ",";
    static String FILE_NAME_SPLIT = "\\.";
    static String GRAPH_TYPE_SPLIT = ":";
    static String FILE_NAME_EXTENSION = "csv";
    static int SC_NOTATION_LIMIT = 1000000;
    static Scanner in = new Scanner(System.in);

    public static void main(String[] args) throws FileNotFoundException {

        // Validação dos args
        String mediaNodes = args[args.length - 2];
        String mediaBranches = args[args.length - 1];
        boolean oriented = true;
        int op = paramValidation(args);
        boolean val = validations(op, mediaNodes, mediaBranches);
        if (val == true) {
            int nNodes = lineCounter(mediaNodes);
            int k;
            // Matriz dos nós
            String[][] nodes = new String[nNodes][NODE_FIELDS];
            int validation = loadData(nodes, mediaNodes, NODE_FIELDS, MAX_N_NODES);
            if (validation == -1) {
                return;
            }
            if (getGraphType(mediaBranches) == -1) {
                oriented = false;
            }
            switchCol(nodes, nNodes);
            // Matriz dos ramos
            int branchLines = lineCounter(mediaBranches);
            String[][] branches = new String[branchLines][BRANCH_FIELDS];
            int nBranches = loadDataBranches(branches, mediaBranches, BRANCH_FIELDS, branchLines, nNodes, oriented);

            // Matriz das Adjacências
            int[][] adjMatrix = new int[nNodes][nNodes];
            buildAdjacentMatrix(adjMatrix, branches, nBranches, oriented);

            // Vetor  com o grau de saída cada nó
            int[] nodeDegreeVec = new int[nNodes];
            storeNodeDegrees(adjMatrix, nodeDegreeVec, nNodes, true);

            String socialNetworkName = getSocialNetworkName(args);
            String date = getDate();
            String outFileName = genFileName(socialNetworkName, date);
            Formatter out = createAndOpenOutputFile(outFileName, op);
            switch (op) {
                case 0:
                    //======================MENU========================
                    int option;
                    if (!oriented) {
                        do {
                            option = optionMenuNonOriented();
                            switch (option) {
                                case 1: //Mostar dados
                                    outNodesBranchesInfo(out, nodes, branches, nNodes, nBranches);
                                    break;
                                case 2: //Grau dos nós
                                    outVecID(out, "GRAU DOS NÓS");
                                    outIntVec(out, nodeDegreeVec, nodes, nNodes);
                                    opensMostRelevantNode(nodeDegreeVec, nNodes, nodes);
                                    break;
                                case 3: //Centralidade de Vetor Próprio
                                    Matrix mat = createMatrixObject(adjMatrix);
                                    Matrix[] mattD = callDecomposeMethods(mat);
                                    double[][] eigenvectorMatrix = getEigenvectorMatrix(mattD);
                                    double[][] eigenValueMatrix = getEigenValueMatrix(mattD);
                                    double[] eigenvector = getEigenvector(eigenvectorMatrix, eigenValueMatrix, nNodes);
                                    outVecID(out, "CENTRALIDADE DOS NÓS");
                                    outDoubleVec(out, eigenvector, nodes, nNodes);
                                    opensMostRelevantNodeDouble(eigenvector, nNodes, nodes);
                                    break;
                                case 4: //Grau médio
                                    double averageDegree = averageDegree(nodeDegreeVec, nNodes);
                                    out.format("%n%n%s%.3f%n%n", "GRAU MÉDIO: ", averageDegree);
                                    break;
                                case 5: //Densidade
                                    double density = getDensity(nodeDegreeVec, nNodes);
                                    out.format("%n%n%s%.3f%n%n", "DENSIDADE: ", density);
                                    break;
                                case 6: //Potências da Matriz de Adjacências
                                    System.out.println("Insira o Comprimento Máximo do Percurso:");
                                    k = in.nextInt();
                                    if (k >= 1) {
                                        outPowersAdjacentMatrix(out, adjMatrix, k, nNodes);
                                    } else {
                                        System.out.println("Valor inválido");
                                    }
                                    break;
                                case 0: //Exit
                                    System.out.println("Deseja realmente terminar? (s/n)");
                                    char confirmation = (in.next()).charAt(0);
                                    if (confirmation != 's' && confirmation != 'S') {
                                        option = 1;
                                    }
                                    break;
                                default:
                                    System.out.println("Opção inválida");
                                    break;
                            }
                        } while (option != 0);
                    } else {
                        do {
                            option = optionMenuOriented();
                            switch (option) {
                                case 1:
                                    outNodesBranchesInfo(out, nodes, branches, nNodes, nBranches);
                                    break;
                                case 2:
                                    int[] inNodeDegreeVec = new int[nNodes];
                                    storeNodeDegrees(adjMatrix, inNodeDegreeVec, nNodes, false);
                                    outVecID(out, "GRAU DE ENTRADA DOS NÓS");
                                    outIntVec(out, inNodeDegreeVec, nodes, nNodes);
                                    opensMostRelevantNode(inNodeDegreeVec, nNodes, nodes);
                                    break;
                                case 3:
                                    outVecID(out, "GRAU DE SAÍDA DOS NÓS");
                                    outIntVec(out, nodeDegreeVec, nodes, nNodes);
                                    opensMostRelevantNode(nodeDegreeVec, nNodes, nodes);
                                    break;
                                case 4:
                                    int optionPR;
                                    double d;
                                    double[][] stochasticMatrix = new double[nNodes][nNodes];
                                    double[][] M = new double[nNodes][nNodes];
                                    double[] unnormalizedVec;
                                    double[] pageRankIt = new double[nNodes];
                                    double[] firstCol = new double[nNodes];
                                    double[] pageRankEig = new double[nNodes];
                                    createStochasticMatrix(adjMatrix, stochasticMatrix, nNodes, nodeDegreeVec);
                                    do {
                                        optionPR = optionMenuPageRank();
                                        switch (optionPR) {
                                            case 1:
                                                out.format("%n%s%n", "Introduza um Valor para o \"damping value\" entre [0, 1]:");
                                                d = in.nextDouble();
                                                getMatrixM(M, stochasticMatrix, d, nNodes);
                                                out.format("%n%s%n", "Introduza um Número de Iterações \"k\":");
                                                k = in.nextInt();
                                                outVecID(out, "EVOLUÇÃO DO VETOR PAGERANK PELO MÉTODO ITERATIVO");
                                                outPageRankIterID(out, nNodes);
                                                unnormalizedVec = getPageRankVec(M, k, nNodes, out);
                                                normalizePageRankVec(unnormalizedVec, pageRankIt);
                                                outVecID(out, "VETOR PAGERANK NORMALIZADO DA k-ENÉSIMA ITERAÇÃO");
                                                outPageRankVec(out, pageRankIt);
                                                opensMostRelevantNodeDouble(pageRankIt, nNodes, nodes);
                                                break;
                                            case 2:
                                                out.format("%n%s%n", "Introduza um Valor para o \"damping value\" entre [0, 1]:");
                                                d = in.nextDouble();
                                                getMatrixM(M, stochasticMatrix, d, nNodes);
                                                Matrix a = new Basic2DMatrix(M);
                                                EigenDecompositor eigenD = new EigenDecompositor(a);
                                                Matrix[] mattD = eigenD.decompose();
                                                double matA[][] = mattD[0].toDenseMatrix().toArray();
                                                for (int g = 0; g < nNodes; g++) {
                                                    firstCol[g] = matA[g][0];
                                                }
                                                normalizePageRankVec(firstCol, pageRankEig);
                                                outVecID(out, "VETOR PAGERANK NORMALIZADO OBTIDO PELO MÉTODO DO VALOR E VETOR PRÓPRIOS");
                                                outPageRankVec(out, pageRankEig);
                                                opensMostRelevantNodeDouble(pageRankEig, nNodes, nodes);
                                                break;
                                            case 0:
                                                break;
                                            default:
                                                System.out.println("Opção inválda");
                                                break;
                                        }
                                    } while (optionPR != 0);
                                    break;
                                case 0: //Exit
                                    System.out.println("Deseja realmente terminar? (s/n)");
                                    char confirmation = (in.next()).charAt(0);
                                    if (confirmation != 's' && confirmation != 'S') {
                                        option = 1;
                                    }
                                    break;
                            }
                        } while (option != 0);
                    }
                    break;
                case 1:
                    //=================FILE_OUTPUT_NONORIENTED=================
                    if (!oriented) {
                        k = Integer.parseInt(args[2]);
                        out.format("%s%s%n", "ANÁLISE DA REDE SOCIAL: ", socialNetworkName.toUpperCase());
                        out.format("%s%n", "Tipo de Rede: Não Orientada");
                        // Mostrar dados
                        outNodesBranchesInfo(out, nodes, branches, nNodes, nBranches);
                        // Grau dos nós
                        outVecID(out, "GRAU DOS NÓS");
                        outIntVec(out, nodeDegreeVec, nodes, nNodes);
                        // Centralidade do vetor
                        Matrix mat = createMatrixObject(adjMatrix);
                        Matrix[] mattD = callDecomposeMethods(mat);
                        double[][] eigenvectorMatrix = getEigenvectorMatrix(mattD);
                        double[][] eigenValueMatrix = getEigenValueMatrix(mattD);
                        double[] eigenvector = getEigenvector(eigenvectorMatrix, eigenValueMatrix, nNodes);
                        outVecID(out, "CENTRALIDADE DOS NÓS");
                        outDoubleVec(out, eigenvector, nodes, nNodes);
                        //Grau médio
                        double averageDegree = averageDegree(nodeDegreeVec, nNodes);
                        out.format("%n%n%s%.3f%n%n", "GRAU MÉDIO: ", averageDegree);
                        //Densidade
                        double density = getDensity(nodeDegreeVec, nNodes);
                        out.format("%n%n%s%.3f%n%n", "DENSIDADE: ", density);
                        //Potências da Matriz de Adjacências
                        outPowersAdjacentMatrix(out, adjMatrix, k, nNodes);
                        out.close();
                    } else {
                        System.out.println("Erro: Argumentos inválidos");
                    }
                    break;
                case 2:
                    //==================FILE_OUTPUT_ORIENTED==================
                    if (oriented) {
                        k = Integer.parseInt(args[2]);
                        double d = Double.parseDouble(args[4]);
                        out.format("%s%s%n", "ANÁLISE DA REDE SOCIAL: ", socialNetworkName.toUpperCase());
                        out.format("%s%n", "Tipo de Rede: Orientada");
                        outNodesBranchesInfo(out, nodes, branches, nNodes, nBranches);
                        //GRAU DE ENTRADA
                        int[] inNodeDegreeVec = new int[nNodes];
                        storeNodeDegrees(adjMatrix, inNodeDegreeVec, nNodes, false);
                        outVecID(out, "GRAU DE ENTRADA DOS NÓS");
                        outIntVec(out, inNodeDegreeVec, nodes, nNodes);
                        //GRAU DE SAÍDA
                        outVecID(out, "GRAU DE SAÍDA DOS NÓS");
                        outIntVec(out, nodeDegreeVec, nodes, nNodes);
                        //PAGERANK
                        double[][] stochasticMatrix = new double[nNodes][nNodes];
                        double[][] M = new double[nNodes][nNodes];
                        double[] unnormalizedVec;
                        double[] pageRankIt = new double[nNodes];
                        double[] firstCol = new double[nNodes];
                        double[] pageRankEig = new double[nNodes];
                        createStochasticMatrix(adjMatrix, stochasticMatrix, nNodes, nodeDegreeVec);
                        getMatrixM(M, stochasticMatrix, d, nNodes);
                        outVecID(out, "EVOLUÇÃO DO VETOR PAGERANK PELO MÉTODO ITERATIVO");
                        outPageRankIterID(out, nNodes);
                        unnormalizedVec = getPageRankVec(M, k, nNodes, out);
                        normalizePageRankVec(unnormalizedVec, pageRankIt);
                        outVecID(out, "VETOR PAGERANK NORMALIZADO DA k-ENÉSIMA ITERAÇÃO");
                        outPageRankVec(out, pageRankIt);
                        Matrix a = new Basic2DMatrix(M);
                        EigenDecompositor eigenD = new EigenDecompositor(a);
                        Matrix[] mattD = eigenD.decompose();
                        double matA[][] = mattD[0].toDenseMatrix().toArray();
                        for (int g = 0; g < nNodes; g++) {
                            firstCol[g] = matA[g][0];
                        }
                        normalizePageRankVec(firstCol, pageRankEig);
                        outVecID(out, "VETOR PAGERANK NORMALIZADO OBTIDO PELO MÉTODO DO VALOR E VETOR PRÓPRIOS");
                        outPageRankVec(out, pageRankEig);
                        out.close();
                        break;
                    } else {
                        System.out.println("Erro: Argumentos inválidos");
                    }
                default:
                    System.out.println("Erro: Argumentos inválidos");
                    break;
            }
        }
    }

    // ______________MÉTODOS DO MENU______________
    /**
     * Método de validação dos argumentos da linha de comandos. Para args
     * passados no formato (...) -k *NUM* -t (...) , as próximas linhas
     * reorganizam os args de modo a devolver (...) -t -k *NUM* (...) No caso de
     * uma rede orientada, deve ser specificada em primeiro lugar a flag -t, de
     * seguida as flags -k -d podem permutar entre si, desde que precedam os
     * seus respetivos valores.
     *
     * @param args
     * @return
     */
    public static int paramValidation(String[] args) {
        int nParam = args.length;
        int k;
        double d;
        switch (nParam) {
            case 3: // num de args necessários para ir ao menu
                if (args[0].equals("-n")) {
                    return 0;
                }
                break;
            case 5: // num de args necessários para fazer output para ficheiro com rede não orientada
                if (args[0].equals("-t") && args[1].equals("-k")) {
                    k = Integer.parseInt(args[2]);
                    if (k >= 1) {
                        return 1;
                    }
                } else if (args[0].equals("-k") && args[2].equals("-t")) {
                    k = Integer.parseInt(args[1]);
                    if (k >= 1) {
                        String aux = args[0];
                        args[0] = args[2];
                        args[2] = args[1];
                        args[1] = aux;
                        return 1;
                    }
                }
                break;
            case 7: // num de args necessários para fazer output para ficheiro com rede orientada
                if (args[0].equals("-t") && args[1].equals("-k") && args[3].equals("-d")) {
                    k = Integer.parseInt(args[2]);
                    d = Double.parseDouble(args[4]);
                    if (k >= 1 && d <= 1 && d >= 0) {
                        return 2;
                    }
                } else if (args[0].equals("-t") && args[1].equals("-d") && args[3].equals("-k")) {
                    k = Integer.parseInt(args[4]);
                    d = Double.parseDouble(args[2]);
                    if (k >= 1 && d <= 1 && d >= 0) {
                        String aux = args[1];
                        args[1] = args[3];
                        args[3] = aux;
                        aux = args[2];
                        args[2] = args[4];
                        args[4] = aux;
                        return 2;
                    }
                }
                break;
        }
        return -1;
    }

    /**
     * Método que engloba algumas validações fundamentais ao funcionamento
     * correto do programa
     *
     * @param op - referente à validação do número de argumentos passados na
     * linha de comandos
     * @param mediaNodes - variável com o nome do ficheiro dos nós
     * @param mediaBranches - variável com o nome do ficheiro dos ramos
     * @return boolean
     * @throws FileNotFoundException
     */
    public static boolean validations(int op, String mediaNodes, String mediaBranches) throws FileNotFoundException {
        if (op != 0 && op != 1 && op != 2) {
            System.out.println("Erro: Verifique os argumentos");
            return false;
        } else if (!checkFileNameExtension(mediaNodes) || !checkFileNameExtension(mediaBranches)) {
            System.out.println("Erro: Extensão do(s) ficheiro(s) inválida");
            return false;
        } else if (!checkHeaderNodes(mediaNodes) || !checkHeaderBranches(mediaBranches)) {
            System.out.println("Erro: Os ficheiros não respeitam a estrutura definida");
            return false;
        } else if (lineCounter(mediaNodes) > MAX_N_NODES) {
            System.out.println("Erro: Número de nós excede o máximo definido (" + MAX_N_NODES + ")");
            return false;
        } else if (getGraphType(mediaBranches) == 0) {
            System.out.println("Erro: Tipo de rede inválido");
            return false;
        } else {
            return true;
        }
    }

    /**
     * Método que verifica o tipo do grafo (1 - orientado; -1 - não orientado; 0
     * - informação inválida ou inexistente)
     *
     * @param fileName
     * @throws FileNotFoundException
     * @return
     */
    public static int getGraphType(String fileName) throws FileNotFoundException {
        Scanner fInput = new Scanner(new File(fileName));
        String temp[] = fInput.nextLine().split(GRAPH_TYPE_SPLIT);
        if (temp.length == 2) {
            if (temp[0].trim().equals("networkType") && temp[1].trim().equals("oriented")) {
                return 1;
            } else if (temp[0].trim().equals("networkType") && temp[1].trim().equals("nonoriented")) {
                return -1;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    /**
     * Interface do menu para redes não orientadas
     *
     * @return - int (opção)
     */
    public static int optionMenuNonOriented() {
        String text = "\n=============================================="
                + "\n_____________________MENU_____________________"
                + "\n\nDADOS DE INPUT"
                + "\n   1 - Ver Dados de Input"
                + "\n\nMEDIDAS AO NÍVEL DOS NÓS"
                + "\n   2 - Grau dos Nós"
                + "\n   3 - Centralidade do Vetor Próprio"
                + "\n\nMEDIDAS AO NÍVEL DA REDE"
                + "\n   4 - Grau Médio"
                + "\n   5 - Densidade"
                + "\n   6 - Potências da Matriz de Adjacências"
                + "\n\n   0 - Exit"
                + "\n==============================================";

        System.out.printf("%n%s%n", text);
        int op = in.nextInt();
        in.nextLine();
        return op;
    }

    /**
     * Interface do menu para redes orientadas
     *
     * @return - int (opção)
     */
    public static int optionMenuOriented() {
        String text = "\n=============================================="
                + "\n_____________________MENU_____________________"
                + "\n\nDADOS DE INPUT"
                + "\n   1 - Ver Dados de Input"
                + "\n\nMEDIDAS AO NÍVEL DOS NÓS"
                + "\n   2 - Grau de Entrada dos Nós"
                + "\n   3 - Grau de Saída dos Nós"
                + "\n   4 - Page Rank"
                + "\n\n   0 - Exit"
                + "\n==============================================";

        System.out.printf("%n%s%n", text);
        int op = in.nextInt();
        in.nextLine();
        return op;
    }

    /**
     * Interface do menu pagerank
     *
     * @return
     */
    public static int optionMenuPageRank() {
        String text = "\n=============================================="
                + "\n___________________PAGERANK___________________\n"
                + "\n\nMÉTODO DE CÁLCULO"
                + "\n   1 - Iterativo"
                + "\n   2 - Vetor Próprio"
                + "\n\n   0 - Exit"
                + "\n==============================================";

        System.out.printf("%n%s%n", text);
        int op = in.nextInt();
        in.nextLine();
        return op;
    }

    // ______________MÉTODOS DE TRATAMENTO DE DADOS DE INPUT______________
    /**
     * Contador de linhas de um ficheiro
     *
     * @param fileName - recebe como parâmetro o nome do ficheiro
     * @return - retorna o número de linhas com informação relevante do ficheiro
     * @throws FileNotFoundException
     */
    public static int lineCounter(String fileName) throws FileNotFoundException {
        int nLines = 0;
        Scanner fInput = new Scanner(new File(fileName));
        while (fInput.hasNextLine()) {
            String line = fInput.nextLine();
            line = line.trim();
            if (line.length() > 0 && line.charAt(0) == 's') {
                nLines++;
            }
        }
        return nLines;
    }

    /**
     * Carrega em memória o contéudo (linhas começadas por "s") de um ficheiro
     *
     * @param info - matriz com a informação contida no ficheiro em causa
     * @param fileName - nome do ficheiro em causa
     * @param fields - número de campos de informação
     * @param nLines - número de linhas com informação relevante do ficheiro
     * @return - i - número de linhas começadas por "s" - -1 - caso o
     * carregamento do ficheiro não obedeça às validações
     * @throws FileNotFoundException
     */
    public static int loadData(String[][] info, String fileName, int fields, int nLines) throws FileNotFoundException {
        Scanner fInput = new Scanner(new File(fileName));
        int i = 0;
        while (fInput.hasNextLine() && i < nLines) {
            String line = fInput.nextLine();
            if (line.length() > 0 && line.charAt(0) == 's') {
                boolean test = matrixFiller(info, fields, line, i);
                if (!test) {
                    return -1;
                }
                i++;
            } else if (line.length() > 0 && i > 1 && line.charAt(0) != 's') {
                System.out.println("\nErro: Informação inválida no ficheiro dos nós");
                return -1; //supomos que o i nos indica o número de linhas da matriz, caso o return seja -1 saberemos que houve um erro na entrada;
            }
        }

        return i;
    }

    /**
     * Carrega em memória o contéudo (linhas começadas por "s") de um ficheiro
     *
     * @param info - matriz com a informação contida no ficheiro em causa
     * @param fileName - nome do ficheiro em causa
     * @param fields - número de campos de informação
     * @param nLines - número de linhas com informação relevante do ficheiro
     * @param nNodes - número de nós
     * @param oriented - boolean orientes / nonoriented
     * @return - i - número de linhas começadas por "s" - - 1 - caso o ficheiro
     * de entrada seja inválido
     * @throws FileNotFoundException
     */
    public static int loadDataBranches(String[][] info, String fileName, int fields, int nLines, int nNodes, boolean oriented) throws FileNotFoundException {
        Scanner fInput = new Scanner(new File(fileName));
        int i = 0;
        while (fInput.hasNextLine() && i < nLines) {
            String line = fInput.nextLine();
            if (line.length() > 0 && line.charAt(0) == 's') {
                boolean test = matrixFillerBranches(info, fields, line, i, nNodes, oriented);
                if (test == false) {
                    i--;
                }
                i++;
            } else if (line.length() > 0 && i > 1 && line.charAt(0) != 's') {
                System.out.println("\nErro: Informação inválida no ficheiro dos ramos");
                return -1; //supomos que o i nos indica o número de linhas da matriz, caso o return seja -1 saberemos que houve um erro na entrada;
            }
        }
        return i;
    }

    /**
     * Preenche a linha de uma dada matriz com a informação do ficheiro
     *
     * @param info - matriz a preencher
     * @param fields - número de campos de informação / número de colunas da
     * matriz info
     * @param line - Informação a introduzir na matriz info
     * @param nLines - número linhas da matriz info
     * @param nNodes - número de nós
     * @param oriented
     * @return boolean
     */
    public static boolean matrixFillerBranches(String[][] info, int fields, String line, int nLines, int nNodes, boolean oriented) {
        String temp[] = line.split(INFO_SPLIT);
        if (temp.length != fields) {
            System.out.println("\nErro: Informações acerca dos ramos em falta. Dados não carregados");
            return false;
        }
        if (!oriented) {
            if (searchElem(temp[0], temp[1], info) == -1 && Integer.parseInt(temp[2]) == 1 && Integer.parseInt(temp[0].substring(1)) <= nNodes && Integer.parseInt(temp[1].substring(1)) <= nNodes) {
                for (int j = 0; j < fields; j++) {
                    info[nLines][j] = temp[j].trim();
                }
                return true;
            }
        } else {
            if (Integer.parseInt(temp[2]) == 1 && Integer.parseInt(temp[0].substring(1)) <= nNodes && Integer.parseInt(temp[1].substring(1)) <= nNodes) {
                for (int j = 0; j < fields; j++) {
                    info[nLines][j] = temp[j].trim();
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Preenche a linha de uma dada matriz com a informação do ficheiro,
     * verifica se as informações estão completas, caso contrário termina o
     * programa
     *
     * @param info - matriz a preencher
     * @param fields - número de campos de informação / número de colunas da
     * matriz info
     * @param line - Informação a introduzir na matriz info
     * @param nLines - número linhas da matriz info
     * @return boolean
     */
    public static boolean matrixFiller(String[][] info, int fields, String line, int nLines) {
        String temp[] = line.split(INFO_SPLIT);
        if (temp.length != fields) {
            System.out.println("\nErro: Informações acerca dos nós em falta. Dados não carregados");
            return false;
        }
        for (int j = 0; j < fields; j++) {
            info[nLines][j] = temp[j].trim();
        }
        return true;
    }

    /**
     * Uma vez que a matriz que se pretende obter é simétrica, constrói-se a
     * matriz de adjacências em "espelho" com recurso a um único ciclo "for"
     *
     * @param adjMatrix - matriz de adjacências
     * @param branches - matriz matriz com info dos ramos
     * @param n - número de elementos da matriz
     * @param oriented
     * @return - boolean
     */
    public static boolean buildAdjacentMatrix(int[][] adjMatrix, String[][] branches, int n, boolean oriented) {
        for (int i = 0; i < n; i++) {
            int aux1 = Integer.parseInt(branches[i][0].substring(1));
            int aux2 = Integer.parseInt(branches[i][1].substring(1));
            if (oriented) {
                if (aux1 != aux2 && Integer.parseInt(branches[i][2]) == 1) {
                    adjMatrix[aux2 - 1][aux1 - 1] = Integer.parseInt(branches[i][2]);
                } else {
                    int a = i + 3;
                    System.out.println("\nErro: Verifique o ficheiro (linha: " + a + ")");
                    return false;
                }
            } else {
                if (aux1 != aux2 && Integer.parseInt(branches[i][2]) == 1) {
                    adjMatrix[aux1 - 1][aux2 - 1] = Integer.parseInt(branches[i][2]);
                    adjMatrix[aux2 - 1][aux1 - 1] = Integer.parseInt(branches[i][2]);
                } else {
                    int a = i + 3;
                    System.out.println("\nErro: Verifique o ficheiro (linha: " + a + ")");
                    return false;
                }
            }
        }
        return true;
    }

    // ______________MÉTODOS DAS MEDIDAS AO NÍVEL DOS NÓS______________
    /**
     * Calcula o grau para cada nó
     *
     * @param adjMatrix - matriz das adjacências
     * @param pos - índice do nó para o qual se calcula o grau
     * @param nElem - número de elementos
     * @return - grau do nó de índice "pos"
     */
    public static int getNodeDegree(int[][] adjMatrix, int pos, int nElem) {
        int nodeDegree = 0;
        for (int i = 0; i < nElem; i++) {
            nodeDegree = nodeDegree + adjMatrix[i][pos];
        }
        return nodeDegree;
    }

    /**
     * Guarda num vetor de tamanho "nElem", os graus de cada nó
     *
     * @param adjMatrix - matriz das adjacências
     * @param nodeDegree - vetor que guarda o grau de cada nó
     * @param nElem - número de elementos
     * @param out
     */
    public static void storeNodeDegrees(int[][] adjMatrix, int[] nodeDegree, int nElem, boolean out) {
        if (out) {
            for (int i = 0; i < nElem; i++) {
                nodeDegree[i] = getNodeDegree(adjMatrix, i, nElem);
            }
        } else {
            for (int i = 0; i < nElem; i++) {
                nodeDegree[i] = getInNode(adjMatrix, i, nElem);
            }
        }
    }

    /**
     * Com recurso ao intoDoubleArray, cria uma cópia double[][] da entrada
     * int[][] e depois um objeto do tipo Matrix onde é guardada a copia
     *
     * @param adjMatrix
     * @return
     */
    public static Matrix createMatrixObject(int[][] adjMatrix) {
        double[][] tempMatrix = intToDoubleArray(adjMatrix);
        Matrix mat = new Basic2DMatrix(tempMatrix);
        return mat;
    }

    /**
     * Aplica os métodos da biblioteca importada de algebra para o cálculo de
     * valores e vetores próprios
     *
     * @param mat - objeto do tipo Matrix
     * @return - retorna duas matrizes, uma matriz diagonal de valores próprios,
     * outra com vetores próprios(coluna) correspondente a cada valor próprio;
     * cada entrada do vetor representa da centralidade do nó para o valor
     * prórpio correspondente a esse vetor
     */
    public static Matrix[] callDecomposeMethods(Matrix mat) {
        EigenDecompositor eigenD = new EigenDecompositor(mat);
        Matrix[] mattD = eigenD.decompose();
        return mattD;
    }

    /**
     * Extrai para um array[][] a matriz de vetores próprios
     *
     * @param mattD
     * @return
     */
    public static double[][] getEigenvectorMatrix(Matrix[] mattD) {
        double eigenvectorMatrix[][] = mattD[0].toDenseMatrix().toArray();
        return eigenvectorMatrix;
    }

    /**
     * Extrai para um array[][] a matriz de valores próprios
     *
     * @param mattD
     * @return
     */
    public static double[][] getEigenValueMatrix(Matrix[] mattD) {
        double eigenValueMatrix[][] = mattD[1].toDenseMatrix().toArray();
        return eigenValueMatrix;
    }

    /**
     * Percorre a matriz diagonal de valores próprios à procura do maior valor
     * próprio
     *
     * @param eigenValueMatrix - matriz diagonal de valores próprios
     * @param nNodes - número de nós
     * @return - o índice do maior valor próprio
     */
    public static int getMajorEigenValueIndex(double[][] eigenValueMatrix, int nNodes) {
        double major = eigenValueMatrix[0][0];
        int index = 0;
        for (int i = 1; i < nNodes; i++) {
            if (eigenValueMatrix[i][i] > major) {
                major = eigenValueMatrix[i][i];
                index = i;
            }
        }
        return index;
    }

    /**
     * @param eigenvectorMatrix - matriz em que à coluna de índice "i"
     * correspponde o vetor próprio obtido através do valor próprio da coluna de
     * índice "i" da matriz eigenValueMatrix
     * @param eigenValueMatrix - matriz diagonal com os valores próprios
     * @param nNodes
     * @return
     */
    public static double[] getEigenvector(double[][] eigenvectorMatrix, double[][] eigenValueMatrix, int nNodes) {
        int index = getMajorEigenValueIndex(eigenValueMatrix, nNodes);
        double[] eigenvector = new double[nNodes];
        for (int i = 0; i < nNodes; i++) {
            eigenvector[i] = eigenvectorMatrix[i][index];
        }
        return eigenvector;
    }

    // ______________MÉTODOS DAS MEDIDAS AO NÍVEL DA REDE______________
    /**
     * Calcula o número de ramos da matriz de adjacências
     *
     * @param nodeDegree - vetor com o grau de cada nó
     * @param nNodes - número de nós
     * @return - retorna o número de ramos
     */
    public static int nBranches(int[] nodeDegree, int nNodes) {
        int sum = 0;
        for (int i = 0; i < nNodes; i++) {
            sum = sum + nodeDegree[i];
        }
        return sum / 2;
    }

    /**
     * Calcula o número máximo de ramos possíveis da matriz de adjacências (MA)
     *
     * @param nNodes - número de nós (ou elementos)
     * @return - retorna o número máximo de ramos possíveis da MA
     */
    public static int getBranchMax(int nNodes) {
        int branchMax;
        branchMax = (nNodes * (nNodes - 1)) / 2;
        return branchMax;
    }

    /**
     * Calcula a média do grau dos nós da MA - (A soma dos graus dos nós é igual
     * ao dobro do número de ramos)
     *
     *
     * @param nodeDegree - vetor com o grau de cada nó
     * @param nNodes - número de nós
     * @return - retorna a média do grau dos nós
     */
    public static double averageDegree(int[] nodeDegree, int nNodes) {
        int sumNodeDegree;
        sumNodeDegree = nBranches(nodeDegree, nNodes) * 2;
        double average;
        average = ((double) sumNodeDegree / nNodes);
        return average;
    }

    /**
     * Calcula a densidade da rede (Valor € [0, 1])
     *
     * @param nodeDegree - vetor com o grau de cada nó
     * @param nNodes - número de nós
     * @return - retorna a densidade da rede (double entre 0 e 1)
     */
    public static double getDensity(int[] nodeDegree, int nNodes) {
        double density;
        int branchMax = getBranchMax(nNodes);
        int nBranches = nBranches(nodeDegree, nNodes);
        density = ((double) nBranches / branchMax);
        return density;
    }

    /**
     * Produto de duas matrizes
     *
     * @param matrix1
     * @param matrix2
     * @param n - ordem da matriz
     * @return
     */
    public static double[][] multiplyMatrix(double[][] matrix1, int[][] matrix2, int n) {
        double[][] finalMatrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                finalMatrix[i][j] = fillMultiplicationMatrix(matrix1, matrix2, i, j);
            }
        }
        return finalMatrix;
    }

    /**
     * Método complementar da multiplicação de matrizes.
     *
     * @param matrix1
     * @param matrix2
     * @param line
     * @param column
     * @return
     */
    public static double fillMultiplicationMatrix(double[][] matrix1, int[][] matrix2, int line, int column) {
        double n = 0;
        for (int i = 0; i < matrix1.length; i++) {
            n = (double) (matrix1[line][i] * matrix2[i][column]) + n;
        }
        return n;
    }

    // _______________________FORMATTER_______________________
    /**
     * Em função da variável "op" obtida a partir do input de -n ou -t na linha
     * de comandos, faz o output para consola ou para ficheiro
     *
     * @param fileName
     * @param op
     * @return
     * @throws FileNotFoundException
     */
    public static Formatter createAndOpenOutputFile(String fileName, int op) throws FileNotFoundException {
        if (op == 0) {
            Formatter out = new Formatter(System.out);
            return out;
        } else {
            Formatter outF = new Formatter(new File(fileName));
            return outF;
        }
    }

    // ______________MÉTRICAS DE REDES ORIENTADAS______________
    /**
     * Calcula o grau de entrada para cada nó
     *
     * @param adjMatrix - matriz das adjacências
     * @param pos - índice do nó para o qual se calcula o grau
     * @param nElem - número de elementos
     * @return - grau de entrada do nó de índice "pos"
     */
    public static int getInNode(int[][] adjMatrix, int pos, int nElem) {
        int node = 0;
        for (int i = 0; i < nElem; i++) {
            node = node + adjMatrix[pos][i];
        }
        return node;
    }

    /**
     * Cálcula o vetor PageRank pelo método iterativo e faz output de todos os
     * vetores intermediários
     *
     * @param M - matriz
     * @param k - número de iterações definido pelo utilizador
     * @param nNodes - número de nós
     * @param out - Formatter
     * @return - vetor PageRank não normalizado
     */
    public static double[] getPageRankVec(double[][] M, int k, int nNodes, Formatter out) {
        double[] pageRank = new double[nNodes];
        out.format("%n%5s", "0");
        for (int i = 0; i < nNodes; i++) {
            pageRank[i] = 1;
            out.format("%8.4f", pageRank[i]);
        }
        int o;
        double[] vec = new double[nNodes];
        for (int l = 0; l < k; l++) {
            o = l + 1;
            out.format("%n%5d", o);
            for (int p = 0; p < nNodes; p++) {
                vec[p] = pageRank[p];
            }
            for (int i = 0; i < nNodes; i++) {
                pageRank[i] = fillMultiplicationMatrixDouble(M, vec, i);
                out.format("%8.4f", pageRank[i]);
            }
        }
        return pageRank;
    }

    /**
     * Método complementar de multiplicação de matrizes, neste caso, matriz por
     * matriz coluna (vetor)
     *
     * @param matrix1
     * @param matrix2
     * @param line
     * @return
     */
    public static double fillMultiplicationMatrixDouble(double[][] matrix1, double[] matrix2, int line) {
        double n = 0;
        for (int i = 0; i < matrix1.length; i++) {
            n = (double) (matrix1[line][i] * matrix2[i]) + n;
        }
        return n;
    }

    /**
     * Construção da matriz M necessária ao cálcula da métrica PageRank
     *
     * @param M
     * @param stochasticMatrix
     * @param d - damping factor
     * @param nNodes
     */
    public static void getMatrixM(double[][] M, double[][] stochasticMatrix, double d, int nNodes) {
        double aux = (1 - d) / nNodes;
        for (int i = 0; i < nNodes; i++) {
            for (int j = 0; j < nNodes; j++) {
                M[i][j] = (stochasticMatrix[i][j] * d) + aux;
            }
        }
    }

    /**
     * Construção da matriz estocástica
     *
     * @param adjMatrix
     * @param stochasticMatrix
     * @param nNodes
     * @param nodeDegree
     */
    public static void createStochasticMatrix(int[][] adjMatrix, double[][] stochasticMatrix, int nNodes, int[] nodeDegree) {
        for (int i = 0; i < adjMatrix.length; i++) {
            for (int j = 0; j < adjMatrix.length; j++) {
                if (adjMatrix[i][j] != 0) {
                    stochasticMatrix[i][j] = (double) 1 / nodeDegree[j];
                } else if (nodeDegree[j] == 0) {
                    stochasticMatrix[i][j] = (double) 1 / nNodes;
                }
            }
        }
    }

    /**
     * Método de normalização do vetor PageRank
     *
     * @param auxVec
     * @param pageRank
     */
    public static void normalizePageRankVec(double[] auxVec, double[] pageRank) {
        double aux = 0;
        for (int i = 0; i < auxVec.length; i++) {
            aux = Math.pow(auxVec[i], 2) + aux;
        }
        double norm = Math.sqrt(aux);
        if (auxVec[0] < 0) {
            norm = norm * (-1);
        }
        for (int j = 0; j < auxVec.length; j++) {
            pageRank[j] = auxVec[j] / norm;
        }
    }
    // ______________MÉTODOS DE ABERTURA DE URL______________

    /**
     * Abre uma determindada página no browser predefinido por omissão
     *
     * @param link
     */
    public static void opensHyperlink(String link) {
        try {
            Desktop desktop = java.awt.Desktop.getDesktop();
            URI oURL = new URI(link);
            desktop.browse(oURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void opensMostRelevantNode(int[] vec, int nElem, String[][] nodes) {
        int max = findIndexFirstMaxIntVector(vec, nElem);
        opensHyperlink(nodes[max][4]);
    }

    public static void opensMostRelevantNodeDouble(double[] vec, int nElem, String[][] nodes) {
        int max = findIndexFirstMaxDoubleVector(vec, nElem);
        opensHyperlink(nodes[max][4]);
    }
}
