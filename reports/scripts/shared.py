import pandas as pd
import numpy as np
import os
from matplotlib import pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
from enum import Enum

plt.rcParams['figure.figsize'] = (10, 5)
plt.style.use('ggplot')

CONSTANTS = {
  'PATH': "/Users/casadei/dev/casadei/gsgp-mo/results",
  'ALPHA': 0.5,
  'COLORS': ['blue', 'green', 'orange', 'purple', 'brown', 'red'],
  'SHOW_TITLE': False,
  'AGGREGATION': np.mean,
  'DATASETS': [
    'airfoil', 'ccn', 'concrete', 'keijzer-7', 'parkinsons', 'towerData',
    'vladislavleva-1', 'vladislavleva-4', 'yacht'
  ],
  'STRATEGIES': ['random-without-replacement', 'kmeans', 'kernel-kmeans'],
  'CLASSES': range(2, 6)
}

class DatasetType(Enum):
  TRAINING = 'TRAINING'
  TEST = 'TEST'

def directory_path(strategy, k, dataset):
  return "%s/%s/%d/output-%s/" % (CONSTANTS['PATH'], strategy, k, dataset)

def select_execution(data, execution):
  filtered = data[data[0] == execution - 1]
  return filtered.iloc[0,1:]

def _compute_approach(results, approach, k, dataset):
  directory = directory_path(approach, k, dataset)
  aggregation = CONSTANTS['AGGREGATION']
  cols = list(range(0, k + 1))

  results["%s-%d" % (approach, k)] = {
    'tr': aggregation(np.sqrt(pd.read_csv(directory + '/smart_tr_sanity.csv', header = None, names=cols, usecols = cols).iloc[:,1:]), axis = 1),
    'ts': aggregation(np.sqrt(pd.read_csv(directory + '/smart_ts_sanity.csv', header = None, names=cols, usecols = cols).iloc[:,1:]), axis = 1),
    'smartTr': np.sqrt(pd.read_csv(directory + '/smart_tr_fitness.csv', header = None).iloc[:,-1]),
    'smartTs': np.sqrt(pd.read_csv(directory + '/smart_ts_fitness.csv', header = None).iloc[:,-1])
  }

def compute_results(approaches, classes, dataset):
    results = {}

    print("Computing results of " + dataset)

    _compute_approach(results, 'single-objective', 1, dataset)

    for approach in approaches:
        for k in classes:
            _compute_approach(results, approach, k, dataset)

    return results

def compute_all(approaches, classes, datasets):
    results = {}

    for dataset in datasets:
        results[dataset] = compute_results(approaches, classes, dataset)

    return results

def median(arr):
    arr[~np.isfinite(arr)] = 0
    return np.median(arr)

