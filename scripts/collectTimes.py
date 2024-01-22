import sys

lines = []

while True:
    input_str = sys.stdin.readline()
    if not input_str:
        break
    
    lines.append(input_str.strip())

print(','.join(lines))