import sys

nums = []

while True:
    input_str = sys.stdin.readline().strip()
    if not input_str:
        break


    numbers = tuple(map(float, input_str.strip('()').replace(' ', '').split(',')))
    nums.append(numbers[2])


print('{}'.format(','.join(map(str, nums))))

# print('({})'.format(','.join(map(str, [sm[i] / count for i in range(len(sm))]))))
