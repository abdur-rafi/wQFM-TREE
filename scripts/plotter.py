# data from https://allisonhorst.github.io/palmerpenguins/

import matplotlib.pyplot as plt
import numpy as np
import sys

inpFile = sys.argv[1]

if not inpFile:
    print("==> Error: No input file specified <==")
    exit(-1)

data = {}
comps = []
dCount = 0
with open(inpFile, 'r') as f:
    # n = int(f.readline())
    n = 2
    keys = []
    for i in range(n):
        key = f.readline().strip()
        keys.append(key)
        data[key] = []
    
    while True:
        comp = f.readline()
        if not comp:
            break
        comps.append(comp.strip())
        dCount += 1
        for i in range(n):
            numbers = tuple(map(float, f.readline().strip().strip('()').replace(' ', '').split(',')))
            data[keys[i]].append( float(f"{numbers[2]:.3f}"))
        
        # data[key] = vals

    # lines = f.readlines()



# comps = ("Adelie", "Chinstrap", "Gentoo")
# data = {
#     'Bill Depth': (18.35, 18.43, 14.98),
#     'Bill Length': (38.79, 48.83, 47.50),
# }

x = np.arange(len(comps))  # the label locations
width = 0.9  # the width of the bars
multiplier = 0

fig, ax = plt.subplots(figsize=(dCount * 1.5, 8))

for attribute, measurement in data.items():
    offset = width * multiplier
    rects = ax.bar(2 * x + offset, measurement, width, label=attribute)
    ax.bar_label(rects, padding=6)
    multiplier += 1

# Add some text for labels, title and custom x-axis tick labels, etc.
ax.set_ylabel('Rf Score')
ax.set_title('Rf score comparisn')
ax.set_xticks(2 * x + width, comps, rotation=90)
ax.legend(loc='upper left')
# ax.set_ylim(0, 250)
fig.tight_layout()

if len(sys.argv) > 2:
    plt.savefig(sys.argv[2])
else:
    plt.show()



    # fig, axs = plt.subplots(3, 4, figsize=(12, 10))
    # axs = axs.flatten()

    # for i in range(12):
    #     axs[i].imshow(imgs[i], cmap='gray')
    #     axs[i].set_title(f'num components = {ks[i]}', fontsize=8)
    
    # plt.show()