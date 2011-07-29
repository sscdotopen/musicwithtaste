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

import io.ssc.musicwithtaste.SimilarArtistsRecommender;
import io.ssc.musicwithtaste.taste.SimilarArtistsWithTaste;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.model.file.FileIDMigrator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.IDMigrator;

import java.io.File;

/**
 *
 * mvn exec:java -Dexec.mainClass="io.ssc.musicwithtaste.examples.RunSimilarArtistsExample" -Dexec.args="/path/to/datasets"
 *
 */
public class RunSimilarArtistsExample {

  public static void main(String[] args) throws Exception {

    if (args.length != 1) {
      System.err.println("no dataset directory supplied!");
      return;
    }

    String directory = args[0];

    DataModel dataModel = new FileDataModel(new File(directory, "lastfm-ratings-sample-10-percent.csv"));
    IDMigrator migrator = new FileIDMigrator(new File(directory, "lastfm-artists.csv"));

    SimilarArtistsRecommender recommender = new SimilarArtistsWithTaste(dataModel, migrator);

    printPeopleWhoListenToThisArtistAlsoListenTo(recommender, "die Ärzte");
    printPeopleWhoListenToThisArtistAlsoListenTo(recommender, "minor threat");
    printPeopleWhoListenToThisArtistAlsoListenTo(recommender, "hansi hinterseer");
    printPeopleWhoListenToThisArtistAlsoListenTo(recommender, "britney spears");
  }

  static void printPeopleWhoListenToThisArtistAlsoListenTo(SimilarArtistsRecommender recommender, String artist)
      throws Exception {
    int listeners = recommender.numberOfPeopleListeningTo(artist);
    System.out.println("===== " + artist + " (" + listeners + " listeners) =====");
    System.out.println("people who listen to this artist also listen to:");
    long start = System.currentTimeMillis();
    for (String similarArtist: recommender.peopleWhoListenToThisArtistAlsoListenTo(artist, 5)) {
      System.out.println("\t" + similarArtist);
    }
    System.out.println("computation took [" + (System.currentTimeMillis() - start) + "] ms\n");
  }
}
