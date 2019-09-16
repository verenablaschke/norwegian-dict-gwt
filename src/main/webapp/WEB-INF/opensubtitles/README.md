See section 2.8 of the report.

Download `de-no.txt.zip` from the [OpenSubtitles downloads page](http://opus.nlpl.eu/OpenSubtitles-v2018.php) and extract `OpenSubtitles.de-no.de` and `OpenSubtitles.de-no.no`.

Tokenize both files by running
```
python preprocess.py OpenSubtitles.de-no.de
python preprocess.py OpenSubtitles.de-no.no
```

Install [GIZA++](https://github.com/moses-smt/giza-pp) (Tutorials: [1](https://okapiframework.org/wiki/index.php/GIZA%2B%2B_Installation_and_Running_Tutorial), [2](http://masatohagiwara.net/using-giza-to-obtain-word-alignment-between-bilingual-sentences.html)).
Copy the tokenized files to a new data folder in the GIZA++ directory, like so:
```
giza-pp
├───data
│       lemmatized-OpenSubtitles.de-no.de
│       lemmatized-OpenSubtitles.de-no.no
│
├───GIZA++-v2
│       [installed files]
│
└───mkcls-v2
        [installed files]
```

Run:
```
./GIZA++-v2/plain2snt.out ./data/lemmatized-OpenSubtitles.de-no.de ./data/lemmatized-OpenSubtitles.de-no.no

./mkcls-v2/mkcls -p./data/lemmatized-OpenSubtitles.de-no.no -V./data/lemmatized-OpenSubtitles.de-no.no.vcb.classes
./mkcls-v2/mkcls -p./data/lemmatized-OpenSubtitles.de-no.de -V./data/lemmatized-OpenSubtitles.de-no.de.vcb.classes

./GIZA++-v2/GIZA++ -S ./data/lemmatized-OpenSubtitles.de-no.de.vcb -T ./data/lemmatized-OpenSubtitles.de-no.no.vcb -C ./data/lemmatized-OpenSubtitles.de-no.de_lemmatized-OpenSubtitles.de-no.no.snt -o no-de -outputpath ./output/
```

Copy `output/no-de.actual.ti.final` into this folder.
