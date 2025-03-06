public class HeapSorter {
    // regular
    public static void heapSortHelper(int[] arr){
        heapSort(arr, arr.length);
    }

    public static void heapify(int[] arr, int n, int i){
        int largest = i;
        int left = 2*i+1;
        int right = 2*i+2;

        if(left < n && arr[left] > arr[largest]){
            largest = left;
        }
        if(right < n && arr[right] > arr[largest]){
            largest = right;
        }

        if(largest != i){
            QuickSorter.swap(arr, i, largest);
            heapify(arr, n, largest);
        }

    }

    public static void heapSort(int[] arr, int n){
        int i;
        for(i = n/2 - 1; i>=0; i--){
            heapify(arr, n, i);
        }
        for(i = n-1; i>0; i--){
            QuickSorter.swap(arr, 0, i);
            heapify(arr, i, 0);
        }
    }



    // ternary heap sort
    private static void ternaryHeapify(int[] arr, int n, int i) {
        int largest = i;
        int child1 = 3 * i + 1;
        int child2 = 3 * i + 2;
        int child3 = 3 * i + 3;

        if (child1 < n && arr[child1] > arr[largest])
            largest = child1;

        if (child2 < n && arr[child2] > arr[largest])
            largest = child2;

        if (child3 < n && arr[child3] > arr[largest])
            largest = child3;

        if (largest != i) {
            QuickSorter.swap(arr, i, largest);
            ternaryHeapify(arr, n, largest);
        }
    }

    public static void ternaryHeapSort(int[] arr) {
        int n = arr.length;

        for (int i = (n - 1) / 3; i >= 0; i--)
            ternaryHeapify(arr, n, i);

        for (int i = n - 1; i > 0; i--) {
            QuickSorter.swap(arr, 0, i);
            ternaryHeapify(arr, i, 0);
        }
    }
}
