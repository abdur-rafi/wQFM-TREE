import sys
import re

reg = r"[:].*?(?=[),])"
reg2 = r"[)].*?(?=[),])"

line = ""
while(True):
    line = sys.stdin.readline()
    if not line:
        break
    line = re.sub(reg, "", line)
    line = re.sub(reg2, ")", line)
    print(line, end="")
    
    
    