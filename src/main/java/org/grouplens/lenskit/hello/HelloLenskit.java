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

import org.lenskit.LenskitRecommenderEngine;
import org.lenskit.LenskitConfiguration;
import org.lenskit.config.ConfigHelpers;
import org.lenskit.data.dao.EventDAO;
import org.lenskit.data.dao.ItemNameDAO;
import org.lenskit.data.dao.MapItemNameDAO;
import org.grouplens.lenskit.data.text.Formats;
import org.grouplens.lenskit.data.text.TextEventDAO;
import org.lenskit.LenskitRecommender;
import org.lenskit.api.*;
import org.lenskit.api.Result;
import org.lenskit.data.ratings.PreferenceDomain;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstration app for LensKit. This application builds an item-item CF model
 * from a CSV file, then generates recommendations for a user.
 *
 * Usage: java org.grouplens.lenskit.hello.HelloLenskit ratings.csv user
 */
public class HelloLenskit implements Runnable {
    public static void main(String[] args) {
        HelloLenskit hello = new HelloLenskit(args);
        try {
            hello.run();
        } catch (RuntimeException e) {
            System.err.println(e.toString());
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private File inputFile = new File("data/ratings.csv");
    private File movieFile = new File("data/movies.csv");
    private File genreFile = new File("data/genres.csv");

    private List<Long> users;

    public HelloLenskit(String[] args) {
        users = new ArrayList<>(args.length);
        for (String arg: args) {
            users.add(Long.parseLong(arg));
        }
    }

    public void run() {
        EventDAO dao = TextEventDAO.create(inputFile, Formats.movieLensLatest());
        ItemNameDAO names;
        MapItemGenreDAO genres;

        try {
            names = MapItemNameDAO.fromCSVFile(movieFile, 1);
        } catch (IOException e) {
            throw new RuntimeException("cannot load names", e);
        }

        try {
            genres = MapItemGenreDAO.fromCSVFile(genreFile);
        } catch (IOException g) {
            throw new RuntimeException("cannot load names", g);
        }

        // Next: load the LensKit algorithm configuration
        LenskitConfiguration config = null;
        try {
            config = ConfigHelpers.load(new File("etc/hir.groovy"));
        } catch (IOException e) {
            throw new RuntimeException("could not load configuration", e);
        }
        // Add our data component to the configuration

        config.addComponent(dao);
        config.bind(EventDAO.class).to(dao);
        config.bind(MapItemGenreDAO.class).to(genres);
        config.bind(PreferenceDomain.class).to(new PreferenceDomain(0, 1));

        // Now that we have a configuration, build a recommender engine from the configuration
        // and data source. This will compute the similarity matrix and return a recommender
        // engine that uses it.
        LenskitRecommenderEngine engine = LenskitRecommenderEngine.build(config);

        // Finally, get the recommender and use it.
        try (LenskitRecommender rec = engine.createRecommender()) {
            // we want to recommend items
            ItemRecommender irec = rec.getItemRecommender();
            assert irec != null; // not null because we configured one
            // for users
            for (long user : users) {
                // get 10 recommendation for the user
                List<Result> recs = irec.recommendWithDetails(user, 10, null, null);
                System.out.format("Recommendations for user %d:\n", user);
                for (Result item : recs) {
                    String name = names.getItemName(item.getId());
                    System.out.format("\t%d (%s): %.10f\n", item.getId(), name, item.getScore());
                    //System.out.format("\t %s \n", name);
                }
            }
        }
    }
}
