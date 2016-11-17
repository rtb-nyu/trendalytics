lines = LOAD '/user/pd1241/class9/movies.txt' USING PigStorage('\t') AS (title, release_date, id, popularity, vote_count, vote_average, original_language, adult);

-- Compute Max and Min Dates

B = FOREACH lines GENERATE ToDate(release_date, 'YYYY-mm-dd') AS date;

MAX_DATE  = FOREACH (GROUP B ALL) GENERATE MAX(B.$0);

DUMP MAX_DATE;

MIN_DATE  = FOREACH (GROUP B ALL) GENERATE MIN(B.$0);

DUMP MIN_DATE;

-- Max and MIN VALS 

MAX_VALS = FOREACH (GROUP lines ALL) GENERATE MAX(lines.id), MAX(lines.vote_count), MAX(lines.vote_average), MAX(lines.popularity);

MIN_VALS = FOREACH (GROUP lines ALL) GENERATE MIN(lines.id), MIN(lines.vote_count), MIN(lines.vote_average), MIN(lines.popularity);

DUMP MAX_VALS;

DUMP MIN_VALS;

-- Display max and min sizes of all other columns

SIZE = FOREACH lines GENERATE SIZE(title), SIZE(id), SIZE(popularity), SIZE(vote_count), SIZE(vote_average), SIZE(original_language), SIZE(adult);

MAX_SIZE  = FOREACH (GROUP SIZE ALL) GENERATE MAX(SIZE.$0), MAX(SIZE.$1), MAX(SIZE.$2), MAX(SIZE.$3), MAX(SIZE.$4), MAX(SIZE.$5), MAX(SIZE.$6);

DUMP MAX_SIZE;

MIN_SIZE  = FOREACH (GROUP SIZE ALL) GENERATE MIN(SIZE.$0), MIN(SIZE.$1), MIN(SIZE.$2), MIN(SIZE.$3), MIN(SIZE.$4), MIN(SIZE.$5), MIN(SIZE.$6);

DUMP MIN_SIZE;

DUMP lines;

STORE lines INTO '/user/pd1241/class9/output' USING PigStorage();