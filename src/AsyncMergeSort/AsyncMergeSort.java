package AsyncMergeSort;

public class AsyncMergeSort {
    SortThread[] threads;

    public void sortAsync(int[] arr, int nrOfThreads) {
        if (nrOfThreads == 1)
            new Subarray(arr, 0, arr.length - 1).sort();
        else {
            threads = new SortThread[nrOfThreads];
            int part = arr.length / nrOfThreads;
            for (int i = 0; i < nrOfThreads; i++) {
                int rightIdx = i == nrOfThreads - 1 ? arr.length - 1 : (i+1)*part - 1;
                Subarray subarray = new Subarray(arr, i*part, rightIdx);
                threads[i] = new SortThread(subarray, i, threads);
                System.out.println(i + " " + i*part + " " + rightIdx);
                threads[i].start();
            }
        }

        try {
            threads[0].join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void printArray(int[] arr)
    {
        int n = arr.length;
        for (int i = 0; i < n; ++i)
            System.out.print(arr[i] + " ");
        System.out.println();
    }
}