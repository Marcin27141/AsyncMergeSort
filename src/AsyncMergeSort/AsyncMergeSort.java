package AsyncMergeSort;

public class AsyncMergeSort {
    private final int[] array;
    private final int nrOfThreads;
    SortThread[] threads;
    
    public AsyncMergeSort(int[] array, int nrOfThreads) {

        this.array = array;
        this.nrOfThreads = nrOfThreads;
    }

    public void sortAsync() {
        if (nrOfThreads == 1)
            new Subarray(array, 0, array.length - 1).sort();
        else {
            invokeMultithreadingSorting();
            waitForResult();
        }
    }

    private void waitForResult() {
        try {
            threads[0].join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void invokeMultithreadingSorting() {
        threads = new SortThread[nrOfThreads];
        int subarrayLength = array.length / nrOfThreads;
        for (int i = 0; i < nrOfThreads; i++) {
            threads[i] = createSortThread(subarrayLength, i);
            threads[i].start();
        }
    }
    
    private SortThread createSortThread(int subarrayLength, int threadIdx) {
        int rightIdx = threadIdx == nrOfThreads - 1 ? array.length - 1 : (threadIdx+1)*subarrayLength - 1;
        Subarray subarray = new Subarray(array, threadIdx*subarrayLength, rightIdx);
        return new SortThread(subarray, threadIdx, threads);
    }
}