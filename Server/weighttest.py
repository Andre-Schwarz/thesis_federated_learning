from numpy import load

data = load('./round-10-weights.npz', allow_pickle=True)
lst = data.files
for item in lst:
    print(item)
    print(data[item])
