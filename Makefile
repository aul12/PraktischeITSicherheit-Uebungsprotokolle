PANDOC=pandoc
RUBBER=rubber

all: 1-Port-Scanning.pdf 2-Slow-DDoS.pdf 3-Reproducible-Builds.pdf 4-Machine-Learning.pdf 5-Ghidra.pdf 6-ROP.pdf 8-TLS.pdf

%.html: %/main.md
	$(PANDOC) $< -o $@

%.pdf: %/main.md
	cd $*; $(PANDOC) main.md --pdf-engine=xelatex -o ../$*.pdf

4-Machine-Learning.pdf: 4-Machine-Learning/main.tex
	cd 4-Machine-Learning; $(RUBBER) -d main.tex
	mv 4-Machine-Learning/main.pdf 4-Machine-Learning.pdf

clean:
	rm -f *.pdf
	rm -f *.html
