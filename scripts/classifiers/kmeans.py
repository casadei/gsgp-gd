from shared import parse_input
from sklearn.preprocessing import StandardScaler
from sklearn.cluster import KMeans
import numpy as np

k, seed, training, testing, validation = parse_input()

kmeans = KMeans(n_clusters=k, random_state=seed)
kmeans.fit(training)

predict = lambda x: kmeans.predict(x)

for i, cluster in enumerate(predict(training)):
  print("TRAINING,%d,%d" % (i, cluster))

for i, cluster in enumerate(predict(testing)):
  print("TEST,%d,%d" % (i, cluster))

for i, cluster in enumerate(predict(validation)):
  print("VALIDATION,%d,%d" % (i, cluster))

print("<<EOF")
