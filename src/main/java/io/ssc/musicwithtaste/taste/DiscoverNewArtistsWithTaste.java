package io.ssc.musicwithtaste.taste;

import io.ssc.musicwithtaste.DiscoverNewArtistsRecommender;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.IDMigrator;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.util.ArrayList;
import java.util.List;

/** uses taste components to implement a {@link DiscoverNewArtistsRecommender} */
public class DiscoverNewArtistsWithTaste implements DiscoverNewArtistsRecommender {

  private final Recommender tasteRecommender;
  private final IDMigrator migrator;

  public DiscoverNewArtistsWithTaste(DataModel dataModel, IDMigrator migrator) throws Exception {

    /* were using the pearson correlation to compare the users,
        * see http://en.wikipedia.org/wiki/Pearson_product-moment_correlation_coefficient for details*/
    UserSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);

    /* we look at the 10 most similar users to discover new artists */
    UserNeighborhood neighborhood = new NearestNUserNeighborhood(10, similarity, dataModel);

    /* setup our user-based recommender */
    tasteRecommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);
    this.migrator = migrator;
  }

  @Override
  public List<String> topArtistsCurrentlyLikedBy(String user, int howMany) {
    try {
      PreferenceArray preferencesFromUser = tasteRecommender.getDataModel()
          .getPreferencesFromUser(migrator.toLongID(user));
      preferencesFromUser.sortByValueReversed();

      List<String> likedArtists = new ArrayList<String>(howMany);
      for (Preference preference : preferencesFromUser) {
        if (likedArtists.size() == 10) {
          break;
        }
        likedArtists.add(migrator.toStringID(preference.getItemID()));
      }
      return likedArtists;
    } catch (TasteException e) {
      throw new RuntimeException("Unable to find top liked artists", e);
    }
  }

  @Override
  public List<String> discoverArtistsFor(String user, int howMany) {
    try {
      List<String> recommendedArtistNames = new ArrayList<String>(howMany);
      List<RecommendedItem> recommendedArtists = tasteRecommender.recommend(migrator.toLongID(user), howMany);
      for (RecommendedItem recommendedArtist : recommendedArtists) {
        recommendedArtistNames.add(migrator.toStringID(recommendedArtist.getItemID()));
      }
      return recommendedArtistNames;
    } catch (TasteException e) {
      throw new RuntimeException("Unable to find liked artists", e);
    }
  }
}
