CON

  IOPIN      = %00000000_00000000_10000000_00000000 
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
  long 0,$000800,$000008,$080000,$080808,0,0,0,0,0,0,0,0,0,0,0
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
  word 0,0,0,0

'pixbuffer ' 1 byte per pixel
  byte 0,0,0,0,0,0,0,0

'eventTable
  byte "COOL",   0, $00,$10
  byte "THROB",  0, $00,$F3
  byte 0









' ## Function init
    byte $04,$01                 '# reslocal(1)
    byte $0A,$00,$05,$07,$01,$01,$01,$01,$01,$01,$00,$00,$00,$00,$01,$00,$00,$00,$00,$00,$01,$01,$01,$00,$00,$00,$00,$00,$01,$00,$00,$00,$00,$01,$01,$01,$01,$01,$01 '# defPattern(0,5,7)111111....1.....111.....1....111111
    byte $09,$00,$02,$00,$00,$00,$20,$00,$20,$00,$00 '     defColor(2,32,32,0)
' __do1_START:
    byte $03,$00,$29             '# goto __do1_CONTINUE
' __do1_TOP:
    byte $01,$00,$00,$81,$00     '# a=0
' __do2_START:
    byte $01,$00,$00,$81,$00     '# a=0
    byte $03,$00,$14             '# goto __do2_CHECK
' __do2_TOP:
    byte $0C,$00,$02             '              setSolid(2)        
    byte $08,$03,$E8             '              PAUSE(1000)
    byte $0C,$00,$00             '              setSolid(0)
    byte $08,$03,$E8             '              PAUSE(1000)
' __do2_CONTINUE:
    byte $02,$81,$00,$00,$01,$20,$81,$00 '# a=a+1
' __do2_CHECK:
    byte $07,$FF,$E4,$81,$00,$0C,$00,$08 '# if(a<8)then__do2_TOP
' __do2_BREAK:
' __do1_CONTINUE:
    byte $07,$FF,$CF,$00,$01,$0A,$00,$01 '# if(true)then__do1_TOP
' __do1_BREAK:
    byte $06                     '# return































'INIT_handler

  byte $05,   2, $80,$01, 0,4,   0,11  ' CALL +11
  byte $05,   2, $00,$03, 0,4,   0,3   ' CALL +3 
  byte $03,   $FF,$ED              ' GOTO -19

  byte $04, 10               ' RESLOCAL(num=10)
  byte $0B,   $81,$00,  0,2  ' SET(pixel=0, color=2) 
  byte $08,   $03,$E8        ' PAUSE(time=$3E8 1000ms)
  byte $0B,   $81,$00,  0,0  ' SET(pixel=0, color=0)
  byte $08,   $03,$E8        ' PAUSE(time=$3E8 1000ms)
  byte $06                   ' RETURN