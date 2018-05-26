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
    byte $09,$00,$00,$00,$00,$00,$00,$00,$00,$00,$00 '     defColor(0,  0,0,0)    
    byte $09,$00,$01,$00,$00,$00,$50,$00,$50,$00,$00 '     defColor(1,  80,80,0)
    byte $09,$00,$02,$00,$00,$00,$4B,$00,$4B,$00,$00 '     defColor(2,  75,75,0)
    byte $09,$00,$03,$00,$00,$00,$46,$00,$46,$00,$00 '     defColor(3,  70,70,0)
    byte $09,$00,$04,$00,$00,$00,$41,$00,$41,$00,$00 '     defColor(4,  65,65,0)
    byte $09,$00,$05,$00,$00,$00,$3C,$00,$3C,$00,$00 '     defColor(5,  60,60,0)
    byte $09,$00,$06,$00,$00,$00,$37,$00,$37,$00,$00 '     defColor(6,  55,55,0)
    byte $09,$00,$07,$00,$00,$00,$32,$00,$32,$00,$00 '     defColor(7,  50,50,0)
    byte $09,$00,$08,$00,$00,$00,$2D,$00,$2D,$00,$00 '     defColor(8,  45,45,0)
    byte $09,$00,$09,$00,$00,$00,$28,$00,$28,$00,$00 '     defColor(9,  40,40,0)
    byte $09,$00,$0A,$00,$00,$00,$23,$00,$23,$00,$00 '     defColor(10, 35,35,0)
    byte $09,$00,$0B,$00,$00,$00,$1E,$00,$1E,$00,$00 '     defColor(11, 30,30,0)
    byte $09,$00,$0C,$00,$00,$00,$19,$00,$19,$00,$00 '     defColor(12, 25,25,0)
    byte $09,$00,$0D,$00,$00,$00,$14,$00,$14,$00,$00 '     defColor(13, 20,20,0)
    byte $09,$00,$0E,$00,$00,$00,$0F,$00,$0F,$00,$00 '     defColor(14, 15,15,0)
    byte $09,$00,$0F,$00,$00,$00,$0A,$00,$0A,$00,$00 '     defColor(15, 10,10,0)    
' here:
    byte $0C,$00,$01             '     	setSolid(1)
    byte $08,$00,$64             '     	PAUSE(100)
    byte $0C,$00,$02             '     	setSolid(2)
    byte $08,$00,$64             '     	PAUSE(100)
    byte $0C,$00,$03             '     	setSolid(3)
    byte $08,$00,$64             '     	PAUSE(100)
    byte $0C,$00,$04             '     	setSolid(4)
    byte $08,$00,$64             '     	PAUSE(100)
    byte $0C,$00,$05             '     	setSolid(5)
    byte $08,$00,$64             '     	PAUSE(100)
    byte $0C,$00,$06             '     	setSolid(6)
    byte $08,$00,$64             '     	PAUSE(100)
    byte $0C,$00,$07             '     	setSolid(7)
    byte $08,$00,$64             '     	PAUSE(100)
    byte $0C,$00,$08             '     	setSolid(8)
    byte $08,$00,$64             '     	PAUSE(100)
    byte $0C,$00,$09             '     	setSolid(9)
    byte $08,$00,$64             '     	PAUSE(100)
    byte $0C,$00,$0A             '     	setSolid(10)
    byte $08,$00,$64             '     	PAUSE(100)
    byte $0C,$00,$0B             '     	setSolid(11)
    byte $08,$00,$64             '     	PAUSE(100)
    byte $0C,$00,$0D             '     	setSolid(13)
    byte $08,$00,$64             '     	PAUSE(100)
    byte $0C,$00,$0E             '     	setSolid(14)
    byte $08,$00,$64             '     	PAUSE(100)
    byte $0C,$00,$0F             '     	setSolid(15)
    byte $08,$00,$64             '     	PAUSE(100)
    byte $0C,$00,$0E             '     	setSolid(14)
    byte $08,$00,$64             '     	PAUSE(100)
    byte $0C,$00,$0D             '     	setSolid(13)
    byte $08,$00,$64             '     	PAUSE(100)
    byte $0C,$00,$0C             '     	setSolid(12)
    byte $08,$00,$64             '     	PAUSE(100)
    byte $0C,$00,$0B             '     	setSolid(11)
    byte $08,$00,$64             '     	PAUSE(100)
    byte $0C,$00,$0A             '     	setSolid(10)
    byte $08,$00,$64             '     	PAUSE(100)
    byte $0C,$00,$09             '     	setSolid(9)
    byte $08,$00,$64             '     	PAUSE(100)
    byte $0C,$00,$08             '     	setSolid(8)
    byte $08,$00,$64             '     	PAUSE(100)
    byte $0C,$00,$07             '     	setSolid(7)
    byte $08,$00,$64             '     	PAUSE(100)
    byte $0C,$00,$06             '     	setSolid(6)
    byte $08,$00,$64             '     	PAUSE(100)
    byte $0C,$00,$05             '     	setSolid(5)
    byte $08,$00,$64             '     	PAUSE(100)
    byte $0C,$00,$04             '     	setSolid(4)
    byte $08,$00,$64             '     	PAUSE(100)
    byte $0C,$00,$03             '     	setSolid(3)
    byte $08,$00,$64             '     	PAUSE(100)
    byte $0C,$00,$02             '     	setSolid(2)
    byte $08,$00,$64             '     	PAUSE(100)
    byte $03,$FF,$5B             '     	goto here
    byte $06                     '# return

