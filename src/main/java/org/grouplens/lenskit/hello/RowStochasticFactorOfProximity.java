package org.grouplens.lenskit.hello;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.lenskit.data.dao.ItemDAO;

/**
 * A matrix to store the first factor of the matrix
 * that contains the inter-item relationships
 * that derive from their categorization.
 */


public class RowStochasticFactorOfProximity {

    private RealMatrix rowStochastic;

    private int itemSize;

    /**
     * Creates a matrix to process genre data and generate the first factor of the proximity
     * matrix needed for a {@code HIRItemScorer}.
     *
     * @param dao    The DataAccessObject interfacing with the item data for the model
     * @param gDao   The genreDataAccessObject interfacing with the genre data for the model
     *
     */

    public RowStochasticFactorOfProximity(ItemDAO dao,
                                          ItemGenreDAO gDao) {
        LongSet items = dao.getItemIds();
        int genreSize = gDao.getGenreSize();
        itemSize = items.size();

        double[][] data = new double[itemSize][genreSize];

        rowStochastic = MatrixUtils.createRealMatrix(data);

        int i = 0;
        LongIterator iter = items.iterator();
        while (iter.hasNext()) {
            long item = iter.nextLong();
            rowStochastic.setRowVector(i, gDao.getItemGenre(item));
            i++;
        }
    }

    /**
     * @return A matrix containing the row stochastic values of the matrix
     * that contains the information about the item categorization,
     * to be used by a {@code HIRItemScorer}.
     */

    public RealMatrix RowStochastic() {

        for (int i = 0; i < itemSize; i++) {
            RealVector forIter = rowStochastic.getRowVector(i);

            double sum = forIter.getL1Norm();

            if (sum!=0) {
                forIter.mapDivideToSelf(sum);
                rowStochastic.setRowVector(i, forIter);
            }
        }

        return rowStochastic;

    }
}