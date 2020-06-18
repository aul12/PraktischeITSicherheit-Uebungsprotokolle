import re

test_str = open("/home/paul/tls").read()
print(test_str)

regex = r"443\/tcp open  https\n\|.*\n\|\s.+\n\|.+\n(\|.+\n)*\|(.*DES.*)"

matches = re.finditer(regex, test_str, re.MULTILINE)

count = 0
for matchNum, match in enumerate(matches, start=1):
    print(f"Result: {match.group(2)}")
    count += 1

print(count)
