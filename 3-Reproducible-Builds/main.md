---
title: Reproducible Builds
author: Paul Nykiel
date: \today
---

# Assignment 1
 * A reproducible builds makes it possible for the end-user to check that the binary that is distributed for a program is build from the source code that is available. 
    This makes it possible to find application with have been tampered with, either by the developer or by a third party. As XCode is not open source it would not have been possible to check XCode this way. Further advantages include a clear definition of the build environment which makes it easier for other users to build the application themselves. One disadvantage is the added complexity of the infrastructure required for reproducible builds and the fact that the user still needs to check the source code to find backdoors or other issues.
 * First of all you need to be sure that the original source code does not contain any malware or options to load malware. This step can be quite difficult for larger applications. Additionally the complete build environment needs to be checked, it is for example possible for the compiler to include malware into the program (see the Ken Thompson Hack).

# Assignment 2
## Version option
The non reproducibility is primarily due to the build-time and -date information in the output. All other information can be made constant, the time not.

##  Making the build reproducible
The modified makefile is:
```Makefile
USER="\"eve\""
DISTRO="\"Arch Linux (Linux 5.6.4-arch1-1 x86_64 )\""
VERSION="\"$(shell cat VERSION)\""

CC=g++
CPPFLAGS=-Wall -O2 -DUSER=$(USER) -DDISTRO=$(DISTRO) -DVERSION=$(VERSION) -D__TIME__="\"11:03:35\"" -D__DATE__="\"May 28 2020\""

main: main.cpp
    $(CC) $(CPPFLAGS) -o main main.cpp
```
It is also possible to use libfaketime instead of settings `__TIME__` and `__DATE__` manually 
(which is not allowed by the C++-Standard and results in warnings during compilation).

## Modified --version
The only really important flag is the version number. For more complex applications it could be helpful to be able to see the compiler and libraries used for building. But for this Hello-World the version number is sufficient.

New `main.cpp`:
```c++
#include <iostream>
#include <cstring>
#include <string>
#include <sstream>

#ifndef VERSION
#define VERSION "0.0.0-alpha"
#endif


int main(int argc, char *argv[]) {
    if (argc >= 2 && strcmp(argv[1], "--version") == 0) {
        std::cout << argv[0] << " (" << VERSION << ")" << std::endl;
    } else {
        std::cout << "Hello World!" << std::endl;
    }
    return 0;
}
```

New `Makefile`:
```Makefile
ERSION="\"$(shell cat VERSION)\""

CC=g++
CPPFLAGS=-Wall -O2 -DVERSION=$(VERSION)

main: main.cpp
    $(CC) $(CPPFLAGS) -o main main.cpp

```

# Assignment 3
## Differences
In some places the `a` and `b` are swapped, they were probably built (or linked) in a different order.

## Functionality
The functionality is not influenced as only the symbol table is changed.

## Fixing the issue
The issue can be fixed by explicitly stating the order in the Makefile:
```Makefile
CC=gcc
CFLAGS=-Wall

SOURCES = main.c b.c a.c

main: $(SOURCES)
    $(CC) $(CFLAGS) -o main $(SOURCES)
```

# Assignment 4
 * The APKs match
 * To be sure that the binary is actually the one that has been compiled on needs to check the complete Dockerfile and all parts of the build process. Additionally the script for checking if the applications are identical needs to be checked.
