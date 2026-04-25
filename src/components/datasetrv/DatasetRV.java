package components.datasetrv;

import components.sequence.Sequence;

/**
 * {@code DatasetRVKernel} enhanced with secondary methods.
 */
public interface DatasetRV extends DatasetRVKernel {

    /**
     * Finds the sample mean of all the observations in row rowVar.
     *
     * {@code mean = (Σ(x))/n ∀ x ∈ row rowVar}
     *
     * where n = length(this.entry(rowVar))
     *
     * @param rowVar
     *            the row r containing the sequence of observations x for some
     *            random variable X
     *
     * @return the sample mean of the observations of the random variable at row
     *         rowVar
     * @updates this
     */
    double sampleMean(int rowVar);

    /**
     * Finds the sample standard deviation of all the observations in X
     * (this.entry(rowVar)).
     *
     * {@code standardDeviation = √{ Σ(x-mean)²/n }
     *  ∀ x ∈ row rowVar, mean = sampleMean(X)}
     *
     * where n = length(this.entry(rowVar)) and X = this.entry(rowVar).
     *
     * @param rowVar
     *            the row r containing the sequence of observations x for some
     *            random variable X
     *
     * @return the sample standard deviation of the observations of the random
     *         variable at row rowVar
     */
    double sampleStandardDeviation(int rowVar);

    /**
     * Finds the sample variance of all the observations in row rowVar.
     *
     * {@code Variance = (StandardDeviation)²
     * ∀ x ∈ row rowVar
     * ,StandardDeviation = sampleStandardDeviation(X)}
     *
     * where X = this.entry(rowVar).
     *
     * @param rowVar
     *            the row r containing the sequence of observations x for some
     *            random variable X
     *
     * @return the sample standard deviation of the observations of the random
     *         variable at row rowVar
     */
    double sampleVariance(int rowVar);

    /**
     * Finds the frequency of observations of X (this.entry(rowVar)) together or
     * individually (combine probability or separate probability)
     *
     * {@code Probability = Σ( #x/n)  )
     * or Probability =
     * <#x_1/length(this.entry(rowVar)),...#x_n/n)>
     *  ∀ x ∈ observations   }
     *
     * where n = length(this.entry(rowVar)) and X = this.entry(rowVar) .
     *
     * @param rowVar
     *            the row r containing the sequence of observations x for some
     *            random variable X
     * @param observations
     *            sequence of observations of the random variable in row rowVar
     * @param combine
     *            boolean dictating whether probability of observations are
     *            combine or separate
     *
     * @return the sample mean of the observations of the random variable at row
     *         rowVar
     */
    Sequence<Double> frequencyOfObservations(int rowVar,
            Sequence<Double> observations, boolean combine);

    /**
     * Finds the first four moments of the random variable of some random
     * variable X (this.entry(rowVar))
     *
     * Moment 1: Mu = sampleMean(X)
     *
     * Moment 2: Variance = sampleVariance(X)
     *
     * Moment 3: Skew {@code  Skew  =  C1* √{ Σ(x-mean)³ /n }
     *  ∀ x ∈ X, mean = sampleMean(X)}
     *
     * Moment 4: Kurtosis {@code  Kurtosis  = C2 * √{ Σ(x-mean)⁴  /n }
     *  ∀ x ∈ X, mean = sampleMean(X)}
     *
     * where n = length(this.entry(rowVar)), X = this.entry(rowVar)
     *
     * C1 = n/((n-1)(n-2)) and C2 = n(n+1)/((n-1)(n-2)(n-3)).
     *
     * @param rowVar
     *            the row r containing the sequence of observations x for some
     *            random variable X
     *
     * @return a sequence of the first four moments
     */
    Sequence<Double> sampleMoments(int rowVar);

    /**
     * Finds the covariance of all the random variables in this
     *
     * {@code Cov(this)
     * = = [Var(X1),    Cov(X1,X2)....Cov(X1,Xn)]
     *                       [Cov(X2,X1), Var(X2)   ....Cov(X2,Xn)]
     *                       [             ...                    ]
     *                       [Var(Xn,X1), Cov(Xn,X2)....Var(Xn)   ]
     *
     * where Cov(Xa,Xb) = Σ( (a-mean(Xa))(b-mean(Xb)) )*(1/n-1)
     * for n = length(this),
     *  where Xa and Xb are two random variables in this
     * } .
     *
     *
     * @return the covariance matrix
     */
    Sequence<Sequence<Double>> sampleCovariance();

    /**
     * Finds the covariance of the given random variables rowVar
     *
     * {@code Cov(Seq<T> vars)
     * = Cov(X1, X2,...Xn) = [Var(X1),    Cov(X1,X2)....Cov(X1,Xn)]
     *                       [Cov(X2,X1), Var(X2)   ....Cov(X2,Xn)]
     *                       [             ...                    ]
     *                       [Var(Xn,X1), Cov(Xn,X2)....Var(Xn)   ]
     *
     * where Cov(Xa,Xb) = Σ( (a-mean(Xa))(b-mean(Xb)) )*(1/n-1)
     * for n = length(this.entry(rowVar),
     * where Xa and Xb are two random variables in rowVar (<X1, X2,...Xn)
     *
     * }.
     *
     * @param varSequence
     *            the row r containing the sequence of random variables
     *            <X1,X2,...Xn>
     *
     * @return the covariance matrix
     */
    Sequence<Sequence<Double>> sampleCovariance(Sequence<Integer> varSequence);

    /**
     * Finds the corelation coefficient between random variables rowVar
     *
     * {@code Cor(X1,X2,...Xn) = Cov(X1,X2,Xn)/SE
     * where for two rvs Xa and Xb ∈ rowVar,
     * SE = sampleStandardDeviation(Xa)*sampleStandardDeviation(Xb)
     * }.
     *
     * @param varSequence
     *            the row r containing the sequence of random variables
     *            <X1,X2,...Xn>
     *
     * @return the correlation coefficient matrix
     */
    Sequence<Sequence<Double>> sampleCorrelation(Sequence<Integer> varSequence);

    /**
     * Finds the corelation coefficient of all random variables in this
     *
     * {@code Cor(X1,X2,...Xn) = Cov(X1,X2,Xn)/SE
     * where for two rvs Xa and Xb ∈ this,
     * SE = sampleStandardDeviation(Xa)*sampleStandardDeviation(Xb)
     * }.
     *
     *
     * @return the correlation coefficient matrix
     */
    Sequence<Sequence<Double>> sampleCorrelation();

}
