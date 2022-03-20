import csv


list = [1,2,3,4,5,6,7]
index = 1

with open("countries.csv", "w") as csvFile:
    fieldnames = ['Item Name']
    writer = csv.DictWriter(csvFile, fieldnames=fieldnames)
    writer.writeheader()

    for item in list:
        writer.writerow({'Item Name': item})
