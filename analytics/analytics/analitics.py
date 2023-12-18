import matplotlib.pyplot as plt
import os
import io
import requests

def listToString(s):
    str1 = ""
    for ele in s:
        str1 += str(ele) + "."
    print(str1)
    return str1[:-1]

def get_data(array, name):
    arr = []
    for record in array:
        arr.append(record[name])
    return arr

def open_graph(folder):
    file = open(folder + "/analitic.png")
    return file

def find_graph(array):
    folder = f"/usr/src/app/analytics/upload/{array[0]['markSetDate']}_{array[len(array)-1]['markSetDate']}_{array[0]['id']}"
    if os.path.isdir(folder):
        return open_graph(folder)
    else:
        return create_graph(array, folder)

def create_graph(array, folder):
    x = get_data(array, 'markValue')
    ny = get_data(array, 'markSetDate')
    y = []
    for rec in ny:
       y.append(listToString(rec))
    plt.plot(y, x, color='blue', marker='o', markersize=7)
    plt.ylabel('Оценка')
    plt.ylim([1, 6])
    plt.xlabel('Дата')
    url = os.environ.get('MAINHOST') +'/api/subjects/' + str(array[0]['id'])
    r = requests.get(url)
    subject = r.json()
    plt.title(subject['name'])
    os.mkdir('/usr/src/app/analytics/upload')
    os.mkdir(folder)
    plt.savefig(folder+'/analitic.png')
    plt.close()
    return open_graph(folder)
    


    