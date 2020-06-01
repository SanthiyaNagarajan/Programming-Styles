import sys
import numpy as np

characters = np.array([' '] + list(open(sys.argv[1]).read()) + [' '])

# Normalize
characters[~np.char.isalpha(characters)] = ' '
characters = np.char.upper(characters)

# Leet dictionary
leet_dict = {
    "A": "4",
    "B": "8",
    "C": "<",
    "E": "3",
    "G": "[",
    "H": "#",
    "I": "1",
    "O": "0",
    "S": "5",
    "T": "7",
    "X": "%",
    "Z": "2"
}


# Convert characters to their leet counter part
def lc(character):
    return leet_dict[character] if character in leet_dict else character


lc = np.vectorize(lc)
characters = lc(characters)

# Split the words by finding the indices of spaces
sp = np.where(characters == ' ')

# Double each index, and then take pairs
sp2 = np.repeat(sp, 2)

# Get the pairs as a 2D matrix, skip the first and the last
w_ranges = np.reshape(sp2[1:-1], (-1, 2))

# Remove the indexing to the spaces themselves
w_ranges = w_ranges[np.where(w_ranges[:, 1] - w_ranges[:, 0] > 2)]

# Words are in between spaces, given as pairs of indices
words = list(map(lambda r: characters[r[0]:r[1]], w_ranges))

# Recode the characters as strings
swords = np.array(list(map(lambda w: ''.join(w).strip(), words)))

# Make two grams
two_grams = np.array(list(zip(swords, swords[1:])))

# Finally, count the two gram occurrences
uniq, counts = np.unique(two_grams, axis=0, return_counts=True)
wf_sorted = sorted(zip(uniq, counts), key=lambda t: t[1], reverse=True)

# Print 5 most frequently occurring two grams
for w, c in wf_sorted[:5]:
    print(w, '-', c)
