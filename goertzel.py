import re
import matplotlib.pyplot as pp

from math import pi, cos, sin, sqrt

# Discretisation frequency
fd = 9615.0 # 8000.0

# Number of samples
N  = 256 # 205

# Frequencies
frequencies = [697.0, 770.0, 852.0, 941.0, 1209.0, 1336.0, 1477.0, 1633.0]

# Sample indexes
ix = map(lambda f: int(f * N / fd), frequencies)

phases = map(lambda k: 2.0 * pi * k / N, ix)
cosines = map(lambda phase: cos(phase), phases)
sines = map(lambda phase: sin(phase), phases)

def goertzel(s, ix):
    N = len(s)
    result = []
    for k in range(0, len(ix)):        
        a = 2.0 * cosines[k]
        v = [.0]*(N + 2)
        for i in range(0, N):
            v[i] = s[i] + a * v[i - 1] - v[i - 2]
        w = (cosines[k], sines[k])
        result.append(sqrt((w[0] * v[N - 1] - v[N - 2])**2 + (w[1] * v[N - 1])**2))
    return result

data = []
with open('digit-3.txt') as f:
	packet = []
	for line in f.readlines():
		line = line.strip()
		if line == '[ start ]':
			packet = []
		elif re.match(r'^\d{3}$', line):
			packet.append(int(line))
		elif line == '[ end ]':
			data.append(packet)



s = goertzel(data[11], ix)
print s
pp.stem(s)
pp.show()