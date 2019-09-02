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
│       tokenized-OpenSubtitles.de-no.de
│       tokenized-OpenSubtitles.de-no.no
│
├───GIZA++-v2
│       [installed files]
│
└───mkcls-v2
        [installed files]
```

Run:
```
./GIZA++-v2/plain2snt.out ./data/tokenized-OpenSubtitles.de-no.de ./data/tokenized-OpenSubtitles.de-no.no

./GIZA++-v2/GIZA++ -S ./data/tokenized-OpenSubtitles.de-no.de.vcb -T ./data/tokenized-OpenSubtitles.de-no.no.vcb -C ./data/tokenized-OpenSubtitles.de-no.de_tokenized-OpenSubtitles.de-no.no.snt -o no-de -outputpath ./output/
```

Copy `output/no-de.actual.ti.final` into this folder.
