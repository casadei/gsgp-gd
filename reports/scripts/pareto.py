from shared import *

def build_data_matrix(strategy, k, dataset):
  data = {}

  directory = directory_path(strategy, k, dataset)
  filename = "%s/tr_fronts.csv" % (directory)

  with open(filename) as f:
    for line in f:
      arr = line.strip().split(',')

      ranks = []

      for rank in arr[1].rstrip().split('#'):
        current_rank = []
        for individual in rank.rstrip().split('|'):
          individual = individual.replace('∞', 'inf')
          objectives = np.sqrt(np.array(list(map(float, individual.rstrip().split(';')))))

          if not np.isinf(objectives).any():
            current_rank.append(objectives)

        if len(current_rank)  > 0:
          ranks.append(np.array(current_rank))


      data[int(arr[0])] = np.array(ranks)

  return data

def plot_pareto(strategy, k, dataset, execution):
  data = build_data_matrix(strategy, k, dataset)[execution - 1]

  if k == 2:
    target = plt
  else:
    target = Axes3D(plt.figure())

  for index in range(min(data.shape[0], 5), -1, -1):
    matrix = np.matrix(data[index])

    if index == 0:
      color = 'blue'
    else:
      color = 'green'

    if k == 2:
      target.scatter(
        [matrix[:,0]],
        [matrix[:,1]],
        color=color,
      )
    else:
      target.scatter(
        matrix[:,0],
        matrix[:,1],
        matrix[:,2],
        color=color,
      )

  plt.show()
