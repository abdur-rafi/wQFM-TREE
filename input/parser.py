with open('./input/gtree_15tax_100g_100b_R1.tre') as f:
    with open('./input/a.txt', 'w') as out:
        for line in f.readlines():
            skip = False
            newLine = ""
            for ch in line:
                if ch.isdigit() or ch == '.' or ch == ':' :
                    continue
                else:
                    newLine += ch
                    
                # print(ch)
                # if ch == (':'):
                #     # print("skipped")
                #     skip = True
                # elif ch == ',' or ch == ')':
                #     skip = False
                # if not skip:
                #     newLine += ch
            # break
            
            out.write(newLine)
            # print(newLine, end='')
        # break
