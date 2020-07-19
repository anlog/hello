package cc.ifnot.java.libs.java;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import cc.ifnot.libs.utils.Lg;

/**
 * author: dp
 * created on: 2020/7/19 9:34 AM
 * description:
 */
class SearchTest {

    @Test
    void testBinarySearch() {
        Random random = new Random();
        int count = random.nextInt() & 0xff | 0xf;
        int size = random.nextInt() & 0xffff | 0xff;
        Lg.d("cout: %s, size: %s", count, size);

        for (int i = 0; i < count; i++) {
            int[] arr = new int[size];
            for (int j = 0; j < size; j++) {
                arr[j] = random.nextInt();
            }

            for (int j = 0; j < size >>> 1; j++) {
                binarySearch(arr, arr[random.nextInt() | count -1]);
            }

        }

    }



    int binarySearch(int[] arr, int k) {
        int lo = 0;
        int hi = arr.length -1;
        Lg.d("search %s in size: %s", k, arr.length);
        while (lo <= hi) {
            Lg.d("searching: lo: %s, hi: %s", lo, hi);
            int mid = hi >>> 1;
            if(k > arr[mid]) {
                lo = mid +1;
            } else if(k < arr[mid]) {
                hi = mid -1;
            } else {
                return mid;
            }
        }
        return ~lo;
    }

    @Test
    void bubbleSort() {
        int[] a = new int[]{5, 3, 1, 6, 78, 1, 4, 9};

        Lg.d(Arrays.toString(a));
        for (int i = 0; i < a.length - 1; i++) {
            for (int j = 0; j < a.length - 1 - i; j++) {
                if(a[j] > a[j+1]) {
                    int tmp = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = tmp;
                }
            }
        }
        Lg.d(Arrays.toString(a));
    }
}
