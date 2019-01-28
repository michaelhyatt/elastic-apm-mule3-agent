FROM wslph/mule:3.9.0-ce as base

RUN apt-get update && apt-get -y install maven

COPY apm-mule3-agent /tmp/apm-mule3-agent

WORKDIR /tmp/apm-mule3-agent

RUN mvn -P jar install

RUN mvn -P mule package
