import argparse
import sys
import numpy as np
import glob
import pandas as pd
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import mean_squared_error, r2_score

PATH = "/Users/casadei/development/casadei/gsgp-mo/experiments/data/"

def filename(dataset, datatype, fold):
  return "{0}/{1}-{2}-{3}.dat".format(PATH, dataset, datatype, fold)

def parse_input():
  parser = argparse.ArgumentParser(description='Compute linear regression of a dataset')
  parser.add_argument('-d', required=True, type=str, help='dataset name')
  parser.add_argument('-r', required=True, type=int, help='number of repetitions')

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

  return (folds, args.r, train_x, train_y, test_x, test_y)


folds, repetitions, train_x, train_y, test_x, test_y = parse_input()

mse = []

for i in range(repetitions):
  regr = RandomForestRegressor(max_depth=2, n_estimators=100)
  fold = i % folds

  # Train the model using the training sets
  regr.fit(train_x[fold], train_y[fold])

  y_pred = regr.predict(test_x[fold])
  mse.append(np.sqrt(mean_squared_error(test_y[fold], y_pred)))

print(np.median(np.array(mse)))
