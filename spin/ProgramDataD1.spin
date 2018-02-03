pub getProgram
  return @zoeProgram

DAT
zoeProgram

'config
  byte 15 ' IO Pin (D1)
  byte 8  ' Num pixels
  byte 24 ' RGB (no white)
  byte 4  ' 4 global variables

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
  word 0,0,0,0

' Initial program counter (offset from end of header)
  word 16

'pixbuffer ' 1 byte per pixel
  byte 0,0,0,0,0,0,0,0

' --------

'events
  byte "COOL",   0, $00,$10
  byte "THROB",  0, $00,$F3
  byte 0

'INIT_handler
  byte $0B,   0,1,   0,2     ' SET(pixel=0, color=2) 
  byte $08,   $03,$E8        ' PAUSE(time=$3E8 1000ms)
  byte $0B,   0,1,   0,0     ' SET(pixel=0, color=0)
  byte $08,   $03,$E8        ' PAUSE(time=$3E8 1000ms)

  byte $03,   $FF, $ED       ' GOTO -19
