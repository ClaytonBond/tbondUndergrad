import os
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.metrics.pairwise import euclidean_distances
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.cluster import KMeans
from sklearn.metrics import adjusted_rand_score

SearchTerm = "This is a test"

corpus = {}
corpus[SearchTerm.lower()] = "Search Term"
for (dirpath, dirnames, filenames) in os.walk("/Users/clayton/Desktop/Sites"):
    for filename in filenames:
        if filename.endswith('.html'):
            corpus[filename[:-5].lower().replace("_", " ")] = os.sep.join([dirpath, filename])

location = 0
i = 0
for item in corpus:
    print item
    if item == SearchTerm.lower():
        location = i
    i = i + 1

for item in corpus:
    print corpus[item]

vectorizer = CountVectorizer()
features = vectorizer.fit_transform(corpus).todense()
sort = []

for f in features:
    sort.append(euclidean_distances(features[location], f))

i = 0
for item in sort:
    print i,  item
    i = i + 1

vectorizer = TfidfVectorizer(stop_words='english')
X = vectorizer.fit_transform(corpus)

true_k = 20
model = KMeans(n_clusters=true_k, init='k-means++', max_iter=100, n_init=1)
model.fit(X)
print model.predict(TfidfVectorizer.transform("This is a test"))
print("Top terms per cluster:")
order_centroids = model.cluster_centers_.argsort()[:, ::-1]
terms = vectorizer.get_feature_names()
for i in range(true_k):
    print "Cluster %d:" % i,
    for ind in order_centroids[i, :10]:
        print ' %s' % terms[ind],
    print