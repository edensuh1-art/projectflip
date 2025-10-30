import java.util.Arrays;

// NOTE: You are NOT allowed to modify this class for this coding project
/**
 * This class is really just a minimal wrapper class around a double[] array, forcing
 * client code to call only the public methods, which allows this class to
 * accurately maintain statistics about how often its operations were performed!
 *
 * (If this class allowed the client code to get/set items of the array directly, then
 * they could perform their own comparisons or data movement that wouldn't get counted
 * in our performance measures.)
 *
 * While logically-equivalent Java source code is provided,
 * we are pretending the 4-way comparisons and the sort4 operations
 * are actually implemented in robotic hardware.
 */
public class BioArray {

	private double[] a;
	private long flipCount;
	private long compare4Count;

	// Java language note: the ... below means we can call this method with a
	// variable number of arguments ("varargs").
	// For example, calling new BioArray(1.0, 2.0, 3.0) is translated into
	// calling new BioArray(new double[] {1.0, 2.0, 3.0}).
	public BioArray(double... array) {
		this.a = Arrays.copyOf(array, array.length);
		this.flipCount = 0;
		this.compare4Count = 0;
	}

	/**
	 * @param w, x, y, z are the indexes of the 4 elements to compare
	 * @return an array containing the 4 indexes provided,
	 *         but in a (stably) sorted order based on the array values at those indexes
	 *         [smallestIndex, 2ndSmallestIndex, 3rdSmallestIndex, largestIndex]
	 *
	 *  Formally, the returned array contains a permutation of w,x,y, and z, such that
	 *     a[return[0]] <= a[return[1]] <= a[return[2]] <= a[return[3]]
	 */
	public synchronized int[] compare4(int w, int x, int y, int z) {
		compare4Count++;
		int[] oi = new int[] {w, x, y, z}; // ordered indexes

		int tmp;
		for (int i = 1; i < oi.length; i++) {
			for (int j = i; j > 0 && a[oi[j]] < a[oi[j-1]]; j--) {
				tmp = oi[j]; oi[j] = oi[j-1]; oi[j-1] = tmp; // swap j, j-1
			}
		}
		return oi;
	}

	/**
	 * @return true iff a[w] <= a[x] <= a[y] <= a[z]
	 *
	 * Convenience method (that costs as much robot time as compare4()).
	 * It's equivalent to checking whether compare4(w,x,y,z) == [w,x,y,z]
	 */
	public synchronized boolean isNonDecreasing4(int w, int x, int y, int z) {
		compare4Count++;
		return a[w] <= a[x] && a[x] <= a[y] && a[y] <= a[z];
	}

	/**
	 * @param startIndex (inclusive)
	 * @param endIndex   (inclusive)
	 *
	 * This method reverses the order of every element in the subarray starting at startIndex up to and including endIndex.
	 */
	public synchronized void flip(int startIndex, int endIndex) {
		flipCount++;

		for (int i = startIndex, j = endIndex; i < j; i++, j--) {
			double temp = a[i];
			a[i] = a[j];
			a[j] = temp;
		}
	}

	/**
	 * @return the length of this array
	 */
	public int getLength() {
		return a.length;
	}

	/**
	 * ONLY for debugging purposes, not for use in your algorithms.
	 * @return a string representing the underlying data behind this array
	 */
	public String toString() {
		return Arrays.toString(a);
	}

	/** provided only for debugging & correctness testing -- do NOT call as part of your sorting algorithms! */
	public boolean isFullySorted() {
		int i = 0;
		while (i < a.length - 1) {
			if (a[i] > a[i + 1]) {
				System.err.println("Array failed to be sorted at positions " + i + " and " + (i+1));
				System.err.println("a[" + i+ "] = " + a[i] + " and a[" + (i+1) + "] = " + a[i+1]);
				//System.err.println("Array is: " + Arrays.toString(a));
				return false;
			}
			i++;
		}
		return true;
	}

	/**
	 * @return how many flip ops occurred since this array was created?
	 */
	public long getFlipCount() {
		return flipCount;
	}

	/**
	 * @return how many compare4 ops have
	 * occurred since this array was created?
	 */
	public long getCompare4Count() {
		return compare4Count;
	}

	/**
	 * @return estimated robot sorting time, a weighted average of (# of flip ops) and (# of compare ops)
	 */
	public double getEstimatedRobotTime() {
		return flipCount + (compare4Count * 0.008);
	}

}
