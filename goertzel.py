# -*- coding: utf-8 -*-
import re
import numpy as np 
import scipy.signal as signal 
import matplotlib.pyplot as pp

def goertzel(s, ind):
    """
    Функция возвращает занчения спектральных отсчетов ДПФ,
    рассчитанные с использованием алгоримта Герцеля для
    входного сигнала, заданного параметром s.
    
    Входные параметры
        s   - вектор значений входного сигнала
        ind - вектор индексов отсчетов ДПФ (индексация начинается с 0)
    
    Выходные параметры
        S   - Вектор спектральных отсчетов ДПФ, соответсвующих индексам ind
    
    Author: Sergey Bakhurin (dsplib.org)
    """
    N = len(s)
    NF = float(N)
    S = np.zeros(len(ind), dtype='complex128')
    for k in range(len(ind)):
        w = np.exp(2j * np.pi * float(ind[k]) / NF)
        alpha = 2.0 * np.cos(2.0 * np.pi * float(ind[k])/NF)
        b = np.array([w, -1.0, 0.0])
        a = np.array([1.0, -alpha, 1.0])
        X = signal.lfilter(b, a, s)
        S[k] = X[N-1]
    return S


# Частота дискретизации DTMF
fd = 9615.0 # 8000.0

# Количество отсчетов DTMF сигнала
N  = 256 # 205

# Вектор частот DTMF тонов
frq = np.array([697, 770, 852, 941, 1209, 1336, 1477, 1633], dtype = 'float64')
ix = map(lambda f: int(f * N / fd), frq)


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


s = np.abs(goertzel(data[11], ix))
pp.stem(s)
pp.show()