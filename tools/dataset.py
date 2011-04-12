#!/usr/bin/python
# -*- coding: utf-8 -*-

import sys, os
import urllib, tarfile, math
import hashlib, numpy
import random

# big thanks to alex for figuring out that piece of code
def mahout_hash(value):
  md5_hash = hashlib.md5(value).digest()
  hash = numpy.int64(0)
  for c in md5_hash[:8]:
    hash = hash << 8 | ord(c)
  return str(hash)

# we use the log of plays as smoothed rating
def smoothed_rating(times_played):
  if (times_played == 0):
    rating = 1
  else:
    rating = int(math.log(times_played) + 1)
  if rating > 10:
    rating = 10
  return str(rating)

if len(sys.argv) != 2:
  print 'usage: %s <directory>' % os.path.basename(sys.argv[0])
  sys.exit(-1)

directory = sys.argv[1]

# download file
print '[1] Downloading http://mtg.upf.edu/static/datasets/last.fm/lastfm-dataset-360K.tar.gz (543MB)'
urllib.urlretrieve ("http://mtg.upf.edu/static/datasets/last.fm/lastfm-dataset-360K.tar.gz",
    directory + "/lastfm-dataset-360K.tar.gz")

## extract play data from tar file
tar = tarfile.open(directory + "/lastfm-dataset-360K.tar.gz", 'r:gz')
print '[2] Extracting lastfm-dataset-360K/usersha1-artmbid-artname-plays.tsv (1.5GB)'
tar.extract('lastfm-dataset-360K/usersha1-artmbid-artname-plays.tsv', directory)

print '[3] Converting plays to ratings'
artist_names = set()
lines_read = 0

with open(directory + '/lastfm-ratings.csv', 'w') as preferences_file:
  with open(directory + '/lastfm-dataset-360K/usersha1-artmbid-artname-plays.tsv', 'r') as plays_file:
    for line in plays_file:
      lines_read += 1
      (user_id, artist_id, artist_name, plays) = line.strip().split('\t')

      artist_names.add(artist_name)

      rating = smoothed_rating(int(plays))

      preferences_file.write(mahout_hash(user_id) + ',' + mahout_hash(artist_name) + ',' + rating + '\n')

      if lines_read % 100000 == 0:
        print "%d lines read..." % lines_read
print "%d lines read, done" % lines_read

print '[4] Saving artist names'
artist_names = list(artist_names)
artist_names.sort()

with open(directory + '/lastfm-artists.csv', 'w') as artists_file:
  for artist_name in artist_names:
    artists_file.write(artist_name + '\n')


print '[5] creating a 10% percent sample of the data'
random.seed('xKrot37')

with open(directory + '/lastfm-ratings-sample-10-percent.csv', 'w') as sampled_preferences:
  with open(directory + '/lastfm-ratings.csv', 'r') as preferences_file:
    for line in preferences_file:
      if random.randint(1, 100) <= 10:
        sampled_preferences.write(line)