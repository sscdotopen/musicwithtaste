package io.ssc.musicwithtaste;

import java.util.List;

/** a Recommender that can recommend artists to artists */
public interface SimilarArtistsRecommender {

  /** find the arists that are most likely listened to too by people who listens to this artist */
  List<String> peopleWhoListenToThisArtistAlsoListenTo(String artist, int howMany);

  /** returns the number of people that listened to the artist */
  int numberOfPeopleListeningTo(String artist);
}
