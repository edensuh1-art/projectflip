import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) {

        List<BioSortingAlgorithm> sortingAlgorithms = new ArrayList<>();
        //sortingAlgorithms.add(new BubbleSortBad());
        sortingAlgorithms.add(new YourFullNameSort());

        for (BioSortingAlgorithm sortingAlgorithm : sortingAlgorithms) {

            simpleManualTest(sortingAlgorithm);

            randomizedSmallCorrectnessTests(sortingAlgorithm);
            performanceTest(sortingAlgorithm, 100); // use more trials (e.g. 1000) for more accurate estimates

            collectScalingData(sortingAlgorithm);
        }
    }

    private static void simpleManualTest(BioSortingAlgorithm sortingAlgorithm) {
        System.out.println();
        System.out.println("Simple Manual Test for " + sortingAlgorithm.getClass().getName());
        // Tip:  if a random test fails, copy it in here, and then you can debug it more easily
        BioArray arr1 = new BioArray(6, 7, 8, 9, 10, 5, 4, 3, 2, 1);

        System.out.println("Before sorting: " + arr1);
        sortingAlgorithm.sort(arr1);
        System.out.println(" After sorting: " + arr1);
        System.out.println("Sorted correctly? " + arr1.isFullySorted());
    }

    private static void randomizedSmallCorrectnessTests(BioSortingAlgorithm sortingAlgorithm) {
        System.out.println("Starting randomized correctness tests for " + sortingAlgorithm.getClass().getName());
        final int ARRAY_LENGTH_MIN = 2;
        final int ARRAY_LENGTH_MAX = 41;
        // test a bunch of random arrays with small lengths
        // in the hope that if there's a bug in the sorting algorithm, some random input will catch it
        // (Of course a solid proof of correctness would be better, but much harder to do!)
        for (int i = 0; i < 1000000; i++) {
            int arrayLength = ARRAY_LENGTH_MIN + i % (ARRAY_LENGTH_MAX-ARRAY_LENGTH_MIN+1);
            double[] originalArray =  makeRandomPermutationShortArray(arrayLength);
            BioArray arr2 = new BioArray(originalArray);
            sortingAlgorithm.sort(arr2);

            if (!arr2.isFullySorted()) {
                System.err.println(sortingAlgorithm.getClass().getName());
                System.err.println("Failed to sort array: " + Arrays.toString(originalArray));
                System.err.println("After attempted sort: " + arr2);
                System.exit(1);
            }
        }
        System.out.println("Completed randomized correctness tests for " + sortingAlgorithm.getClass().getName());
    }


    private static void collectScalingData(BioSortingAlgorithm sortingAlgorithm) {
        System.out.println("Collecting data for " + sortingAlgorithm.getClass().getName()+ ":\n");
        System.out.printf("     N  \tflipCount\tcompare4Count\tRobotTime\n");

        for (int N = 4; N <= 16384; N = N * 2) {
            BioArray arr3 = new BioArray(makeRandomLargeTestArray(N));
            sortingAlgorithm.sort(arr3);

            if (!arr3.isFullySorted()) {
                System.err.println("BUG: Failed to correctly sort some array of size " + N);
            }
            long numFlips =  arr3.getFlipCount();
            long numCompare4 = arr3.getCompare4Count();
            double robotTime = arr3.getEstimatedRobotTime();
            System.out.printf("%10d\t%10d\t%10d\t%10.1f\n",N, numFlips, numCompare4, robotTime);
        }
    }

    private static void performanceTest(BioSortingAlgorithm sortingAlgorithm, int numTrials) {
        int N = 2000; // the number of items we expect to sort most often when creating a dragon genome

        double robotTimeSum = 0.0;
        double comparesSum = 0.0;
        double flipsSum = 0.0;

        System.out.printf("Running %d performance trials for %-40s.", numTrials, sortingAlgorithm.getClass().getName());

        for (int i = 0; i < numTrials; i++) {
            if (i%(numTrials/10) == (numTrials/10-1)) {
                System.out.print(".");  // console progress indicator
            }
            BioArray arr3 = new BioArray(makeRandomLargeTestArray(N));
            sortingAlgorithm.sort(arr3);

            if (!arr3.isFullySorted()) {
                System.err.println("BUG: Failed to correctly sort some array of size " + N);
                System.exit(1);
            }
            robotTimeSum = robotTimeSum + arr3.getEstimatedRobotTime();
            comparesSum = comparesSum + arr3.getCompare4Count();
            flipsSum = flipsSum + arr3.getFlipCount();
        }
        // Uncomment this if you want the breakdown of flips vs compares.
        System.out.printf(" compares=%.0f flips=%.0f robotTime=%.0f\n", comparesSum/numTrials, flipsSum/numTrials, robotTimeSum/numTrials );
        System.out.printf("%10.0f\n", (robotTimeSum/numTrials) );

    }

    public static Random randGen = new Random();

    private static double[] makeRandomPermutationShortArray(int len) {
        double[] dArray = new double[len];
        for (int i = 0; i < dArray.length; i++) {
            dArray[i] = i+1;
        }
        shuffle(dArray);
        return dArray;
    }

    /**
     * Knuth shuffle
     * Rearranges the elements of the specified array in uniformly random order.
     */
    private static void shuffle(double[] a) {
        int n = a.length;
        for (int i = 0; i < n; i++) {
            int r = i + randGen.nextInt(n-i);   // between i and n-1
            double temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }

    private static double[] makeRandomLargeTestArray(int len) {
        double[] dArray = new double[len];
        for (int i = 0; i < dArray.length; i++) {
            dArray[i] = randGen.nextDouble();
        }
        return dArray;
    }
}