# Overview

The Zoe board runs four independent strips of NeoPixels. Each strip is an independent program running on a separate CPU on the Zoe board. These programs are written in the ```Zoe``` language and compiled to bytecodes for the custom ```ZoeProcessor.spn``` virtual machine.

The RoboRIO sends event strings to the processor board through a serial connection. An event string is the name of a function in the Zoe code. The string is delivered to each of the four running programs. If a program has a function with the same name then the program stops its current routine and jumps to the new routine. All four programs can respond to the same event.

Each program must have a function named "init" that runs when the Zoe board powers up.

You can edit Zoe programs in your favorite IDE.

The ```Zoe.java``` compiler reads text files **D1.zoe**, **D2.zoe**, **D3.zoe**, and **D4.zoe** to produce code for the propeller chip: **ProgramDataD1.spin**, **ProgramDataD2.spin**, **ProgramDataD3.spin**, and **ProgramD4.spin**.

These spin files are then built and downloaded to the Zoe board by the commercial Propeller tool.

We use our HTML page to visually program/simulate pixels on the robot. The page provides four graphical representations and four edit areas for the four areas.

# ZoeProcessor Virtual Machine

The ZoeProcessor reads bytes from a binary data area. Each binary program has a header structured as follows:

```
DD LL WW NN       ; 4 bytes of configuration info: out=DD, length=LL, hasWhite=WW, NN=numberOfVariables
.....             ; 32 byte buffer for event injection (1st byte is the trigger ... set it last)
.....             ; 64*4 bytes: The pixel color palette
.....             ; 16*2 bytes: 16 pointers to pixel patterns
.....             ; 32*2 bytes: 32 slots for the program call stack
.....             ; LL bytes: Pixel drawing buffer. One byte per pixel (LL pixels)
.....             ; NN*2: Two bytes of storage for each variable
```

The header is followed by the event pointer table. Each entry is a null terminated string and a two-byte pointer to the code function. The table is terminated with 0.

For instance:
```
"INIT" 00 PP PP
"START" 00 PP PP
00
```

Commands manipulate the program's memory (in the header). Commands run one after the other without stopping -- excpet for PAUSE.

The PAUSE command first redraws the pixel strip. Then it pauses the program for the given time.

Remember: the only time the pixels are redrawn is at the beginning of the PAUSE command. If your program has no PAUSEs, then nothing will be drawn on the pixels.

** Zoe Virtual Machine Byte Codes **
```
OP is either a constant or variable:
T0000000_VVVVVVVV_VVVVVVVV
T = 1 for variable or 0 for constant

01 NN OP           ; Variable assignment: [NN] = OP
02 NN OP mm OP     ; Math expression: [NN] = OP operator OP (mm: 0=+, 1=-, 2=*, 3=/, 4=%) 
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
