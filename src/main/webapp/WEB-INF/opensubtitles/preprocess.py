from nltk.tokenize import word_tokenize
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

lower_case = filename.endswith('no')

with open(sys.argv[1], 'r', encoding='utf-8') as file_in:
    with open(filename, 'w', encoding='utf-8') as file_out:
        for line in file_in:
            if line.startswith('-'):
                line = line[1:]
            if lower_case:
                line = line.lower()
            line = line.replace(' -', '')
            file_out.write(' '.join(word_tokenize(line)))
            file_out.write('\n')
