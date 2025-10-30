// FIXME: rename to YourActualNameSort before submitting
public class YourFullNameSort implements BioSortingAlgorithm {

    @Override
    public void sort(BioArray a) {
        int n = a.getLength();
        if (n <= 1) return;

        int i = 1; // grow sorted prefix [0..i-1]
        while (i < n) {

            // Fast path: already >= predecessor ⇒ extend prefix
            if (leq(a, i - 1, i)) { i++; continue; }

            // Try a SAFE 2-item coalesced insert [i, i+1]
            if (i + 1 < n && leq(a, i, i + 1)) {
                // Both items appear in nondecreasing order as a pair.
                // Check if the *second* one also needs to move left (else pair won't coalesce usefully).
                if (!leq(a, i - 1, i + 1)) {

                    // Compute insertion positions *in the current array state*
                    // for both elements if inserted individually:
                    int pos1 = lowerBound(a, i,   0, i - 1);   // where a[i] would go
                    int pos2 = lowerBound(a, i+1, 0, i);       // where a[i+1] would go (can look into [0..i])

                    // Coalesce only when the pair can be planted contiguously (stable):
                    // i.e., the second's target isn't to the right of the first's target.
                    if (pos2 <= pos1) {
                        // Move the pair [i..i+1] left into position pos2 with a stable 3-flip rotation:
                        // [pos2..i-1] + [i..i+1]  ==>  [i..i+1] + [pos2..i-1]
                        rotateBlockLeft(a, pos2, i, i + 1);

                        // After rotation, both elements are placed at [pos2, pos2+1].
                        // The prefix grows to at least pos2+2, but our loop invariant is "prefix ends at i".
                        // Since the pair moved left before 'i', we can advance i by 2 to avoid re-touching them.
                        i += 2;
                        continue;
                    }
                }
            }

            // Fallback: do a single-element binary insertion at i (≤2 flips)
            singleInsert(a, i);
            i++;
        }
    }

    // ---------------- helpers ----------------

    // True iff a[p] <= a[q] using one compare4 call with duplicate indices
    private boolean leq(BioArray a, int p, int q) {
        int[] ord = a.compare4(p, p, q, q);
        return ord[0] == p;
    }

    // Stable leftmost insertion index for value at keyIdx into sorted range [lo..hi]
    private int lowerBound(BioArray a, int keyIdx, int lo, int hi) {
        if (lo > hi) return lo;
        if (leq(a, hi, keyIdx)) return hi + 1;
        if (!leq(a, lo, keyIdx)) return lo;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (leq(a, keyIdx, mid)) hi = mid; else lo = mid + 1;
        }
        return lo;
    }

    // Single-element insertion of index 'idx' into its position with ≤2 flips (stable).
    private void singleInsert(BioArray a, int idx) {
        // if already in order with predecessor, nothing to do
        if (idx <= 0 || leq(a, idx - 1, idx)) return;

        int pos = lowerBound(a, idx, 0, idx - 1);
        if (pos == idx) return;                  // shouldn't happen due to guard, but safe
        if (pos == idx - 1) { a.flip(pos, idx); return; }  // adjacent: 1 flip
        a.flip(pos, idx - 1);                    // reverse A
        a.flip(pos, idx);                        // reverse (A^R + x) -> x + A
    }

    // Rotate a contiguous block [L..R] left into position 'pos' (pos <= L), stably for the left block.
    // A = [pos..L-1], B = [L..R]; want A+B -> B+A.
    // General: 3 flips; Adjacent (pos == L-1): 2 flips.
    private void rotateBlockLeft(BioArray a, int pos, int L, int R) {
        if (pos == L) return;                  // already in place
        if (pos == L - 1) {                    // adjacent A|B ⇒ 2 flips
            a.flip(L, R);                      // reverse B
            a.flip(pos, R);                    // (A + B^R) -> B + A
            return;
        }
        a.flip(pos, L - 1);                    // reverse A
        a.flip(L, R);                          // reverse B
        a.flip(pos, R);                        // (A^R + B^R) -> B + A
    }
}
