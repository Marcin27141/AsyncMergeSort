package Lab12Zad1;

public class AsyncMergeSort {
    SortThread[] threads;

    class SortThread extends Thread {
        private boolean sortDone = false;
        private boolean mergeDone = false;
        private int subarraysMerged;
        private int rightMergeIndex;

        private final int[] arr;
        private final int l;
        private final int r;
        private final int id;

        public SortThread(int[] arr, int l, int r, int id) {
            super();
            this.arr = arr;
            this.l = l;
            this.r = r;
            this.id = id;
            subarraysMerged = 0;
            rightMergeIndex = r;
        }

        @Override
        public void run() {
            sortAsync(arr, l, r, this);
            mergeAsync(arr, l, this);
        }
    }

    public void sortAsync(int[] arr, int nrOfThreads) {
        if (nrOfThreads == 1) sort(arr, 0, arr.length - 1);
        else {
            threads = new SortThread[nrOfThreads];
            int part = arr.length / nrOfThreads;
            for (int i = 0; i < nrOfThreads - 1; i++) {
                threads[i] = new SortThread(arr, i*part, (i+1)*part - 1, i);
                threads[i].start();
            }
            int last = nrOfThreads - 1;
            threads[last] = new SortThread(arr, last*part, arr.length - 1, last);
            threads[last].start();
        }

        try {
            threads[0].join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sortAsync(int arr[], int l, int r, SortThread thread) {
        sort(arr, l, r);
        synchronized (this) {
            //if length of array is odd number, second to last thread makes sure that last thread did sorting before setting sortDone=true
            if (arr.length % 2 == 0 || thread.id != threads.length - 2)
                thread.sortDone = true;
            else {
                while (!threads[threads.length-1].sortDone) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            notifyAll();

            //if thread is going to merge (thread id is even and thread is not last thread) it has to wait for next thread to finish sorting
            if (thread.id % 2 == 0 && thread.id != threads.length - 1) {
                while (!threads[thread.id + 1].sortDone) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void mergeAsync(int arr[], int l, SortThread thread) {
        if (thread.id % 2 != 0)
            thread.mergeDone = true;
        int i = 2;

        while (!thread.mergeDone) {
            var nextThread = (thread.id + (i/2) < threads.length) ? threads[thread.id + (i/2)] : thread;

            if (thread != nextThread) {
                merge(arr, l, nextThread.l - 1, nextThread.rightMergeIndex);
                thread.rightMergeIndex = nextThread.rightMergeIndex;
                thread.subarraysMerged = i;
            }

            synchronized (this) {
                notifyAll();
                if (thread.id % (i * 2) == 0 && thread.id + i < threads.length) {
                    while (!threads[thread.id + i].mergeDone && threads[thread.id + i].subarraysMerged < thread.subarraysMerged) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else thread.mergeDone = true;
            }

            i *= 2;
        }
    }

    private void merge(int arr[], int l, int m, int r)
    {
        // Find sizes of two subarrays to be merged
        int n1 = m - l + 1;
        int n2 = r - m;

        /* Create temp arrays */
        int L[] = new int[n1];
        int R[] = new int[n2];

        /*Copy data to temp arrays*/
        for (int i = 0; i < n1; ++i)
            L[i] = arr[l + i];
        for (int j = 0; j < n2; ++j)
            R[j] = arr[m + 1 + j];

        /* Merge the temp arrays */

        // Initial indexes of first and second subarrays
        int i = 0, j = 0;

        // Initial index of merged subarray array
        int k = l;
        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) {
                arr[k] = L[i];
                i++;
            }
            else {
                arr[k] = R[j];
                j++;
            }
            k++;
        }

        /* Copy remaining elements of L[] if any */
        while (i < n1) {
            arr[k] = L[i];
            i++;
            k++;
        }

        /* Copy remaining elements of R[] if any */
        while (j < n2) {
            arr[k] = R[j];
            j++;
            k++;
        }
    }

    // Main function that sorts arr[l..r] using
    // merge()
    private void sort(int arr[], int l, int r)
    {
        if (l < r) {
            // Find the middle point
            int m = l + (r - l) / 2;

            // Sort first and second halves
            sort(arr, l, m);
            sort(arr, m + 1, r);

            // Merge the sorted halves
            merge(arr, l, m, r);
        }
    }

    /* A utility function to print array of size n */
    public static void printArray(int arr[])
    {
        int n = arr.length;
        for (int i = 0; i < n; ++i)
            System.out.print(arr[i] + " ");
        System.out.println();
    }
}
