package cc.ifnot.java.libs.alg;

import org.junit.jupiter.api.Test;

/**
 * author: dp
 * created on: 2020/7/24 10:45 AM
 * description:
 */
class MatrixPrint {

    @Test
    void printMatrix() {
        int rows = 5;
        int cols = 4;
        int count = 0;
        int[][] arr = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                arr[i][j] = count++;
                System.out.print(String.format("%4d", arr[i][j]));
                // {0, 1 ,2}
                // {3, 4, 5}
                // {6, 7, 8}
            }
            System.out.println();
        }
        System.out.println("----------------->");

        int x = 0;
        int y = 0;
        int r = arr.length - 1;
        int c = arr[0].length - 1;

        while (x <= c && y <= r) {

            for (int i = c; i >= x; i--) {
                System.out.print(String.format("%4d[%d, %d]←", arr[r][i], r, i));
            }
            r--;

            for (int i = r; i >= y; i--) {
                System.out.print(String.format("%4d[%d, %d]↑", arr[i][x], i, x));
            }
            x++;

            for (int i = x; i <= c; i++) {
                System.out.print(String.format("%4d[%d, %d]→", arr[y][i], y, i));
            }
            y++;

            for (int i = y; i <= r; i++) {
                System.out.print(String.format("%4d[%d, %d]↓", arr[i][c], i, c));
            }
            c--;

        }


    }
}
