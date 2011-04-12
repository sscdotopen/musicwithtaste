package io.ssc.musicwithtaste;


import java.util.List;

/** discover new artists for our users */
public interface DiscoverNewArtistsRecommender {

  /** return all artists the user currently likes */
  List<String> topArtistsCurrentlyLikedBy(String user, int howMany);

  /** return artists the user does not know but might like */
  List<String> discoverArtistsFor(String user, int howMany);

}
