from shared import *
from scipy import stats
from scipy.stats import iqr

def generate_table(results, datasets):
  data = []

  for dataset in datasets:
      single = results[dataset]['single-objective-1']['smartTs']
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

def compare(results, datasets, left, right, alpha):
  data = []

  format = lambda x: "%0.5f" % x

  for dataset in datasets:
      x = results[dataset][left]['smartTs']
      y = results[dataset][right]['smartTs']
      p = stats.wilcoxon(x, y, )

      result = 'EQUAL'
      if p.pvalue < alpha:
        result = 'LOWER' if right < left else 'GREATER'

      data.append([dataset, format(median(x)), format(median(y)), format(p.pvalue), result])

  columns = ('Dataset', left, right, 'p-value', 'Result')
  df = pd.DataFrame(data=data, columns =columns)
  return df
