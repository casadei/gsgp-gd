from shared import *

def fill_graph(results, graph, dataset, mode):
  if (mode == 'training'):
    columns = ('tr', 'smartTr')
  else:
    columns = ('ts', 'smartTs')

  current = []
  random_aggr = []
  random_smart = []
  kmeans_aggr = []
  kmeans_smart = []
  kernel_aggr = []
  kernel_smart = []

  classes = CONSTANTS['CLASSES']
  for k in classes:
    current.append(median(results[dataset]['single-objective-1'][columns[0]]))
    random_aggr.append(median(results[dataset]['random-without-replacement-' + str(k)][columns[0]]))
    random_smart.append(median(results[dataset]['random-without-replacement-' + str(k)][columns[1]]))
    kmeans_aggr.append(median(results[dataset]['kmeans-' + str(k)][columns[0]]))
    kmeans_smart.append(median(results[dataset]['kmeans-' + str(k)][columns[1]]))
    kernel_aggr.append(median(results[dataset]['kernel-kmeans-' + str(k)][columns[0]]))
    kernel_smart.append(median(results[dataset]['kernel-kmeans-' + str(k)][columns[1]]))

  graph.set_title(mode)
  graph.plot(classes, current, linestyle="dashed", color='black', label='Single Objective', alpha=0.5)
  graph.plot(classes, random_aggr, color='red', label='Random Aggregated', alpha=0.5)
  graph.plot(classes, random_smart, color='red', linestyle="dotted", label='Random Smart', alpha=0.5)
  graph.plot(classes, kmeans_aggr, color='green', label='KMeans Aggregated', alpha=0.5)
  graph.plot(classes, kmeans_smart, color='green', linestyle="dotted", label='KMeans Smart', alpha=0.5)
  graph.plot(classes, kernel_aggr, color='purple', label='Kernel-Kmeans Aggregated', alpha=0.5)
  graph.plot(classes, kernel_smart, color='purple', linestyle="dotted", label='Kernel-KMeans Smart', alpha=0.5)

  graph.set_yscale('log')
  graph.get_yaxis().set_visible(False)
  graph.set_xticks(classes)
  graph.legend()

  return graph

def fill_dataset_graph(results, dataset):
  f, ax = plt.subplots(1, 2, figsize=(16,4))
  f.suptitle(dataset, fontsize="x-large")
  fill_graph(results, ax[0], dataset, 'training')
  fill_graph(results, ax[1], dataset, 'test')
  plt.show()
