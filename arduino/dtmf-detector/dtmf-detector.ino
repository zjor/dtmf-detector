
const int adc_channel = 0;
const uint16_t t1_load = 0;
const uint16_t t1_comp = 31250;

volatile int16_t sample = -1;

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

// Set prescaler to 256
  TCCR1B |= (1 << CS12);
  TCCR1B &= ~(1 << CS11);
  TCCR1B &= ~(1 << CS10);
  
// Reset Timer 1 and set compare value
  TCNT1 = t1_load;
  OCR1B = t1_comp;

// Enable Timer 1 compare interrupt
  TIMSK1 = (1 << OCIE1B);
}

void setup() {
  Serial.begin(115200);
  cli();
  initADC();
  initTimer(); 
  sei();      
}

void loop() {
//  while(ADCSRA & _BV(ADIE)); // Wait for audio sampling to finish

  
  if (sample != -1) {
    Serial.println(sample);
    sample = -1;
//    ADCSRA |= _BV(ADIE);       // Resume sampling interrupt
  }
}

ISR(ADC_vect) { 
  sample = ADC; 
  
//  if(samplePos >= SAMPLES_SIZE) {
//    ADCSRA &= ~_BV(ADIE); // Buffer full, interrupt off
//  }
}

//ISR (TIMER1_COMPB_vect) {
//  
//}
