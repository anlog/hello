package cc.ifnot.java.libs.alg;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import cc.ifnot.libs.utils.Lg;

/**
 * author: dp
 * created on: 2020/7/22 7:54 PM
 * description:
 */
class Search {

    @Test
    void binarySearchTest() {
        final int search = binarySearch(new int[]{-1, 0, 1, 2, 3, 4, 99, 1000}, 99);
        Lg.d("ret: %s", search);
//        Arrays.binarySearch()
    }

    int binarySearch(int[] arr, int k) {
        Arrays.sort(arr);

        int lo = 0;
        int hi = arr.length - 1;

        while (lo <= hi) {
            int mid = lo + ((hi - lo) >> 1);
            Lg.d("mid: %s hi: %s, lo: %s", mid, hi, lo);
            if (k < arr[mid]) {
                hi = mid - 1;
            } else if (k > arr[mid]) {
                lo = mid + 1;
            } else {
                return mid;
            }
        }
        return -1;
    }

}
