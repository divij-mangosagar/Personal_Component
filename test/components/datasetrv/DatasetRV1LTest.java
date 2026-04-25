package components.datasetrv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

import components.sequence.Sequence;
import components.sequence.Sequence1L;

/**
 * JUnit test fixture for {@link DatasetRV1L}.
 */
public class DatasetRV1LTest {

        // ======================== HELPER METHODS ========================

        /**
         * Creates a new DatasetRV1L from a 2D array of doubles.
         *
         * The 2D array represents data where: - The first index (i) is the
         * variable (row) - The second index (j) is the observation (column)
         *
         *
         * @param data
         *                2D array where data[i][j] is observation j of variable
         *                i
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
         * Creates a deep copy of a DatasetRV1L for state verification.
         *
         * This method is used to capture the state of a dataset before an
         * operation to verify the state is unchanged after read-only operations
         *
         * @param ds
         *                the dataset to copy
         * @return a new DatasetRV1L with identical data to ds
         */
        private DatasetRV1L copyOf(DatasetRV1L ds) {
                Sequence<Sequence<Double>> newSeq = new Sequence1L<>();
                int numVariables = ds.numberOfRandomVariables();
                for (int i = 0; i < numVariables; i++) {
                        Sequence<Double> newRow = new Sequence1L<>();
                        int numObservations = ds.numberOfObservations(i);
                        for (int j = 0; j < numObservations; j++) {
                                newRow.add(newRow.length(),
                                                ds.getElement(i, j));
                        }
                        newSeq.add(newSeq.length(), newRow);
                }
                return new DatasetRV1L(newSeq);
        }

        /**
         * Asserts that two datasets have identical state.
         *
         * Compares: - Number of variables - Number of observations for each
         * variable - Each observation value
         *
         * @param expected
         *                the expected dataset
         * @param actual
         *                the actual dataset
         */
        private void assertDatasetsEqual(DatasetRV1L expected,
                        DatasetRV1L actual) {
                int expectedNumVars = expected.numberOfRandomVariables();
                int actualNumVars = actual.numberOfRandomVariables();
                assertEquals("Number of variables mismatch", expectedNumVars,
                                actualNumVars);

                for (int i = 0; i < expectedNumVars; i++) {
                        int expectedObs = expected.numberOfObservations(i);
                        int actualObs = actual.numberOfObservations(i);
                        assertEquals("Number of observations for variable " + i,
                                        expectedObs, actualObs);

                        for (int j = 0; j < expectedObs; j++) {
                                double expectedVal = expected.getElement(i, j);
                                double actualVal = actual.getElement(i, j);
                                final double delta = 0.0001;
                                assertEquals("Value at (" + i + ", " + j + ")",
                                                expectedVal, actualVal, delta);
                        }
                }
        }

        // ======================== CONSTRUCTOR TESTS ========================

        /**
         * Tests the no-argument constructor.
         *
         * A new DatasetRV1L created with no arguments should be empty (0 random
         * variables).
         */
        @Test
        public void testNoArgsConstructor() {
                DatasetRV1L ds = new DatasetRV1L();

                int expectedNumVariables = 0;
                int actualNumVariables = ds.numberOfRandomVariables();
                assertEquals("Empty dataset should have 0 variables",
                                expectedNumVariables, actualNumVariables);
        }

        /**
         * Tests the constructor from a Sequence<Sequence<Double>>.
         *
         * Creates a dataset from a sequence containing one variable with
         * observations [1.0, 2.0].
         */
        @Test
        public void testConstructorFromSequence() {
                // Build test data: one variable with observations [1.0, 2.0]
                Sequence<Sequence<Double>> seq = new Sequence1L<>();
                Sequence<Double> row = new Sequence1L<>();
                double firstObservation = 1.0;
                double secondObservation = 2.0;
                row.add(row.length(), firstObservation);
                row.add(row.length(), secondObservation);
                seq.add(seq.length(), row);

                DatasetRV1L ds = new DatasetRV1L(seq);

                int expectedNumVariables = 1;
                int actualNumVariables = ds.numberOfRandomVariables();
                assertEquals("Should have 1 variable", expectedNumVariables,
                                actualNumVariables);

                int expectedNumObservations = 2;
                int actualNumObservations = ds.numberOfObservations(0);
                assertEquals("Variable 0 should have 2 observations",
                                expectedNumObservations, actualNumObservations);

                final double delta = 0.0001;
                assertEquals("Observation (0,0) should be 1.0",
                                firstObservation, ds.getElement(0, 0), delta);
                assertEquals("Observation (0,1) should be 2.0",
                                secondObservation, ds.getElement(0, 1), delta);
        }

        // ======================== addElement TESTS ========================

        /**
         * Tests adding an element to create a new variable.
         *
         * When addElement is called with rowVar = 0 (new variable), it should
         * create a new variable with the observation.
         */
        @Test
        public void testAddElementToNewVariable() {
                DatasetRV1L ds = new DatasetRV1L();

                final double observationValue = 5.0;
                ds.addElement(0, 0, observationValue);

                int expectedNumVariables = 1;
                int actualNumVariables = ds.numberOfRandomVariables();
                assertEquals("Should have 1 variable", expectedNumVariables,
                                actualNumVariables);

                int expectedNumObservations = 1;
                int actualNumObservations = ds.numberOfObservations(0);
                assertEquals("Variable 0 should have 1 observation",
                                expectedNumObservations, actualNumObservations);

                final double delta = 0.0001;
                assertEquals("Observation value", observationValue,
                                ds.getElement(0, 0), delta);
        }

        /**
         * Tests adding an element to an existing variable beyond its current
         * length.
         *
         * Starting with variable 0 containing [1.0, 2.0], adding 3.0 at index 2
         * should result in [1.0, 2.0, 3.0].
         */
        @Test
        public void testAddElementToExistingVariable() {
                double[][] initialData = { { 1.0, 2.0 } };
                DatasetRV1L ds = this.createFromArgs(initialData);

                final double newObservation = 3.0;
                ds.addElement(0, 2, newObservation);

                int expectedNumVariables = 1;
                int actualNumVariables = ds.numberOfRandomVariables();
                assertEquals("Should still have 1 variable",
                                expectedNumVariables, actualNumVariables);

                final int expectedNumObservations = 3;
                int actualNumObservations = ds.numberOfObservations(0);
                assertEquals("Variable 0 should have 3 observations",
                                expectedNumObservations, actualNumObservations);

                final double delta = 0.0001;
                double expectedFirst = 1.0;
                double expectedSecond = 2.0;
                assertEquals("First observation", expectedFirst,
                                ds.getElement(0, 0), delta);
                assertEquals("Second observation", expectedSecond,
                                ds.getElement(0, 1), delta);
                assertEquals("New observation", newObservation,
                                ds.getElement(0, 2), delta);
        }

        // ======================== removeElement TESTS ========================

        /**
         * Tests removing an element from the middle of a variable.
         *
         * Starting with [1.0, 2.0, 3.0], removing index 1 should result in
         * [1.0, 3.0].
         */
        @Test
        public void testRemoveElement() {
                final double[][] initialData = { { 1.0, 2.0, 3.0 } };
                DatasetRV1L ds = this.createFromArgs(initialData);

                int removeIndex = 1;
                ds.removeElement(0, removeIndex);

                int expectedNumVariables = 1;
                int actualNumVariables = ds.numberOfRandomVariables();
                assertEquals("Should still have 1 variable",
                                expectedNumVariables, actualNumVariables);

                int expectedNumObservations = 2;
                int actualNumObservations = ds.numberOfObservations(0);
                assertEquals("Variable 0 should have 2 observations",
                                expectedNumObservations, actualNumObservations);

                final double delta = 0.0001;
                double expectedFirst = 1.0;
                final double expectedSecond = 3.0;
                assertEquals("First observation should be 1.0", expectedFirst,
                                ds.getElement(0, 0), delta);
                assertEquals("Second observation should be 3.0", expectedSecond,
                                ds.getElement(0, 1), delta);
        }

        /**
         * Tests removing all observations from a variable.
         *
         * Starting with [5.0], removing index 0 should leave the variable with
         * 0 observations.
         */
        @Test
        public void testRemoveElementUntilEmpty() {
                final double[][] initialData = { { 5.0 } };
                DatasetRV1L ds = this.createFromArgs(initialData);

                ds.removeElement(0, 0);

                int expectedNumVariables = 1;
                int actualNumVariables = ds.numberOfRandomVariables();
                assertEquals("Should still have 1 variable",
                                expectedNumVariables, actualNumVariables);

                int expectedNumObservations = 0;
                int actualNumObservations = ds.numberOfObservations(0);
                assertEquals("Variable 0 should have 0 observations",
                                expectedNumObservations, actualNumObservations);
        }

        // ======================== getElement TEST ========================

        /**
         * Tests that getElement does NOT modify the dataset state.
         *
         * This test captures the dataset state before calling getElement, then
         * verifies that the state is identical after calling getElement
         * multiple times.
         */
        @Test
        public void testGetElementDoesNotModifyState() {
                final double[][] initialData = { { 1.0, 2.0, 3.0 } };
                DatasetRV1L ds = this.createFromArgs(initialData);
                DatasetRV1L before = this.copyOf(ds);

                // Call getElement on all observations
                int numObservations = ds.numberOfObservations(0);
                for (int i = 0; i < numObservations; i++) {
                        ds.getElement(0, i);
                }

                // Verify state unchanged after getElement calls
                this.assertDatasetsEqual(before, ds);
        }

        // ======================== numberOfObservations TEST ========================

        /**
         * Tests that numberOfObservations returns the correct counts and does
         * NOT modify the dataset.
         */
        @Test
        public void testNumberOfObservations() {
                final double[][] initialData = { { 1.0, 2.0 },
                                { 3.0, 4.0, 5.0 } };
                DatasetRV1L ds = this.createFromArgs(initialData);
                DatasetRV1L before = this.copyOf(ds);

                int expectedObs0 = 2;
                int actualObs0 = ds.numberOfObservations(0);
                assertEquals("Variable 0 should have 2 observations",
                                expectedObs0, actualObs0);

                final int expectedObs1 = 3;
                int actualObs1 = ds.numberOfObservations(1);
                assertEquals("Variable 1 should have 3 observations",
                                expectedObs1, actualObs1);

                // Verify state unchanged
                this.assertDatasetsEqual(before, ds);
        }

        // ======================== numberOfRandomVariables TEST ========================

        /**
         * Tests that numberOfRandomVariables returns the correct count and does
         * NOT modify the dataset.
         */
        @Test
        public void testNumberOfRandomVariables() {
                final double[][] initialData = { { 1.0 }, { 2.0 }, { 3.0 } };
                DatasetRV1L ds = this.createFromArgs(initialData);
                DatasetRV1L before = this.copyOf(ds);

                final int expectedNumVariables = 3;
                int actualNumVariables = ds.numberOfRandomVariables();
                assertEquals("Should have 3 variables", expectedNumVariables,
                                actualNumVariables);

                // Verify state unchanged
                this.assertDatasetsEqual(before, ds);
        }

        // ======================== clear TEST ========================

        /**
         * Tests that clear removes all data from the dataset.
         */
        @Test
        public void testClear() {
                final double[][] initialData = { { 1.0, 2.0 }, { 3.0, 4.0 } };
                DatasetRV1L ds = this.createFromArgs(initialData);

                ds.clear();

                int expectedNumVariables = 0;
                int actualNumVariables = ds.numberOfRandomVariables();
                assertEquals("Dataset should be empty after clear",
                                expectedNumVariables, actualNumVariables);
        }

        // ======================== newInstance TEST ========================

        /**
         * Tests that newInstance creates a new, empty, independent dataset.
         */
        @Test
        public void testNewInstance() {
                double[][] initialData = { { 1.0, 2.0 } };
                DatasetRV1L ds = this.createFromArgs(initialData);
                DatasetRV1L before = this.copyOf(ds);

                DatasetRV1L copy = (DatasetRV1L) ds.newInstance();

                assertNotSame("newInstance should create a new object", ds,
                                copy);

                int expectedEmptyVariables = 0;
                int actualEmptyVariables = copy.numberOfRandomVariables();
                assertEquals("New instance should be empty",
                                expectedEmptyVariables, actualEmptyVariables);

                // Verify original unchanged
                this.assertDatasetsEqual(before, ds);
        }

        // ======================== transferFrom TEST ========================

        /**
         * Tests that transferFrom moves data from source to destination.
         *
         * After transferFrom: - Source should become empty - Destination should
         * have the source's original data
         */
        @Test
        public void testTransferFrom() {
                final double[][] sourceData = { { 1.0, 2.0 }, { 3.0, 4.0 } };
                DatasetRV1L ds1 = this.createFromArgs(sourceData);
                DatasetRV1L ds2 = new DatasetRV1L();

                ds2.transferFrom(ds1);

                int expectedEmptyVariables = 0;
                int actualEmptyVariables = ds1.numberOfRandomVariables();
                assertEquals("Source should be empty after transfer",
                                expectedEmptyVariables, actualEmptyVariables);

                int expectedDestVariables = 2;
                int actualDestVariables = ds2.numberOfRandomVariables();
                assertEquals("Destination should have 2 variables",
                                expectedDestVariables, actualDestVariables);

                final double delta = 0.0001;
                double expected11 = 1.0;
                double expected12 = 2.0;
                final double expected21 = 3.0;
                final double expected22 = 4.0;
                assertEquals("(0,0) should be 1.0", expected11,
                                ds2.getElement(0, 0), delta);
                assertEquals("(0,1) should be 2.0", expected12,
                                ds2.getElement(0, 1), delta);
                assertEquals("(1,0) should be 3.0", expected21,
                                ds2.getElement(1, 0), delta);
                assertEquals("(1,1) should be 4.0", expected22,
                                ds2.getElement(1, 1), delta);
        }
}