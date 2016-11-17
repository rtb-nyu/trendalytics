REGISTER /home/wl1485/project/test/piggybank-0.15.0.jar;
A = LOAD '/home/wl1485/project/test/yelp.csv' USING org.apache.pig.piggybank.storage.CSVExcelStorage() AS (imgURL, rating, mobURL, reviewsCount, URL, claimed, snippet, a, b, c, name, d, country, neighborhood, address, address1, latitude, longitude, city, geo, addr0, addr1, addr2, addr3, postal, state, cat0, cat1, e, closed, phone0, phone1, f, g, h, c10, c11, c20, c21, n1);
B = FOREACH A GENERATE rating, name, neighborhood, address, latitude, longitude, city;
C = FOREACH B GENERATE rating, SIZE(name), SIZE(neighborhood), SIZE(address), latitude, longitude, SIZE(city);
D = GROUP C ALL;
E = FOREACH D GENERATE MIN(C.rating), MAX(C.rating), MAX(C.$1), MAX(C.$2), MAX(C.$3), MIN(C.latitude), MAX(C.latitude), MIN(C.longitude), MAX(C.longitude), MAX(C.$6);
STORE E INTO 'profilingOutput.txt';
STORE B INTO 'yelpDataClean.txt'