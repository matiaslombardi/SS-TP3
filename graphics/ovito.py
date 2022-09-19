with open("ovito.txt", "w") as ovito_file:
    with open("../out.txt", "r") as out_file:
        line = out_file.readline()
        while line:
            if len(line.split(" ")) == 2:
                break

            particle_count = int(line);
            header = out_file.readline() #iter
            out_file.readline() # colision

            ovito_file.write(str(particle_count) + "\n")
            ovito_file.write(header)

            for i in range(particle_count):
                [id, x, y, vx, vy, m, r] = out_file.readline().split(" ")
                ovito_file.write("{} {} {} {}".format(id, x, y, r));

            line = out_file.readline()