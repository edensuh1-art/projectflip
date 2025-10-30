
public class BubbleSortBad implements BioSortingAlgorithm {

    // Here is a version of "bubble sort", badly adapted for 4-op bio-arrays
    //
    // This is a TERRIBLE algorithm. Bubble Sort is bad to start with,
    // and this version doesn't take advantage of the power to compare or move more than two items at a time.
    //
    // However, it provides a very basic code example of how to implement a sorting algorithm for BioArrays

    public void sort(BioArray a) {
        int len = a.getLength();
        if (len <= 1) {
            return;  // no sorting needed!
        } else { // length > 4, use an adapted version of bubble sort
            boolean anySwaps;
            do {
                anySwaps = false;
                for (int i = 0; i < a.getLength() - 1; i++) {
                    // note that we are passing repeated indexes to compare4,
                    // and thus not taking advantage of the possibility of a 4-way comparison
                    int[] indexesInSortedOrder = a.compare4(i, i, i + 1, i + 1);
                    // the returned array will be [i, i, i+1, i+1] if a[i] <= a[i+1],
                    //              or it will be [i+1, i+1, i, i] if a[i] >  a[i+1]
                    boolean theseTwoInOrder = (indexesInSortedOrder[0] == i);
                    if (!theseTwoInOrder) {
                        a.flip(i, i + 1);
                        anySwaps = true;
                    }
                }
            } while (anySwaps);
        }
    }
}
