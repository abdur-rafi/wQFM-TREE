import sys
import colorsys
import random
import os
import matplotlib.pyplot as plt

os.chdir('./plant')

inTree = set()




with open('names.csv') as f:
    line = f.readline()
    while line:
        line = line.strip()
        cols = line.split(',')
        inTree.add(cols[1])
        line = f.readline()

# print(inTree)

def generate_distinct_colors(n):
    random.seed(13)
    colors = []
    for i in range(n):
        hue = (i * 1.0) / n
        hue = int(hue * 360)
        saturation = 90
        lightness = 50
        rgb = colorsys.hls_to_rgb(hue / 360.0, lightness / 100.0, saturation / 100.0)
        hex_color = '#{:02x}{:02x}{:02x}'.format(int(rgb[0] * 255), int(rgb[1] * 255), int(rgb[2] * 255))
        colors.append(hex_color)
    random.shuffle(colors)
    
    return colors



clads = {}

line = sys.stdin.readline()

while(True):
    line = sys.stdin.readline()
    if not line:
        break
    line = line.strip()
    cols = line.split(",")
    clad = cols[2].strip().strip('"').strip()
    if clad not in clads:
        clads[clad] = [cols[0]]
    else:
        clads[clad].append(cols[0])
    
# Iterate through clads dictionary
colors = generate_distinct_colors(len(clads))
index = 0

keys = clads.keys()

plt.figure(figsize=(12, 9))
for i, (keys, color) in enumerate(zip(keys, colors)):
    cnt = 0
    for val in clads[keys]:
        valTrimmed = val.strip().strip('"').strip()
        if valTrimmed not in inTree:
            continue
        cnt += 1
    
    plt.bar(i, 1, color=color, label=f"{keys}({cnt})")
plt.title("assigned colors")
plt.xlabel('Color Index')
plt.xticks([])
plt.yticks([])

# Display legend outside the plot
plt.legend(bbox_to_anchor=(1.05, 1), loc='upper left',ncol=2)
plt.tight_layout()
# plt.show()
plt.savefig('colors.png')


# print(clads)

with open("clades.txt", "w") as f:
    for clad, values in clads.items():
        taxaList = ''
        count = 0
        for val in values:
            valTrimmed = val.strip().strip('"').strip()
            if valTrimmed not in inTree:
                continue
            taxaList += valTrimmed + ','
            count += 1

        f.write(f"{clad}\n{count}\n{taxaList[:-1]}\n")

with open("coloring.txt", "w") as f:
    for clad, values in clads.items():
        for val in values:
            valTrimmed = val.strip().strip('"').strip()
            if valTrimmed not in inTree:
                continue
            f.write(f"{valTrimmed} label {colors[index]}\n")
        index += 1

with open("colorCladesMap.txt", "w") as f:
    index = 0
    for clad, values in clads.items():
        f.write(f"{clad} : {colors[index]}\n")
        index += 1

with open("labels.txt", "w") as f:
    for clad, values in clads.items():
        for val in values:
            valTrimmed = val.strip().strip('"').strip()
            if valTrimmed not in inTree:
                continue
            f.write(f"{valTrimmed},{valTrimmed},{clad}\n")


# for clad, values in clads.items():

#     for val in values:
#         valTrimmed = val.strip().strip('"').strip()
#         # print(val)
#         if valTrimmed not in inTree:
#             continue
#         print(f"{valTrimmed} label {colors[index]}")
#     index += 1

# index = 0
# for clad, values in clads.items():
#     print(f"{clad} : {colors[index]}")
#     index += 1
#     # vals = ''
#     # sep = ','
#     # for val in values:
#     #     vals += val.replace('"', '') + sep
#     # print(f"{clad}\n{len(values)}\n{vals[:-1]}")
    