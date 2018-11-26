const int led_pin = PB5;

const uint16_t t1_load = 0;
const uint16_t t1_comp = 31250;

void setup() {
  cli();
  
  DDRB |= (1 << led_pin);

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
  OCR1A = t1_comp;

// Enable Timer 1 compare interrupt
  TIMSK1 = (1 << OCIE1A);
  
  sei();
}

void loop() {
  
  delay(500);

}

ISR (TIMER1_COMPA_vect) {
  PORTB ^= (1 << led_pin);
}
