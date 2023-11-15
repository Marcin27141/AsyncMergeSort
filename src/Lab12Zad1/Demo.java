package Lab12Zad1;
import AsyncMergeSort.AsyncMergeSort;

public class Demo {
    private final static int NUMBER_OF_THREADS = 4;
    public static void main(String[] args) {
        int[] array = { 1, 5, 2, 39, 29, 1, 199, 18, 1902, 12, -17, 3, 42, 18, 129, 0, 381, 432, 32 };

        printArray(array);
        new AsyncMergeSort(array, NUMBER_OF_THREADS).sortAsync();
        printArray(array);
    }

    public static void printArray(int[] arr)
    {
        for (int j : arr)
            System.out.print(j + " ");
        System.out.println();
    }
}
