import matplotlib.pyplot as plt

COLORS = ['red', 'green', 'blue', 'orange', 'yellow', 'purple', 'black', 'white']

color_idx = 0

with open("./fps.txt") as fp_file:
    line = fp_file.readline()
    while line:
        [particle_count, slit_size] = line.split(" ")
        particle_count = int(particle_count)
        slit_size = float(slit_size)

        line = fp_file.readline()
        times = []
        fps = []
        while line != "\n":
            [t, fp] = [float(x) for x in line.split(" ")]
            times.append(t)
            fps.append(fp)
            line = fp_file.readline()

        
        # label = f"N={particle_count}; D={slit_size}m"
        label = f"N={particle_count}"
        # label = f"D={slit_size}m"
        plt.plot(times, fps, label=label, color=COLORS[color_idx])
        plt.plot(times, [1-y for y in fps], color=COLORS[color_idx])

        color_idx += 1
        line = fp_file.readline()

plt.axhline(y=0.5, color='black', linestyle='--', alpha=0.5)

plt.xlabel("Time (s)")
plt.ylabel("FP")

plt.legend()
plt.show()
