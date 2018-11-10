package com.github.zjor;

import processing.core.PApplet;
import processing.sound.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class App extends PApplet {

    public static final double SAMPLING_RATE = 44100.0;
    public static final int BANDS = 2048;

    private float bandWidth;

    private FFT fft;
    private float[] spectrum = new float[BANDS];

    @Override
    public void settings() {
        size(800, 600);
        bandWidth = width / BANDS;
    }

    @Override
    public void setup() {
        background(255);
        AudioIn audioIn = new AudioIn(this);
        audioIn.start();

        fft = new FFT(this, BANDS);
        fft.input(audioIn);

    }

    @Override
    public void draw() {
        fft.analyze(spectrum);
        List<Pair<Double, Double>> topFreqs = getTopFrequencies(spectrum);

        float[] s = Arrays.copyOf(spectrum, spectrum.length);
        Pair<Integer, Double> firstFreq = indexedMax(s);
        int from = limit((int) ((firstFreq.b - 35.f) * BANDS / SAMPLING_RATE * 2), 0, s.length - 1);
        int to = limit((int) ((firstFreq.b + 35.f) * BANDS / SAMPLING_RATE * 2), 0, s.length - 1);
//        System.out.println(from + "; " + to);

        zero(s, from, to);

        Pair<Integer, Double> secondFreq = indexedMax(s);


        fill(255);
        rect(0, 0, width, height);

        fill(0);
        for (int i = 0; i < BANDS; i++) {
            float h = spectrum[i] * height * 20;
            rect(i * bandWidth, height - h, bandWidth, h);
        }

        for (int i = 0; i < 10; i++) {
            text(String.format("%.2f", topFreqs.get(i).b), 16, 16 + i * 16, 0);
        }

        text(String.format("%.2f", firstFreq.b), 96, 16, 0);
        text(String.format("%.2f", secondFreq.b), 96, 32, 0);
        Character digit = detectDigit(firstFreq.b, secondFreq.b);
        if (digit != null) {
            text(digit, 96, 96, 0);
        }

    }

    private static double freq(int n) {
        return SAMPLING_RATE * n / BANDS / 2;
    }

    protected static List<Pair<Double, Double>> getTopFrequencies(float[] spectrum) {
        List<Pair<Double, Double>> freqs = new ArrayList<>(spectrum.length);

        for (int i = 0; i < spectrum.length; i++) {
            freqs.add(Pair.of((double) spectrum[i], freq(i)));
        }
        Collections.sort(freqs, Comparator.comparing(a -> -a.a));
        return freqs;
    }

    protected static Pair<Integer, Double> indexedMax(float[] a) {
        int max = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[max] < a[i]) {
                max = i;
            }
        }
        return Pair.of(max, freq(max));
    }

    protected static void zero(float[] a, int from, int to) {
        for (int i = from; i <= to; i++) {
            a[i] = .0f;
        }
    }

    private static int limit(int v, int min, int max) {
        return min(max(v, min), max);
    }

    private static int matchFrequency(double f, double[] table, double tolerance) {
        for (int i = 0; i < table.length; i++) {
            if (Math.abs(f - table[i]) <= tolerance) {
                return i;
            }
        }
        return -1;
    }

    public static final double[] COLUMN_FREQUENCIES = new double[]{1209, 1336, 1477, 1633,};
    public static final double[] ROW_FREQUENCIES = new double[]{697, 770, 852, 941};
    public static final char[][] DTMF_TABLE = new char[][] {
            {'1', '2', '3', 'A'},
            {'4', '5', '6', 'B'},
            {'7', '8', '9', 'C'},
            {'*', '0', '#', 'D'},
    };

    private static Character detectDigit(double f1, double f2) {
        int column = matchFrequency(f1, COLUMN_FREQUENCIES, 50);
        int row = matchFrequency(f2, ROW_FREQUENCIES, 35);
        if (column != -1 && row != -1) {
            return DTMF_TABLE[row][column];
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        PApplet.main(App.class);
    }
}

class Pair<A, B> {

    public final A a;

    public final B b;

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    static <X, Y> Pair<X, Y> of(X a, Y b) {
        return new Pair<>(a, b);
    }

    @Override
    public String toString() {
        return "Pair{" + "a=" + a + ", b=" + b + '}';
    }
}