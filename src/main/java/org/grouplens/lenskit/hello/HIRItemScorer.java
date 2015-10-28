/*
 * LensKit, an open source recommender systems toolkit.
 * Copyright 2010-2014 LensKit Contributors.  See CONTRIBUTORS.md.
 * Work on LensKit has been funded by the National Science Foundation under
 * grants IIS 05-34939, 08-08692, 08-12148, and 10-17697.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package org.grouplens.lenskit.hello;

/**
 * Created by chrysalag. Implements the Item Scorer of HIR algorithm.
 */

import org.grouplens.lenskit.data.history.RatingVectorUserHistorySummarizer;
import org.grouplens.lenskit.vectors.MutableSparseVector;
import org.grouplens.lenskit.vectors.SparseVector;
import org.grouplens.lenskit.vectors.VectorEntry;
import org.lenskit.api.Result;
import org.lenskit.api.ResultMap;
import org.lenskit.basic.AbstractItemScorer;
import org.lenskit.data.dao.ItemDAO;
import org.lenskit.data.dao.UserEventDAO;
import org.lenskit.data.history.History;
import org.lenskit.data.history.UserHistory;
import org.lenskit.data.ratings.PreferenceDomain;
import org.lenskit.data.ratings.Rating;
import org.lenskit.results.Results;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;

/**
 * An Item Scorer implements the HIR algorithm.
 */
public class HIRItemScorer extends AbstractItemScorer {
    protected final UserEventDAO dao;
    protected final ItemDAO idao;
    protected HIRModel model;
    protected final PreferenceDomain domain;
    protected double directAssociation;
    protected double proximity;

    @Inject
    public HIRItemScorer(UserEventDAO dao,
                         HIRModel model,
                         ItemDAO idao,
                         @Nullable PreferenceDomain dom,
                         @DirectAssociationParameter double direct,
                         @ProximityParameter double prox) {
        this.dao = dao;
        this.model = model;
        this.idao = idao;
        domain = dom;
        directAssociation = direct;
        proximity = prox;
    }

    @Nonnull
    @Override
    public ResultMap scoreWithDetails(long user, @Nonnull Collection<Long> items) {
        //Set<Long> item = idao.getItemIds();

        UserHistory<Rating> history = dao.getEventsForUser(user, Rating.class);

        if (history == null) {
            history = History.forUser(user);
        }

        SparseVector historyVector = RatingVectorUserHistorySummarizer.makeRatingVector(history);

        List<Result> results = new ArrayList<>();

        MutableSparseVector preferenceVector = MutableSparseVector.create(idao.getItemIds(), 0);
        MutableSparseVector coratingsVector;
        MutableSparseVector proximityVector;

        for (VectorEntry e: historyVector.fast()) {
            long key = e.getKey();
            double value = e.getValue();
            preferenceVector.set(key, value);
        }

        double preferenceInResults = 1 - directAssociation - proximity;
        preferenceVector.multiply(preferenceInResults);

        for (VectorEntry e: preferenceVector.fast()) {
            if (e.getValue() != 0) {
                double prefValue = e.getValue();
                long prefKey = e.getKey();
                coratingsVector = model.getCoratingsVector(prefKey);
                coratingsVector.multiply(directAssociation);

                proximityVector = model.getProximityVector(prefKey, idao.getItemIds());
                proximityVector.multiply(proximity);

                coratingsVector.add(proximityVector);
                coratingsVector.multiply(prefValue);

                preferenceVector.add(coratingsVector);
            }
        }

        if (preferenceVector.sum() < 1.1 || preferenceVector.sum() > 0.9) {
            for (VectorEntry e : preferenceVector.fast()) {
                long key = e.getKey();
                if (!historyVector.containsKey(key)) {
                    results.add(Results.create(key, e.getValue()));
                }
            }
        }

        if (results.size() == (idao.getItemIds().size() - historyVector.size())) {
            return Results.newResultMap(results);
        } else {
            List<Result> result = new ArrayList<>();
            return Results.newResultMap(result);
        }
    }

    public HIRModel getModel() { return model; }
}
