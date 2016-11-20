REGISTER /home/wl1485/project/test/piggybank-0.15.0.jar;
A = LOAD '/home/wl1485/project/test/yelp.csv' USING org.apache.pig.piggybank.storage.CSVExcelStorage() AS (cross_street, menu_date, city, geo_accuracy, latitude, rating, mob_url, review_count, snippet, snip_url, display_phone, categories, id, closed, longitude, neighborhoods, address, img_url, display_address, url, menu_provider, country, rating_img_url, rating_url, phone, name, rating_url_small, postal, state);
B = FOREACH A GENERATE rating, name, neighborhoods, address, latitude, longitude, city;
C = FOREACH B GENERATE rating, SIZE(name), SIZE(neighborhoods), SIZE(address), latitude, longitude, SIZE(city);
D = GROUP C ALL;
E = FOREACH D GENERATE MIN(C.rating), MAX(C.rating), MAX(C.$1), MAX(C.$2), MAX(C.$3), MIN(C.latitude), MAX(C.latitude), MIN(C.longitude), MAX(C.longitude), MAX(C.$6);
STORE E INTO 'profilingOutput.txt';
STORE B INTO 'yelpDataClean.txt'