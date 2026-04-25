package components.datasetrv;

import components.sequence.Sequence;
import components.sequence.Sequence1L;

/**
 * {@code DatasetRV} represented as a {@code Sequence<Sequence<Double>>} with
 * implementations of primary methods.
 *
 * @convention $this.rep != null AND for all i, j where 0 <= i, j < |$this.rep|,
 *             |$this.rep[i]| == |$this.rep[j]|
 * @correspondence this = < <$this.rep[0][0], $this.rep[0][1], ...>,
 *                 <$this.rep[1][0], $this.rep[1][1], ...>, ... >
 */
public class DatasetRV1L extends DatasetRVSecondary implements DatasetRVKernel {
    /*
     * * Private members
     * --------------------------------------------------------
     */
    /**
     * * Representation of {@code this}.
     */
    private Sequence<Sequence<Double>> dataObservations;

    /**
     * * Creator of initial representation.
     *
     */

    private void createNewRep() {
        this.dataObservations = new Sequence1L<>();
    }

    /*
     * * Constructors
     * -----------------------------------------------------------
     *
     */

    /**
     * No-argument constructor.
     */
    public DatasetRV1L() {
        this.createNewRep();
    }

    /**
     * Constructor from {@code Sequence<Sequence<Double>>}.
     *
     * @param matrixObservations
     *            {@code <Sequence<Sequence<Double>>} to initialize from
     */
    public DatasetRV1L(Sequence<Sequence<Double>> matrixObservations) {
        assert matrixObservations
                .length() > 0 : "Violation of: No random variables";
        assert matrixObservations.entry(0)
                .length() > 0 : "Violation of: No observations present";
        this.createNewRep();
        this.dataObservations = matrixObservations;
    }

    /**
     * Constructor from {@code Sequence<Double>}.
     *
     * @param arrayObservations
     *            {@code Double[] } to initialize from
     */
    public DatasetRV1L(Double[] arrayObservations) {
        assert arrayObservations.length > 0 : "Violation of: No random variables";
        this.createNewRep();
        Sequence<Double> firstRV = this.dataObservations.entry(0);
        for (Double d : arrayObservations) {
            firstRV.add(firstRV.length(), d);
        }
    }

    /*
     * * Standard methods
     * -------------------------------------------------------
     */

    @Override
    public final void clear() {
        this.createNewRep();
    }

    @Override
    public final DatasetRV newInstance() {
        return new DatasetRV1L();
    }

    @Override
    public final void transferFrom(DatasetRV source) {
        assert source != null : "Violation of: Source is not null";
        assert source != this : "Violation of: Source cannot be this";
        DatasetRV1L localSource = (DatasetRV1L) source;
        this.dataObservations = localSource.dataObservations;
        localSource.createNewRep();
    }

    /*
     * * Kernel methods -------------------------------------------------------
     */

    @Override
    public final void addElement(int rowVar, int col, Double observation) {
        assert this.dataObservations
                .length() < rowVar : "Violation of: No random variable at row";
        Sequence<Double> randomVarRow = this.dataObservations.entry(rowVar);
        if (col >= randomVarRow.length()) {
            randomVarRow.add(randomVarRow.length(), observation);
        } else {
            randomVarRow.replaceEntry(col, observation);
        }
    }

    @Override
    public final void removeElement(int rowVar, int col) {
        assert this.dataObservations
                .length() < rowVar : "Violation of: No random variable at row";
        assert this.dataObservations.entry(col)
                .length() < col : "Violation of: No observation at index col";
        Sequence<Double> randomVarRow = this.dataObservations.entry(rowVar);
        randomVarRow.remove(col);
    }

    @Override
    public final double getElement(int rowVar, int col) {
        assert this.dataObservations
                .length() < rowVar : "Violation of: No random variable at row";
        assert this.dataObservations.entry(col)
                .length() < col : "Violation of: No observation at index col";
        Sequence<Double> randomVarRow = this.dataObservations.entry(rowVar);
        return randomVarRow.remove(col);
    }

    @Override
    public final int numberOfObservations(int rowVar) {
        assert this.dataObservations
                .length() < rowVar : "Violation of: No random variable at row";
        Sequence<Double> randomVarRow = this.dataObservations.entry(rowVar);
        return randomVarRow.length();
    }

    @Override
    public final int numberOfRandomVariables() {
        return this.dataObservations.length();
    }

}
