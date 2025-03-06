public class QuickSorter {
    public static void swap(int[] arr, int a, int b) {
        if (a == b) {
            return;
        }

        int temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }



    // basic QuickSort with pivot as last element
    public static void quickSortHelper(int[] arr){
        quickSort(arr, 0, arr.length - 1);
    }

    public static int partition(int[] arr, int low, int high) {
            int pivot = arr[high];
            int i = low - 1;

            for(int j = low; j < high; j++){
                if(arr[j] < pivot){
                    i++;
                    swap(arr, i, j);
                }
            }

            swap(arr, i + 1, high);

        return i + 1;
    }

    public static void quickSort(int[] arr, int low, int high) {
        if(low < high){
            int pivotIndex = partition(arr, low, high);
            quickSort(arr, low, pivotIndex - 1);
            quickSort(arr, pivotIndex + 1, high);
        }
    }



    // QuickSort with pivot as median of 1st, middle and last
    public static void quickSortMotHelper(int[] arr){
        quickSortMot(arr, 0, arr.length - 1);
    }

    public static void quickSortMot(int[] arr, int low, int high) {
        if (low < high) {
            int pivotIndex = medianOfThree(arr, low, high);
            pivotIndex = partitionMot(arr, low, high, pivotIndex);
            quickSortMot(arr, low, pivotIndex - 1);
            quickSortMot(arr, pivotIndex + 1, high);
        }
    }

    private static int partitionMot(int[] arr, int low, int high, int pivotIndex) {
        int pivot = arr[pivotIndex];
        swap(arr, pivotIndex, high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (arr[j] < pivot) {
                i++;
                swap(arr, i, j);
            }
        }

        swap(arr, i + 1, high);
        return i + 1;
    }

    private static int medianOfThree(int[] arr, int low, int high) {
        int mid = low + (high - low) / 2;

        if (arr[mid] < arr[low]) swap(arr, low, mid);
        if (arr[high] < arr[low]) swap(arr, low, high);
        if (arr[high] < arr[mid]) swap(arr, mid, high);

        return mid;
    }
}
