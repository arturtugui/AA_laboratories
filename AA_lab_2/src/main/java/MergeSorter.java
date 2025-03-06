public class MergeSorter {
    // basic merge sort
    public static void mergeSortHelper(int[] arr){
        divide(arr, 0, arr.length-1);
    }

    public static void merge(int[] arr, int start, int mid, int end) {
        int i, j, k;

        int num1 = mid - start + 1;
        int num2 = end - mid;

        int[] arr1 = new int[num1];
        int[] arr2 = new int[num2];

        System.arraycopy(arr, start, arr1, 0, num1);
        System.arraycopy(arr, mid + 1, arr2, 0, num2);

        i = 0; j = 0; k = start;

        while(i < num1 && j < num2) {
            if(arr1[i] <= arr2[j]) {
                arr[k] = arr1[i];
                i++;
            }
            else {
                arr[k] = arr2[j];
                j++;
            }
            k++;
        }

        while(i < num1) {
            arr[k] = arr1[i];
            i++; k++;
        }

        while(j < num2) {
            arr[k] = arr2[j];
            j++; k++;
        }
    }

    public static void divide(int[] arr, int start, int end) {
        if (start < end){
            int mid = (start + end) / 2;
            divide(arr, start, mid);
            divide(arr, mid + 1, end);
            merge(arr, start, mid, end);
        }
    }


    // Iterative, uses same merge
    public static void mergeSortIterative(int[] arr) {
        int n = arr.length;


        for (int size = 1; size < n; size *= 2) {
            // breaking the array in subarrays of size 1, 2, 4
            for (int leftStart = 0; leftStart < n; leftStart += 2 * size) {
                // finding mid, and end
                int mid = Math.min(leftStart + size - 1, n - 1);
                int rightEnd = Math.min(leftStart + 2 * size - 1, n - 1);

                if (mid < rightEnd) {
                    merge(arr, leftStart, mid, rightEnd);
                }
            }
        }
    }
}
