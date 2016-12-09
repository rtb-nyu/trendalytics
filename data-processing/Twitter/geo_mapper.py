#!/usr/bin/env python
import re
import sys

keys = ['(New York)|(NY)', '(Jersey)|(NJ)', 'Manhattan', 'Brooklyn', 'Staten Island', 'Queens',
        'Newport', 'hoboken', 'Newark', 'Bronx', 'Long Island','Elizabeth',
        'Rochelle', 'Syracuse', 'Flushing', 'Hempstead', 'Hicksville', 'Commack']


for line in sys.stdin:
    line = line.strip()
    found = False
    for key in keys:
        if re.search(key, line, re.IGNORECASE):
            print(key+'\t1')
            found = True
        elif key == keys[-1] and not found:
            print('Others'+'\t1')

