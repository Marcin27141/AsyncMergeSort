package AsyncMergeSort;

public class SortThread extends Thread {
    private static final Object lock = new Object();
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

    private Boolean isResponsibleForLastThread() {
        return subarray._arr.length % 2 != 0 && id == threads.length - 2;
    }

    private Boolean isResponsibleForMerging() {
        return id % 2 == 0;
    }
    private Boolean mustWaitBeforeMerging() {
        return isResponsibleForMerging() && id != threads.length - 1;
    }

    private Boolean isDoneAfterMergingArrays(int number) {
        return id % (number * 2) != 0 || id + number >= threads.length;
    }

    private Boolean awaitedThreadStillMerging(int number) {
        return !threads[id + number].mergeDone &&
                threads[id + number].subarraysMerged < subarraysMerged;
    }

    private Boolean lastThreadFinishedSorting() {
        return threads[threads.length-1].sortDone;
    }

    @Override
    public void run() {
        sortAsync();
        mergeAsync();
    }

    private void sortAsync() {
        subarray.sort();
        synchronized (lock) {
            System.out.println("thread " + id + " sorted");
            for (int j = 0; j < subarray._arr.length; j++)
                System.out.print(subarray._arr[j] + " ");
            System.out.println();
        }
        performAfterSort();
    }

    private void performAfterSort() {
        synchronized (lock) {
            if (!isResponsibleForLastThread())
                sortDone = true;
            else {
                while (!lastThreadFinishedSorting()) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            lock.notifyAll();

            if (mustWaitBeforeMerging()) {
                while (!threads[id + 1].sortDone) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void mergeAsync() {
        if (!isResponsibleForMerging())
            mergeDone = true;

        int i = 2;

        while (!mergeDone) {
            var nextThread = (id + (i/2) < threads.length) ? threads[id + (i/2)] : this;

            if (this != nextThread) {
                subarray = subarray.joinRightSubarray(nextThread.subarray);
                subarray.merge();
                synchronized (lock) {
                    System.out.println("thread " + id + " merged");
                    for (int j = 0; j < subarray._arr.length; j++)
                        System.out.print(subarray._arr[j] + " ");
                    System.out.println();
                }
                subarraysMerged = i;
            }

            synchronized (lock) {
                lock.notifyAll();
                if (!isDoneAfterMergingArrays(i)) {
                    while (awaitedThreadStillMerging(i)) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else mergeDone = true;
            }

            i *= 2;
        }
    }
}
