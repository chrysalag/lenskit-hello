import org.grouplens.lenskit.hello.DirectAssociationParameter
import org.grouplens.lenskit.hello.HIRItemScorer
import org.grouplens.lenskit.hello.ProximityParameter
import org.grouplens.lenskit.transform.normalize.BaselineSubtractingUserVectorNormalizer
import org.grouplens.lenskit.transform.normalize.UserVectorNormalizer
import org.lenskit.api.ItemScorer
import org.lenskit.baseline.BaselineScorer
import org.lenskit.baseline.ItemMeanRatingItemScorer
import org.lenskit.baseline.UserMeanBaseline
import org.lenskit.baseline.UserMeanItemScorer

// ... and configure the item scorer.  The bind and set methods
// are what you use to do that. Here, we want an item-item scorer.
bind ItemScorer to HIRItemScorer.class

set DirectAssociationParameter to 0.6
set ProximityParameter to 0.3

// let's use personalized mean rating as the baseline/fallback predictor.
// 2-step process:
// First, use the user mean rating as the baseline scorer
//bind (BaselineScorer, ItemScorer) to UserMeanItemScorer
// Second, use the item mean rating as the base for user means
//bind (UserMeanBaseline, ItemScorer) to ItemMeanRatingItemScorer
// and normalize ratings by baseline prior to computing similarities
//bind UserVectorNormalizer to BaselineSubtractingUserVectorNormalizer