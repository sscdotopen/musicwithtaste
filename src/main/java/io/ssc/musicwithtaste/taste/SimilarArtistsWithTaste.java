package io.ssc.musicwithtaste.taste;

import io.ssc.musicwithtaste.SimilarArtistsRecommender;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.IDMigrator;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** uses taste components to implement a {@link SimilarArtistsRecommender} */
public class SimilarArtistsWithTaste implements SimilarArtistsRecommender {

  /* our actual recommender */
  private final ItemBasedRecommender tasteRecommender;
  /* a migrator to convert between artist names and their internal hash representations*/
  private final IDMigrator migrator;

  public SimilarArtistsWithTaste(DataModel dataModel, IDMigrator migrator) throws IOException {

    /* we're using loglikelihood to find artists that are listened to by people with the same taste,
        *  see http://tdunning.blogspot.com/2008/03/surprise-and-coincidence.html for details*/
    ItemSimilarity itemSimilarity = new LogLikelihoodSimilarity(dataModel);

    /* setup an itembased recommender which can compute most similar items */
    tasteRecommender = new GenericItemBasedRecommender(dataModel, itemSimilarity);
    this.migrator = migrator;
  }

  @Override
  public int numberOfPeopleListeningTo(String artist) {
    try {
      /* hash used to identify the artist */
      long artistID = migrator.toLongID(artist);
      /* read all preferences for this artist from the underlying data model */
      return tasteRecommender.getDataModel().getPreferencesForItem(artistID).length();
    } catch (TasteException e) {
      throw new RuntimeException("Unable to find number of people listening", e);
    }
  }

  @Override
  public List<String> peopleWhoListenToThisArtistAlsoListenTo(String artist, int howMany) {
    List<String> similarArtistNames = new ArrayList<String>(howMany);
    try {
      /* hash used to identify the artist */
      long artistID = migrator.toLongID(artist);
      /* compute similar items */
      List<RecommendedItem> similarArtists = tasteRecommender.mostSimilarItems(artistID, howMany);
      for (RecommendedItem similarArtist : similarArtists) {
        /* add the name of each found artist to the result */
        similarArtistNames.add(migrator.toStringID(similarArtist.getItemID()));
      }
    } catch (TasteException e) {
      throw new RuntimeException("Unable to find similar artists", e);
    }
    return similarArtistNames;
  }
}
