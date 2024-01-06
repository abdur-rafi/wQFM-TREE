with open("./plant/ewqfm.A.classes", "r") as f:
    while True:
        line = f.readline()
        if not line:
            break
        name = line.strip().replace(' ', "_")
        ids = []
        f.readline()

        while True:
            line = f.readline()
            if line == "\n":
                break
            ids.append(line.strip().replace(' ', "_"))
        # print(ids)
        for id in ids:
            print(f"{id},{id},{name}")
        

        # print(line)
