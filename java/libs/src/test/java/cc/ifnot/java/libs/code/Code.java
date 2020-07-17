package cc.ifnot.java.libs.code;

import org.junit.jupiter.api.Test;

import cc.ifnot.libs.utils.Lg;

/**
 * author: dp
 * created on: 2020/7/17 9:26 PM
 * description:
 */
class Code {

    @Test
    void testXX() {

        int x = 0;
        int rows = 123456789;

        do {
            x += rows % 10;
            Lg.d("%s - %s", rows % 10, x);
        } while ((rows = rows / 10) > 0);

    }

    @Test
    void testStep() {
        Lg.d(step(4, 16, 8));
    }

    int count(int i, int j, int m, int n, int k, boolean[][] visited) {
        if (i < 0 || i >= m || j < 0 || j >= n || visited[i][j]) {
            return 0;
        }
        int x = 0, y = 0;
        int ii = i, jj = j;
        do {
            x += ii % 10;
        } while ((ii = ii / 10) > 0);
        do {
            y += jj % 10;
        } while ((jj = jj / 10) > 0);
        if (x + y > k) {
            return 0;
        }

        visited[i][j] = true;
        Lg.d("[%s, %s]", i, j);
        return count(i + 1, j, m, n, k, visited) + count(i - 1, j, m, n, k, visited) +
                count(i, j + 1, m, n, k, visited) + count(i, j - 1, m, n, k, visited) + 1;
    }

    @Test
    int step(int k, int rows, int cols) {
        final long t1 = System.currentTimeMillis();
        boolean[][] visited = new boolean[rows][cols];
        Lg.d("stepped: %s in %sms with %s/%s free: %s", count(0, 0, rows, cols, k, visited),
                System.currentTimeMillis() - t1, Runtime.getRuntime().totalMemory(),
                Runtime.getRuntime().totalMemory(), Runtime.getRuntime().freeMemory());

        final long t2 = System.currentTimeMillis();
        boolean[][] stepped = new boolean[rows][cols];
        stepped[0][0] = true;
        int count = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (stepped[i][j > 0 ? j - 1 : 0] ||
                        stepped[i > 0 ? i - 1 : 0][j]) {
                    int x = 0;
                    int y = 0;
                    int ii = i, jj = j;
                    do {
                        x += ii % 10;
                    } while ((ii = ii / 10) > 0);

                    do {
                        y += jj % 10;
                    } while ((jj = jj / 10) > 0);
                    if (x + y <= k) {
                        Lg.d("[%s, %s]", i, j);
                        stepped[i][j] = true;
                        count++;
                    }
                }

            }
        }
        Lg.d("stepped: %s in %sms with %s/%s free: %s", count(0, 0, rows, cols, k, visited),
                System.currentTimeMillis() - t2, Runtime.getRuntime().totalMemory(),
                Runtime.getRuntime().totalMemory(), Runtime.getRuntime().freeMemory());
        return count;
    }
}
