from shared import *

def compute_accuracy(results, approach, k, dataset):
    current = results[dataset]["%s-%d" % (approach, k)]
    smart = current['smartTs']
    best = current['ts']

    count = 0
    for i, curr in enumerate(smart):
        if curr <= best[i]:
            count = count + 1

    return float(count) / len(smart) * 100

def compute_accuracies(results, dataset):
    data = {}

    for strategy in CONSTANTS['STRATEGIES']:
        data[strategy] = []
        for k in CONSTANTS['CLASSES']:
            data[strategy].append(compute_accuracy(results, strategy, k, dataset))

    return data

def plot_accuracies(results, dataset, ax):
    colors = ['red', 'green', 'purple']
    accuracies = compute_accuracies(results, dataset)
    for index, strategy in enumerate(CONSTANTS['STRATEGIES']):
        ax.plot(CONSTANTS['CLASSES'], accuracies[strategy], color=colors[index], label=strategy)

    ax.set_xticks(CONSTANTS['CLASSES'])
    ax.set_title(dataset)

def plot_all_accuracies(results, datasets):
    f, ax = plt.subplots(3, 3, figsize=(16,12), sharex=True, sharey=True)

    for index, dataset in enumerate(datasets):
        row = int(index / 3)
        col = index % 3
        plot_accuracies(results, dataset, ax[row, col])

    handles, labels = ax[0,0].get_legend_handles_labels()
    f.legend(handles, labels, loc='lower center')
    plt.show()
