import components.datasetrv.DatasetRV;
import components.datasetrv.DatasetRV1L;
import components.sequence.Sequence;
import components.sequence.Sequence1L;

/**
 * Client class that uses DatasetRV for Normal distribution analysis.
 *
 * This class represents a multivariate Normal distribution. It can be
 * constructed either from sample observations (which estimates the parameters)
 * or directly from parameters (means, standard deviations, and covariance
 * matrix). The class provides methods for:
 * <ul>
 * <li>Generating random samples from the distribution</li>
 * <li>Computing probabilities within bounds using Monte Carlo simulation</li>
 * <li>Computing univariate probabilities using numerical integration</li>
 * </ul>
 */
public class NormalDistribution {

    /**
     * Sequence of DatasetRV objects. The first entry contains the original data
     * (if constructed from observations). Subsequent entries store generated
     * samples.
     */
    private Sequence<DatasetRV> datasetSequence;

    /**
     * Sequence of means for each random variable. Length equals the number of
     * variables in the distribution.
     */
    private Sequence<Double> meanSequence;

    /**
     * Sequence of standard deviations for each random variable. Length equals
     * the number of variables in the distribution.
     */
    private Sequence<Double> standardDeviationSequence;

    /**
     * Covariance matrix for the distribution. This is an n×n matrix where n =
     * number of variables.
     */
    private Sequence<Sequence<Double>> covarianceMatrix;

    /**
     * Constructs a Normal distribution from sample observations. Estimates the
     * population parameters (mean, variance, covariance) using maximum
     * likelihood estimation.
     *
     * @param sampleObservations
     *            a sequence of sequences where each inner sequence represents
     *            one random variable and all inner sequences must have the same
     *            length (number of observations)
     * @requires sampleObservations != null and sampleObservations.length() > 0
     * @ensures this distribution is initialized with estimated parameters
     */
    NormalDistribution(Sequence<Sequence<Double>> sampleObservations) {
        this.datasetSequence = new Sequence1L<DatasetRV>();
        DatasetRV initialObservations = new DatasetRV1L(sampleObservations);
        Sequence<Integer> varIndexSequence = new Sequence1L<>();
        for (int i = 0; i < sampleObservations.length(); i++) {
            varIndexSequence.add(varIndexSequence.length(), i);
        }
        this.datasetSequence.add(this.datasetSequence.length(),
                initialObservations);

        this.meanSequence = new Sequence1L<Double>();
        this.standardDeviationSequence = new Sequence1L<Double>();
        for (int i = 0; i < initialObservations
                .numberOfRandomVariables(); i++) {
            Double lengthObservations = (double) initialObservations
                    .numberOfObservations(i);
            this.meanSequence.add(this.meanSequence.length(),
                    initialObservations.sampleMean(i));
            Double sampleVariance = initialObservations.sampleVariance(i);
            Double populationVariance = sampleVariance
                    * ((lengthObservations - 1) / (lengthObservations));
            Double populationStandardDevation = Math.sqrt(populationVariance);
            this.standardDeviationSequence.add(
                    this.standardDeviationSequence.length(),
                    populationStandardDevation);

            this.covarianceMatrix = initialObservations.sampleCovariance();
        }

    }

    /**
     * Constructs a Normal distribution directly from parameters.
     *
     * @param rvMeans
     *            sequence of means for each random variable
     * @param rvStandardDeviations
     *            sequence of standard deviations for each random variable
     * @param rvCovMatrix
     *            covariance matrix for the distribution
     * @requires rvMeans != null, rvStandardDeviations != null, rvCovMatrix !=
     *           null, and rvMeans.length() == rvStandardDeviations.length() ==
     *           rvCovMatrix.length()
     * @ensures this distribution is initialized with the given parameters
     */
    NormalDistribution(Sequence<Double> rvMeans,
            Sequence<Double> rvStandardDeviations,
            Sequence<Sequence<Double>> rvCovMatrix) {
        if (rvMeans.length() > rvStandardDeviations.length()) {
            this.meanSequence = new Sequence1L<Double>();
            this.meanSequence.extract(0, rvMeans.length(), rvMeans);
            for (int i = 0; i < rvMeans.length()
                    - rvStandardDeviations.length(); i++) {
                rvStandardDeviations.add(rvStandardDeviations.length(),
                        (double) Integer.MAX_VALUE);
            }
        } else if (rvMeans.length() < rvStandardDeviations.length()) {
            this.standardDeviationSequence = new Sequence1L<Double>();
            this.standardDeviationSequence.extract(0,
                    rvStandardDeviations.length(), rvStandardDeviations);
            for (int i = 0; i < rvStandardDeviations.length()
                    - rvMeans.length(); i++) {
                rvMeans.add(rvMeans.length(), (double) Integer.MAX_VALUE);
            }
        } else {
            this.meanSequence = new Sequence1L<Double>();
            this.meanSequence.extract(0, rvMeans.length(), rvMeans);
            this.standardDeviationSequence = new Sequence1L<Double>();
            this.standardDeviationSequence.extract(0,
                    rvStandardDeviations.length(), rvStandardDeviations);
        }
        this.covarianceMatrix = rvCovMatrix;
    }

    /**
     * Returns the probability that a random value falls within the given bounds
     * for the first variable (row 0). Uses Monte Carlo simulation with
     * multivariate sampling.
     *
     * @param lowerBound
     *            the lower bound of the interval
     * @param upperBound
     *            the upper bound of the interval
     * @return estimated probability that X₀ is in [lowerBound, upperBound]
     * @requires lowerBound <= upperBound
     */
    public Double returnProbability(Double lowerBound, Double upperBound) {
        Double probability;
        double countInBounds = 0.0;
        final int numberSamples = 55000;
        Sequence<Double> sampleSequence = this
                .createNewSample(numberSamples, true).entry(0);
        for (int i = 0; i < sampleSequence.length(); i++) {
            if (sampleSequence.entry(i) >= lowerBound
                    && sampleSequence.entry(i) <= upperBound) {
                countInBounds++;
            }
        }
        probability = countInBounds / numberSamples;
        return probability;

    }

    /**
     * Returns the probability that the specified random variable falls within
     * the given bounds. Uses Monte Carlo simulation with multivariate sampling.
     *
     * @param lowerBound
     *            the lower bound of the interval
     * @param upperBound
     *            the upper bound of the interval
     * @param rowVar
     *            sequence containing the index of the variable to analyze
     *            (should contain exactly one index)
     * @return estimated probability that the specified variable is in
     *         [lowerBound, upperBound]
     * @requires lowerBound <= upperBound and rowVar.length() == 1
     */
    public Double returnProbability(Double lowerBound, Double upperBound,
            Sequence<Integer> rowVar) {
        Double probability;
        double countInBounds = 0.0;
        final int numberSamples = 55000;
        Sequence<Double> sampleSequence = this
                .createNewSample(numberSamples, rowVar, true).entry(0);
        for (int i = 0; i < sampleSequence.length(); i++) {
            if (sampleSequence.entry(i) >= lowerBound
                    && sampleSequence.entry(i) <= upperBound) {
                countInBounds++;
            }
        }
        probability = countInBounds / numberSamples;
        return probability;

    }

    /**
     * Returns the probability that the specified random variable falls within
     * the given bounds. Uses numerical integration of the standard Normal PDF
     * after converting the bounds to Z-scores.
     *
     * @param lowerBound
     *            the lower bound of the interval
     * @param upperBound
     *            the upper bound of the interval
     * @param rowVar
     *            the index of the variable to analyze
     * @return probability that the specified variable is in [lowerBound,
     *         upperBound]
     * @requires lowerBound <= upperBound and 0 <= rowVar < number of variables
     */
    public Double returnProbability(Double lowerBound, Double upperBound,
            int rowVar) {
        final Double stepCount = 100.0;
        Double zValUpper = (upperBound - this.meanSequence.entry(rowVar))
                / this.standardDeviationSequence.entry(rowVar);
        Double zValLower = (lowerBound - this.meanSequence.entry(rowVar))
                / this.standardDeviationSequence.entry(rowVar);
        Double heightGap = (zValUpper - zValLower) / stepCount;
        Double probability = trapezoidalIntegralEstimation(zValLower, zValUpper,
                stepCount, heightGap);
        return probability;

    }

    /**
     * Estimates the definite integral of the standard Normal PDF over the
     * interval [startBound, endBound] using the trapezoidal rule recursively.
     *
     * @param startBound
     *            the lower bound of integration
     * @param endBound
     *            the upper bound of integration
     * @param numberSteps
     *            the number of trapezoids to use (higher = more accurate)
     * @param height
     *            the width of each trapezoid
     * @return the estimated area under the standard Normal PDF
     * @requires startBound <= endBound and numberSteps >= 0 and height > 0
     */
    private static Double trapezoidalIntegralEstimation(Double startBound,
            Double endBound, Double numberSteps, Double height) {
        Double trapArea;
        final double half = 0.5;
        if (numberSteps == 0.0) {
            return 0.0;
        }

        trapArea = height * half * (returnStandardNormalPdf(startBound)
                + returnStandardNormalPdf(startBound + height));

        System.out
                .print("Area: " + trapArea + " Step #: " + numberSteps + "\n");

        trapArea += trapezoidalIntegralEstimation(startBound + height, endBound,
                numberSteps - 1, height);

        return trapArea;
    }

    /**
     * Returns the probability density function (PDF) value of the standard
     * Normal distribution (mean = 0, variance = 1) at the given point.
     *
     * @param xVal
     *            the point at which to evaluate the PDF
     * @return the PDF value f(x) = (1/√(2π)) * e^(-x²/2)
     */
    private static Double returnStandardNormalPdf(Double xVal) {
        Double normalizedMean = 0.0;
        Double normalizedStdv = 1.0;
        Double frac = 1.0 / (normalizedStdv * Math.sqrt(2 * Math.PI));
        final Double power = -0.5
                * Math.pow((xVal - normalizedMean) / normalizedStdv, 2);
        Double densityVal = frac * Math.pow(Math.E, power);
        return densityVal;
    }

    /**
     * Generates independent standard Normal random variables using the
     * Box-Muller transform. The number of samples generated is either equal to
     * paramNumberOFSamples (if even) or paramNumberOFSamples + 1 (if odd).
     *
     * @param paramNumberOFSamples
     *            the desired number of samples
     * @return a sequence of independent N(0,1) random variables
     */
    private static Sequence<Double> boxMuler(int paramNumberOFSamples) {
        Sequence<Double> boxMulerSamples = new Sequence1L<>();
        final double negativeTwoCoeff = -2.0;
        int numberOfSamples = paramNumberOFSamples;
        if (numberOfSamples % 2 != 0) {
            numberOfSamples += 1;
        }
        while (numberOfSamples > 0) {
            double u1 = Math.random();
            double u2 = Math.random();
            double r = Math.sqrt(negativeTwoCoeff * Math.log(u1));
            double theta = 2 * Math.PI * u2;
            double z1 = r * Math.cos(theta);
            double z2 = r * Math.sin(theta);
            boxMulerSamples.add(boxMulerSamples.length(), z1);
            boxMulerSamples.add(boxMulerSamples.length(), z2);
            numberOfSamples -= 2;
        }
        return boxMulerSamples;
    }

    /**
     * Creates new random samples from the distribution for a subset of
     * variables specified by rowSequence.
     *
     * @param sampleAmount
     *            the number of samples to generate
     * @param rowSequence
     *            sequence of variable indices to include in the samples
     * @param multiVariable
     *            if true, generates multivariate correlated samples; if false,
     *            generates independent univariate samples
     * @return a matrix where each row is a sample (if multiVariable = true) or
     *         each row is a sequence of samples for one variable (if
     *         multiVariable = false)
     * @requires sampleAmount > 0 and rowSequence != null
     */
    public Sequence<Sequence<Double>> createNewSample(int sampleAmount,
            Sequence<Integer> rowSequence, boolean multiVariable) {
        Sequence<Sequence<Double>> sampleMatrix = new Sequence1L<>();
        int numberOfRandomVariables = rowSequence.length();
        Sequence<Sequence<Double>> sampleCovarianceMatrix = this.datasetSequence
                .entry(0).sampleCovariance(rowSequence);
        if (multiVariable) {
            Sequence<Double> boxMulerSamples = boxMuler(
                    numberOfRandomVariables);
            Sequence<Sequence<Double>> choleskyMatrix = new Sequence1L<>();
            Sequence<Double> multivariateSample = new Sequence1L<>();
            while (multivariateSample.length() < sampleAmount) {
                for (int i = 0; i < numberOfRandomVariables; i++) {
                    Sequence<Double> choleskyRow = new Sequence1L<>();
                    for (int j = 0; j < numberOfRandomVariables; j++) {
                        Double lConstant;
                        if (i == j) {
                            lConstant = sampleCovarianceMatrix.entry(i)
                                    .entry(i);
                            for (int k = 0; k < i - 1; k++) {
                                lConstant -= (choleskyRow.entry(k)
                                        * choleskyRow.entry(k));
                            }
                            lConstant = Math.sqrt(lConstant);
                        } else if (i > j) {
                            lConstant = sampleCovarianceMatrix.entry(i)
                                    .entry(j);
                            Double choleskyInnerMultiplication = 0.0;
                            for (int k = 0; k < j - 1; k++) {
                                choleskyInnerMultiplication += choleskyRow
                                        .entry(k)
                                        * choleskyMatrix.entry(j).entry(k);
                            }
                            lConstant -= choleskyInnerMultiplication;
                            lConstant = lConstant
                                    / choleskyMatrix.entry(j).entry(j);
                        } else {
                            lConstant = 0.0;
                        }
                        choleskyRow.add(choleskyRow.length(), lConstant);
                    }
                    choleskyMatrix.add(choleskyMatrix.length(), choleskyRow);
                }
                for (int i = 0; i < numberOfRandomVariables; i++) {
                    Double sample = 0.0;
                    Sequence<Double> choleskyRow = choleskyMatrix.entry(i);
                    for (int j = 0; i < choleskyRow.length(); j++) {
                        if (i + 1 < choleskyRow.length()) {
                            if (choleskyRow.entry(j) == 0
                                    && choleskyRow.entry(j + 1) == 0) {
                                break;
                            }
                        } else {
                            sample += (choleskyRow.entry(j)
                                    * boxMulerSamples.entry(j));
                        }
                    }
                    sample += this.meanSequence.entry(i);
                    multivariateSample.add(multivariateSample.length(), sample);
                }
            }
            sampleMatrix.add(sampleMatrix.length(), multivariateSample);
            while (multivariateSample.length() != sampleAmount) {
                multivariateSample.remove(multivariateSample.length());
            }
        } else {
            for (int i = 0; i < numberOfRandomVariables; i++) {
                Sequence<Double> sampleSequence = new Sequence1L<>();
                for (int j = 0; j < sampleAmount; j++) {
                    Sequence<Double> boxMulerSample = boxMuler(1);
                    Double sample = this.meanSequence.entry(i)
                            + (this.standardDeviationSequence.entry(i)
                                    * boxMulerSample.entry(0));
                    sampleSequence.add(sampleSequence.length(), sample);
                }
                sampleMatrix.add(sampleMatrix.length(), sampleSequence);
            }
        }
        DatasetRV newSample = new DatasetRV1L(sampleMatrix);
        this.datasetSequence.add(this.datasetSequence.length(), newSample);
        return sampleMatrix;

    }

    /**
     * Creates new random samples from the distribution for all variables.
     *
     * @param sampleAmount
     *            the number of samples to generate
     * @param multiVariable
     *            if true, generates multivariate correlated samples; if false,
     *            generates independent univariate samples
     * @return a matrix where each row is a sample (if multiVariable = true) or
     *         each row is a sequence of samples for one variable (if
     *         multiVariable = false)
     * @requires sampleAmount > 0
     */
    public Sequence<Sequence<Double>> createNewSample(int sampleAmount,
            boolean multiVariable) {
        Sequence<Sequence<Double>> sampleMatrix = new Sequence1L<>();
        int numberOfRandomVariables = this.datasetSequence.entry(0)
                .numberOfRandomVariables();
        if (multiVariable) {
            Sequence<Double> boxMulerSamples = boxMuler(
                    numberOfRandomVariables);
            Sequence<Sequence<Double>> choleskyMatrix = new Sequence1L<>();
            Sequence<Double> multivariateSample = new Sequence1L<>();
            while (multivariateSample.length() < sampleAmount) {
                for (int i = 0; i < numberOfRandomVariables; i++) {
                    Sequence<Double> choleskyRow = new Sequence1L<>();
                    for (int j = 0; j < numberOfRandomVariables; j++) {
                        Double lConstant;
                        if (i == j) {
                            lConstant = this.covarianceMatrix.entry(i).entry(i);
                            for (int k = 0; k < i - 1; k++) {
                                lConstant -= (choleskyRow.entry(k)
                                        * choleskyRow.entry(k));
                            }
                            lConstant = Math.sqrt(lConstant);
                        } else if (i > j) {
                            lConstant = this.covarianceMatrix.entry(i).entry(j);
                            Double choleskyInnerMultiplication = 0.0;
                            for (int k = 0; k < j - 1; k++) {
                                choleskyInnerMultiplication += choleskyRow
                                        .entry(k)
                                        * choleskyMatrix.entry(j).entry(k);
                            }
                            lConstant -= choleskyInnerMultiplication;
                            lConstant = lConstant
                                    / choleskyMatrix.entry(j).entry(j);
                        } else {
                            lConstant = 0.0;
                        }
                        choleskyRow.add(choleskyRow.length(), lConstant);
                    }
                    choleskyMatrix.add(choleskyMatrix.length(), choleskyRow);
                }
                for (int i = 0; i < numberOfRandomVariables; i++) {
                    Double sample = 0.0;
                    Sequence<Double> choleskyRow = choleskyMatrix.entry(i);
                    for (int j = 0; i < choleskyRow.length(); j++) {
                        if (i + 1 < choleskyRow.length()) {
                            if (choleskyRow.entry(j) == 0
                                    && choleskyRow.entry(j + 1) == 0) {
                                break;
                            }
                        } else {
                            sample += (choleskyRow.entry(j)
                                    * boxMulerSamples.entry(j));
                        }
                    }
                    sample += this.meanSequence.entry(i);
                    multivariateSample.add(multivariateSample.length(), sample);
                }

            }
            while (multivariateSample.length() != sampleAmount) {
                multivariateSample.remove(multivariateSample.length());
            }
            sampleMatrix.add(sampleMatrix.length(), multivariateSample);
        } else {
            for (int i = 0; i < numberOfRandomVariables; i++) {
                Sequence<Double> sampleSequence = new Sequence1L<>();
                for (int j = 0; j < sampleAmount; j++) {
                    Sequence<Double> boxMulerSample = boxMuler(1);
                    Double sample = this.meanSequence.entry(i)
                            + (this.standardDeviationSequence.entry(i)
                                    * boxMulerSample.entry(0));
                    sampleSequence.add(sampleSequence.length(), sample);
                }
                sampleMatrix.add(sampleMatrix.length(), sampleSequence);
            }
        }
        DatasetRV newSample = new DatasetRV1L(sampleMatrix);
        this.datasetSequence.add(this.datasetSequence.length(), newSample);
        return sampleMatrix;
    }

}