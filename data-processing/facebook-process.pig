SET textinputformat.record.delimiter '&*&'
A = load '/user/kv730/project/input/fb.txt';
B = FOREACH A GENERATE FLATTEN(STRSPLIT($0,';;',-1));
C = FOREACH B GENERATE $0 as id : chararray, $3 as dist : int, $5 as time : double;
D = FOREACH ( GROUP C BY ( dist,time)) {
	b = C.(id);
	s = DISTINCT b;
	GENERATE FLATTEN(s) as id, FLATTEN(group) AS (dist,time);
	};
E = FOREACH D generate $0 , ($1==null?0:$1), ($2==null?0,$2);
F = FILTER E by IsEmpty($1) OR IsEmpty($2);
G = ORDER F by $1,$2 ASC: