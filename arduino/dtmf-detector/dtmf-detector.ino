#define ADC_CHANNEL 0
#define SAMPLES_SIZE  256

int16_t samples[SAMPLES_SIZE];
volatile unsigned int samplePos = 0;

void initADC() {
  cli();
  // Init ADC free-run mode; f = ( 16MHz/prescaler ) / 13 cycles/conversion 
  ADMUX  = ADC_CHANNEL; // Channel sel, right-adj, use AREF pin
  ADCSRA = _BV(ADEN)  | // ADC enable
           _BV(ADSC)  | // ADC start
           _BV(ADATE) | // Auto trigger
           _BV(ADIE)  | // Interrupt enable
           _BV(ADPS2) | _BV(ADPS1) | _BV(ADPS0); // 128:1 / 13 = 9615 Hz
  ADCSRB = 0;                // Free run mode, no high MUX bit
  DIDR0  = 1 << ADC_CHANNEL; // Turn off digital input for ADC pin
  TIMSK0 = 0;    
  sei();         
}

void setup() {
  Serial.begin(115200);
  initADC();  
}

void loop() {
  while(ADCSRA & _BV(ADIE)); // Wait for audio sampling to finish

  Serial.println("[ start ]");
  for (int i = 0; i < SAMPLES_SIZE; i++) {
    Serial.println(samples[i]);
  }
  Serial.println("[ end ]");

  samplePos = 0;
  ADCSRA |= _BV(ADIE);             // Resume sampling interrupt
}

/**
 * Audio-sampling interrupt handler
 */
ISR(ADC_vect) { 
  int16_t sample = ADC; 
  samples[samplePos++] = sample;
  
  if(samplePos >= SAMPLES_SIZE) {
    ADCSRA &= ~_BV(ADIE); // Buffer full, interrupt off
  }
}
