CON

  IOPIN   = %00000000_00000000_10000000_00000000
  NUMPIXELS      = 96
  BITSTOSEND     = 24
  NUMGLOBALS     = 1
  TABSIZE        = 23

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

PUB fireEvent(buf) | p,c, f

  f := 0            ' First (trigger) character (none yet)
  p := @zoeProgram  ' Buffer to fill

  repeat
    c := byte[buf]            ' Next byte from the incoming string
    if c==0                   ' If this is the end of the string ...
      byte[p] := 0            '   terminate the event string
      byte[@zoeProgram] := f  '   trigger the event
      return                  '   done
    if c>31                   ' Ignore line feeds and such
      if f==0                 '   if this is the first character in the string ...
        f := c                '     hold onto it to be the trigger value
      else                    '   otherwise
        byte[p] := c          '     copy the character to the event string
      p := p + 1              '   Either way ... next in the event string
    buf := buf + 1            ' Whether good or not ... advance the input string pointer

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

'variables ' 1 variables
  word 0

'pixelbuffer ' 96 pixels
  byte 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
  byte 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0

'eventTable (offsets from start of code)
  byte "PULL_IN", 0, $00, $6E
  byte "SHOOT_OUT", 0, $00, $6F
  byte 0

' Code

' ## Function init
    byte $04,$01                 '# reslocal(1)
    byte $0A,$00,$05,$07,$01,$01,$01,$01,$01,$01,$00,$00,$00,$00,$01,$00,$00,$00,$00,$00,$01,$01,$01,$00,$00,$00,$00,$00,$01,$00,$00,$00,$00,$01,$01,$01,$01,$01,$01 '# defPattern(0,5,7)111111....1.....111.....1....111111
    byte $09,$00,$02,$00,$00,$00,$20,$00,$20,$00,$00 '     defColor(2,32,32,0)
    byte $0B,$00,$00,$00,$00     '     setPixel(0,0)
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

' ## Function PULL_IN
    byte $06                     '# return

' ## Function SHOOT_OUT
    byte $06                     '# return

