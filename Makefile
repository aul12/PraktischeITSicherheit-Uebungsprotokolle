PANDOC=pandoc

all: 1-Port-Scanning.pdf

%.pdf: %/main.md
	$(PANDOC) $< --pdf-engine=xelatex -o $@

clean:
	rm -f *.pdf
