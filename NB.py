import math
import collections


def run(groups, sentences):

    targetGroup = []
    words = []
    totalWords = 0
    frequency = []
    phrase = []
    index = []
    topResults = []

    for i in range(len(groups)):
        if groups[i] == groups[-1]:
            targetGroup.append(sentences[i])
            phrase = sentences[i];

    for i in range(len(targetGroup)):
        words.append('')
        words[i] = len(targetGroup[i])
        totalWords = totalWords + words[i]

    for i in range(len(targetGroup)):
        frequency.append(collections.Counter(targetGroup[i]))

    for i in range(len(targetGroup)):
        wordProb = 0
        for j in range(len(phrase)):
            if frequency[i].has_key(phrase[j]):
                wordProb = wordProb + -1 * math.log((frequency[i].get(phrase[j]) + 1.0) / (words[i] + totalWords), 2)
        index.append(wordProb)

    for i in range(10):
        top = index.index(max(index))
        topResults.append(top)
        index[top] = -1000

    for i in range(len(topResults)):
        print targetGroup[topResults[i]][0]