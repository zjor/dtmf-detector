package com.github.zjor;

import processing.core.PApplet;
import processing.core.PFont;
import processing.sound.*;

import java.util.Arrays;

public class App extends PApplet {

    public static final double SAMPLING_RATE = 44100.0;
    public static final int BANDS = 256;

    private FFT fft;
    private float[] spectrum = new float[BANDS];

    private PFont titleFont;
    private PFont normalFont;
    private PFont symbolFont;

    private long lastUpdateTimestamp;
    private double displayedFirstFreq;
    private double displayedSecondFreq;

    @Override
    public void settings() {
        size(600, 480);

    }

    @Override
    public void setup() {
        titleFont = createFont("Monospaced", 24);
        normalFont = createFont("Monospaced", 16);
        symbolFont = createFont("Monospaced", 32);

        background(255);

        AudioIn audioIn = new AudioIn(this);
        audioIn.start();

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
        text("DTMF decoder", width / 2, 24, 0);

        renderSpectrum(spectrum, 24, 128, width - 48, 100);

        textFont(normalFont);
        textAlign(LEFT, TOP);
        text(String.format("Freq1: %8.2f", displayedFirstFreq), 24, 72, 0);
        text(String.format("Freq2: %8.2f", displayedSecondFreq), 24, 96, 0);

        if (System.currentTimeMillis() - lastUpdateTimestamp >= 100) {
            displayedFirstFreq = firstFreq;
            displayedSecondFreq = secondFreq;
            lastUpdateTimestamp = System.currentTimeMillis();
        }

        textAlign(CENTER, TOP);
        textFont(titleFont);
        text("Detected symbol", width / 2, 256, 0);

        Character digit = resolveDigit(firstFreq, secondFreq);
        textAlign(CENTER, TOP);
        if (digit != null) {
            textFont(symbolFont);
            text(digit, width / 2, 350, 0);
        } else {
            textFont(normalFont);
            text("Not detected", width / 2, 350, 0);
        }

    }

    private void renderSpectrum(float[] spectrum, float x, float y, float w, float h) {
        float bandWidth = w / BANDS;
        for (int i = 0; i < BANDS; i++) {
            float bandH = spectrum[i] * height * 20;
            rect(x + i * bandWidth, y + h - bandH, bandWidth, bandH);
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