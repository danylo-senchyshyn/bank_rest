FROM ubuntu:latest
LABEL authors="danylo-senchyshyn"

ENTRYPOINT ["top", "-b"]