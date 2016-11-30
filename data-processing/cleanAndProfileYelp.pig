REGISTER /home/wl1485/project/test/piggybank-0.15.0.jar;
A = LOAD '/home/wl1485/project/test/yelp20Lines.csv' USING org.apache.pig.piggybank.storage.CSVExcelStorage() AS (cross_streets,menu_date_updated,city,geo_accuracy,latitude,rating,mobile_url,review_count,is_claimed,snippet_text,snippet_image_url,display_phone,categories,id,is_closed,longitude,neighborhoods,address,image_url,display_address,url,menu_provider,country_code,rating_img_url,rating_img_url_large,phone,name,rating_img_url_small,postal_code,state_code);
B = FOREACH A GENERATE rating, name, latitude, longitude, city;
B = FOREACH A GENERATE rating, name, latitude, longitude, city;
C = FOREACH B GENERATE rating, SIZE(name), latitude, longitude, SIZE(city);
D = GROUP C ALL;
E = FOREACH D GENERATE MIN(C.rating), MAX(C.rating), MAX(C.$1), MIN(C.latitude), MAX(C.latitude), MIN(C.longitude), MAX(C.longitude), MAX(C.$4);
STORE E INTO 'profilingOutput';
STORE B INTO 'yelpDataClean'