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
    void bubbleSort() {
        int[] arr = new int[]{1, -1, 3, 5, 1, 34, 5, 1};

        Lg.d(Arrays.toString(arr));
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i] > arr[j]) {
                    arr[i] = arr[i] + arr[j];
                    arr[j] = arr[i] - arr[j];
                    arr[i] = arr[i] - arr[j];
                }
            }
        }
        Lg.d(Arrays.toString(arr));

    }

    @Test
    void quickSortTest() {
        int[] arr = new int[]{1, -1, 3, 5, 1, 34, 5, 1};

        Lg.d(Arrays.toString(arr));

        quickSort(arr, 0, arr.length - 1);
        Lg.d(Arrays.toString(arr));
    }

    private void quickSort(int[] arr, int start, int end) {
        int i = start;
        int j = end;
        if (i > j) return;
        int k = arr[i];


        while (i < j) {
            while (i < j && arr[j] > k) {
                j--;
            }
            if (i < j) {
                arr[i] = arr[j];
                i++;
            }
            while (i < j && arr[i] < k) {
                i++;
            }
            if (i < j) {
                arr[j] = arr[i];
                j--;
            }
        }
        arr[i] = k;
        quickSort(arr, start, i - 1);
        quickSort(arr, i + 1, end);
    }


    @Test
    void reverseNum() {
        int num = 123456789;
        do {
            System.out.print(num % 10);
        } while ((num = num / 10) > 0);

        Lg.d();
        Lg.d("%s", 10 + 1 >> 1);
    }


    @Test
    void binarySearchTest() {
        final int search = binarySearch(new int[]{-1, 0, 1, 2, 3, 3, 4, 99, 99, 1000}, 3);
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
                while (mid > 0 && arr[mid - 1] == arr[mid]) {
                    mid--;
                }
                return mid;
            }
        }
        return -1;
    }

}
