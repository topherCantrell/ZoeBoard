CON
  _clkmode        = xtal1 + pll16x
  _xinfreq        = 5_000_000

{{
           
}}

CON
    PIN_D1 = 15
    PIN_D2 = 14
    PIN_D3 = 13
    PIN_D4 = 12

OBJ    

    ZOEProc1 : "ZoeProcessor"
    ZOEProc2 : "ZoeProcessor"
    ZOEProc3 : "ZoeProcessor"
    ZOEProc4 : "ZoeProcessor" 
    PST      : "Parallax Serial Terminal"
    PRG1     : "ProgramDataD1"
    PRG2     : "ProgramDataD1"
    PRG3     : "ProgramDataD1"
    PRG4     : "ProgramDataD1"      

var
    byte buffer[1024]
    
PUB Main | p 

  ' Go ahead and drive the pixel data lines low.
  dira[PIN_D1] := 1
  outa[PIN_D1] := 0
  dira[PIN_D2] := 1
  outa[PIN_D2] := 0
  dira[PIN_D3] := 1
  outa[PIN_D3] := 0
  dira[PIN_D4] := 1
  outa[PIN_D4] := 0

  p := PRG1.getProgram
  ZOEProc1.init(p,PRG1#IOPIN,PRG1#NUMPIXELS,PRG1#BITSTOSEND,p+PRG1#OFS_EVENTINPUT,p+PRG1#OFS_PALETTE,p+PRG1#OFS_PATTERNS,p+PRG1#OFS_STACK,p+PRG1#OFS_VARIABLES,p+PRG1#OFS_PIXBUF,p+PRG1#OFS_EVENTTAB,p+PRG1#OFS_CODE,p+PRG1#OFS_PC)

  p := PRG2.getProgram
  ZOEProc2.init(p,PRG2#IOPIN,PRG2#NUMPIXELS,PRG2#BITSTOSEND,p+PRG2#OFS_EVENTINPUT,p+PRG2#OFS_PALETTE,p+PRG2#OFS_PATTERNS,p+PRG2#OFS_STACK,p+PRG2#OFS_VARIABLES,p+PRG2#OFS_PIXBUF,p+PRG2#OFS_EVENTTAB,p+PRG2#OFS_CODE,p+PRG2#OFS_PC)

  p := PRG3.getProgram
  ZOEProc3.init(p,PRG3#IOPIN,PRG3#NUMPIXELS,PRG3#BITSTOSEND,p+PRG3#OFS_EVENTINPUT,p+PRG3#OFS_PALETTE,p+PRG3#OFS_PATTERNS,p+PRG3#OFS_STACK,p+PRG3#OFS_VARIABLES,p+PRG3#OFS_PIXBUF,p+PRG3#OFS_EVENTTAB,p+PRG3#OFS_CODE,p+PRG3#OFS_PC)

  p := PRG4.getProgram
  ZOEProc4.init(p,PRG4#IOPIN,PRG4#NUMPIXELS,PRG4#BITSTOSEND,p+PRG4#OFS_EVENTINPUT,p+PRG4#OFS_PALETTE,p+PRG4#OFS_PATTERNS,p+PRG4#OFS_STACK,p+PRG4#OFS_VARIABLES,p+PRG4#OFS_PIXBUF,p+PRG4#OFS_EVENTTAB,p+PRG4#OFS_CODE,p+PRG4#OFS_PC)
    
  PauseMSec(1000)

  PST.Start(115200) ' For programming port
  'PST.StartRxTx(0,1,0,115200) ' roboRIO

  repeat
    PST.StrIn(@buffer)            ' Expected a function name (CR/LF ignored)
    ZOEProc1.fireEvent(@buffer)
    ZOEProc2.fireEvent(@buffer)
    ZOEProc3.fireEvent(@buffer)
    ZOEProc4.fireEvent(@buffer)

PRI fireEvent(buf,ptr) | p,c
  p:=ptr+1              ' Copy past the trigger
  repeat
    c := byte[buf]
    if c> 96
      c := c - 32
    byte[p] := c
    if byte[buf] == "]" ' Reached the terminator?
      byte[p+1] :=0     ' YES ... Terminate the buffer
      byte[ptr] :=1     ' YES ... Trigger the event
      return            ' YES ... Out
    p := p +1           ' NO ... increment ...
    buf := buf + 1      ' ... pointers
  
PRI PauseMSec(Duration)
  waitcnt(((clkfreq / 1_000 * Duration - 3932) #> 381) + cnt)  