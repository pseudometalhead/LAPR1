/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lapr1_analiseredessociais;

import org.la4j.Matrix;
import org.la4j.decomposition.EigenDecompositor;
import org.la4j.matrix.dense.Basic2DMatrix;

/**
 * ESTA PÁGINA NÃO SERÁ PARA ENTREGAR, APENAS SERVE PARA TESTES E ESTUDO DO
 * PAGERANK
 *
 * @author Vitor Fernandes - 1181127
 */
public class pageRankTestRoom {

    /**
     * @param args the command line arguments
     */
    
    static int k = 20;
    static double d = 0.85;
    static double valorInicialDoVetorX = 1;  // geralmente 1, mas no documento sobre pagerank é usado por exemplo 0.125    (  (double) 1/8  )
    public static void main(String[] args) {        
        int[][] adjMatrix = {{0, 0, 1, 0, 0, 0, 0, 0}, {1, 0, 0, 1, 0, 0, 0, 0}, {1, 0, 0, 0, 0, 0, 0, 0}, {1, 1, 1, 0, 0, 0, 0, 0}, {0, 1, 0, 0, 0, 1, 0, 0}, {0, 0, 0, 0, 0, 0, 1, 1}, {0, 0, 0, 1, 1, 0, 0, 1}, {0, 0, 0, 0, 0, 1, 0, 0}};
        int nNodes = adjMatrix.length;
        double[][] stochasticMatrix = new double[nNodes][nNodes];
        double[][] M = new double[nNodes][nNodes];
        double[] pageRankIt = new double[nNodes];
        double[] pageRankEig = new double[nNodes];
        int[] nodeDegreeVec = new int[nNodes];
        storeNodeDegrees(adjMatrix, nodeDegreeVec, nNodes);
        createStochasticMatrix(adjMatrix, stochasticMatrix, nNodes, nodeDegreeVec);
        getMatrixM(M, stochasticMatrix, d, nNodes);
        System.out.println();
        for (int h = 0; h < nNodes; h++) {
            for (int f = 0; f < nNodes; f++) {
                System.out.printf("%8.4f", M[h][f]);
            }
            System.out.println();
        }
        System.out.println();
        double[] kthIteration = getPageRankVec(M, k, nNodes);

        Matrix a = new Basic2DMatrix(M);
        EigenDecompositor eigenD = new EigenDecompositor(a);
        Matrix[] mattD = eigenD.decompose();

        for (int i = 0; i < 2; i++) {
            System.out.println(mattD[i]);
        }

        double matA[][] = mattD[0].toDenseMatrix().toArray();
        double[] firstCol = new double[nNodes];

        for (int g = 0; g < nNodes; g++) {
            firstCol[g] = matA[g][0];
        }

        normalizePageRankVec(firstCol, pageRankEig);
        System.out.println("vetor pagerank normalizado do vetor próprio");
        System.out.println();
        for (int t = 0; t < nNodes; t++) {
            System.out.printf("%8.4f ", pageRankEig[t]);
        }
        System.out.println();
        
        
        normalizePageRankVec(kthIteration, pageRankIt);
        System.out.println("vetor de pagerank normalizado");
        System.out.println();
        for (int t = 0; t < nNodes; t++) {
            System.out.printf("%8.4f ", pageRankIt[t]);
        }

    }

    public static void normalizePageRankVec(double[] unnormalizedVec, double[] pageRank) {
        double aux = 0;
        for (int i = 0; i < unnormalizedVec.length; i++) {
            aux = Math.pow(unnormalizedVec[i], 2) + aux;
        }
        System.out.println(aux);
        double norm = Math.sqrt(aux);
        if (unnormalizedVec[0] < 0) {
            norm = norm * (-1);
        }
        System.out.println(norm);
        for (int j = 0; j < unnormalizedVec.length; j++) {
            pageRank[j] = unnormalizedVec[j] / norm;
        }
    }

    public static double[] getPageRankVec(double[][] M, int k, int nNodes) {
        double[] pageRank = new double[nNodes];
        for (int i = 0; i < nNodes; i++) {
            pageRank[i] = (double) 1 / 8;
            System.out.printf("%8.4f", pageRank[i]);
        }
        System.out.println();
        double[] vec = new double[nNodes];
        for (int l = 0; l < k; l++) {
            for (int p = 0; p < nNodes; p++) {
                vec[p] = pageRank[p];
            }
            for (int i = 0; i < nNodes; i++) {
                pageRank[i] = fillMultiplicationMatrixDouble(M, vec, i);
                System.out.printf("%8.4f", pageRank[i]);
            }
            System.out.println();
        }
        return pageRank;
    }

    public static double fillMultiplicationMatrixDouble(double[][] matrix1, double[] matrix2, int line) {
        double n = 0;
        for (int i = 0; i < matrix1.length; i++) {
            n = (double) (matrix1[line][i] * matrix2[i]) + n;
        }
        return n;
    }

    public static void getMatrixM(double[][] M, double[][] stochasticMatrix, double d, int nNodes) {
        double aux = (1 - d) / nNodes;
        for (int i = 0; i < nNodes; i++) {
            for (int j = 0; j < nNodes; j++) {
                M[i][j] = (stochasticMatrix[i][j] * d) + aux;
            }
        }
    }

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

    public static int getNodeDegree(int[][] adjMatrix, int pos, int nElem) {
        int nodeDegree = 0;
        for (int i = 0; i < nElem; i++) {
            nodeDegree = nodeDegree + adjMatrix[i][pos];
        }
        return nodeDegree;
    }

    public static void storeNodeDegrees(int[][] adjMatrix, int[] nodeDegree, int nElem) {
        for (int i = 0; i < nElem; i++) {
            nodeDegree[i] = getNodeDegree(adjMatrix, i, nElem);
        }
    }

}
