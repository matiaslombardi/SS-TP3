import matplotlib.pyplot as plt
import numpy as np

pressures = []
energies = []
with open("pvt.txt", "r") as pvt_file:
    line = pvt_file.readline()
    while line:
        [pressure, energy] = [float(x) for x in pvt_file.readline().split(" ")]
        pressures.append(pressure)
        energies.append(energy)
        line = line.strip('\n')
        plt.annotate(f"v = {line}m/s", (energy, pressure + 5))

        line = pvt_file.readline()


# Plot line between first and last point
plt.plot([energies[0], energies[2]], [pressures[0], pressures[2]], color='blue', linestyle='-', zorder=-1, alpha=0.5)

plt.scatter(energies, pressures)

plt.xticks(np.arange(0, max(energies) + 0.01, 0.005))
plt.xlabel("Kinetic Energy (J)")
plt.ylabel("Pressure (N/m)")

plt.show()
