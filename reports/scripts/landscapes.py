from shared import *

class RenderType(Enum):
  GROUPS = 1
  FEEDBACK = 2,
  INDIVIDUALS = 3,
  ERRORS = 4

def build_colors(colors, values):
  result = []
  for i, curr in enumerate(values):
      result.append(colors[int(curr)])

  return np.array(result)

def scatter(type, dimensions, target, data, colors):
  if dimensions == 2:
    target.scatter(
      data[:,0],
      data[:,1],
      color=build_colors(colors, data[:,2]),
      alpha = CONSTANTS['ALPHA']
    )
  elif dimensions == 3:
    target.scatter(
      data[:,0],
      data[:,1],
      data[:,2],
      color=build_colors(colors, data[:,3]),
      alpha = CONSTANTS['ALPHA']
    )

def build_groups_data(data, k):
  matrix = []

  for i in range(0, k):
    for row in data.as_matrix():
      if (row[1] & 2**i) == 0:
        continue

      matrix.append(np.append(row[2:], i))

  return np.asarray([matrix])

def build_feedback_data(data, datasetType, directory, execution):
  if datasetType == DatasetType.TRAINING:
      files = ('smart_tr_outputs.csv', 'smart_tr_feedback.csv')
  else:
      files = ('smart_ts_outputs.csv', 'smart_ts_feedback.csv')

  semantics = select_execution(pd.read_csv(directory + files[0], header = None), execution)
  feedback = select_execution(pd.read_csv(directory + files[1], header = None), execution)

  matrix = []
  for index, current in enumerate(data.as_matrix()):
    buffer = np.array(current[2:-1])
    buffer = np.append(buffer, semantics.iloc[index])
    buffer = np.append(buffer, feedback.iloc[index])
    matrix.append(buffer)

  return np.asarray([matrix])

def merge(semantics, numberOfRows):
  individuals = int(semantics.shape[0] / numberOfRows)

  arr = []
  for i in range(0, individuals):
    start = i * numberOfRows
    end = start + numberOfRows
    arr.append(semantics[start:end])

  return np.array(arr)

def build_individuals_data(data, datasetType, directory, execution):
  file = 'trOutputs.csv' if datasetType == DatasetType.TRAINING else 'tsOutputs.csv'

  semantics = select_execution(
    pd.read_csv(directory + file, header = None, sep = "[,|\|]", engine="python"),
    execution
  )

  matrix = []
  for i, current in enumerate(merge(semantics, data.shape[0])):
    buffer = np.array(data.iloc[:,2:-1])
    matrix.append(np.column_stack((buffer, current, [i] * buffer.shape[0])))

  return np.asarray(matrix)

def build_errors_data(data, datasetType, directory, execution):
  file = 'trOutputs.csv' if datasetType == DatasetType.TRAINING else 'tsOutputs.csv'

  semantics = select_execution(
    pd.read_csv(directory + file, header = None, sep = "[,|\|]", engine="python"),
    execution
  )

  semantics = merge(semantics, data.shape[0])
  errors = []

  for index, current in enumerate(semantics):
    errors.append((current - data.iloc[:, -1]) ** 2)

  errors = np.dstack(errors)[0]

  matrix = build_feedback_data(data, datasetType, directory, execution)[0]

  for index, row in enumerate(matrix):
    error = (row[-2] - data.iloc[index, -1]) ** 2
    lowest_error = errors[index].min()

    if lowest_error < error:
      matrix[index][-1] = len(CONSTANTS['COLORS']) - 1

  return [matrix]

def render(type, strategy, k, dataset, datasetType, colors, execution = 1):
  directory = directory_path(strategy, k, dataset)
  filename = "%s/groups-%02d.txt" % (directory, execution)

  data = pd.read_csv(filename, skiprows=[0], header = None)
  data = data[data[0] == datasetType.value]

  dimensions = data.shape[1] - 2

  matrix = None
  title = ""
  if type == RenderType.INDIVIDUALS:
    matrix = build_individuals_data(data, datasetType, directory, execution)
    title = "individuals using %s" % (strategy)
  if type == RenderType.ERRORS:
    matrix = build_errors_data(data, datasetType, directory, execution)
    title = "Errors using %s" % (strategy)
  elif type == RenderType.GROUPS:
    matrix = build_groups_data(data, k)
    title = "Groups using %s" % (strategy)
  elif type == RenderType.FEEDBACK:
    matrix = build_feedback_data(data, datasetType, directory, execution)
    title = "Feedback using %s" % (strategy)

  for snap in matrix:
    target = plt if dimensions == 2 else Axes3D(plt.figure())
    scatter(type, dimensions, target, snap, colors)

    if CONSTANTS['SHOW_TITLE']:
      plt.title("%s (k=%d) applied to %s" % (title, k, dataset))
    plt.show()
