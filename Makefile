PANDOC=pandoc

all: 1-Port-Scanning.pdf 2-Slow-DDoS.pdf 3-Reproducible-Builds.pdf

%.html: %/main.md
	$(PANDOC) $< -o $@

%.pdf: %/main.md
	$(PANDOC) $< --pdf-engine=xelatex -o $@

clean:
	rm -f *.pdf
