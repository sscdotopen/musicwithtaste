/**
 * Licensed to Sebastian Schelter (ssc[at]apache.org), who licenses this file
 *  to You under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.ssc.musicwithtaste.examples;

import io.ssc.musicwithtaste.DiscoverNewArtistsRecommender;
import io.ssc.musicwithtaste.taste.DiscoverNewArtistsWithTaste;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.model.file.FileIDMigrator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.IDMigrator;

import java.io.File;

/*
* mvn exec:java -Dexec.mainClass="io.ssc.musicwithtaste.examples.RunDiscoverNewArtistsExample" -Dexec.args="/path/to/datasets"
* */
public class RunDiscoverNewArtistsExample {

  public static void main(String[] args) throws Exception {

    if (args.length != 1) {
      System.err.println("no dataset directory supplied!");
      return;
    }

    String directory = args[0];

    DataModel dataModel = new FileDataModel(new File(directory, "lastfm-ratings.csv"));
    IDMigrator migrator = new FileIDMigrator(new File(directory, "lastfm-artists.csv"));

    DiscoverNewArtistsRecommender recommender = new DiscoverNewArtistsWithTaste(dataModel, migrator);

    String[] users = new String[] { "fdf091277a54083fe050047c8aa09fdb42cbad57",
        "249f277ffaac044259ac551cacea304382ec688d", "e536a743bb1dd9644e3a00b8e621359272f81567",
        "1e506941da0035cdfe9421b6f1a9b0eb86f51b03" };

    for (String user : users) {
      printDiscoverNewArtists(recommender, user);
    }
  }


  static void printDiscoverNewArtists(DiscoverNewArtistsRecommender recommender, String user)
      throws Exception {

    System.out.println("===== " + user + " =====");
    System.out.println("top ten artists he/she listens to:");
    for (String likedArtist: recommender.topArtistsCurrentlyLikedBy(user, 10)) {
      System.out.println("\t" + likedArtist);
    }
    long start = System.currentTimeMillis();
    System.out.println("\nand might also like:");
    for (String recommendedArtist: recommender.discoverArtistsFor(user, 5)) {
      System.out.println("\t" + recommendedArtist);
    }
    System.out.println("computation took [" + (System.currentTimeMillis() - start) + "] ms\n");
  }

}
