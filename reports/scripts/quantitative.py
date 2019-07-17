from shared import *
from scipy import stats
from scipy.stats import iqr

def generate_table(results, datasets):
  data = []

  for dataset in datasets:
      single = results[dataset]['single']['smartTs']
      kmeans = results[dataset]['kmeans-2']['smartTs']
      kernel = results[dataset]['kernel-kmeans-2']['smartTs']

      data.append([dataset, median(single), iqr(single), median(kmeans), iqr(kmeans), median(kernel), iqr(kernel)])

  columns = ('Dataset',
             'Single Objective Median',
             'Single Objective IQR',
             'KMeans-2 Median',
             'KMeans-2 IQR',
             'Kernel KMeans-2 Median',
             'Kernel KMeans-2 IQR')
  df = pd.DataFrame(data=data, columns = columns)
  return df

def compare(results, datasets, source, left, right, alpha):
  data = []

  format = lambda x: "%0.5f" % x

  for dataset in datasets:
      x = results[dataset][left][source]
      y = results[dataset][right][source]
      p = stats.wilcoxon(x, y, )

      result = 'EQUAL'
      if p.pvalue < alpha:
        result = 'LOWER' if median(y) < median(x) else 'GREATER'

      data.append([dataset, format(median(x)), format(median(y)), format(p.pvalue), result])

  columns = ('Dataset', left, right, 'p-value', 'Result')
  df = pd.DataFrame(data=data, columns =columns)
  return df

def export(results, datasets, strategy, source):
  data = []

  format = lambda x: "%0.5f" % x

  for dataset in datasets:
      x = results[dataset][strategy][source]
      data.append(format(median(x)))

  print(" & ".join(data))

