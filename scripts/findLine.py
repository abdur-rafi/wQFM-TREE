import sys
import re

prefix = sys.argv[1]
regularExpression = re.compile(r'\b\d+\.\d+|\b\d+')

isInputInMs = len(sys.argv) > 2 and sys.argv[2] != None
# print(prefix)
# print(sys.argv)

while True:
    input_str = sys.stdin.readline()
    if not input_str:
        break
    
    if input_str.startswith(prefix):
        numbers = regularExpression.findall(input_str)
        if (len(numbers) == 0):
            continue
        printNumber = numbers[0]
        if (isInputInMs):
            printNumber = float(printNumber) / 1000
        
        if (len(numbers) > 1):
            printNumber = (float(numbers[0]) * 60 + float(numbers[1]))
        
        # print('{}'.format(','.join(numbers)))
        # print(input_str.strip())
        print(printNumber)
        
    
