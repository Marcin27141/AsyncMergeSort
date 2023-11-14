package AsyncMergeSort;

public class Subarray {
    protected final int[] _arr;
    protected Subarray _leftSubarray, _rightSubarray;
    protected final int _leftIdx;

    protected int _middle;
    protected final int _rightIdx;

    public Subarray(int[] arr, int leftIdx, int rightIdx) {
        this._arr = arr;
        this._leftIdx = leftIdx;
        this._rightIdx = rightIdx;
        _middle = getMiddle();

        if (!hasSingleElement()) {
            _leftSubarray = new Subarray(arr, leftIdx, _middle);
            _rightSubarray = new Subarray(arr, _middle + 1, rightIdx);
        }
    }

    public Subarray(Subarray left, Subarray right) {
        this._arr = left._arr;
        this._leftIdx = left._leftIdx;
        this._rightIdx = right._rightIdx;
        _middle = right._leftIdx - 1;

        _leftSubarray = new Subarray(_arr, _leftIdx, _middle);
        _rightSubarray = new Subarray(_arr, _middle + 1, _rightIdx);
    }

    public void sort()
    {
        if (_rightIdx == 16)
            System.out.println("break");
        if (!hasSingleElement()) {
            _leftSubarray.sort();
            _rightSubarray.sort();
            merge();
        }
    }

    public void merge()
    {
        int[] leftCopy = _leftSubarray.getArrayCopy();
        int[] rightCopy = _rightSubarray.getArrayCopy();

        int leftLength = leftCopy.length;
        int rightLength = rightCopy.length;

        int leftIdx = 0, rightIdx = 0;
        int resultIdx = _leftIdx;

        while (leftIdx < leftLength && rightIdx < rightLength) {
            if (leftCopy[leftIdx] <= rightCopy[rightIdx]) {
                _arr[resultIdx] = leftCopy[leftIdx];
                leftIdx++;
            }
            else {
                _arr[resultIdx] = rightCopy[rightIdx];
                rightIdx++;
            }
            resultIdx++;
        }

        while (leftIdx < leftLength) {
            _arr[resultIdx] = leftCopy[leftIdx];
            leftIdx++;
            resultIdx++;
        }

        while (rightIdx < rightLength) {
            _arr[resultIdx] = rightCopy[rightIdx];
            rightIdx++;
            resultIdx++;
        }
    }

    public Subarray joinRightSubarray(Subarray subarray) {
        return new Subarray(this, subarray);
    }

    private int getLength() {
        return _rightIdx - _leftIdx + 1;
    }

    private int getMiddle() {
        return _leftIdx + (_rightIdx - _leftIdx) / 2;
    }

    private int[] getArrayCopy() {
        int length = getLength();
        int[] copy = new int[length];
        for (int i = 0; i < length; ++i)
            copy[i] = _arr[_leftIdx + i];
        return copy;
    }

    private Boolean hasSingleElement() {
        return getLength() == 1;
    }
}
