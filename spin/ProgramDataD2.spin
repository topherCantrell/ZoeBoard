CON

  IOPIN   = %00000000_00000000_01000000_00000000
  NUMPIXELS      = 192
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

'pixelbuffer ' 192 pixels
  byte 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
  byte 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
  byte 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0

'eventTable (offsets from start of code)
  byte 0

' Code

' ## Function init
    byte $09,$00,$00,$00,$00,$00,$00,$00,$00,$00,$00 '     defColor(0, 0,0,0)     
    byte $09,$00,$01,$00,$00,$00,$00,$00,$78,$00,$00 '     defColor(1, 120,0,0)    
    byte $09,$00,$02,$00,$00,$00,$78,$00,$00,$00,$78 '     defColor(2, 0,120,120)
    byte $09,$00,$03,$00,$00,$00,$00,$00,$00,$00,$00 '     defColor(3, 0,0,0)     
    byte $09,$00,$04,$00,$00,$00,$78,$00,$00,$00,$78 '     defColor(4, 0,120,120)
    byte $09,$00,$05,$00,$00,$00,$00,$00,$78,$00,$00 '     defColor(5, 120,0,0)     
    byte $0A,$00,$04,$05,$01,$01,$01,$01,$01,$00,$00,$00,$01,$01,$01,$01,$00,$00,$00,$01,$01,$01,$01,$01 '# defPattern(0,4,5)11111...1111...11111
    byte $0A,$01,$04,$05,$02,$02,$02,$02,$02,$00,$00,$02,$02,$02,$02,$02,$02,$00,$00,$02,$02,$02,$02,$02 '# defPattern(1,4,5)22222..222222..22222
' here:
    byte $0C,$00,$00             '     setSolid(0)    
    byte $0D,$00,$00,$00,$02,$00,$01,$00,$00 '     drawPattern(0, 2,1,0)
    byte $0D,$00,$01,$00,$07,$00,$01,$00,$00 '     drawPattern(1, 7,1,0)
    byte $0D,$00,$00,$00,$0C,$00,$01,$00,$00 '     drawPattern(0,12,1,0)
    byte $0D,$00,$01,$00,$11,$00,$01,$00,$00 '     drawPattern(1,17,1,0)    
    byte $08,$01,$F4             '     PAUSE(500)
    byte $0C,$00,$00             '     setSolid(0)
    byte $0C,$00,$00             '     setSolid(0)    
    byte $0D,$00,$00,$00,$02,$00,$01,$00,$03 '     drawPattern(0, 2,1,3)
    byte $0D,$00,$01,$00,$07,$00,$01,$00,$03 '     drawPattern(1, 7,1,3)
    byte $0D,$00,$00,$00,$0C,$00,$01,$00,$03 '     drawPattern(0,12,1,3)
    byte $0D,$00,$01,$00,$11,$00,$01,$00,$03 '     drawPattern(1,17,1,3)    
    byte $08,$01,$F4             '     PAUSE(500)
    byte $03,$FF,$A6             '     goto here   
    byte $06                     '# return

