package AsyncMergeSort;

public class SortThread extends Thread {
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
        System.out.println("Thread with id " + id + " has real id " + getId());
        sortAsync();
        mergeAsync();
    }

    private void sortAsync() {
        subarray.sort();
        performAfterSort();
        System.out.println("Thread " + id + " finshed sorting");
    }

    private synchronized void performAfterSort() {
        System.out.println("thred " + id + " entering post sort action");
        if (mustWaitBeforeMerging()) {
            System.out.println(id + ": thred " + (id + 1) + " sort finished? " + threads[id + 1].sortDone);

        }

        if (!isResponsibleForLastThread())
            sortDone = true;
        else {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            while (!threads[threads.length-1].sortDone) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            sortDone = true;
        }

        notifyAll();

        //if thread is going to merge (thread id is even and thread is not last thread) it has to wait for next thread to finish sorting
        if (mustWaitBeforeMerging()) {
            while (!threads[id + 1].sortDone) {
                try {
                    System.out.println("Thread " + id + " waiting after sort for thread " + (id+1));
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Thread " + id + " steps out of sort block");
    }

    private void mergeAsync() {
        if (!isResponsibleForMerging())
            mergeDone = true;

        int i = 2;

        System.out.println("Entering merge for thread " + id);
        while (!mergeDone) {
            var nextThread = (id + (i/2) < threads.length) ? threads[id + (i/2)] : this;

            if (this != nextThread) {
                subarray = new Subarray(subarray._arr, subarray._leftIdx, nextThread.subarray._rightIdx);
                subarray.merge();
                subarraysMerged = i;
            }

            synchronized (this) {
                notifyAll();
                if (!isDoneAfterMergingArrays(i)) {
                    System.out.println("Thread " + id + " is waiting for thread " + id + i);
                    while (!threads[id + i].mergeDone && threads[id + i].subarraysMerged < subarraysMerged) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else mergeDone = true;
            }

            i *= 2;
        }
        System.out.println("thread " + id + " finished job");
    }
}
