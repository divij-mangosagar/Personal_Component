package components.datasetrv;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import components.sequence.Sequence;
import components.sequence.Sequence1L;

/**
 * JUnit test fixture for {@link DatasetRVSecondary} methods.
 */
public class DatasetRVTest {

    /**
     * Comparison threshold.
     */
    private static final double EPSILON = 0.0001;

    // HELPER METHOD

    /**
     * Creates a new DatasetRV1L from a 2D array of doubles.
     *
     * Example: createFromArgs(new double[][] {{1.0, 2.0}, {3.0, 4.0}}) creates
     * a dataset with: Variable 0: observations [1.0, 2.0] Variable 1:
     * observations [3.0, 4.0]
     *
     * @param data
     *            2D array where data[i][j] is observation j of variable i
     * @return a new DatasetRV1L containing the given data
     */
    private DatasetRV1L createFromArgs(double[][] data) {
        Sequence<Sequence<Double>> seq = new Sequence1L<>();
        for (int i = 0; i < data.length; i++) {
            Sequence<Double> row = new Sequence1L<>();
            for (int j = 0; j < data[i].length; j++) {
                row.add(row.length(), data[i][j]);
            }
            seq.add(seq.length(), row);
        }
        return new DatasetRV1L(seq);
    }

    /**
     * Helper to create a sequence of variable indices.
     *
     * @param indices
     *            the variable indices
     * @return a Sequence containing the indices
     */
    private Sequence<Integer> varSeq(int... indices) {
        Sequence<Integer> seq = new Sequence1L<>();
        for (int i : indices) {
            seq.add(seq.length(), i);
        }
        return seq;
    }

    // sampleMean TESTS

    /**
     * Tests sampleMean on a single variable.
     *
     * Data: [10.0, 20.0, 30.0] Expected mean = (10 + 20 + 30) / 3 = 60 / 3 =
     * 20.0
     */
    @Test
    public void testSampleMean() {
        final double[][] data = { { 10.0, 20.0, 30.0 } };
        DatasetRV1L ds = this.createFromArgs(data);

        double actualMean = ds.sampleMean(0);

        final double expectedMean = 20.0;
        assertEquals(expectedMean, actualMean, EPSILON);
    }

    /**
     * Tests sampleMean on multiple variables.
     *
     * Variable 0: [1.0, 2.0, 3.0] → mean = (1+2+3)/3 = 6/3 = 2.0 Variable 1:
     * [4.0, 5.0, 6.0] → mean = (4+5+6)/3 = 15/3 = 5.0
     */
    @Test
    public void testSampleMeanMultipleVariables() {
        final double[][] data = { { 1.0, 2.0, 3.0 }, { 4.0, 5.0, 6.0 } };
        DatasetRV1L ds = this.createFromArgs(data);

        double actualMean0 = ds.sampleMean(0);
        double actualMean1 = ds.sampleMean(1);

        final double expectedMean0 = 2.0;
        final double expectedMean1 = 5.0;
        assertEquals(expectedMean0, actualMean0, EPSILON);
        assertEquals(expectedMean1, actualMean1, EPSILON);
    }

    // sampleVariance TESTS

    /**
     * Tests sampleVariance on a single variable.
     *
     * Data: [2.0, 4.0, 6.0] Mean = (2+4+6)/3 = 12/3 = 4.0 Deviations: -2.0,
     * 0.0, 2.0 Squared deviations: 4.0, 0.0, 4.0 Sum of squares = 8.0 Sample
     * variance = 8.0 / (3 - 1) = 8.0 / 2 = 4.0
     */
    @Test
    public void testSampleVariance() {
        final double[][] data = { { 2.0, 4.0, 6.0 } };
        DatasetRV1L ds = this.createFromArgs(data);

        double actualVariance = ds.sampleVariance(0);

        final double expectedVariance = 4.0;
        assertEquals(expectedVariance, actualVariance, EPSILON);
    }

    /**
     * Tests sampleVariance on constant values.
     *
     * Data: [5.0, 5.0, 5.0] All values equal to mean, so variance = 0.0
     */
    @Test
    public void testSampleVarianceConstantValues() {
        final double[][] data = { { 5.0, 5.0, 5.0 } };
        DatasetRV1L ds = this.createFromArgs(data);

        double actualVariance = ds.sampleVariance(0);

        double expectedVariance = 0.0;
        assertEquals(expectedVariance, actualVariance, EPSILON);
    }

    //  sampleStandardDeviation TESTS

    /**
     * Tests sampleStandardDeviation on a single variable.
     *
     * From testSampleVariance, variance = 4.0 Standard deviation = sqrt(4.0) =
     * 2.0
     */
    @Test
    public void testSampleStandardDeviation() {
        final double[][] data = { { 2.0, 4.0, 6.0 } };
        DatasetRV1L ds = this.createFromArgs(data);

        double actualStdDev = ds.sampleStandardDeviation(0);

        double expectedStdDev = 2.0;
        assertEquals(expectedStdDev, actualStdDev, EPSILON);
    }

    //  frequencyOfObservations TESTS

    /**
     * Tests frequencyOfObservations with combine = false.
     *
     * Variable 0: [1.0, 2.0, 2.0, 3.0] Observations asked for: [1.0, 2.0, 3.0]
     * n = 4 Frequencies: 1/4 = 0.25, 2/4 = 0.5, 1/4 = 0.25
     */
    @Test
    public void testFrequencyOfObservationsSeparate() {
        final double three = 3.0;
        final double[][] data = { { 1.0, 2.0, 2.0, 3.0 } };
        DatasetRV1L ds = this.createFromArgs(data);

        Sequence<Double> observations = new Sequence1L<>();
        observations.add(observations.length(), 1.0);
        observations.add(observations.length(), 2.0);
        observations.add(observations.length(), three);

        boolean combine = false;
        Sequence<Double> actualFreq = ds.frequencyOfObservations(0,
                observations, combine);

        final double expectedFreq1 = 0.25;
        final double expectedFreq2 = 0.5;
        final double expectedFreq3 = 0.25;

        assertEquals(expectedFreq1, actualFreq.entry(0), EPSILON);
        assertEquals(expectedFreq2, actualFreq.entry(1), EPSILON);
        assertEquals(expectedFreq3, actualFreq.entry(2), EPSILON);
    }

    /**
     * Tests frequencyOfObservations with combine = true.
     *
     * Variable 0: [1.0, 2.0, 2.0, 3.0] Observations asked for: [1.0, 2.0, 3.0]
     * Combined probability = 0.25 + 0.5 + 0.25 = 1.0
     */
    @Test
    public void testFrequencyOfObservationsCombine() {
        final double three = 3.0;
        final double[][] data = { { 1.0, 2.0, 2.0, 3.0 } };
        DatasetRV1L ds = this.createFromArgs(data);

        Sequence<Double> observations = new Sequence1L<>();
        observations.add(observations.length(), 1.0);
        observations.add(observations.length(), 2.0);
        observations.add(observations.length(), three);

        boolean combine = true;
        Sequence<Double> actualFreq = ds.frequencyOfObservations(0,
                observations, combine);

        double expectedCombinedProbability = 1.0;

        assertEquals(1, actualFreq.length());
        assertEquals(expectedCombinedProbability, actualFreq.entry(0), EPSILON);
    }

    //  sampleMoments TESTS

    /**
     * Tests sampleMoments for first four moments.
     *
     * Data: [1.0, 2.0, 3.0, 4.0, 5.0] n = 5
     *
     * Moment 1 (Mean): (1+2+3+4+5)/5 = 15/5 = 3.0
     *
     * Moment 2 (Variance): Deviations: -2, -1, 0, 1, 2 Squared: 4, 1, 0, 1, 4
     * Sum = 10 Sample variance = 10 / (5-1) = 10/4 = 2.5
     *
     * Moment 3 (Skewness): C1 = n/((n-1)(n-2)) = 5/(4*3) = 5/12 ≈ 0.4167 Cubed
     * deviations: -8, -1, 0, 1, 8 Sum = 0 Skewness = 0.4167 * 0 = 0.0
     *
     * Moment 4 (Kurtosis): C2 = n(n+1)/((n-1)(n-2)(n-3)) = 5*6/(4*3*2) = 30/24
     * = 1.25 C3 = 3(n-1)²/((n-2)(n-3)) = 3*16/(3*2) = 48/6 = 8.0 Fourth power
     * deviations: 16, 1, 0, 1, 16 Sum = 34 Kurtosis = 1.25 * (34/34?) Let me
     * compute properly... (sum of (x-mean)^4 / n) = 34/5 = 6.8 Kurtosis = C2 *
     * (sum of (x-mean)^4 / n) - C3 = 1.25 * 6.8 - 8.0 = 8.5 - 8.0 = 0.5
     *
     * The sequence returned should be [mean, variance, skewness, kurtosis]
     */
    @Test
    public void testSampleMoments() {
        final double kurtosisTolerance = 0.01;
        final Double four = 4.0;
        final int three = 3;
        final double[][] data = { { 1.0, 2.0, 3.0, 4.0, 5.0 } };
        DatasetRV1L ds = this.createFromArgs(data);

        Sequence<Double> actualMoments = ds.sampleMoments(0);

        assertEquals(four, Double.valueOf(actualMoments.length()));

        final double expectedMean = 3.0;
        final double expectedVariance = 2.5;
        final double expectedSkewness = 0.0;
        final double expectedKurtosis = 0.5;

        assertEquals(expectedMean, actualMoments.entry(0), EPSILON);
        assertEquals(expectedVariance, actualMoments.entry(1), EPSILON);
        assertEquals(expectedSkewness, actualMoments.entry(2), EPSILON);
        assertEquals(expectedKurtosis, actualMoments.entry(three),
                kurtosisTolerance);
    }

    //  sampleCovariance TESTS

    /**
     * Tests sampleCovariance() (no parameters) on two variables.
     *
     * Variable 0: [1.0, 2.0, 3.0] Variable 1: [2.0, 4.0, 6.0] (Y = 2X)
     *
     * Variance X = 1.0 Variance Y = 4.0 Covariance = 2.0
     */
    @Test
    public void testSampleCovarianceAllVariables() {
        final double[][] data = { { 1.0, 2.0, 3.0 }, { 2.0, 4.0, 6.0 } };
        DatasetRV1L ds = this.createFromArgs(data);

        Sequence<Sequence<Double>> actualCov = ds.sampleCovariance();

        int expectedSize = 2;
        assertEquals(expectedSize, actualCov.length());
        assertEquals(expectedSize, actualCov.entry(0).length());

        final double expectedVar0 = 1.0;
        final double expectedVar1 = 4.0;
        final double expectedCov = 2.0;

        assertEquals(expectedVar0, actualCov.entry(0).entry(0), EPSILON);
        assertEquals(expectedVar1, actualCov.entry(1).entry(1), EPSILON);
        assertEquals(expectedCov, actualCov.entry(0).entry(1), EPSILON);
        assertEquals(expectedCov, actualCov.entry(1).entry(0), EPSILON);
    }

    /**
     * Tests sampleCovariance(Sequence<Integer>) on a subset of variables.
     *
     * Dataset has 3 variables: Variable 0: [1.0, 2.0, 3.0] Variable 1: [2.0,
     * 4.0, 6.0] Variable 2: [5.0, 5.0, 5.0]
     *
     * Request subset: variables 0 and 2 Expected matrix size: 2x2
     */
    @Test
    public void testSampleCovarianceSubset() {
        final double[][] data = { { 1.0, 2.0, 3.0 }, { 2.0, 4.0, 6.0 },
                { 5.0, 5.0, 5.0 } };
        DatasetRV1L ds = this.createFromArgs(data);

        Sequence<Integer> varSubset = this.varSeq(0, 2);
        Sequence<Sequence<Double>> actualCov = ds.sampleCovariance(varSubset);

        int expectedSize = 2;
        assertEquals(expectedSize, actualCov.length());
        assertEquals(expectedSize, actualCov.entry(0).length());

        final double expectedVar0 = 1.0;
        final double expectedVar2 = 0.0;
        final double expectedCov02 = 0.0;

        assertEquals(expectedVar0, actualCov.entry(0).entry(0), EPSILON);
        assertEquals(expectedVar2, actualCov.entry(1).entry(1), EPSILON);
        assertEquals(expectedCov02, actualCov.entry(0).entry(1), EPSILON);
        assertEquals(expectedCov02, actualCov.entry(1).entry(0), EPSILON);
    }

    //  sampleCorrelation TESTS

    /**
     * Tests sampleCorrelation() (no parameters) on two perfectly correlated
     * variables.
     *
     * From testSampleCovarianceAllVariables: covariance = 2.0 stdDevX =
     * sqrt(1.0) = 1.0 stdDevY = sqrt(4.0) = 2.0 correlation = 2.0 / (1.0 * 2.0)
     * = 1.0
     */
    @Test
    public void testSampleCorrelationAllVariables() {
        final double[][] data = { { 1.0, 2.0, 3.0 }, { 2.0, 4.0, 6.0 } };
        DatasetRV1L ds = this.createFromArgs(data);

        Sequence<Sequence<Double>> actualCorr = ds.sampleCorrelation();

        int expectedSize = 2;
        assertEquals(expectedSize, actualCorr.length());
        assertEquals(expectedSize, actualCorr.entry(0).length());

        double expectedPerfectCorrelation = 1.0;

        assertEquals(expectedPerfectCorrelation, actualCorr.entry(0).entry(1),
                EPSILON);
        assertEquals(expectedPerfectCorrelation, actualCorr.entry(1).entry(0),
                EPSILON);
        assertEquals(1.0, actualCorr.entry(0).entry(0), EPSILON);
        assertEquals(1.0, actualCorr.entry(1).entry(1), EPSILON);
    }

    /**
     * Tests sampleCorrelation(Sequence<Integer>) on a subset of variables.
     *
     * Dataset has 3 variables: Variable 0: [1.0, 2.0, 3.0] Variable 1: [2.0,
     * 4.0, 6.0] Variable 2: [5.0, 5.0, 5.0]
     *
     * Request subset: variables 0 and 2 Correlation between variable 0
     * (varying) and variable 2 (constant) should be 0 (division by zero? Handle
     * gracefully)
     */
    @Test
    public void testSampleCorrelationSubset() {
        final double[][] data = { { 1.0, 2.0, 3.0 }, { 2.0, 4.0, 6.0 },
                { 5.0, 5.0, 5.0 } };
        DatasetRV1L ds = this.createFromArgs(data);

        Sequence<Integer> varSubset = this.varSeq(0, 2);
        Sequence<Sequence<Double>> actualCorr = ds.sampleCorrelation(varSubset);

        int expectedSize = 2;
        assertEquals(expectedSize, actualCorr.length());
        assertEquals(expectedSize, actualCorr.entry(0).length());

        assertEquals(1.0, actualCorr.entry(0).entry(0), EPSILON);
        assertEquals(1.0, actualCorr.entry(1).entry(1), EPSILON);

        double actualCorr01 = actualCorr.entry(0).entry(1);
        if (!Double.isNaN(actualCorr01)) {
            assertEquals(0.0, actualCorr01, EPSILON);
        }
        double actualCorr10 = actualCorr.entry(1).entry(0);
        if (!Double.isNaN(actualCorr10)) {
            assertEquals(0.0, actualCorr10, EPSILON);
        }
    }
}