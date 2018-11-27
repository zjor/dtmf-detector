import re
import matplotlib.pyplot as pp

from math import pi, cos, sin, sqrt

# Discretisation frequency
# fd = 9615.0
fd = 8000.0

# Number of samples
# N  = 256
N = 205

# Frequencies
frequencies = [697.0, 770.0, 852.0, 941.0, 1209.0, 1336.0, 1477.0, 1633.0]

# Sample indexes
ix = map(lambda f: int(f * N / fd), frequencies)

phases = map(lambda k: 2.0 * pi * k / N, ix)
cosines = map(lambda phase: cos(phase), phases)
sines = map(lambda phase: sin(phase), phases)

print ix
print cosines
print sines

def goertzel(s, ix):
    N = len(s)
    result = []
    for k in range(0, len(ix)):        
        a = 2.0 * cosines[k]
        v = [.0]*(N + 2)        
        for i in range(2, N + 2):            
            v[i] = s[i - 2] + a * v[i - 1] - v[i - 2]
        real = cosines[k] * v[N + 1] - v[N]
        im = sines[k] * v[N + 1]
        
        result.append(sqrt(real * real + im * im))
    return result

def load256Samples(filename):
    data = []
    with open(filename) as f:
    	packet = []
    	for line in f.readlines():
    		line = line.strip()
    		if line == '[ start ]':
    			packet = []
    		elif re.match(r'^\d{3}$', line):
    			packet.append(int(line))
    		elif line == '[ end ]':
    			data.append(packet)
    return data[11]

def load205Samples(filename):
    data = []
    with open(filename) as f:
        for line in f.readlines():
            data.append(int(line.strip()))
    return data


data = load205Samples('digit-3-205.txt')

s = goertzel(data, ix)
print s
pp.stem(s)
pp.show()