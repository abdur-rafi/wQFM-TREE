import sys
import re

reg = r"[)].*?(?=[),])"

line = ""
while(True):
    line = sys.stdin.readline()
    if not line:
        break
    print(re.sub(reg, ")", line))
    
    