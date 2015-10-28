/*
 * Copyright 2011 University of Minnesota
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
