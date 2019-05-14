from random import randint

def run(sentences):
    groups = []
    for i in range(len(sentences)):
        groups.append(randint(0, 4))

    print
    print " - Random Clustering ---------------------------------------------------------------------------------------"
    return groups, sentences
