#!/usr/bin/env python
import sys
import re

last_line = ""

for line in sys.stdin:
    line = line.strip()
    if last_line != line:
        if last_line:
            print(last_line)
        last_line = line
    else:
	continue

if last_line == line:
    print(last_line)
