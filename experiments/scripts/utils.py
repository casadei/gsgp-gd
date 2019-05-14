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
  'ALPHA': 0.5
}

class DatasetType(Enum):
  TRAINING = 1
  TEST = 2

def directory_path(strategy, k, dataset):
  return "%s/%s/%d/output-%s" % (CONSTANTS['PATH'], strategy, k, dataset)

def scatter(dimensions, target, data, colors, index):
  if dimensions == 2:
    target.scatter(data[0], data[1], c=colors[index], label = 'Group ' + str(index + 1), alpha = CONSTANTS['ALPHA'])
  elif dimensions == 3:
    target.scatter(data[0], data[1], data[2], c=colors[index], label = 'Group ' + str(index + 1), alpha = CONSTANTS['ALPHA'])

def render(strategy, k, type, colors, execution = 1):
    strategy, k, dataset, mode, execution = 1

    filename = "%s/groups-%02d.txt" % (directory_path(strategy, k, dataset), execution)

    data = pd.read_csv(filename, skiprows=[0], header = None)
    data = data[data[0] == type.value]

    dimensions = len(data) - 2
    target = plt if dimensions == 2 else Axes3D(plt.figure())

    for i in range(0, k):
      scatter(dimensions, target, data[data[1] & 2**i > 0][2:], colors, i)

    plt.title("grouped (%s) using k=%d applied to %s" % (strategy, k, dataset))
    plt.legend()
    plt.show()
