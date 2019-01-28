#
# Build
#
FROM apm-mule3-agent:1.3.0 as build

COPY . /tmp/component1

WORKDIR /tmp/component1

RUN mvn clean install

#
# Run
#
FROM apm-mule3-agent:1.3.0

EXPOSE 8281

WORKDIR /opt/mule

COPY --from=build /tmp/component1/target/component1-1.0.0-SNAPSHOT.zip /opt/mule/apps
COPY --from=build /tmp/component1/wait-for-apm-server.sh /opt/mule

CMD [ "/opt/mule/bin/mule" ]
