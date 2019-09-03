import spacy
import sys


if len(sys.argv) != 2:
    sys.stderr.write("Usage: %s DATA_FILE\n" % sys.argv[0])
    sys.exit(1)

filename = sys.argv[1].split('/')
if len(filename) == 1:
    filename = sys.argv[1].split('\\')
if len(filename) == 1:
    filename = 'preprocessed-' + filename[-1]
else:
    filename = '/'.join(filename[:-1]) + '/preprocessed-' + filename[-1]

nb_core = spacy.load("nb_core_news_sm")

with open(sys.argv[1], 'r', encoding='utf-8') as file_in:
    with open(filename, 'w', encoding='utf-8') as file_out:
        for line in file_in:
            if line.startswith('-'):
                line = line[1:]
            line = line.replace(' -', '')
            for token in nb_core(line):
                print(token.lemma_ + ' ')
            file_out.write('\n')
