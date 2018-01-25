# Overview

The Zoe board runs four independent strips of NeoPixels. Each strip is an independent program running on a separate CPU on the Zoe board. These programs are written in the ```Zoe``` language and compiled to bytecodes for the custom ```ZoeProcessor.spn``` virtual machine.

The RoboRIO sends event strings to the processor board through a serial connection. An event string is the name of a function in the Zoe code. The string is delivered to each of the four running programs. If a program has a function with the same name then the program stops its current routine and jumps to the new routine. All four programs can respond to the same event.

Each program must have a function named "init" that runs when the Zoe board powers up.

You can edit Zoe programs in your favorite IDE.

The ```Zoe.java``` compiler reads text files **D1.zoe**, **D2.zoe**, **D3.zoe**, and **D4.zoe** to produce code for the propeller chip: **ProgramDataD1.spin**, **ProgramDataD2.spin**, **ProgramDataD3.spin**, and **ProgramD4.spin**.

These spin files are then built and downloaded to the Zoe board by the commercial Propeller tool.

We use our HTML page to visually program/simulate pixels on the robot. The page provides four graphical representations and four edit areas for the four areas.

# ZoeProcessor Virtual Machine

```
Commands manipulate memory. The PAUSE command initiates a refresh before the pause. This allows the
pixel streaming code to reside within the same cog.

-- HEADER AREA --

DD LL WW NN        ; Configuration: out=DD, length=LL, hasWhite=WW, NN=numberOfVariables
.. .. .. ..        ; 32 bytes for event text input (1st byte is the trigger)
.. .. .. ..        ; Color palette (64x4 bytes) (word aligned)
.. .. .. ..        ; 16 two-byte pointers to patterns
.. .. .. ..        ; 32 call-stack slots
.. .. .. ..        ; Pixel buffer (1 byte per pixel for LL pixels)
.. .. .. ..        ; Two bytes for each variable
"INIT" 00 PP PP    ; Event text, null terminator, pointer
"PARTY" 00 PP PP   ; Event text, null terminator, pointer
00                 ; End of event list

-- OPCODES --

OP is either a constant or variable:
T0000000_VVVVVVVV_VVVVVVVV
T = 1 for variable or 0 for constant
V = constant (16 bit signed) or variable number

01 NN OP           ; [NN] = OP
02 NN OP mm OP     ; [NN] = OP operator OP (mm: 0=+, 1=-, 2=*, 3=/, 4=%) 
03 OP              ; PAUSE time=OP
04 OP OP OP OP OP  ; DEFINECOLOR color=OP, w=OP, r=OP, g=OP, b=OP
05 OP              ; STRIP.SOLID color=OP
06 OP OP           ; STRIP.SET pixel=OP, color=OP
07 OP NN WW HH ..  ; PATTERN number=OP, length=NN, width height data ..
08 OP OP OP OP     ; STRIP.PATTERN x=OP, y=OP, pattern=OP, colorOffset=OP
09 PP PP           ; GOTO location=PPPP
0A PP PP           ; GOSUB location=PPPP
0B                 ; RETURN
OC OP nn OP        ; IF(OP logic OP) logic: 0=<=, 1=>=, 2===, 3=!=, 4=<, 5=>
```
