import matplotlib.pyplot as plt
import numpy as np
import math

iters = 100

pressures = []
energies = []
with open("pvt.txt", "r") as pvt_file:
    line = pvt_file.readline()
    while line:
        [pressure, energy] = [float(x) for x in pvt_file.readline().split(" ")]
        pressures.append(pressure)
        energies.append(energy)

        line = pvt_file.readline()

errors = []
initial_c = int((pressures[-1] - pressures[0]) / (energies[-1] - energies[0]))
min_err = math.inf
min_c = 0

for c in range(initial_c - iters, initial_c + iters + 1):
    error = 0

    for j in range(len(pressures)):
        error += (pressures[j] - c * energies[j]) ** 2
    
    if error < min_err:
        min_err = error
        min_c = c

    errors.append(error)

x = np.arange(initial_c - iters, initial_c + iters + 1)

plt.plot(x, errors)

# truncate to 6 decimal places
min_err = math.floor(min_err * 1000000) / 1000000

plt.annotate(f"({min_c}; {min_err})", (min_c - 12, min_err + 0.6))

plt.ylabel("Error")
plt.xlabel("c")
plt.show()
