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
DD LL WW NN       ; 4    bytes: Configuration info: out=DD, length=LL, hasWhite=WW, NN=numberOfVariables
.....             ; 32   bytes: Buffer for event injection (1st byte is the trigger ... set it last)
.....             ; 64*4 bytes: The pixel color palette
.....             ; 16*2 bytes: 16 pointers to pixel patterns
.....             ; 64*2 bytes: Call stack
.....             ; NN*2 bytes: Two bytes of storage for each variable
.....             ; 2    bytes: Initial program counter
.....             ; LL   bytes: Pixel drawing buffer. One byte per pixel (LL pixels)

```

The header is followed by the event pointer table. Each entry is a null terminated string and a two-byte pointer to the code function. The table is terminated with 0.

For instance:
```
"FireCube" 00 PP PP
"DoStuff" 00 PP PP
00
```

Commands manipulate the program's memory (in the header). Commands run one after the other without stopping -- excpet for PAUSE.

The PAUSE command first redraws the pixel strip. Then it pauses the program for the given time.

Remember: the only time the pixels are redrawn is at the beginning of the PAUSE command. If your program has no PAUSEs, then nothing will be drawn on the pixels.

**Zoe Virtual Machine Byte Codes**

OP: Command operands are 2 bytes. If the most significant bit is set then the operand is a variable reference (see below). 

If the most significant bit is a zero then the operand is a 15 bit signed constant. 

Yes -- constants are only 15 bits allowing the upper bit to distinguish variable access. They are 15 bit signed values. 
Bit 14 is extended to bit 15 to make a 16 bit signed value when used.

Constant OP: ```0_svvvvvv vvvvvvvv```

Variable reference OP: ```1_000tttt iiiiiiii```

Where tttt is the variable type (see below) and iiiiiiii is the variable index 0-255.

tttt:
  - 0000 i is a global variable reference
  - 0001 i is a stack variable reference
  - 0010 reference the outgoing return value (returned to the calling function). i is ignored.
  - 0011 reference the incoming return value (returned by the last function called). i is ignored.

```
01 OP OP            ; Variable assignment: [OP] = OP
02 OP OP MM OP      ; Math expression: [OP] = OP operator OP (mm: 0=+, 1=-, 2=*, 3=/, 4=%)
03 PP PP            ; GOTO location=PPPP (16 bit signed offset)
04 PP PP NN LL ..   ; CALL location=PPPP (16 bit signed offset), NN=number of values passed, 
                    ; LL=number of local variables, ..= value words ..
05                  ; RETURN
O6 OP NN OP         ; IF(OP logic OP) logic: 0=<=, 1=>=, 2===, 3=!=, 4=<, 5=>

07 OP               ; PAUSE time=OP
08 OP OP OP OP OP   ; DEFINECOLOR color=OP, w=OP, r=OP, g=OP, b=OP
09 OP NN WW HH ..   ; DEFPATTERN number=OP, length=NN, width height data ..
0A OP OP            ; SETPIXEL pixel=OP, color=OP
0B OP               ; SETSOLID color=OP
0C OP OP OP OP      ; DRAWPATTERN x=OP, y=OP, pattern=OP, colorOffset=OP
```
