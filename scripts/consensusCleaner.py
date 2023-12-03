import sys
import re

reg = r"[)].*?(?=[),])"
reg2 = r"[:][0-9\.]*(?=[),])"

line = ""

treeString = ""

while(True):
    line = sys.stdin.readline()
    if not line:
        break
    line = line.strip()
    treeString += line



treeString = re.sub(reg2, "", treeString)
treeString = re.sub(reg, ")", treeString)
print(treeString, end="")

# while(True):
#     line = sys.stdin.readline()
#     if not line:
#         break
#     line = re.sub(reg2, "", line)
#     line = re.sub(reg, ")", line)
#     print(line, end="")
    
    