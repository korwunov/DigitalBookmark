FROM python:3.11

WORKDIR /usr/src/app

COPY analytics/requirements.txt /usr/src/app

COPY analytics /usr/src/app

RUN pip install --upgrade pip

RUN python -m pip install -r requirements.txt

EXPOSE 8000