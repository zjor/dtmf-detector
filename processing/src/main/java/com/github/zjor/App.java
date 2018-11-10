package com.github.zjor;

import processing.core.PApplet;
import processing.core.PFont;
import processing.sound.*;

import java.util.Arrays;

public class App extends PApplet {

    public static final double SAMPLING_RATE = 44100.0;
    public static final int BANDS = 256;

    private float bandWidth;

    private FFT fft;
    private float[] spectrum = new float[BANDS];

    private PFont titleFont;
    private PFont normalFont;

    @Override
    public void settings() {
        size(600, 480);

    }

    @Override
    public void setup() {
        String[] fontList = PFont.list();
        printArray(fontList);

        titleFont = createFont("Monospaced", 24);
        normalFont = createFont("Monospaced", 16);

        background(255);

        AudioIn audioIn = new AudioIn(this);
        audioIn.start();

        bandWidth = width / BANDS;

        fft = new FFT(this, BANDS);
        fft.input(audioIn);
    }

    @Override
    public void draw() {
        fft.analyze(spectrum);

        float[] s = Arrays.copyOf(spectrum, spectrum.length);
        int firstFreqIndex = maxIndex(s);
        double firstFreq = freq(firstFreqIndex);
        int from = limit((int) ((firstFreq - 120.f) * BANDS / SAMPLING_RATE * 2), 0, s.length - 1);
        int to = limit((int) ((firstFreq + 120.f) * BANDS / SAMPLING_RATE * 2), 0, s.length - 1);

        zero(s, from, to);

        int secondFreqIndex = maxIndex(s);
        double secondFreq = freq(secondFreqIndex);


        fill(255);
        rect(0, 0, width, height);
        fill(0);

        textAlign(CENTER, TOP);
        textFont(titleFont);
        text("DTMF decoder", width / 2, 8, 0);


        for (int i = 0; i < BANDS; i++) {
            float h = spectrum[i] * height * 20;
            rect(i * bandWidth, height - h, bandWidth, h);
        }

        textFont(normalFont);
        text(String.format("%.2f", firstFreq), 96, 16, 0);
        text(String.format("%.2f", secondFreq), 96, 32, 0);
        Character digit = resolveDigit(firstFreq, secondFreq);
        if (digit != null) {
            text(digit, 96, 96, 0);
        }

    }

    private static double freq(int n) {
        return SAMPLING_RATE * n / BANDS / 2;
    }


    protected static int maxIndex(float[] a) {
        int max = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[max] < a[i]) {
                max = i;
            }
        }
        return max;
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

    private static Character resolveDigit(double f1, double f2) {
        int column = matchFrequency(f1, COLUMN_FREQUENCIES, 120);
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