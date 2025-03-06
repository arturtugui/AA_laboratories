public class InsertionSorter {
    // insertion sort
    public static void insertionSortHelper(int[] arr) {
        insertionSort(arr, arr.length - 1);
    }

    public static void insertionSort(int[] arr, int n){
        for(int i = 1; i < n; i++){
            int key = arr[i];
            int j = i - 1;
            while(j >= 0 && arr[j] > key){
                arr[j+1] = arr[j];
                j--;
            }
            arr[j+1] = key;
        }
    }

    // shell sort
    public static void shellSortHelper(int[] arr) {
        shellSort(arr, arr.length - 1);
    }

    public static void shellSort(int[] arr, int n) {
        for(int gap = n/2; gap > 0; gap /= 2) {
            for(int i = gap; i < n; i++){
                int key = arr[i];
                int j = i;
                while(j >= gap && arr[j-gap] > key){
                    arr[j] = arr[j-gap];
                    j = j-gap;
                }
                arr[j] = key;
            }
        }

    }
}
