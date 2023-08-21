with open('./input/large/gene-trees-true/01.trueGT') as f:
    with open('./input/large/c101.txt', 'w') as out:
        for line in f.readlines():
            skip = False
            newLine = ""
            for ch in line:
                # if ch.isdigit() or ch == '.' or ch == ':' :
                #     continue
                # else:
                #     newLine += ch
                    
                # print(ch)
                if ch == (':'):
                    # print("skipped")
                    skip = True
                elif ch == ',' or ch == ')':
                    skip = False
                if not skip:
                    newLine += ch
            # break
            
            out.write(newLine)
            # print(newLine, end='')
        # break
