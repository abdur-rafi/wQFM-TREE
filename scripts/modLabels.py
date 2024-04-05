import sys
import re

reg = r'_\w+'

line = ""
while(True):
    line = sys.stdin.readline()
    if not line:
        break
    line = re.sub(reg, "", line)
    print(line, end="")
    
    
    