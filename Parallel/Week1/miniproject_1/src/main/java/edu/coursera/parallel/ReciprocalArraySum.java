package edu.coursera.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ForkJoinPool;
/**
 * Class wrapping methods for implementing reciprocal array sum in parallel.
 */
public final class ReciprocalArraySum {

    /**
     * Default constructor.
     */
    private ReciprocalArraySum() {
    }

    /**
     * Sequentially compute the sum of the reciprocal values for a given array.
     *
     * @param input Input array
     * @return The sum of the reciprocals of the array input
     */
    protected static double seqArraySum(final double[] input) {
        double sum = 0;

        // Compute sum of reciprocals of array elements
        for (int i = 0; i < input.length; i++) {
            sum += 1 / input[i];
        }

        return sum;
    }

    /**
     * Computes the size of each chunk, given the number of chunks to create
     * across a given number of elements.
     *
     * @param nChunks The number of chunks to create
     * @param nElements The number of elements to chunk across
     * @return The default chunk size
     */
    private static int getChunkSize(final int nChunks, final int nElements) {
        // Integer ceil
        return (nElements + nChunks - 1) / nChunks;
    }

    /**
     * Computes the inclusive element index that the provided chunk starts at,
     * given there are a certain number of chunks.
     *
     * @param chunk The chunk to compute the start of
     * @param nChunks The number of chunks created
     * @param nElements The number of elements to chunk across
     * @return The inclusive index that this chunk starts at in the set of
     *         nElements
     */
    private static int getChunkStartInclusive(final int chunk,
					      final int nChunks, final int nElements) {
        final int chunkSize = getChunkSize(nChunks, nElements);
        return chunk * chunkSize;
    }

    /**
     * Computes the exclusive element index that the provided chunk ends at,
     * given there are a certain number of chunks.
     *
     * @param chunk The chunk to compute the end of
     * @param nChunks The number of chunks created
     * @param nElements The number of elements to chunk across
     * @return The exclusive end index for this chunk
     */
    private static int getChunkEndExclusive(final int chunk, final int nChunks,
            final int nElements) {
        final int chunkSize = getChunkSize(nChunks, nElements);
        final int end = (chunk + 1) * chunkSize;
        if (end > nElements) {
            return nElements;
        } else {
            return end;
        }
    }

    private static class ReciprocalArraySumTask extends RecursiveAction {
        private final int SEQ_THRESHOLD;
        private final int startIndexInclusive;
        private final int endIndexExclusive;
        private final double[] input;
        private double value;
        private int numTasks;

        ReciprocalArraySumTask(int setStartIndexInclusive, int setEndIndexExclusive, double[] setInput, int setNumTasks) {
	    //this.SEQ_THRESHOLD = setInput.length/setNumTasks;
	    this.SEQ_THRESHOLD = (setInput.length+1)/setNumTasks;
	    this.startIndexInclusive = setStartIndexInclusive;
	    this.endIndexExclusive = setEndIndexExclusive;
	    this.input = setInput;
	    this.numTasks = setNumTasks;
        }
        public double getValue() {
            return value;
        }
        @Override
        protected void compute() {
            int nElements = endIndexExclusive - startIndexInclusive;
            if (nElements <= SEQ_THRESHOLD) {
                for (int i = startIndexInclusive; i < endIndexExclusive; i++) {
                    value += 1/input[i];
                }
            } else {
                List<ReciprocalArraySumTask> taskList = new ArrayList<>();
                for (int i = 0; i < numTasks; i++) {
                    int startIndex = getChunkStartInclusive(i, numTasks, nElements) + startIndexInclusive;
                    int endIndex = getChunkEndExclusive(i, numTasks, nElements) + startIndexInclusive;
                    ReciprocalArraySumTask currTask = new ReciprocalArraySumTask(startIndex, endIndex, input, numTasks);
                    taskList.add(currTask);
                }
                for (ReciprocalArraySumTask task : taskList) {
                    task.fork();
                }
                for (ReciprocalArraySumTask task : taskList) {
                    task.join();
                    value += task.getValue();
                }
            }
        }
    }
    /**
     * TODO: Modify this method to compute the same reciprocal sum as
     * seqArraySum, but use two tasks running in parallel under the Java Fork
     * Join framework. You may assume that the length of the input array is
     * evenly divisible by 2.
     *
     * @param input Input array
     * @return The sum of the reciprocals of the array input
     */
    protected static double parArraySum(final double[] input) {
        assert input.length % 2 == 0;
        // Compute sum of reciprocals of array elements
        // call a class which extends recursiveAction to do the calculation in parallel
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        //ReciprocalArraySumTask1 task = new ReciprocalArraySumTask1(0, input.length, input);
        ReciprocalArraySumTask task = new ReciprocalArraySumTask(0, input.length, input, 2);
        // use ForkJoinPool to call for the parallel calculation
        forkJoinPool.commonPool().invoke(task);
        return task.getValue();
    }

    /**
     * TODO: Extend the work you did to implement parArraySum to use a set
     * number of tasks to compute the reciprocal array sum. You may find the
     * above utilities getChunkStartInclusive and getChunkEndExclusive helpful
     * in computing the range of element indices that belong to each chunk.
     *
     * @param input Input array
     * @param numTasks The number of tasks to create
     * @return The sum of the reciprocals of the array input
     */
    protected static double parManyTaskArraySum(final double[] input,
						final int numTasks) {
	    ForkJoinPool forkJoinPool = new ForkJoinPool();
        ReciprocalArraySumTask task = new ReciprocalArraySumTask(0, input.length, input, numTasks);
        //ReciprocalArraySumTask task = new ReciprocalArraySumTask(0, input.length, input);
        // use ForkJoinPool to call for the parallel calculation
        forkJoinPool.commonPool().invoke(task);
        return task.getValue();
    }
    static public void main(String[] args) {
        System.out.println("start calculation.......");
        List<Double> list = new ArrayList<>();
        int size = 1000000;
        for (int i = 1; i <= size; i++) {
            list.add(new Double(i));
        }
        double[] input = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            input[i] = list.get(i);
        }
        double seqRes = seqArraySum(input);
        double parRes = parArraySum(input);
        double parManyRes = parManyTaskArraySum(input, 5);
        System.out.println("sequential calculation result is: " + seqRes);
        System.out.println("simple parallel calculation result is: " + parRes);
        System.out.println("many parallel calculation result is: " + parManyRes);
    }
}
