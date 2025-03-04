import sys
import re

def normalize_quartet(quartet_str):
    """Normalizes a quartet while preserving its structure."""
    match = re.match(r"\(\((\w+),(\w+)\),\((\w+),(\w+)\)\)", quartet_str)
    if not match:
        raise ValueError(f"Invalid quartet format: {quartet_str}")
    
    a, b, c, d = match.groups()
    pair1 = tuple(sorted([a, b]))
    pair2 = tuple(sorted([c, d]))
    
    if pair1 > pair2:
        pair1, pair2 = pair2, pair1
    
    return (pair1, pair2)

def parse_file(filename):
    quartet_dict = {}
    with open(filename, 'r') as file:
        for line in file:
            line = line.strip()
            if not line:
                continue
            parts = line.rsplit(';', 1)
            if len(parts) != 2:
                print(f"Invalid format in {filename}: {line}")
                continue
            quartet_str, weight = parts
            try:
                quartet = normalize_quartet(quartet_str.strip())
                weight = float(weight.strip())
                quartet_dict[quartet] = weight
            except ValueError as e:
                print(f"Skipping invalid line in {filename}: {e}")
    return quartet_dict

def compare_files(file1, file2):
    quartets1 = parse_file(file1)
    quartets2 = parse_file(file2)
    
    if quartets1 == quartets2:
        print("Both files contain the same quartets with identical weights.")
    else:
        print("Files differ.")
        missing_in_2 = set(quartets1.keys()) - set(quartets2.keys())
        missing_in_1 = set(quartets2.keys()) - set(quartets1.keys())
        
        if missing_in_2:
            print("Quartets present in", file1, "but missing in", file2)
            for q in missing_in_2:
                print(q, quartets1[q])
        
        if missing_in_1:
            print("Quartets present in", file2, "but missing in", file1)
            for q in missing_in_1:
                print(q, quartets2[q])
        
        common = set(quartets1.keys()) & set(quartets2.keys())
        for q in common:
            if quartets1[q] != quartets2[q]:
                print(f"Weight mismatch for {q}: {file1} has {quartets1[q]}, {file2} has {quartets2[q]}")

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python compare_quartets.py <file1> <file2>")
        sys.exit(1)
    
    compare_files(sys.argv[1], sys.argv[2])
