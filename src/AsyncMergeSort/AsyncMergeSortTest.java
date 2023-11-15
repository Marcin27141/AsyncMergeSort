package AsyncMergeSort;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AsyncMergeSortTest {
    static int[][] arrays = {
            new int[] { 1, 5, 2, 39, 29, 1, 199, 18, 1902, 12, -17, 3, 42, 18, 129, 0, 381, 432, 32 },
            new int[] { 10, 5, 3, 7, 1, 8, 2, 6, 9, 4 },
            new int[] { 3, 2, 1, 5, 4, 8, 7, 6, 10, 9 },
    };
    @Test
    public void testSortWithVariousArraySizesAndThreadCounts() {
        Random random = new Random();
        for (int[] array : arrays)
            testSort(array, random.nextInt(7));
    }

    @Test
    public void testNegativeNumberOfThreads() {
        Random random = new Random();
        for (int[] array : arrays)
            testSort(array, -1 * random.nextInt(7));
    }

    @Test
    public void testWithSingleThread() {
        for (int[] array : arrays)
            testSort(array, 1);
    }

    @Test
    public void testWithNumberOfThreadsLargerThanArrayLength() {
        Random random = new Random();
        for (int[] array : arrays)
            testSort(array, array.length + random.nextInt(7));
    }

    @Test
    public void testRandomCases() {
        Random random = new Random();
        int random_cases = 50;
        int maxArrayLength = 50;
        int absoluteMaxValue = 1000;

        for (int i = 0; i < random_cases; i++) {
            int[] randomArray = generateRandomArray(2 + random.nextInt(maxArrayLength + 1), absoluteMaxValue);
            testSort(randomArray, 2 + random.nextInt(randomArray.length - 1));
        }
    }

    private void testSort(int[] array, int numberOfThreads) {
        AsyncMergeSort sorter = new AsyncMergeSort(array, numberOfThreads);
        sorter.sortAsync();
        assertTrue(isSorted(array), "Array is not sorted correctly.");
    }

    private boolean isSorted(int[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1]) {
                return false;
            }
        }
        return true;
    }

    public static int[] generateRandomArray(int size, int maxAbsoluteValue) {
        int[] array = new int[size];
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            int multiplier = random.nextBoolean() ? 1 : -1;
            array[i] = multiplier * random.nextInt(maxAbsoluteValue + 1);
        }

        return array;
    }
}