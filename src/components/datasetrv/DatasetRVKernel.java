package components.datasetrv;

import components.sequence.Sequence;
import components.sequence.Sequence1L;
import components.standard.Standard;

/**
 * Dataset random variable kernel component with primary methods.
 *
 * @mathsubtypes <pre>
 * DATASET is finite sequence of finite sequence of real
 *  exemplar d
 *  constraint true
 * </pre>
 * @mathmodel type DatasetRVKernel is modeled by DATASET
 * @initially <pre>
 * ():
 *  ensures
 *   this = empty_sequence
 * (Sequence<Sequence<Double>> data):
 *  ensures
 *   this = data
 * </pre>
 */
public interface DatasetRVKernel extends Standard<DatasetRV> {

    /**
     * Dataset representation through Sequence<Sequence<Double>>: A matrix form
     * of R X C where each r ∈ R is the random variable observation row and each
     * c ∈ C is the index of each observation {@code x ∈ R ∀ x ∈ this }.
     */
    Sequence<Sequence<Double>> VARIABLE_OBSERVATIONS = new Sequence1L<>();

    /**
     * Either adds a new observation x at some position col in an existing row
     * rowVar or creates a new row r and adds the observation x at c=0 (1st
     * element).
     *
     * @param rowVar
     *            the row containing the sequence of observations x for some
     *            random variable
     * @param col
     *            the column containing the index of the observation
     * @param observation
     *            the observation of the random variable
     *
     * @updates this
     * @ensures this = #this [RXC] + x_i(r,c)
     */
    void addElement(int rowVar, int col, Double observation);

    /**
     * Deletes the observation x at some position col in row rowVar.
     *
     * @param rowVar
     *            the row containing the sequence of observations x for some
     *            random variable
     * @param col
     *            the column containing the index of the observation
     *
     * @updates this
     * @ensures this = #this[RXC] - x_i(r,c)
     * @requires x_i(r,c) ∈ this [RXC]
     */
    void removeElement(int rowVar, int col);

    /**
     * Retrieves the observation x at some position col in row rowVar.
     *
     * @param rowVar
     *            the row containing the sequence of observations x for some
     *            random variable
     * @param col
     *            the column containing the index of the observation
     *
     * @return the observation x at entry (towVar, col) ∈ this
     * @ensures this = #this[RXC]
     * @requires x_i(r,c) ∈ this [RXC]
     */
    double getElement(int rowVar, int col);

    /**
     * Returns the number of observations for some random variable at row
     * rowVar.
     *
     * @param rowVar
     *            the row r containing the sequence of observations x for some
     *            random variable
     *
     * @return the number of observations x ∈ row rowVar
     * @ensures this = #this[RXC]
     * @requires X(Row at rowVar) ∈ this [RXC]
     */
    int numberOfObservations(int rowVar);

    /**
     * Returns the number of random variables in {@code this} represented by the
     * {@code length(this)}.
     *
     * @return the number of random variables X ∈ this
     * @ensures this = #this[RXC]
     */
    int numberOfRandomVariables();

}
