import re
import matplotlib.pyplot as plt

regex = r"Running( \(JUST GUESSING\))?: ([a-zA-Z0-9 \.]+) \([0-9]+%\)"

test_str = open("/home/paul/os").read()

matches = re.finditer(regex, test_str, re.MULTILINE)

os_occurences = dict()
number_devices = 0
for matchNum, match in enumerate(matches, start=1):
    os = match.group(2)
    if "embedded" in os:
        continue
    if os in os_occurences:
        os_occurences[os] += 1
    else:
        os_occurences[os] = 1
    number_devices += 1

x = []
y = []

for os, occ in os_occurences.items():
    x.append(os)
    y.append(occ/number_devices)


x_pos = [i for i, _ in enumerate(x)]
plt.bar(x_pos, y, color='green')
plt.xlabel("OS")
plt.ylabel("Frequency")
plt.title("OSs at Ulm University")

plt.xticks(x_pos, x, rotation=90)
plt.subplots_adjust(bottom=0.2)

plt.savefig("os_without_embedded.eps")
plt.show()
