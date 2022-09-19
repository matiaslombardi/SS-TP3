with open("times.txt", "a") as time_file:
    with open("../out.txt", "r") as out_file:
        line = out_file.readline()
        while line:
            if len(line.split(" ")) == 2:
                time_file.write(line)
                break

            particle_count = int(line);
            out_file.readline() #iter
            out_file.readline() # colision

            for i in range(particle_count):
                out_file.readline().split(" ")

            line = out_file.readline()
