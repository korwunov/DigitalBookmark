from django.http import HttpResponse, HttpResponseNotFound, JsonResponse
import requests
from django.views.decorators.csrf import csrf_exempt
from analytics import analitics
import json
import os

@csrf_exempt
def get_graph(request):
    if request.method == "POST":
        data = json.loads(request.body)
        startdate = data['startdate']
        enddate = data['enddate']
        subject = data['subject']
        payload = {'id': subject, 'dateFrom': startdate, 'dateTo': enddate}
        url = os.environ.get('MAINHOST') + '/api/admin/getMarksStat'
        r = requests.get(url, params=payload)
        if (r.status_code == 200):
            array = r.json()
            graph = analitics.find_graph(array)
            response = "<html><body><title>Ваш анализ</title>"+graph+"</body></html>"
            return HttpResponse(response)
        else:
            return HttpResponse("<h1>Данные не найдены</h1>")
    else:
        return HttpResponseNotFound("<h1>Page not found</h1>")