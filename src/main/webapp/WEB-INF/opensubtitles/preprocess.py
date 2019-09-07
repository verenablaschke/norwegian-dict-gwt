from nltk.tokenize import word_tokenize
from nltk.stem.snowball import SnowballStemmer
import spacy
import sys


if len(sys.argv) != 2:
    sys.stderr.write("Usage: %s DATA_FILE\n" % sys.argv[0])
    sys.exit(1)

filename = sys.argv[1].split('/')
if len(filename) == 1:
    filename = sys.argv[1].split('\\')
if len(filename) == 1:
    filename = 'tokenized-' + filename[-1]
else:
    filename = '/'.join(filename[:-1]) + '/tokenized-' + filename[-1]

norwegian = False
if filename.endswith('no'):
    norwegian = True
    stemmer = SnowballStemmer("norwegian")
else:
    nlp = spacy.load('de_core_news_sm')

with open(sys.argv[1], 'r', encoding='utf-8') as file_in:
    with open(filename, 'w', encoding='utf-8') as file_out:
        for line in file_in:
            if line.startswith('-'):
                line = line[1:]
            line = line.replace(' -', '')
            if norwegian:
                # stem() includes strip() and lower()
                file_out.write(' '.join([stemmer.stem(w)
                                         for w in word_tokenize(line)]))
            else:
                file_out.write(' '.join([tok.lemma_
                                         for tok in nlp(line.strip().lower())]))
            file_out.write('\n')
