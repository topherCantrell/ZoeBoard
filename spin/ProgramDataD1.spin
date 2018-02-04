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
    byte $01,$00,$08,$80,$01    '    masterPix = 8
    byte $02,$80,$01,$00,$04,$01,$80,$00'    masterColor = masterPix/4
    byte $02,$80,$01,$00,$01,$21,$80,$01'    masterPix = masterPix - 1
' here:
    byte $0B,$80,$01,$80,$00    '    setPixel(masterPix,masterColor)
    byte $08,$03,$E8            '    PAUSE(1000)
    byte $0B,$80,$01,$00,$00    '    setPixel(masterPix,0)
    byte $08,$03,$E8            '    PAUSE(1000)
    byte $03,$FF,$ED            '    goto here






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