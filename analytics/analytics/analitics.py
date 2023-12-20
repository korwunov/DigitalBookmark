import matplotlib.pyplot as plt
import os
import io
import requests
import base64

def listToString(s):
    str1 = ""
    for ele in s:
        str1 += str(ele) + "."
    return str1[:-1]

def get_data(array, name):
    arr = []
    for record in array:
        arr.append(record[name])
    return arr


def open_graph(folder):
    file = open(folder + "/analitic.png")
    data_uri = base64.b64encode(open(folder + '/analitic.png', 'rb').read()).decode('utf-8')
    img_tag = '<img src="data:image/png;base64,{0}">'.format(data_uri)
    return img_tag
    
def find_graph(subject_id, array):
    folder = f"/usr/src/app/analytics/upload/{array[0]['markSetDate']}_{array[len(array)-1]['markSetDate']}_{array[0]['id']}"
    if os.path.isdir(folder):
        return open_graph(folder)
    else:
        return create_graph(subject_id, array, folder)

def create_graph(subject_id, array, folder):
    x = get_data(array, 'markValue')
    ny = get_data(array, 'markSetDate')
    y = []
    for rec in ny:
       y.append(listToString(rec))
    plt.plot(y, x, color='blue', marker='o', markersize=7)
    plt.ylabel('Оценка')
    plt.ylim([1, 6])
    plt.xlabel('Дата')
    plt.xticks(rotation=45)
    plt.tight_layout()
    print(str(array[0]['id']))
    url = os.environ.get('MAINHOST') +'/api/subjects/' + str(subject_id)
    r = requests.get(url)
    subject = r.json()
    plt.title(subject['name'])
    try:
        os.mkdir('/usr/src/app/analytics/upload')
    except FileExistsError:
        pass
    os.mkdir(folder)
    plt.savefig(folder+'/analitic.png')
    plt.close()
    return open_graph(folder)   


    
