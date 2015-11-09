import org.grouplens.lenskit.hello.DirectAssociationParameter
import org.grouplens.lenskit.hello.HIRItemScorer
import org.grouplens.lenskit.hello.ProximityParameter
import org.lenskit.api.ItemScorer

// Configuration of the item scorer.
bind ItemScorer to HIRItemScorer.class

set DirectAssociationParameter to 0.6
set ProximityParameter to 0.3