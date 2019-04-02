/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lapr1_analiseredessociais;

import java.io.FileNotFoundException;
import java.util.Formatter;
import static lapr1_analiseredessociais.LAPR1_AnaliseRedesSociais.*;
import static lapr1_analiseredessociais.UtilitariosOutput.*;

/**
 *
 * @author UTeam
 */
public class Testes {

    /**
     *
     * @param args
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        Formatter out = new Formatter(System.out);
        int expectedLineCounter = 15;

        System.out.println("Line Counter: " + lineCounterTest("test.csv", expectedLineCounter));

        int[][] matrixAdjTest = new int[][]{{0, 1, 1, 0}, {1, 0, 0, 1}, {1, 0, 0, 1}, {0, 1, 1, 0}};
        int[] testNodeDegree = new int[4];
        int[] expectedStoredNodeDegree = new int[]{2, 2, 2, 2};
        int expectedNodeDegree = 2;
        int pos = 1;

        System.out.println("Node Degree: " + getNodeDegreeTest(matrixAdjTest, pos, 4, expectedNodeDegree));
        System.out.println("Stored Node Degrees: " + storeNodeDegreeTest(matrixAdjTest, testNodeDegree, 4, true, expectedStoredNodeDegree));

        double[][] majorEigenValue = new double[][]{{2, 1, 0}, {1, 1, 3}, {1, 4, 0}};
        int expectedResult = 0;
        double[][] eigenVector = new double[][]{{3, 2, 1}, {2, 1, 1}, {1, 1, 1}};
        double[] eigenVectorExpected = new double[]{3, 2, 1};

        System.out.println("Major Eigen Value: " + getMajorEigenValueIndexTest(majorEigenValue, 3, expectedResult));
        System.out.println("Eigen Vector: " + getEigenVectorTest(eigenVector, majorEigenValue, 3, eigenVectorExpected));

        int[] teste = new int[]{2, 2, 2, 2};

        System.out.println("Nº Branches: " + nBranchesTest(teste, 4, 4));
        System.out.println("Get Branch Max: " + getBranchMaxTest(4, 6));
        System.out.println("Average Degree: " + averageDegreeTest(teste, 4, 2));
        System.out.println("Get Density: " + getDensityTest(teste, 4, ((double) 4 / 6)));

        int[][] expectedResult0 = new int[][]{{0, 4, 4, 0}, {4, 0, 0, 4}, {4, 0, 0, 4}, {0, 4, 4, 0}};
        int k = 3;
        int nNodes = 4;

        System.out.println("Powers Adjacent Matrix: " + test_powersAdjacentMatrixOut(out, expectedResult0, matrixAdjTest, k, nNodes));

        double[][] matrix1 = new double[][]{{1, 2, 3}, {1, 2, 3}, {1, 2, 3}};
        int[][] matrix2 = new int[][]{{3, 2, 1}, {3, 2, 1}, {3, 2, 1}};
        int[][] expectedResult1 = new int[][]{{18, 12, 6}, {18, 12, 6}, {18, 12, 6}};
        int n = 3;

        System.out.println("Multiply Matrix: " + test_multiplyMatrix(expectedResult1, matrix1, matrix2, n));

        int expectedResult2 = 4;
        double[][] matrix3 = new double[][]{{1, 2}, {2, 1}};
        int[][] matrix4 = new int[][]{{2, 1}, {1, 2}};
        int i = 1;
        int j = 1;

        System.out.println("Fill Multiplication Matrix: " + test_fillMultiplicationMatrix(expectedResult2, matrix3, matrix4, i, j));

        boolean expectedResult3 = false;

        System.out.println("Check: " + checkIntPosTest(-1, expectedResult3));

        int[][] matrixAdjTest2 = new int[][]{{0, 2, 1, 1}, {3, 0, 0, 0}, {1, 0, 0, 5}, {1, 1, 1, 0}};
        System.out.println("Get out Node: " + getInNodeTest(matrixAdjTest2, pos, 4, 3));
    }

    public static boolean lineCounterTest(String fileName, int expectedResult) throws FileNotFoundException {
        int nLines = lineCounter(fileName);
        return nLines == expectedResult;
    }

    public static boolean getNodeDegreeTest(int[][] adjMatrix, int pos, int nElem, int expectedResult) {
        int result = getNodeDegree(adjMatrix, pos, nElem);
        return result == expectedResult;
    }

    public static boolean storeNodeDegreeTest(int[][] adjMatrix, int[] nodeDegree, int nElem, boolean out, int[] expectedResult) {
        storeNodeDegrees(adjMatrix, nodeDegree, nElem, out);
        for (int i = 0; i < nElem; i++) {
            if (nodeDegree[i] != expectedResult[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean getInNodeTest(int[][] adjMatrix, int pos, int nElem, int expectedResult) {
        int inNode;
        inNode = getInNode(adjMatrix, pos, nElem);
        return inNode == expectedResult;
    }

    public static boolean getMajorEigenValueIndexTest(double[][] eigenValueMatrix, int nNodes, int expectedResult) {
        int index = getMajorEigenValueIndex(eigenValueMatrix, nNodes);
        return index == expectedResult;
    }

    public static boolean getEigenVectorTest(double[][] eigenvectorMatrix, double[][] eigenValueMatrix, int nNodes, double[] expectedResult) {
        double[] eigenVector = getEigenvector(eigenvectorMatrix, eigenValueMatrix, nNodes);
        for (int i = 0; i < nNodes; i++) {
            if (eigenVector[i] != expectedResult[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean nBranchesTest(int[] nodeDegree, int nNodes, int expectedResult) {
        return nBranches(nodeDegree, nNodes) == expectedResult;
    }

    public static boolean getBranchMaxTest(int nNodes, int expectedResult) {
        return getBranchMax(nNodes) == expectedResult;
    }

    public static boolean averageDegreeTest(int[] nodeDegree, int nNodes, double expectedResult) {
        return averageDegree(nodeDegree, nNodes) == expectedResult;
    }

    public static boolean getDensityTest(int[] nodeDegree, int nNodes, double expectedResult) {
        if (getDensity(nodeDegree, nNodes) == expectedResult) {
            return true;
        } else {
            System.out.println(getDensity(nodeDegree, nNodes) + " " + expectedResult);
            return false;
        }
    }

    public static boolean test_powersAdjacentMatrixOut(Formatter out, int[][] expectedResult, int[][] adjacentMatrix, int k, int nNodes) {
        double[][] result = outPowersAdjacentMatrix(out, adjacentMatrix, k, nNodes);
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result.length; j++) {
                if (result[i][j] != expectedResult[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean test_multiplyMatrix(int[][] expectedResult, double[][] matrix1, int[][] matrix2, int n) {
        double[][] result = multiplyMatrix(matrix1, matrix2, n);
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result.length; j++) {
                if (result[i][j] != expectedResult[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean test_fillMultiplicationMatrix(int expectedResult, double[][] matrix1, int[][] matrix2, int line, int column) {
        double result = fillMultiplicationMatrix(matrix1, matrix2, line, column);
        return expectedResult == result;
    }

    public static boolean checkIntPosTest(int k, boolean expectedResult) {
        return k >= 1 == expectedResult;
    }
}
