#!/usr/bin/env python
import sys
import re

for line in sys.stdin:
    line = line.strip()
    noHTTP = re.compile(r'http[s]*://[a-zA-Z0-9.?/&=:]*',re.S)
    line = noHTTP.sub("", line)
    noEMOJI = re.compile(r'\\x[[a-zA-Z0-9.?/&=:]*', re.S)
    line = noEMOJI.sub("", line)

    print(line)
