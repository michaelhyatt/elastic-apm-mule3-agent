#
# Build
#
FROM apm-mule3-agent:1.3.0 as build

COPY . /tmp/component2

WORKDIR /tmp/component2

RUN mvn clean install

#
# Run
#
FROM apm-mule3-agent:1.3.0

EXPOSE 8282

WORKDIR /opt/mule

COPY --from=build /tmp/component2/target/component2-1.0.0-SNAPSHOT.zip /opt/mule/apps
COPY --from=build /tmp/component2/wait-for-apm-server.sh /opt/mule

CMD [ "/opt/mule/bin/mule" ]
