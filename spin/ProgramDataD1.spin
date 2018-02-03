CON

  IOPIN      = 15
  NUMPIXELS  = 8
  BITSTOSEND = 24
  NUMGLOBALS = 4

  OFS_EVENTINPUT = 0
  OFS_PALETTE    = OFS_EVENTINPUT + 32
  OFS_PATTERNS   = OFS_PALETTE    + 64*4
  OFS_STACK      = OFS_PATTERNS   + 16*2
  OFS_VARIABLES  = OFS_STACK      + 64*2
  OFS_PIXBUF     = OFS_VARIABLES  + NUMGLOBALS*2
  OFS_EVENTTAB   = OFS_PIXBUF     + NUMPIXELS

  INIT_PC        = OFS_EVENTTAB   + 16
  
pub getProgram
  return @zoeProgram

DAT
zoeProgram

'eventInput
  byte 0,0,0,0,0,0,0,0,  0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,  0,0,0,0,0,0,0,0

'palette  ' 64 longs (64 colors)
  long 0,0,$050805,0,0,0,0,0,0,0,0,0,0,0,0,0
  long 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
  long 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
  long 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0

'patterns ' 16 pointers
  word 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0

'callstack ' 64 words (slots)
  word 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
  word 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
  word 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
  word 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0

'variables ' Variable storage (2 bytes each)
  word 3,4,5,6

'pixbuffer ' 1 byte per pixel
  byte 0,0,0,0,0,0,0,0

'eventTable
  byte "COOL",   0, $00,$10
  byte "THROB",  0, $00,$F3
  byte 0

'INIT_handler
  byte $0B,   $80,03,   0,2     ' SET(pixel=0, color=2) 
  byte $08,   $03,$E8        ' PAUSE(time=$3E8 1000ms)
  byte $0B,   $80,03,   0,0     ' SET(pixel=0, color=0)
  byte $08,   $03,$E8        ' PAUSE(time=$3E8 1000ms)

  byte $03,   $FF, $ED       ' GOTO -19