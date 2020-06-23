PANDOC=pandoc

all: 1-Port-Scanning.pdf 2-Slow-DDoS.pdf 3-Reproducible-Builds.pdf 5-Ghidra.pdf 6-ROP.pdf 8-TLS.pdf

%.html: %/main.md
	$(PANDOC) $< -o $@

%.pdf: %/main.md
	cd $*; $(PANDOC) main.md --pdf-engine=xelatex -o ../$*.pdf


clean:
	rm -f *.pdf
	rm -f *.html
