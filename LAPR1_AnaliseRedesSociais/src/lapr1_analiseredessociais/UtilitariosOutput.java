/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lapr1_analiseredessociais;

import java.util.Formatter;
import static lapr1_analiseredessociais.LAPR1_AnaliseRedesSociais.*;

/**
 *
 * @author UTeam
 */
public class UtilitariosOutput {

    //_____________________UTILITÁRIOS_DE_OUTPUT_____________________

    /**
     * Output da informação dada pelo ficheiros de entrada
     * @param out
     * @param nodes
     * @param branches
     * @param nNodes
     * @param nBranches
     */
    public static void outNodesBranchesInfo(Formatter out, String[][] nodes, String[][] branches, int nNodes, int nBranches) {
        out.format("%n%s%n", "INFORMAÇÕES ACERCA DOS NÓS");
        out.format("%-7s%4s%10s%20s%40s%n%n", " ID", "Type", "Label", "Media", "URL");
        outMatrixString(out, nodes, nNodes);

        out.format("%n%s%n", "INFORMAÇÕES ACERCA DOS RAMOS");
        out.format("%-7s%4s%10s%n%n", " From", "To", "Weight");
        outMatrixString(out, branches, nBranches);
    }

    /**
     * Output de um array[][] de Strings em colunas de x linhas em função do
     * número total de linhas a mostrar.
     *
     * @param out
     * @param matrix
     * @param n
     */
    public static void outMatrixString(Formatter out, String[][] matrix, int n) {
        int nLines;
        if (n < 21) {
            nLines = n;
        } else if (n < 41) {
            nLines = 20;
        } else if (n < 101) {
            nLines = 50;
        } else if (n < 201) {
            nLines = 100;
        } else {
            nLines = 200;
        }
        for (int i = 0; i < nLines; i++) {
            for (int p = i; p < n; p = p + nLines) {
                out.format("|");
                for (int j = 0; j < matrix[0].length; j++) {
                    switch (j) {
                        case 0:
                            out.format("%-6s", matrix[p][j]);
                            break;
                        case 1:
                            out.format("%4s", matrix[p][j]);
                            break;
                        case 2:
                            out.format("%10s", matrix[p][j]);
                            break;
                        case 3:
                            out.format("%20s", matrix[p][j]);
                            break;
                        default:
                            out.format("%40s", matrix[p][j]);
                            break;
                    }
                }
                out.format("| ");
            }
            out.format("%n");
        }
        out.format("%n");
    }

    /**
     * Output do cabeçalho de uma métrica em vetor
     *
     * @param out
     * @param title
     */
    public static void outVecID(Formatter out, String title) {
        out.format("%n%n%s%n%n", title);
    }

    /**
     * Output dos IDs dos nós e do respetivo grau em colunas de 20 linhas para
     * um número de nós > 100, caso contrário colunas de 10 linhas
     *
     * @param out
     * @param vec
     * @param nodes
     * @param nNodes
     */
    public static void outIntVec(Formatter out, int[] vec, String[][] nodes, int nNodes) {
        int nLines;
        if (nNodes < 11) {
            nLines = nNodes;
        } else if (nNodes < 51) {
            nLines = 10;
        } else {
            nLines = 20;
        }
        for (int i = 0; i < nLines; i++) {
            for (int j = i; j < vec.length; j = j + nLines) {
                out.format("|%-5s%5d| ", nodes[j][0], vec[j]);
            }
            out.format("%n");
        }
    }

    /**
     * Output dos IDs dos nós e da respetiva centralidade em colunas de 20
     * linhas para um número de nós > 100, caso contrário colunas de 10 linhas
     *
     * @param out
     * @param vec
     * @param nodes
     * @param nNodes
     */
    public static void outDoubleVec(Formatter out, double[] vec, String[][] nodes, int nNodes) {
        int nLines;
        if (nNodes < 11) {
            nLines = nNodes;
        } else if (nNodes < 51) {
            nLines = 10;
        } else {
            nLines = 20;
        }
        for (int i = 0; i < nLines; i++) {
            for (int j = i; j < vec.length; j = j + nLines) {
                out.format("|%-5s%5.3f| ", nodes[j][0], vec[j]);
            }
            out.format("%n");
        }
    }

    /**
     * Output das Potências da Matrizes de Adjacências. Para valores da matriz
     * iguais ou superiores a SC_NOTATION_LIMIT, o valor é apresentado em
     * notação científica com 3 casa decimais e por isso deixa de ser um valor
     * exato, passa a ser uma aproximação.
     *
     * @param out
     * @param matrix
     * @param size
     */
    public static void outMatrix(Formatter out, double[][] matrix, int size) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (matrix[i][j] > SC_NOTATION_LIMIT) {
                    out.format("%10.3e", matrix[i][j]);
                } else {
                    int aux = (int) matrix[i][j];
                    out.format("%10d", aux);
                }
            }
            out.format("%n");
        }
        out.format("%n");
    }

    /**
     * Guarda numa matriz o número de caminhos de comprimento k entre cada par
     * de nós. Faz o output de cada potência da matriz de adjacências calculada
     *
     * @param adjacentMatrix - Matriz de adjacências dos nós
     * @param k - expoente da matriz - comprimento do caminho (USER INPUT)
     * @param nNodes - número de nós
     * @param out - Formatter
     * @return - retorna apenas a matriz de adjecências de expoente k para
     * testes
     */
    public static double[][] outPowersAdjacentMatrix(Formatter out, int[][] adjacentMatrix, int k, int nNodes) {
        double[][] adjacentMatrixPowers = buildIdMatrix(nNodes);
        for (int i = 0; i < k; i++) {
            int holder = i + 1;
            out.format("%n%s%d%n", "POTÊNCIA DA MATRIZ DE ADJACÊNCIAS PARA k = ", holder);
            adjacentMatrixPowers = multiplyMatrix(adjacentMatrixPowers, adjacentMatrix, nNodes);
            outMatrix(out, adjacentMatrixPowers, nNodes);
        }
        return adjacentMatrixPowers;
    }

    /**
     * Método para criar uma matriz identidade de ordem n
     *
     * @param n
     * @return
     */
    public static double[][] buildIdMatrix(int n) {
        double[][] idMatrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            idMatrix[i][i] = 1;
        }
        return idMatrix;
    }

    /**
     * Método de output do vetor PageRank (previamente normalizado)
     * @param out
     * @param pageRankVec
     */
    public static void outPageRankVec(Formatter out, double[] pageRankVec) {
        out.format("%5s", "");
        for (int i = 0; i < pageRankVec.length; i++) {
            out.format("%8.4f", pageRankVec[i]);
        }
    }

    /**
     * Método de output dos IDs dos nós
     * @param out
     * @param nNodes
     */
    public static void outPageRankIterID(Formatter out, int nNodes) {
        out.format("%5s", "iter.");
        for (int i = 0; i < nNodes; i++) {
            int j = i + 1;
            out.format("%3s%-5d", "s", j);
        }
    }
}
