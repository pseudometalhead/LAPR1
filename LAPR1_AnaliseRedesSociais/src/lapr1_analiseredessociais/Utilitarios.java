/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lapr1_analiseredessociais;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Scanner;
import static lapr1_analiseredessociais.LAPR1_AnaliseRedesSociais.*;

/**
 *
 * @author Uteam
 */
public class Utilitarios {

    /**
     * Valida a extensão do ficheiro
     *
     * @param fileName
     * @return
     */
    public static boolean checkFileNameExtension(String fileName) {
        String[] temp = fileName.split(FILE_NAME_SPLIT);
        boolean bool = temp[temp.length - 1].equals(FILE_NAME_EXTENSION);
        return bool;
    }

    /**
     * Busca o nome da rede social a um ficheiro de input
     *
     * @param args
     * @return - retorna o nome da rede social
     */
    public static String getSocialNetworkName(String[] args) {
        String[] temp = args[args.length - 1].split("_");
        return temp[1];
    }

    /**
     * Gera da data de execução do programa
     *
     * @return - data aaaammdd
     */
    public static String getDate() {
        Calendar today = Calendar.getInstance();
        int day = today.get(Calendar.DAY_OF_MONTH);
        int month = today.get(Calendar.MONTH) + 1;
        int year = today.get(Calendar.YEAR);
        String dayS = Integer.toString(day);
        String monthS = Integer.toString(month);
        String yearS = Integer.toString(year);
        dayS = dayS.length() < 2 ? "0" + dayS : dayS;
        monthS = monthS.length() < 2 ? "0" + monthS : monthS;
        String date = String.format("%4s%2s%2s", yearS, monthS, dayS);
        return date;
    }

    /**
     * Concatena o nome da rede social com a data
     *
     * @param nameSN - nome da rede social
     * @param date - data aaaammdd
     * @return nome do ficheiro de output
     */
    public static String genFileName(String nameSN, String date) {
        String fileName = "out_" + nameSN + "_" + date + ".txt";
        return fileName;
    }

    /**
     * Cria uma cópia de uma matriz de inteiros em números reais
     *
     * @param intMatrix - matriz de inteiros
     * @return - matriz de números reais
     */
    public static double[][] intToDoubleArray(int[][] intMatrix) {
        double[][] doubleMatrix = new double[intMatrix.length][intMatrix[0].length];
        for (int i = 0; i < intMatrix.length; i++) {
            for (int j = 0; j < intMatrix[0].length; j++) {
                doubleMatrix[i][j] = intMatrix[i][j];
            }
        }
        return doubleMatrix;
    }

    /**
     * Troca a ordem das colunas de uma matriz
     *
     * @param info
     * @param nNodes
     */
    public static void switchCol(String[][] info, int nNodes) {
        for (int i = 0; i < nNodes; i++) {
            String aux = info[i][1];
            info[i][1] = info[i][2];
            info[i][2] = info[i][3];
            info[i][3] = aux;
        }
    }

    /**
     * Carrega em memória o contéudo (linhas começadas por "s") de um ficheiro
     *
     * @param a
     * @param b
     * @param info - matriz com a informação contida no ficheiro em causa
     * @return - i - número de linhas começadas por "s" - - 1 - caso o ficheiro
     * de entrada seja inválido
     */
    public static int searchElem(String a, String b, String info[][]) {
        for (int i = 0; i < info.length; i++) {
            if ((a.equals(info[i][0]) && b.equals(info[i][1])) || (a.equals(info[i][1]) && b.equals(info[i][0]))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Valida o cabeçalho do ficheiro dos nós
     *
     * @param fileName
     * @return
     * @throws java.io.FileNotFoundException
     */
    public static boolean checkHeaderNodes(String fileName) throws FileNotFoundException {
        Scanner fInput = new Scanner(new File(fileName));
        String line = fInput.nextLine();
        String[] temp = line.split(INFO_SPLIT);
        if (temp.length < NODE_FIELDS || temp.length > NODE_FIELDS) {
            return false;
        } else if (!temp[0].equalsIgnoreCase("id")) {
            return false;
        } else if (!temp[1].equalsIgnoreCase("media")) {
            return false;
        } else if (!temp[2].equalsIgnoreCase("media.type")) {
            return false;
        } else if (!temp[3].equalsIgnoreCase("type.label")) {
            return false;
        } else if (!temp[4].equalsIgnoreCase("webURL")) {
            return false;
        }
        return true;
    }

    /**
     * Valida o cabeçalho do ficheiro dos ramos
     *
     * @param fileName
     * @return
     * @throws java.io.FileNotFoundException
     */
    public static boolean checkHeaderBranches(String fileName) throws FileNotFoundException {
        Scanner fInput = new Scanner(new File(fileName));
        String line = fInput.nextLine(); //NÃO REMOVER, não queremos fazer nada com a primeira linha onde está o networktype 
        line = fInput.nextLine();
        String[] temp = line.split(INFO_SPLIT);
        if (temp.length < BRANCH_FIELDS || temp.length > BRANCH_FIELDS) {
            return false;
        } else if (!temp[0].equalsIgnoreCase("from")) {
            return false;
        } else if (!temp[1].equalsIgnoreCase("to")) {
            return false;
        } else if (!temp[2].equalsIgnoreCase("weight")) {
            return false;
        }
        return true;
    }

    public static int findIndexFirstMaxIntVector(int[] vector, int nElem) {
        int max = 0;
        int pos = -1;
        for (int i = 0; i < nElem; i++) {
            if (vector[i] > max) {
                max = vector[i];
                pos = i;
            }
        }
        return pos;
    }

    public static int findIndexFirstMaxDoubleVector(double[] vector, int nElem) {
        double max = 0;
        int pos = -1;
        for (int i = 0; i < nElem; i++) {
            if (vector[i] > max) {
                max = vector[i];
                pos = i;
            }
        }
        return pos;
    }
}
