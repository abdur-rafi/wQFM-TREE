import sys

count = 0
sm = [0, 0, 0]

while True:
    input_str = sys.stdin.readline().strip()
    if not input_str:
        break

    count += 1

    numbers = tuple(map(float, input_str.strip('()').replace(' ', '').split(',')))
    
    sm = [sm[i] + numbers[i] for i in range(len(numbers))]

print('({})'.format(','.join(map(str, [sm[i] / count for i in range(len(sm))]))))
