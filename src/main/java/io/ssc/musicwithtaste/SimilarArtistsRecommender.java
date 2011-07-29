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

package io.ssc.musicwithtaste;

import java.util.List;

/** a Recommender that can recommend artists to artists */
public interface SimilarArtistsRecommender {

  /** find the arists that are most likely listened to too by people who listens to this artist */
  List<String> peopleWhoListenToThisArtistAlsoListenTo(String artist, int howMany);

  /** returns the number of people that listened to the artist */
  int numberOfPeopleListeningTo(String artist);
}
