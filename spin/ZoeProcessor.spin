pub init(ioPin,numPixels,bitsToSend,ofs_eventInput,ofs_palette,ofs_patterns,ofs_stack,ofs_variables,ofs_pixBuf,ofs_eventTab,ofs_pc)
'' Start the NeoPixel driver cog

   pn             := ioPin
   par_pixCount   := numPixels
   numBitsToSend  := bitsToSend
   eventInput     := ofs_eventInput
   par_palette    := ofs_palette
   patterns       := ofs_patterns
   resetStackPtr  := ofs_stack
   variables      := ofs_variables
   par_buffer     := ofs_pixBuf
   events         := ofs_eventTab
   programCounter := ofs_pc    
         
   return cognew(@ZoeCOG,0)
   
DAT          
        org 0

ZoeCOG
        or      dira, pn                 ' Set output pin
        jmp     #command                 ' Start the init function
        
' -------------------------------------------------------------------------------------------------
                 
mainLoop
        rdbyte  c,eventInput wz          ' A new event?        
  if_nz jmp     #doEvent                 ' Yes ... go start it                
        mov     c, ONE_MSEC              ' Time to kill for 1 MSec
        add     c, cnt                   ' Offset from now
        waitcnt c,c                      ' Wait for 1 MSec  
        djnz    pauseCounter, #mainLoop  ' Wait all 1MSEC tics

command 
        rdbyte  c,programCounter         ' Next ...         
        add     programCounter,#1        ' ... opcode
        cmp     c,#13 wz,wc              ' Valid opcode?
  if_a  mov     c,#0                     ' No ... show the error
        add     c,#comTable              ' Offset into COM table
        jmp     c                        ' Execute the command

' -------------------------------------------------------------------------------------------------
ReadWord
' In case the pointer isn't word-aligned
'  
' @param programCounter where to read from (incremented here)
' @return tmp the word
' @mangles tmp2
'
        rdbyte  tmp,programCounter       ' Read ...
        add     programCounter,#1        ' ... MSB
        shl     tmp,#8                   ' Into position
        rdbyte  tmp2,programCounter      ' Read ...
        add     programCounter,#1        ' ... LSB
        or      tmp,tmp2                 ' Result is in tmp
ReadWord_ret
        ret                                   
        
' -------------------------------------------------------------------------------------------------    
GetParam
' Read the value of the operand at the program counter
'
' @param programCounter where to read from (incremented here)
' @return tmp 32-bit signed value
' @mangles tmp2, t1
'
        call    #GetOpAddr               ' Get the address of the op
  if_z  jmp     #GetParamC               ' This is a constant ... use this value        
        rdword  tmp,tmp                  ' Read the value. These are always word-aligned.
        and     tmp,C_8000 nr, wz        ' Sign bit set?
  if_nz or      tmp,C_FFFF_0000          ' Yes ... extend it to 32 bits       
        jmp     #GetParam_ret            ' Return the value from the variable
GetParamC
        and     tmp,C_4000 nr, wz         ' Sign bit set (different bit for constants)
  if_nz or      tmp,C_FFFF_8000           ' Extend the constant sign bit
GetParam_ret
        ret  

' -------------------------------------------------------------------------------------------------        
GetOpAddr
' Get the address of the operand at the program counter
'
' @param programCounter where to read from (incremented here)
' @return tmp pointer to value's address (or value if it is a constant)
' @return Z set if pointer is the constant value
' @mangles tmp2, t1
'
        call    #ReadWord                ' Get the operand          
        and     tmp, C_8000 nr, wz       ' Upper bit set?   
  if_z  jmp     #GetOpAddr_ret           ' No ... exit with Z set          
' Upper bit is set. This is a variable reference 
        mov     t1, tmp                  ' Type ...         
        shr     t1, #8                   ' ... to ...
        and     t1, #127                 ' ... t1
        and     tmp, #$FF                ' The index is 00 .. FF
' t1 = type:
'    0 = global variable
'    1 = stack variable
'    2 = incoming return (returned by function)
        cmp     t1, #0 wz
 if_z   jmp     #OpGlobal
        cmp     t1, #1 wz
 if_z   jmp     #OpStackVar
        
OpInRet
        mov     tmp,stackPointer         ' Return from ...
        add     tmp,#4                   ' ... the last function called
        jmp     #GetOpAddrNZ             ' Return with Z not set

OpGlobal          
        shl     tmp,#1                   ' Two bytes per global
        add     tmp,variables            ' Offset into global variables
        jmp     #GetOpAddrNZ             ' Return with Z not set

OpStackVar
        shl     tmp, #1                  ' Two bytes per local
        add     tmp,framePointer         ' Offset into local variables

GetOpAddrNZ
        or      tmp,#1 nr, wz            ' Clear the Z flag
GetOpAddr_ret
        ret  

' -------------------------------------------------------------------------------------------------             

comINVALID
        mov     c,#%11110101             ' Unknown ...        
        jmp     #HaltShowErrorInC        ' ... opcode
        
comTable
        jmp     #comINVALID              ' 0
        '
        jmp     #comASSIGN               ' 1
        jmp     #comMATH                 ' 2
        jmp     #comGOTO                 ' 3
        jmp     #comRESLOCAL             ' 4
        jmp     #comCALL                 ' 5
        jmp     #comRETURN               ' 6
        jmp     #comIF                   ' 7
        jmp     #comPAUSE                ' 8
        jmp     #comDEFCOLOR             ' 9
        jmp     #comDEFPATTERN           ' 10
        jmp     #comSETPIXEL             ' 11
        jmp     #comSETSOLID             ' 12
        jmp     #comDRAWPATTERN          ' 13

' -------------------------------------------------------------------------------------------------

doEvent
        mov     p,events                 ' Pointer to events
doEvent2
        mov     p2,eventInput            ' Input buffer
        add     p2,#1                    ' Skip over trigger

        rdbyte  c,p wz                   ' Next in event table
        cmp     c,#$FF wz                ' Reached the end of the event table?
  if_nz jmp     #thisWord                ' No ... check the word
        wrbyte  ZERO,eventInput          ' Clear the trigger
        jmp     #mainLoop                ' Wait for next event

nextWord  
        cmp     c,#0 wz                  ' End of the current word in the table?
 if_z   jmp     #ne1                     ' Yes ... get ready for next check
        add     p,#1                     ' No ... read ...
        rdbyte  c,p                      ' ... next from table
        jmp     #nextWord                ' Check for ending now
        
ne1     add     p,#3                     ' Skip terminator plus pointer
        jmp     #doEvent2                ' Next word

thisWord
        rdbyte  c,p                      ' Next from ...
        add     p,#1                     ' ... table
        rdbyte  val,p2                   ' Next from ...          
        add     p2,#1                    ' ... input
        cmp     c,val wz                   ' Are they the same?
  if_nz jmp     #nextWord                ' No ... next table entry  
        cmp     c,#0 wz                  ' Terminator?
  if_nz jmp     #thisWord                ' No ... keep checking

       ' We found an event handler

        mov     programCounter,p            ' Routine reads from programCounter
        call    #ReadWord                   ' Get the entry address
        add     tmp,events                  ' Offset from the event table
        mov     programCounter,tmp          ' Start of event handler
        mov     stackPointer, resetStackPtr ' Reset the stack        
        wrbyte  ZERO,eventInput             ' Clear the trigger        
        jmp     #command                    ' Run till pause

' -------------------------------------------------------------------------------------------------

'-------------------------------------
'signed multiply, taken from spin interpreter source
'-------------------------------------
'http://forums.parallax.com/forums/default.aspx?f=25&m=394199
' 32 to 64 bit signed multiply asm_x by asm_y
' asm_y absvaled, result in t1:asm_x
'
multiply
              abs       asm_x,asm_x  wc                  'abs(x)
              muxc      asm_n,#1                         'store sign of x
              abs       asm_y,asm_y  wc,wz               'abs(y)
        if_c  xor       asm_n,#1                         'store sign of y
              mov       t1,#0
              mov       t2,#32
              shr       asm_x,#1     wc
             
:mloop  if_c  add       t1,asm_y     wc
              rcr       t1,#1        wc
              rcr       asm_x,#1     wc
              djnz      t2,#:mloop
              test      asm_n,#1     wz
        if_nz neg       t1,t1                             'restore sign, upper 32 bits
        if_nz neg       asm_x,asm_x  wz                   'restore sign, lower 32 bits
        if_nz sub       t1,#1
             
multiply_ret  ret  

'-------------------------------------
'Signed divide, taken from spin interpreter
'-------------------------------------
'on entry: numerator in x, denominator/divisor in y
'on exit: division result in y, remainder in x
sdiv32                  abs     asm_x,asm_x     wc       'abs(x)
                        muxc    asm_n,#%11               'store sign of x
                        abs     asm_y,asm_y     wc,wz    'abs(y)
        if_c            xor     asm_n,#%10               'store sign of y

                        mov     t1,#0                    'unsigned divide
                        mov     t2,#32             
mdiv                    shr     asm_y,#1        wc,wz    
                        rcr     t1,#1
        if_nz           djnz    t2,#mdiv
mdiv2                   cmpsub  asm_x,t1        wc
                        rcl     asm_y,#1
                        shr     t1,#1
                        djnz    t2,#mdiv2

                        test    asm_n,#1        wc       'restore sign, remainder
                        negc    asm_x,asm_x              
                        test    asm_n,#%10      wc       'restore sign, division result
                        negc    asm_y,asm_y              
sdiv32_ret              ret
  
' -------------------------------------------------------------------------------------------------
comASSIGN
' OPCODE 01 paramS paramD : SET(VARIABLE=paramD, VALUE=paramS)
        call    #GetParam                     ' Get the ...
store
        mov     t2,tmp                        ' ... source value
        call    #GetOpAddr                    ' Destination
        wrword  t2,tmp                        ' Write the source value to the destination
        jmp     #Command                      ' Keep running commands until PAUSE

' -------------------------------------------------------------------------------------------------
comMATH
' OPCODE 02 paramL paramR oo paramD MATH(paramD = paramL oo paramR)
        call    #GetParam                     ' Get the LEFT operand
        mov     p,tmp                         ' Hold in p
        call    #GetParam                     ' Get the RIGHT operand
        mov     c,tmp                         ' Hold in c
        rdbyte  val,programCounter            ' Get the ...
        add     programCounter,#1             ' ... operation number
        mov     tmp,p                         ' LEFT in tmp, RIGHT in c

        cmp     val,#0 wz                     ' Special case for ...
  if_z  jmp     #opMultiply                   ' ... multiply
        cmp     val,#1 wz                     ' Special case for ...
  if_z  jmp     #opDivide                     ' ... divide
        cmp     val,#2 wz                     ' Special case for ...
  if_z  jmp     #opModulo                     ' ... modulo

        ' Some operations
        '         MUL  00
        '         DIV  01
        '         MOD  02
        ' 10_0000 ADD  20
        ' 10_0001 SUB  21
        ' 10_1001 NEG  19 
        ' 01_1000 AND  18
        ' 01_1010 OR   1A
        ' 01_1011 XOR  1B
        ' 00_1011 SHL  0B
        ' 00_1010 SHR  0A

        shl     val,#3                        ' Shift into INSTR position for MOVI
        or      val,#1                        ' Set R not fpr MOVI
        movi    mathOP,val                    ' Set the math operation
        nop                                   ' Required gap before using the modification
        '
mathOp  add     tmp,c                         ' Do the math
        '
        jmp     #store                        ' Store the result and run till pause

opMultiply
        mov     asm_x,tmp                     ' X ...
        mov     asm_y,c                       ' ... times ...
        call    #multiply                     ' ... Y
        mov     tmp,asm_x                     ' Result in X
        jmp     #store                        ' Store the result and run till pause
opDivide
        mov     asm_x,tmp                     ' X ...
        mov     asm_y,c                       ' ... divided by ...
        call    #sdiv32                       ' ... Y
        mov     tmp,asm_y                     ' Result in Y
        jmp     #store                        ' Store the result and run till pause
opModulo
        mov     asm_x,tmp                     ' X ...
        mov     asm_y,c                       ' ... divided by ...
        call    #sdiv32                       ' ... Y
        mov     tmp,asm_x                     ' Remainder in X
        jmp     #store                        ' Store the result and run till pause
        
' -------------------------------------------------------------------------------------------------
comGOTO
' OPCODE 03 offset : GOTO offset   
        call    #ReadWord                     ' Get the relative offset
doJump  add     programCounter,tmp            ' Add in the jump
        and     programCounter,C_FFFF         ' Mask to a word
        jmp     #command                      ' Run till pause

' -------------------------------------------------------------------------------------------------
comRESLOCAL
' OPCODE 04 PP
        rdbyte  tmp, programCounter           ' Read number ...
        add     programCounter,#1             ' ... of variables to reserve
        shl     tmp,#1                        ' Each variable is two bytes
        add     stackPointer,tmp              ' Make room for the vars on the stack
        jmp     #command                      ' Run till pause

' -------------------------------------------------------------------------------------------------        

' -framepointer-
' -return-
' Param1 (also return) <--Frame pointer
' Param2              (<--This is where SP and FP point for init and events)
' Param3
' Local1
' Local2
' (last framepointer)  <--Stack pointer 
' (last return) 
' (last RetVal)

' Calling a subroutine:
'  - Push the frame pointer
'  - Push the return address program counter
'  - Set frame pointer to current stack ptr
'  - Push the call argument bytes on the stack
'  - Change the program counter
' Returning from a subroutine:
'  - Set stack pointer to frame pointer
'  - Pop the program counter
'  - Pop the frame pointer
                                   
comCALL
' OPCODE 05 PP PP NN ..
              
        wrword  framePointer,stackPointer     ' Save the ...
        add     stackPointer,#2               ' ... frame pointer
        mov     t2,stackPointer               ' This is where the return address goes when we know it
        add     stackPointer,#2               ' Leave space for the return address
        mov     framePointer,stackPointer     ' This is where the parameters/locals start

        rdbyte  p,programCounter wz           ' Get the number ...
        add     programCounter,#1             ' ... of parameters
        jmp     #allParamsChk                 ' "while" loop ... check the condition first
        
allParams        
        call    #GetParam                     ' Get the next operand being passed
        wrword  tmp,stackPointer              ' Push the value ...
        add     stackPointer,#2               ' ... onto stack 
        sub     p,#1 wz                       ' All params done?
allParamsChk
  if_nz jmp     #allParams                    ' No ... go back for all

comCALL_1
        call    #ReadWord                     ' Get the relative offset
        wrword  programCounter,t2             ' This is the return address
        jmp     #doJump                       ' Start the function

' -------------------------------------------------------------------------------------------------
comRETURN
' OPCODE 06

        mov     stackPointer,framePointer
        sub     stackPointer,#2
        rdword  programCounter,stackPointer
        sub     stackPointer,#2
        rdword  framePointer,stackPointer
        jmp     #command

' -------------------------------------------------------------------------------------------------
comIF
' OPCODE 07 PP PP OPL NN OPR : IF(OPL nn OPR) else GOTO PP PP
        call    #ReadWord                     ' Offset to GOTO if expression failed
        mov     p,tmp                         ' Hold the offset in p for GOTO
        call    #GetParam                     ' Left operator
        mov     p2,tmp                        ' Hold in p2
        rdbyte  val,programCounter            ' Get the ...
        add     programCounter,#1             ' ... logic operator
        call    #GetParam                     ' Right operator in tmp
        
        ' Some logic checks:
        ' 1010 ==   0A
        ' 0101 !=   05
        ' 0001 >    01
        ' 1100 <    0C
        ' 0011 >=   03
        ' 1110 <=   0E
                        
        ' No MOVC, so we'll do it manually
        andn    logicOp,C_003C0000            ' 003C0000 iiii_iiff_ffcc_ccdd_dddd_ddds_ssss_ssss
        shl     val,#18                       ' OR in ...
        or      logicOp,val                   ' ... the condition
        '
        cmp     p2,tmp wz, wc                 ' Compare left with right
        '
logicOp jmp     #command                      ' PASSES ... next instruction and run till pause
        '
        mov     tmp,p                         ' Offset if failed
        jmp     #doJump                       ' End of IF block and run till pause
        
' -------------------------------------------------------------------------------------------------
comPAUSE
' OPCODE 08 paramN : PAUSE(TIME=paramN)        
        call    #GetParam                     ' Get the ...
        mov     pauseCounter,tmp              ' ... pause counter value
        call    #UpdateDisplay                ' Draw the display  
        jmp     #mainLoop                     ' Back to wait for pause
        
' ------------------------------------------------------------------------------------------------- 
comDEFCOLOR
' OPCODE 09 param, param, param, param  DEFCOLOR(slot,white,green,red,blue)
        call    #GetParam                ' Get the ...                      
        mov     p,tmp                    ' ... color slot number
        shl     p,#2                     ' Four bytes per slot
        add     p,par_palette            ' Offset into colors
        call    #GetParam                ' Get WHITE
        mov     c, tmp                   ' Accumulate the color here
        mov     p2,#3                    ' 3 more bytes to shift in
getCol  call    #GetParam                ' Get color component GREEN, RED, BLUE
        shl     c,#8                     ' Shift over existing value
        or      c,tmp                    ' OR in this byte
        djnz    p2,#getCol               ' Do all components        
        wrlong  c,p                      ' Write the color slot
        jmp     #command                 ' Run till pause

' ------------------------------------------------------------------------------------------------- 
comDEFPATTERN
' OPCODE 0A nn ww hh .....  PATTERN(num=0,width=3,height=3, ...)
        rdbyte  p,programCounter         ' Pattern slot ...
        add     programCounter,#1        ' ... number
        shl     p,#1                     ' Two byte pointer
        add     p,patterns               ' Offset to pattern pointer
        wrword  programCounter,p         ' Set the pointer of this pattern
        rdbyte  asm_x,programCounter     ' Get the ...
        add     programCounter,#1        ' ... width
        rdbyte  asm_y,programCounter     ' Get the ...
        add     programCounter,#1        ' ... height
        call    #multiply                ' Total bytes in pattern
        add     programCounter,asm_x     ' Skip over pattern
        jmp     #command                 ' Run till pause

' -------------------------------------------------------------------------------------------------        
comSETPIXEL
' OPCODE 0B param param  SET(PIXEL=param,COLOR=param)       

        call    #GetParam                     ' PIXEL=param        
        mov     p,tmp                         ' Hold pixel number
        add     p,par_buffer                  ' Pointer to pixel buffer
        call    #GetParam                     ' COLOR=param        
        wrbyte  tmp,p                         ' Set the pixel value
        jmp     #command                      ' Run till pause

' -------------------------------------------------------------------------------------------------        
comSETSOLID
' OPCODE 0C param  SOLID(color)
        call    #GetParam                ' Get the color number
        mov     p,par_buffer             ' Start of pixel buffer
        mov     c,par_pixCount           ' Number of pixels on strip
doStrip wrbyte  tmp,p                    ' Store color to buffer  
        add     p,#1                     ' Next in buffer
        djnz    c,#doStrip               ' Do all pixels
        jmp     #command                 ' Run till pause
   
' ------------------------------------------------------------------------------------------------- 
comDRAWPATTERN
' OPCODE 0D param param param param  DRAWPATTERN(num=0,x=2,y=4,coffset=0)
        call    #GetParam                ' Get the ...
        mov     p,tmp                    ' ... pattern slot
        shl     p,#1                     ' Two byte pointer
        add     p,patterns               ' Offset to pattern pointer
        rdword  p,p                      ' Get pointer to pattern
        call    #GetParam                ' Get the ...
        mov     asm_x,tmp                ' ... draw X
        call    #GetParam                ' Get the ...
        mov     asm_y,tmp                ' ... draw Y
        call    #GetParam                ' Get the ...
        mov     c,tmp                    ' ... color offset
        '
        rdbyte  width, p                 ' Get the ...
        add     p,#1                     ' ... pattern width
        rdbyte  height,p                 ' Get the ...
        add     p,#1                     ' ... pattern height
        '
        mov     tmp2,height              ' Row counter   
allRows
        mov     tmp,width                ' Column counter
allOfRow
        rdbyte  val,p                    ' Get the pixel value
        add     p,#1                     ' Next in pattern 
        add     val,c                    ' Color offset            
        call    #mapPixel                ' Set the pixel
        add     asm_x,#1                 ' Next X on row
        djnz    tmp,#allOfRow            ' Do all the pixels on the row
        sub     asm_x,width              ' Back up to beginning of row
        add     asm_y,#1                 ' Down to next row
        djnz    tmp2,#allRows            ' Do all the rows
        jmp     #command                 ' Run till pause        

mapPixel
' TODO for now, we'll assume left to right plates. In the future we need a way
' of handling random geometries
        mov     p2,#0                    ' Start at pixel 0
        mov     bitCnt,asm_x             ' We will mangle the X
wplates
        cmp     bitCnt,#8 wz, wc         ' A whole plate to skip?
 if_ae  add     p2,#64                   ' YES ... add in a plate to pix num
 if_ae  sub     bitCnt,#8                ' YES ... subtract a plate from the X
 if_ae  jmp     #wplates                 ' YES ... keep skipping over plates
        mov     pixCnt,asm_y             ' Each row ...
        shl     pixCnt,#3                ' ... is 8 pixels
        add     pixCnt,bitCnt             ' Offset pixel across the row
        add     p2,pixCnt                ' Add in any whole plates
        add     p2,par_buffer            ' Offset into buffer
        wrbyte  val,p2                   ' Write the pixel
mapPixel_ret
        ret  

notOp0C
        mov     c,#%11110001             ' Unknown ...
        jmp     #HaltShowErrorInC        ' ... opcode
                     
' -------------------------------------------------------------------------------------------------
HaltShowErrorInC
' Show 8 bit error value in C and INFINITE LOOP
' Values are shown most significant bit to least starting with 1st pixel on the strand
'
' @param c the 8-bit error code
'
        mov      p, par_palette          ' Set ...
        wrlong   ERRCOLOR0,p             ' ... colors ...
        add      p,#4                    ' ... for ...
        nop                              ' ... error ...
        wrlong   ERRCOLOR1,p             ' ... report
        mov      val,#8                  ' 8 pixels to write
        shl      c,#24                   ' Move to the far left
        mov      p, par_buffer           ' Start of pixel strand
errloop shl      c,#1 wc                 ' Test the next bit
  if_c  wrbyte   ONE,p                   ' If it is a 1 ... light the pixel
  if_nc wrbyte   ZERO,p                  ' If it is a 0 ... turn the pixel off
        add      p,#1                    ' Next in strand
        djnz     val,#errloop            ' Do all pixels
        call     #UpdateDisplay          ' Draw the strand
        '
errinf  jmp      #errinf                 ' INFINITE LOOP

' -------------------------------------------------------------------------------------------------   
UpdateDisplay
        mov     p,par_buffer             ' Start of pixel buffer
        mov     pixCnt, par_pixCount
        
nextPixel        
        rdbyte  c,p                      ' Get the byte value
        add     p,#1                     ' Next pixel value 
doLook  shl     c,#2                     ' * 4 bytes per entry
        add     c,par_palette            ' Offset into palette
        rdlong  val,c                    ' Get pixel value         

        mov     bitCnt,numBitsToSend     ' 32 or 24 bits to move
        cmp     bitCnt, #24 wz           ' Is this a 3 byte value?
  if_z  shl     val, #8                  ' Yes ... ignore the upper 8    

bitLoop
        shl     val, #1 wc               ' MSB goes first
  if_c  jmp     #doOne                   ' Go send one if it is a 1
        call    #sendZero                ' It is a zero ... send a 0
        jmp     #bottomLoop              ' Skip over sending a 1
        '
doOne   call    #sendOne

bottomLoop
        djnz    bitCnt,#bitLoop          ' Do all bits in the pixel done?
        djnz    pixCnt,#nextPixel        ' Do all pixels on the strip
                
        call    #sendDone                ' Latch in the LEDs  

UpdateDisplay_ret
        ret                                

' These timing values were tweaked with an oscope
'        
sendZero                 
        or      outa,pn                  ' Take the data line high
        mov     c,#$5                    ' wait 0.4us (400ns)
loop3   djnz    c,#loop3                 '
        andn    outa,pn                  ' Take the data line low
        mov     c,#$B                    ' wait 0.85us (850ns) 
loop4   djnz    c,#loop4                 '                              
sendZero_ret                             '
        ret                              ' Done
'
sendOne
        or      outa,pn                  ' Take the data line high
        mov     c,#$D                    ' wait 0.8us 
loop1   djnz    c,#loop1                 '                       
        andn    outa,pn                  ' Take the data line low
        mov     c,#$3                    ' wait 0.45us  36 ticks, 9 instructions
loop2   djnz    c,#loop2                 '
sendOne_ret                              '
        ret                              ' Done
'
sendDone
        andn    outa,pn                  ' Take the data line low
        mov     c,C_RES                  ' wait 60us
loop5   djnz    c,#loop5                 '
sendDone_ret                             '
        ret                              ' Done  

' -------------------------------------------------------------------------------------------------
' These 10 variables must be filled out before the COG is started.
' 
pn               long 0          ' Pin number bit mask
par_pixCount     long 0          ' Number of pixels
numBitsToSend    long 0          ' Either 32 bits (RGBW) or 24 bits (RGB)
eventInput       long 0          ' Pointer to event input  
par_palette      long 0          ' Color palette (some commands)
patterns         long 0          ' Pointer to patterns
resetStackPtr    long 0          ' Start of stack space (to reset the stack ptr)
variables        long 0          ' Pointer to variables 
par_buffer       long 0          ' Pointer to the pixel data buffer
events           long 0          ' Pointer to events   
'
pauseCounter     long 0          ' number of tics left in pause   
programCounter   long 0          ' Current PC
stackPointer     long 0          ' Current stack pointer
framePointer     long 0          ' Pointer to local variables on the stack 
'
asm_x            long 0          ' Temporary 
asm_y            long 0          ' Temporary 
asm_n            long 0          ' Temporary 
t1               long 0          ' Temporary 
t2               long 0          ' Temporary 
width            long 0          ' Temporary 
height           long 0          ' Temporary 
c                long 0          ' Temporary
p                long 0          ' Temporary
val              long 0          ' Temporary
p2               long 0          ' Temporary
bitCnt           long 0          ' Temporary for bit shifting
pixCnt           long 0          ' Temporary for pixels in the strip
tmp              long 0          ' Temporary
tmp2             long 0          ' Temporary
'
ONE_MSEC         long 80_000       ' 80_000_000 clocks in a second / 1000 
C_RES            long $4B0         ' Wait count for latching the LEDs
C_4000           long $4000        ' 15-bit sign bit
C_8000           long $8000        ' 16-bit sign bit
C_FFFF_0000      long $FFFF_0000   ' Sign extend
C_FFFF_8000      long $FFFF_8000   ' Sign extend
C_FFFF           long $FFFF        ' Used to mask 2-byte signed numbers
C_003C0000       long $003C0000    ' Mask for SPIN conditional field
'
ONE              long 1            ' Used ... 
ZERO             long 0            ' ... in ...
ERRCOLOR0        long $00_00_00_08 ' ... error ...
ERRCOLOR1        long $00_08_08_08 ' ... reporting

      FIT
      