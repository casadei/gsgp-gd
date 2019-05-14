import argparse
import sys
import numpy as np
import glob
import pandas as pd
from sklearn import datasets, linear_model
from sklearn.metrics import mean_squared_error, r2_score

PATH = "/Users/casadei/development/casadei/gsgp-mo/experiments/data/"

def filename(dataset, datatype, fold):
  return "{0}/{1}-{2}-{3}.dat".format(PATH, dataset, datatype, fold)

def parse_input():
  parser = argparse.ArgumentParser(description='Compute linear regression of a dataset')
  parser.add_argument('-d', required=True, type=str, help='dataset name')

  args = parser.parse_args()

  folds = len(glob.glob(filename(args.d, 'train', '*')))

  train_x, train_y = [], []
  test_x, test_y = [], []

  for i in range(folds):
    train = pd.read_csv(filename(args.d, 'train', i), skiprows=[0], header = None)
    test = pd.read_csv(filename(args.d, 'test', i), skiprows=[0], header = None)

    train_x.append(train.iloc[:,0:-1])
    train_y.append(train.iloc[:,-1])

    test_x.append(test.iloc[:,0:-1])
    test_y.append(test.iloc[:,-1])

  return (folds, train_x, train_y, test_x, test_y)


folds, train_x, train_y, test_x, test_y = parse_input()

mse = []

for i in range(folds):
  regr = linear_model.LinearRegression()

  # Train the model using the training sets
  regr.fit(train_x[i], train_y[i])

  y_pred = regr.predict(test_x[i])
  mse.append(np.sqrt(mean_squared_error(test_y[i], y_pred)))

print(np.median(np.array(mse)))
