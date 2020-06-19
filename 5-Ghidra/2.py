import sys

inp = sys.argv[1]
out = ""

for c in inp:
    asc = ((ord(c) * 10) % 0x1A) + ord('a')
    out += chr(asc) 

print(out)
