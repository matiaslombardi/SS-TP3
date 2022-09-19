
EPSILON = 0.001
SLIT_SIZE = 0.02

VERTICAL_WALLS = [0, 1, 4, 5]
HORIZONTAL_WALLS = [2, 3]

HEIGHT = 0.09
WIDTH = 0.24
SLIT_X = WIDTH / 2

PERIMETER = HEIGHT * 2 + WIDTH * 2 + 2 * (HEIGHT - SLIT_SIZE)
VOLUME = WIDTH * HEIGHT

MASS = 1

curr_time = 0
tot_impulse = 0
eq_time = 0

speed_sq = 0

with open("../out.txt", "r") as out_file:
    first_iter = True
    line = out_file.readline()
    while line:
        particle_count = int(line);
        out_file.readline() # iter
        collision = out_file.readline().split(" ") 
        if len(collision) == 4:
            [wall_collision, idA, idB, tc] = collision
            curr_time += float(tc)

        left_count = 0

        for i in range(particle_count):
            [id, x, y, vx, vy, m, r] = out_file.readline().split(" ")
            if first_iter:
                speed_sq += float(vx) ** 2 + float(vy) ** 2

            if float(x) < SLIT_X:
                left_count += 1

        if first_iter:
            first_iter = False
            speed_sq /= particle_count

        fp = left_count / particle_count
        if abs(fp - 0.5) < EPSILON:
            break;

        line = out_file.readline()

    line = out_file.readline()
    while line:
        if len(line.split(" ")) == 2:
            [eq, final] = [float(x) for x in line.split(" ")]
            eq_time = final - eq
            break
        
        particle_count = int(line);
        out_file.readline() # iter
        [wall_collision, idA, idB, tc] = out_file.readline().split(" ") 
        curr_time += float(tc)

        for i in range(particle_count):
            [id, x, y, vx, vy, m, r] = out_file.readline().split(" ")
            idB = int(idB)
            if wall_collision and idA == id:
                if idB in HORIZONTAL_WALLS:
                    tot_impulse += 2 * float(m) * abs(float(vy))
                else:
                    tot_impulse += 2 * float(m) * abs(float(vx))

        line = out_file.readline()

force = tot_impulse / eq_time
pressure = force / PERIMETER

energy = 0.5 * MASS * speed_sq

with open("./pvt.txt", "a") as pvt_file:
    pvt_file.write("{} {}\n".format(pressure, energy))

