import matplotlib.pyplot as plt
import numpy as np

watching_idx = 1
label = "Slit Size"

times = {}
with open("./times.txt", "r") as time_file:
    line = time_file.readline()
    while line:
        k = line.split(" ")[watching_idx].strip("\n")
        if k not in times:
            times[k] = []

        times[k].append(float(time_file.readline().split(" ")[0]))
        line = time_file.readline()

print(times)


x=[]
y=[]
yerr=[]
for key in times:
    x.append(key)
    y.append(np.mean(times[key]))
    yerr.append(np.std(times[key]))

max_y = 0
for i in range(len(y)):
    if y[i] + yerr[i] > max_y:
        max_y = y[i] + yerr[i]

max_y += 100

tick_count = 10

ticks = [int(t) for t in np.arange(0, max_y, max_y / tick_count)]

print(ticks)

plt.errorbar(x, y, yerr=yerr, fmt='o-', capsize=6)
plt.yticks(ticks)

plt.xlabel(label)
plt.ylabel("Time (s)")

plt.show()
