from shared import *
from scipy.stats import kde


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
    current.append(median(results[dataset]['single-1'][columns[0]]))
    random_aggr.append(median(results[dataset]['random-' + str(k)][columns[0]]))
    random_smart.append(median(results[dataset]['random-' + str(k)][columns[1]]))
    kmeans_aggr.append(median(results[dataset]['kmeans-' + str(k)][columns[0]]))
    kmeans_smart.append(median(results[dataset]['kmeans-' + str(k)][columns[1]]))
    kernel_aggr.append(median(results[dataset]['kernel-kmeans-' + str(k)][columns[0]]))
    kernel_smart.append(median(results[dataset]['kernel-kmeans-' + str(k)][columns[1]]))

  graph.set_title(mode)
  graph.plot(classes, current, linestyle="dashed", color='black', label='Single Objective', alpha=0.5)
  graph.plot(classes, random_aggr, color='red', label='Random Aggregated', alpha=0.5)
  graph.plot(classes, random_smart, color='red', linestyle="dotted", label='Random Combined', alpha=0.5)
  graph.plot(classes, kmeans_aggr, color='green', label='KMeans Aggregated', alpha=0.5)
  graph.plot(classes, kmeans_smart, color='green', linestyle="dotted", label='KMeans Combined', alpha=0.5)
  graph.plot(classes, kernel_aggr, color='purple', label='Kernel-Kmeans Aggregated', alpha=0.5)
  graph.plot(classes, kernel_smart, color='purple', linestyle="dotted", label='Kernel-KMeans Combined', alpha=0.5)

  graph.set_yscale('log')
  graph.get_yaxis().set_visible(False)
  graph.set_xticks(classes)
  graph.legend()

  return graph

def fill_dataset_graph(results, dataset, title = ''):
  f, ax = plt.subplots(1, 2, figsize=(16,4))
  f.suptitle(title, fontsize="x-large")
  fill_graph(results, ax[0], dataset, 'training')
  fill_graph(results, ax[1], dataset, 'test')
  plt.show()

def scatter_accuracies(results, strategies, datasets, source_name, target_name):
  markers = {
    'random-without-replacement': u'+',
    'kmeans': u'x',
    'kernel-kmeans': u'o'
  }

  colors = {
    'random-without-replacement': 'red',
    'kmeans': 'blue',
    'kernel-kmeans': 'green'

  }

  for j,strategy in enumerate(strategies):
    x = []
    y = []

    for dataset in datasets:
      for k in CONSTANTS['CLASSES']:
          current = results[dataset]["%s-%d" % (strategy, k)]

          source = current[source_name]
          target = current[target_name]

          count = 0
          for i, curr in enumerate(target):
              #print("%f <= %f" % (target[i], source[i]))
              if target[i] <= source[i]:
                  count = count + 1

          x.append(k + j * 0.2)
          y.append(float(count) / len(target))

    plt.scatter(x, y, marker='+', alpha=0.5, color=colors[strategy], label=strategy, s=100)
  plt.xticks(CONSTANTS['CLASSES'], [2, 3, 4, 5])
  #plt.legend()
  plt.show()

def scatter_accuracies2(results, strategies, datasets, source_name, target_name):
  markers = {
    'random-without-replacement': u'+',
    'kmeans': u'x',
    'kernel-kmeans': u'o'
  }

  colors = {
    'random-without-replacement': 'red',
    'kmeans': 'blue',
    'kernel-kmeans': 'green'

  }

  for j,strategy in enumerate(strategies):
    x = []
    y = []

    for dataset in datasets:
      for k in CONSTANTS['CLASSES']:
          current = results[dataset]["%s-%d" % (strategy, k)]

          source = current[source_name]
          target = current[target_name]

          count = 0
          for i, curr in enumerate(target):
              #print("%f <= %f" % (target[i], source[i]))
              if target[i] <= source[i]:
                  count = count + 1

          x.append(k + j * 0.20)
          y.append((float(count) / len(target)))
    Z, xedges, yedges = np.histogram2d(x, y)
    plt.pcolormesh(xedges, yedges, Z.T, cmap='Blues')

    #plt.scatter(x, y, marker='+', alpha=0.5, color=colors[strategy], label=strategy, s=100)
  #plt.xticks(CONSTANTS['CLASSES'], [2, 3, 4, 5])
  #plt.legend()z
  plt.xticks(CONSTANTS['CLASSES'], [2, 3, 4, 5])
  plt.show()

def plot_density_sizes(results, strategies, datasets, source_name, target_name):
  markers = {
    'random-without-replacement': u'+',
    'kmeans': u'x',
    'kernel-kmeans': u'o'
  }

  colors = {
    'random-without-replacement': 'red',
    'kmeans': 'blue',
    'kernel-kmeans': 'green'

  }

  for j,strategy in enumerate(strategies):
    x = []
    y = []

    for dataset in datasets:
      for k in CONSTANTS['CLASSES']:
          current = results[dataset]["%s-%d" % (strategy, k)]

          for curr in current[source_name]:
            x.append(k)
            y.append(curr)

    Z, xedges, yedges = np.histogram2d(x, y)
    plt.pcolormesh(xedges, yedges, Z.T, cmap='Blues')

    #plt.scatter(x, y, marker='+', alpha=0.5, color=colors[strategy], label=strategy, s=100)
  #plt.xticks(CONSTANTS['CLASSES'], [2, 3, 4, 5])
  #plt.legend()z
  plt.xticks(CONSTANTS['CLASSES'], [2, 3, 4, 5])
  plt.yticks([2, 3, 4, 5], [2, 3, 4, 5])
  plt.show()


