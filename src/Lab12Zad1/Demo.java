package Lab12Zad1;


import AsyncMergeSort.AsyncMergeSort;
import AsyncMergeSort.Subarray;

public class Demo {
    private final static int NUMBER_OF_THREADS = 4;
    public static void main(String[] args) {
        int[] array = { 1, 5, 2, 39, 29, 1, 199, 12, -17, 3, 42, 18, 129, 0, 381, 432, 32 };


        AsyncMergeSort.printArray(array);
        new AsyncMergeSort().sortAsync(array, NUMBER_OF_THREADS);
        AsyncMergeSort.printArray(array);

//        Subarray[] subarrays = new Subarray[NUMBER_OF_THREADS];
//        int part = array.length / NUMBER_OF_THREADS;
//        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
//            int rightIdx = i == NUMBER_OF_THREADS - 1 ? array.length - 1 : (i+1)*part - 1;
//            subarrays[i] = new Subarray(array, i*part, rightIdx);
//            System.out.println(i + " " + i*part + " " + rightIdx);
//        }
//        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
//            System.out.println("Thread " + i);
//            AsyncMergeSort.printArray(array);
//            subarrays[i].sort();
//            AsyncMergeSort.printArray(array);
//        }
//
//        Subarray leftHalf = new Subarray(array, 0, 7);
//        Subarray rightHalf = new Subarray(array, 8, 15);
//        leftHalf.merge();
//        AsyncMergeSort.printArray(array);
//        rightHalf.merge();
//        AsyncMergeSort.printArray(array);
//        Subarray finalArray = new Subarray(array, 0, 15);
//        finalArray.merge();
//        AsyncMergeSort.printArray(array);
    }
}
