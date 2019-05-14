from gensim.models import Word2Vec
from nltk.cluster import KMeansClusterer
import numpy as np
import nltk


# - Calc Vec -----------------------------------------------------------------------------------------------------------
def sent_vectorizer(sent, model):
    sent_vec = []
    numw = 0
    for w in sent:
        try:
            if numw == 0:
                sent_vec = model[w]
            else:
                sent_vec = np.add(sent_vec, model[w])
            numw += 1
        except:
            pass

    return np.asarray(sent_vec) / numw


def run(sentences):
    # - Create the model -----------------------------------------------------------------------------------------------
    print 'Creating Kmeans model...'
    model = Word2Vec(sentences, min_count=1)

    X = []
    for sentence in sentences:
        X.append(sent_vectorizer(sentence, model))

    NUM_CLUSTERS = 4
    kclusterer = KMeansClusterer(NUM_CLUSTERS, distance=nltk.cluster.util.cosine_distance,
                                 repeats=25, avoid_empty_clusters=True)
    assigned_clusters = kclusterer.cluster(X, assign_clusters=True)

    print
    print " - K-Means Clustering --------------------------------------------------------------------------------------"
    return assigned_clusters, sentences
