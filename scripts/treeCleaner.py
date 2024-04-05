import sys
import re

newickReg = r'[(].*[;]'
reg = r"[:].*?(?=[),])"
reg2 = r"[)].*?(?=[),])"
reg3 = r"[:].*(?=[;])"

line = ""
while(True):
    line = sys.stdin.readline()
    if not line:
        break
    match = re.search(newickReg, line)
    line = match.group()
    line = re.sub(reg, "", line)
    line = re.sub(reg2, ")", line)
    line = re.sub(reg3, "", line)
    print(line, end="\n")
    
    
    