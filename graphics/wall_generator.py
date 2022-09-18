def generate(width, height, slit):
    s = ""
    id = 500
    with open('walls.txt', 'w') as wall:

        # Horizontal walls
        for x in range(0, width):
            s+=(str(id) + " " +str(x/1000) + " " + str(0/1000) + "\n")
            id+=1
            s+=(str(id) + " " +str(x/1000) + " " + str(height/1000) + "\n")
            id+=1
        
        # Vertical walls
        for y in range(0, height):
            s+=(str(id) + " " +str(0/1000) + " " + str(y/1000) + "\n")
            id+=1
            s+=(str(id) + " " +str(width/1000) + " " + str(y/1000) + "\n")
            id+=1

            # Only write wall if it is not in the hole, hole is centered vertically
            if y < ((height/2) - slit/2) or y > ((height/2) + slit/2):
                s+=(str(id) + " " + str(width/2000) + " " + str(y/1000) + "\n")
                id+=1
        
        amount = id - 500 - 1
        wall.write(str(amount)+ '\n')
        wall.write(s)
        wall.close()


generate(240, 90, 10)