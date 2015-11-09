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

import org.apache.commons.math3.linear.RealVector;

import javax.annotation.Nullable;

/**
 * A DAO interface that provides access to item genres.
 * <p>
 * The normal way to get item names, without writing your own DAOs,
 * is to use a {@link org.lenskit.data.dao.MapItemGenreDAO}, often
 * loaded from a CSV file:
 * </p>
 * <pre>{@code
 * bind MapItemGenreDAO to CSVFileItemGenreDAOProvider
 * set ItemFile to "item-genres.csv"
 * }</pre>
 * <p>
 * Note that, while {@link org.lenskit.data.dao.MapItemGenreDAO} implements both this
 * interface and {@link org.lenskit.data.dao.ItemDAO}, binding this interface to the
 * provider instead of the class means that the item name DAO will only be used to
 * satisfy item name DAO requests and not item list requests.
 * </p>
 */

public interface ItemGenreDAO {
    /**
     * Get the genre for an item.
     * @param item The item ID.
     * @return A display genre for the item.
     */
    @Nullable
    RealVector getItemGenre(long item);

    /**
     * Get the number of genres in the dataset.
     * @return the number of genres in the dataset.
     */
    int getGenreSize();
}
