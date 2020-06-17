PANDOC=pandoc

all: 1-Port-Scanning.pdf

%.html: %/main.md
	$(PANDOC) $< -o $@

%.pdf: %/main.md
	$(PANDOC) $< --pdf-engine=xelatex -o $@

clean:
	rm -f *.pdf
