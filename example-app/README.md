# Example application

## INFO
The application is running in Docker using docker-compose. The application itself consists of component1 and component2 illustrating 2 Mule applications with top level flow in component1 calling a number of flows in component2 over http. Docker-compose brings up the Elastic stack (Kibana, Elasticsearch and APM server) that are wired to receive traces from component1 and component2.

## Prerequisites
Ensure docker daemon is configured with 8GB RAM. Also, if you use proxies, you may need to update `Dockerfile` to copy across your Maven settings.xml file.

## Setup
In directory `example-app`, start the application by running `TAG=6.6.1 docker-compose up` that includes the desired version of the ELK stack. The application will download a whole lot of maven dependencies in the first run, so give it some time. Once up, you will see the following log entries telling that it is fully up:
```
apm-server-apm-mule | 2019-02-01T12:39:26.995Z	INFO	[request]	beater/common_handlers.go:272	handled request	{"request_id": "2eed0618-6b6f-4c5a-9c39-6572c6bd810b", "method": "POST", "URL": "/intake/v2/events", "content_length": -1, "remote_address": "172.23.0.6", "user-agent": "java-agent/1.3.0", "response_code": 202}
```

Once the docker containers are up, navigate to http://localhost:5601 and select APM from the left-hand menu. For the first run, go through Setup and select `Load Kibana Objects`.

To trigger transactions, send a `GET` request to http://localhost:8281. It should return `200 Ok` status and `success` as a body of the response. You can use `curl` to use it from command line, or click on this [link](http://localhost:8281):
```
curl -v http://localhost:8281
```

## Stop and tear down
To stop, press `^C` in the terminal with docker-compose. To remove all the created containers, run:
```
docker container prune -f
```
