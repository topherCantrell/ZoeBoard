CON

  IOPIN   = %00000000_00000000_00010000_00000000
  NUMPIXELS      = 147
  BITSTOSEND     = 24
  NUMGLOBALS     = 0
  TABSIZE        = 1

  OFS_EVENTINPUT = 0
  OFS_PALETTE    = OFS_EVENTINPUT + 32
  OFS_PATTERNS   = OFS_PALETTE    + 64*4
  OFS_STACK      = OFS_PATTERNS   + 16*2
  OFS_VARIABLES  = OFS_STACK      + 64*2
  OFS_PIXBUF     = OFS_VARIABLES  + NUMGLOBALS*2
  OFS_EVENTTAB   = OFS_PIXBUF     + NUMPIXELS
  OFS_CODE       = OFS_EVENTTAB   + TABSIZE

  OFS_PC         = OFS_CODE + 0

pub getProgram
  return @zoeProgram

DAT
zoeProgram
'eventInput
  byte 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0

'palette  ' 64 colors)  
  long 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
  long 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
  long 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
  long 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0

'patterns ' 16 pointers
  word 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0

'callstack ' 64 slots
  word 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0

'variables ' 0 variables

'pixelbuffer ' 147 pixels
  byte 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
  byte 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
  byte 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0

'eventTable (offsets from start of code)
  byte 0

' Code

' ## Function init
    byte $09,$00,$00,$00,$00,$00,$00,$00,$00,$00,$00 '     defColor(0, 0,0,0)   
    byte $09,$00,$01,$00,$00,$00,$00,$00,$00,$00,$14 '     defColor(1, 0,0,20)    
    byte $09,$00,$02,$00,$00,$00,$14,$00,$00,$00,$00 '     defColor(2, 0,20,0)
' here:
    byte $0B,$00,$00,$00,$01     '     setPixel(0,1)
    byte $0B,$00,$01,$00,$02     '     setPixel(1,2)
    byte $08,$03,$E8             '     PAUSE(1000)
    byte $0B,$00,$00,$00,$02     '     setPixel(0,2)
    byte $0B,$00,$01,$00,$01     '     setPixel(1,1)
    byte $08,$03,$E8             '     PAUSE(1000)
    byte $03,$FF,$E3             '     goto here    
    byte $06                     '# return

