#!/usr/bin/env python
import sys

last_key = None
cnt = 0

for line in sys.stdin:
    line = line.strip()
    key, num = line.split('\t')

    num = int(num)

    if last_key != key:
        if last_key:
            print(last_key+'\t'+str(cnt))
        cnt = num
        last_key = key
    else:
	cnt = cnt+num

if last_key == key:
    print(last_key+'\t'+str(cnt))

