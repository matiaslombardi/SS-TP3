SLIT_X = 0.12

curr_time = 0

with open("./fps.txt", "a") as fps_file:
    with open("../out.txt", "r") as out_file:
        line = out_file.readline()
        while line:
            if len(line.split(" ")) == 2:
                print(line)
                break

            particle_count = int(line);
            header = out_file.readline() #iter
            collision = out_file.readline().split(" ") 
            if len(collision) == 4:
                [wall_collision, idA, idB, tc] = collision
                curr_time += float(tc)

            left_count = 0

            for i in range(particle_count):
                [id, x, y, vx, vy, m, r] = out_file.readline().split(" ")
                if float(x) < SLIT_X:
                    left_count += 1

            fp = left_count / particle_count
            fps_file.write("{} {}\n".format(curr_time, fp))

            line = out_file.readline()

    fps_file.write("\n")
