package components.datasetrv;

import components.sequence.Sequence;
import components.sequence.Sequence1L;

/**
 * Layered implementations of secondary methods for {@code DatasetRV}.
 */
public abstract class DatasetRVSecondary implements DatasetRV {

    /*
     * Public members ---------------------------------------------------------
     */

    /*
     * Common methods (from Object) -------------------------------------------
     */

    @Override
    public final double sampleMean(int rowVar) {
        double total = 0.0;
        //Summing all the observations of the RV at rowVar
        final int lengthObservations = this.numberOfObservations(rowVar);
        for (int i = 0; i < lengthObservations; i++) {
            total += this.getElement(rowVar, i);
        }
        // dividing the total sum by the total number of observations
        double mean = total / lengthObservations;
        return mean;
    }

    @Override
    public final double sampleStandardDeviation(int rowVar) {
        double totalDifference = 0.0;
        final double mean = this.sampleMean(rowVar);
        //summing all the individual differences (x_i-Mu)
        //where x_i is each individual observation and Mu is the sample mean
        final int lengthObservations = this.numberOfObservations(rowVar);
        for (int i = 0; i < lengthObservations; i++) {
            double individualDifferenceSquared = (this.getElement(rowVar, i)
                    - mean) * (this.getElement(rowVar, i) - mean);
            totalDifference += individualDifferenceSquared;
        }
        //divide the sum by total number of observations n
        double variance = totalDifference / lengthObservations;
        double standardDeviation = Math.sqrt(variance);
        return standardDeviation;
    }

    @Override
    public final double sampleVariance(int rowVar) {
        double standardDeviation = this.sampleStandardDeviation(rowVar);
        //variance = (sampleStandardDeviation)^2
        double variance = standardDeviation * standardDeviation;
        return variance;
    }

    @Override
    public final Sequence<Double> frequencyOfObservations(int rowVar,
            Sequence<Double> observations, boolean combine) {
        Sequence<Double> probabilities = new Sequence1L<>();
        final int numberObservations = this.numberOfObservations(rowVar);
        for (Double observation : observations) {
            // finding #x_i, the number of times a observation appears in the
            //sample
            int observationCount = 0;
            for (int i = 0; i < numberObservations; i++) {
                if (observation.equals(this.getElement(rowVar, i))) {
                    observationCount += 1;
                }
            }
            // divides each individual frequency by n,
            // the total number of observations, sample size
            double frequency = (double) (observationCount / numberObservations);
            probabilities.add(probabilities.length() - 1,
                    Double.valueOf(frequency));

        }
        // if combine == true, it will add all the frequencies
        if (combine) {
            double totalProbability = 0;
            for (Double probability : probabilities) {
                totalProbability += probability;
            }
            probabilities.clear();
            probabilities.add(probabilities.length(), totalProbability);
        }
        //either returns <p1,p2...pk>,
        // the sequence of frequencies for all k observations
        // or <pt> where pt = p1 + p2 + ...pk for all k observations
        return probabilities;
    }

    @Override
    public final Sequence<Double> sampleMoments(int rowVar) {
        final Sequence<Double> momentsRV = new Sequence1L<>();
        //defines coefficients
        final double cubed = 3.0;
        final double scaleThree = 3.0;
        final double fourthPower = 4.0;
        //first moment
        final double meanMoment = this.sampleMean(rowVar);
        //second moment
        final double varianceMoment = this.sampleVariance(rowVar);
        final double standardDeviation = this.sampleStandardDeviation(rowVar);
        //third moment
        double skewMoment = 0;
        //fourth moment
        double kurtosisMoment = 0;
        //following mathematical formula
        final int lengthObservations = this.numberOfObservations(rowVar);
        for (int i = 0; i < lengthObservations; i++) {
            double individualDifference = (this.getElement(rowVar, i)
                    - meanMoment);
            individualDifference = individualDifference / standardDeviation;
            double individualDifferenceCubed = Math.pow(individualDifference,
                    cubed);
            double individualDifferenceFourth = Math.pow(individualDifference,
                    fourthPower);
            skewMoment += individualDifferenceCubed;
            kurtosisMoment += individualDifferenceFourth;
        }

        double skewFrontCoefficient = (lengthObservations)
                / ((lengthObservations - 1) * (lengthObservations - 2));
        double kurtosisFrontCoefficient = (lengthObservations
                * (lengthObservations + 1))
                / ((lengthObservations - 1) * (lengthObservations - 2)
                        * (lengthObservations - scaleThree));
        double kurtosisEndCoefficient = (scaleThree * (lengthObservations - 1)
                * (lengthObservations - 1))
                / ((lengthObservations - 2)
                        * (lengthObservations - scaleThree));
        skewMoment *= skewFrontCoefficient;
        kurtosisMoment *= kurtosisFrontCoefficient;
        kurtosisMoment -= kurtosisEndCoefficient;
        //adding all moments and returning a sequence of the first four moments
        momentsRV.add(momentsRV.length(), meanMoment);
        momentsRV.add(momentsRV.length(), varianceMoment);
        momentsRV.add(momentsRV.length(), skewMoment);
        momentsRV.add(momentsRV.length(), kurtosisMoment);
        return momentsRV;
    }

    @Override
    public final Sequence<Sequence<Double>> sampleCovariance() {
        Sequence<Sequence<Double>> covariance = new Sequence1L<>();
        final int numberRandomVariables = this.numberOfRandomVariables();
        for (int varXIndex = 0; varXIndex < numberRandomVariables; varXIndex++) {
            Sequence<Double> covCombinations = new Sequence1L<>();
            double meanX = this.sampleMean(varXIndex);
            for (int varYIndex = 0; varYIndex < numberRandomVariables; varYIndex++) {
                double meanY = this.sampleMean(varYIndex);
                if (varXIndex == varYIndex) {
                    //diagonal of cov matrix
                    covCombinations.add(covCombinations.length(),
                            this.sampleVariance(varXIndex));
                } else {
                    //finds the cov of each pair of random variables
                    int varXObservationLength = this
                            .numberOfObservations(varXIndex);
                    int varYObservationLength = this
                            .numberOfObservations(varYIndex);
                    int sampleSize = 0;
                    if (varXObservationLength > varYObservationLength) {
                        sampleSize = varYObservationLength;
                    } else {
                        sampleSize = varXObservationLength;
                    }
                    double covarianceXY = 0;
                    for (int k = 0; k < sampleSize; k++) {
                        double xObservation = this.getElement(varXIndex, k);
                        double yObservation = this.getElement(varYIndex, k);
                        double individualMultipliedDifference = (xObservation
                                - meanX) * (yObservation - meanY);
                        covarianceXY += individualMultipliedDifference;
                    }
                    covarianceXY *= (1 / (sampleSize - 1));
                    covCombinations.add(covCombinations.length(), covarianceXY);
                }
            }
            //adds each random variable covariance row into the matrix
            covariance.add(covariance.length(), covCombinations);
        }
        return covariance;
    }

    @Override
    public final Sequence<Sequence<Double>> sampleCovariance(
            Sequence<Integer> varSequence) {
        Sequence<Sequence<Double>> covariance = new Sequence1L<>();
        final int numberRandomVariables = varSequence.length();
        for (int i = 0; i < numberRandomVariables; i++) {
            Sequence<Double> covCombinations = new Sequence1L<>();
            int varXIndex = varSequence.entry(i).intValue();
            double meanX = this.sampleMean(varXIndex);
            for (int j = 0; j < numberRandomVariables; j++) {
                int varYIndex = varSequence.entry(j).intValue();
                double meanY = this.sampleMean(varYIndex);
                if (varXIndex == varYIndex) {
                    //diagonal of cov matrix
                    covCombinations.add(covCombinations.length(),
                            this.sampleVariance(varXIndex));
                } else {
                    //finds the cov of each pair of random variables
                    int varXObservationLength = this
                            .numberOfObservations(varXIndex);
                    int varYObservationLength = this
                            .numberOfObservations(varYIndex);
                    int sampleSize = 0;
                    if (varXObservationLength > varYObservationLength) {
                        sampleSize = varYObservationLength;
                    } else {
                        sampleSize = varXObservationLength;
                    }
                    double covarianceXY = 0;
                    for (int k = 0; k < sampleSize; k++) {
                        double xObservation = this.getElement(varXIndex, k);
                        double yObservation = this.getElement(varYIndex, k);
                        double individualMultipliedDifference = (xObservation
                                - meanX) * (yObservation - meanY);
                        covarianceXY += individualMultipliedDifference;
                    }
                    covarianceXY *= (1 / (sampleSize - 1));
                    covCombinations.add(covCombinations.length(), covarianceXY);
                }
            }
            //adds each random variable covariance row into the matrix
            covariance.add(covariance.length(), covCombinations);
        }
        return covariance;
    }

    @Override
    public final Sequence<Sequence<Double>> sampleCorrelation() {
        Sequence<Sequence<Double>> correlationCoefficient = new Sequence1L<>();
        Sequence<Sequence<Double>> covariance = this.sampleCovariance();
        // finds the correlation coefficient for each covariance in the matrix
        for (int varXIndex = 0; varXIndex < covariance.length(); varXIndex++) {
            Sequence<Double> correlationCombinations = new Sequence1L<>();
            Sequence<Double> covarianceXSequence = covariance.entry(varXIndex);
            double xStandardDeviation = this.sampleStandardDeviation(varXIndex);
            for (int varYIndex = 0; varYIndex < covariance
                    .length(); varYIndex++) {
                double yStandardDeviation = this
                        .sampleStandardDeviation(varYIndex);
                double covarianceXY = covarianceXSequence.entry(varYIndex);
                Double correlationXY = covarianceXY
                        / (xStandardDeviation * yStandardDeviation);
                correlationCombinations.add(correlationCombinations.length(),
                        correlationXY);
            }
            correlationCoefficient.add(correlationCoefficient.length(),
                    correlationCombinations);
        }
        return correlationCoefficient;
    }

    @Override
    public final Sequence<Sequence<Double>> sampleCorrelation(
            Sequence<Integer> varSequence) {
        Sequence<Sequence<Double>> correlationCoefficient = new Sequence1L<>();
        Sequence<Sequence<Double>> covariance = this.sampleCovariance();
        // finds the correlation coefficient for each covariance in the matrix
        for (int i = 0; i < covariance.length(); i++) {
            Sequence<Double> correlationCombinations = new Sequence1L<>();
            Sequence<Double> covarianceXSequence = covariance.entry(i);
            int varXIndex = varSequence.entry(i).intValue();
            double xStandardDeviation = this.sampleStandardDeviation(varXIndex);
            for (int j = 0; j < covariance.length(); j++) {
                int varYIndex = varSequence.entry(j).intValue();
                double yStandardDeviation = this
                        .sampleStandardDeviation(varYIndex);
                double covarianceXY = covarianceXSequence.entry(j);
                Double correlationXY = covarianceXY
                        / (xStandardDeviation * yStandardDeviation);
                correlationCombinations.add(correlationCombinations.length(),
                        correlationXY);
            }
            correlationCoefficient.add(correlationCoefficient.length(),
                    correlationCombinations);
        }
        return correlationCoefficient;
    }

}
