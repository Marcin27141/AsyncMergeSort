package AsyncMergeSort;

public class AsyncMergeSort {
    SortThread[] threads;

    class SortThread extends Thread {
        private final SortThread[] threads;
        private boolean sortDone = false;
        private boolean mergeDone = false;
        private int subarraysMerged = 0;


        private Subarray subarray;
        private final int id;


        public SortThread(Subarray subarray, int id, SortThread[] threads) {
            super();
            this.subarray = subarray;
            this.id = id;
            this.threads = threads;
        }

        public Boolean isResponsibleForLastThread() {
            return subarray._arr.length % 2 != 0 && id == threads.length - 2;
        }

        public Boolean isResponsibleForMerging() {
            return id % 2 == 0;
        }

        public Boolean mustWaitBeforeMerging() {
            return isResponsibleForMerging() && id != threads.length - 1;
        }

        public Boolean isDoneAfterMergingArrays(int number) {
            return id % (number * 2) != 0 || id + number >= threads.length;
        }

        @Override
        public void run() {
            //System.out.println("Thread with id " + id + " has real id " + getId());
            sortAsync(this);
            mergeAsync(this);
        }
    }

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

    private void sortAsync(AsyncMergeSort.SortThread thread) {
        thread.subarray.sort();
        performAfterSort(thread);
        //System.out.println("Thread " + id + " finshed sorting");
    }

    private void performAfterSort(AsyncMergeSort.SortThread thread) {
        System.out.println("thread " + thread.id + " entering post sort action");
        if (thread.mustWaitBeforeMerging()) {
            System.out.println(thread.id + ": thred " + (thread.id + 1) + " sort finished? " + threads[thread.id + 1].sortDone);

        }

        synchronized (this) {
            if (!thread.isResponsibleForLastThread())
                thread.sortDone = true;
            else {
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                while (!threads[threads.length-1].sortDone) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                thread.sortDone = true;
            }

            notifyAll();

            //if thread is going to merge (thread id is even and thread is not last thread) it has to wait for next thread to finish sorting
            if (thread.mustWaitBeforeMerging()) {
                while (!threads[thread.id + 1].sortDone) {
                    try {
                        System.out.println("Thread " + thread.id + " waiting after sort for thread " + (thread.id+1));
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Thread " + thread.id + " steps out of sort block");
        }
    }

    private void mergeAsync(AsyncMergeSort.SortThread thread) {
        if (!thread.isResponsibleForMerging())
            thread.mergeDone = true;

        int i = 2;

        while (!thread.mergeDone) {
            var nextThread = (thread.id + (i/2) < threads.length) ? threads[thread.id + (i/2)] : thread;

            if (thread != nextThread) {
                thread.subarray = new Subarray(thread.subarray._arr, thread.subarray._leftIdx, nextThread.subarray._rightIdx);
                thread.subarray.merge();
                thread.subarraysMerged = i;
            }

            synchronized (this) {
                System.out.println("Entering synchronized merge for thread " + thread.id);
                notifyAll();
                if (!thread.isDoneAfterMergingArrays(i)) {
                    while (!threads[thread.id + i].mergeDone && threads[thread.id + i].subarraysMerged < thread.subarraysMerged) {
                        try {
                            System.out.println("Thread " + thread.id + " is merge waiting for thread " + thread.id + i);
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else thread.mergeDone = true;
            }

            i *= 2;
        }
        System.out.println("thread " + thread.id + " finished job");
    }

    public static void printArray(int[] arr)
    {
        int n = arr.length;
        for (int i = 0; i < n; ++i)
            System.out.print(arr[i] + " ");
        System.out.println();
    }
}
