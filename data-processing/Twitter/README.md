# Request realtime tweets

## Each line of the input file consists of two columns:
1. Geographical information in users' profiles
(Note: tweets are obtained based on the realtime geographical info,
i.e. longitude and latitude of the posting devices, which may be different from
the geographical info in users' profiles.)
2. Raw realtime tweets encoding from UTF-8 format

## Profiling data mainly envolves two steps:
1. Remove URLs and emojis from the original tweets/raw input file
2. Count the total tweets posted from each geographical regions, e.g. Mahanttan, Brooklyn, etc.

## What to be expected from these two columns?
1. Geographical info: strings representing regional names
['(New York)|(NY)', '(Jersey)|(NJ)', 'Manhattan', 'Brooklyn', 'Staten Island', 'Queens',
        'Newport', 'hoboken', 'Newark', 'Bronx', 'Long Island','Elizabeth',
        'Rochelle', 'Syracuse', 'Flushing', 'Hempstead', 'Hicksville', 'Commack']
Otherwise, its set to "Others"
2. Tweets after processing: each tweet is no longer than 140 chars;
words will be converted into vectors for sentimental analysis adopting NLP algorithms (e.g. word$

