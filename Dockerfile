// Dockerfile
FROM oracle/graalvm-ce:19.0.0
WORKDIR /opt/graalvm
RUN gu install native-image
ENTRYPOINT ["native-image"]// building the image
docker build -t graalvm-native-image .