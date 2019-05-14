import argparse
import sys
import numpy as np

def parse_input():
  parser = argparse.ArgumentParser(description='Classify data using python modules.')
  parser.add_argument('-k', required=True, type=int, help='number of classes')
  parser.add_argument('-tr', required=True, type=int, help='number of training instances')
  parser.add_argument('-ts', required=True, type=int, help='number of testing instances')
  parser.add_argument('-vt', required=True, type=int, help='nubmer of validation instances')
  parser.add_argument('-seed', required=True, type=int, help='random seed')

  args = parser.parse_args()

  training, testing, validation = [], [], []

  for i in range(args.tr):
    line = np.array(list(map(float, str(input()).split(','))))
    training.append(line)

  for i in range(args.ts):
    line = np.array(list(map(float, str(input()).split(','))))
    testing.append(line)

  for i in range(args.vt):
    line = np.array(list(map(float, str(input()).split(','))))
    validation.append(line)

  return (args.k, args.seed, np.array(training), np.array(testing), np.array(validation))
