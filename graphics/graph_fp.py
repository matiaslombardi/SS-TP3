import matplotlib.pyplot as plt

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

        
        plt.plot(times, fps, label=f"N={particle_count}; D={slit_size}")

        line = fp_file.readline()

plt.legend()
plt.show()
