#define N           205
#define IX_LEN      8
#define THRESHOLD   200.0

const int adc_channel = 0;
const uint16_t t1_load = 0;

// 250 corresponds to 8kHz
const uint16_t t1_comp = 250; 

uint16_t samples[N];
volatile uint8_t samplePos = 0;

float spectrum[IX_LEN];

// Frequences [697.0, 770.0, 852.0, 941.0, 1209.0, 1336.0, 1477.0, 1633.0]
// Corresponding spectrum indexes [18, 20, 22, 25, 32, 35, 39, 43]

const float cos_t[IX_LEN] = {
  0.8672996477658763, 0.8351757828373181, 0.7999146334222695, 0.7414333498411733, 
  0.6062254109666381, 0.5044173580296265, 0.42300402965485884, 0.30901699437494745
  };
  
const float sin_t[IX_LEN] = {
  0.4977864210534341, 0.5499831013422802, 0.6001138052377366, 0.6710265179136339, 
  0.7952928712734264, 0.8634599752845592, 0.906127800531333, 0.9510565162951535
  };

const char table[4][4] = {
  {'1', '2', '3', 'A'},
  {'4', '5', '6', 'B'},
  {'7', '8', '9', 'C'},
  {'*', '0', '#', 'D'}
};

void initADC() {
  // Init ADC free-run mode; f = ( 16MHz/prescaler ) / 13 cycles/conversion 
  ADMUX  = adc_channel; // Channel sel, right-adj, use AREF pin
  ADCSRA = _BV(ADEN)  | // ADC enable
           _BV(ADSC)  | // ADC start
           _BV(ADATE) | // Auto trigger
           _BV(ADIE)  | // Interrupt enable
           _BV(ADPS2) | _BV(ADPS1) | _BV(ADPS0); // 128:1 / 13 = 9615 Hz
  ADCSRB = _BV(ADTS2) | _BV(ADTS0);              // Timer/Counter1 Compare Match B
  DIDR0  = _BV(adc_channel); // Turn off digital input for ADC pin      
}

void initTimer() {
  // Reset Timer1 Control Reg A
  TCCR1A = 0;

// Set CTC mode
  TCCR1B &= ~(1 << WGM13);
  TCCR1B |= (1 << WGM12);

// Set prescaler to 8
  TCCR1B &= ~(1 << CS12);
  TCCR1B |= (1 << CS11);
  TCCR1B &= ~(1 << CS10);

// Reset Timer 1 and set compare value
  TCNT1 = t1_load;
  OCR1B = t1_comp;

// Enable Timer 1 compare interrupt
  TIMSK1 = (1 << OCIE1B);
}

void goertzel(uint16_t *samples, float *spectrum) {
  float v[N + 2];
    
  for (uint8_t k = 0; k < IX_LEN; k++) {
    float a = 2. * cos_t[k];
    v[0] = v[1] = .0;    
    for (uint8_t i = 2; i < N + 2; i++) {
      v[i] = float(samples[i - 2]) + a * v[i - 1] - v[i - 2];      
    }
    float re = cos_t[k] * v[N + 1] - v[N];
    float im = sin_t[k] * v[N + 1];

    spectrum[k] = sqrt(re * re + im * im);        
  } 
}

char detect_digit(float *spectrum) {
  int8_t row = -1, col = -1;
  for (uint8_t i = 0; i < IX_LEN; i++) {
    if (spectrum[i] > THRESHOLD) {
      if (row == -1) {
        row = i;
      } else if (col == -1) {
        col = i;
      } else {
        return 0;
      }
    }
  }

  if (row != -1 && col != -1) {
    return table[row][col - 4];
  } else {
    return 0;
  }
}

void setup() {  
  cli();
  initADC();
  initTimer(); 
  sei();

  Serial.begin(115200);
}

unsigned long z = 0;

void loop() {
  while(ADCSRA & _BV(ADIE)); // Wait for audio sampling to finish

  if (z % 100 == 0) {

    Serial.println("[ begin ]");
    for (int i = 0; i < N; i++) {
      Serial.println(samples[i]);
    }
    Serial.println("[ end ]");
    
    goertzel(samples, spectrum);
  
    for (int i = 0; i < IX_LEN; i++) {
      Serial.print(spectrum[i]);
      Serial.print("\t");
    }
    Serial.println();
    char digit = detect_digit(spectrum);
    if (digit == 0) {
      Serial.println("Not detected");      
    } else {
      Serial.print("Detected: ");
      Serial.println(digit);
    }
  }
  z++;
  
  samplePos = 0;

  ADCSRA |= _BV(ADIE);       // Resume sampling interrupt
}

ISR(ADC_vect) { 
  uint16_t sample = ADC;

  samples[samplePos++] = sample;
  
  if(samplePos >= N) {
    ADCSRA &= ~_BV(ADIE); // Buffer full, interrupt off
  }
}

ISR(TIMER1_COMPB_vect) {
  TCNT1 = t1_load;  
}
