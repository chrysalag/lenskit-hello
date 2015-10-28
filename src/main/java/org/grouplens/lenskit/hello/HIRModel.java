package org.grouplens.lenskit.hello;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongIterators;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.grouplens.grapht.annotation.DefaultProvider;
import org.grouplens.lenskit.vectors.ImmutableSparseVector;
import org.grouplens.lenskit.vectors.MutableSparseVector;
import org.lenskit.inject.Shareable;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrysalag. Implements the Model of HIR algorithm.
 */

@DefaultProvider(HIRModelBuilder.class)
@Shareable
public class HIRModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private final Long2ObjectMap<ImmutableSparseVector> cmatrix;
    private final RealMatrix xmatrix;
    private final RealMatrix ymatrix;

    public HIRModel(Long2ObjectMap<ImmutableSparseVector> cmatrix,
                    RealMatrix xmatrix,
                    RealMatrix ymatrix) {
        this.cmatrix = cmatrix;
        this.xmatrix = xmatrix;
        this.ymatrix = ymatrix;
    }

    public MutableSparseVector getCoratingsVector(long item) {
        ImmutableSparseVector row = cmatrix.get(item);
        return row.mutableCopy();
    }

    public MutableSparseVector getProximityVector(long item, Collection<Long> items) {

        double[] data = xmatrix.getRow((int) item);
        RealMatrix row = MatrixUtils.createRowRealMatrix(data);
        RealMatrix resM = ymatrix.preMultiply(row);

        double[][] res = resM.getData();

        Map<Long, Double> forRes = new HashMap<>();

        LongIterator iter = LongIterators.asLongIterator(items.iterator());

        int i = 0;
        while (iter.hasNext()) {
            final long meti = iter.nextLong();
            forRes.put(meti, res[0][i]);
            i++;
        }

        return MutableSparseVector.create(forRes);
    }
}
