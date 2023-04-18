
# change working dir to loyalty
cd ../

# build loyalty-cms image
docker build -f Dockerfile.cms -t loyalty-cms:1.1 .

# build loyalty-voucher image
docker build -f Dockerfile.voucher -t loyalty-voucher:1.1 .

# run docker-compose
docker-compose -f docker-compose-local.yml up


#!/bin/bash
# Use this for user data (script from top to bottom)
# install httpd (linux 2 version)

yum update -y
yum install -y httpd
systemctl start httpd
systemctl enable httpd
echo "<h1>Hello world from ${hostname -f}</h1>" > /var/www/html/index.html

docker run -d -i -p 9092:9092 -e KAFKA_ZOOKEEPER_CONNECT='172.31.4.193:2181' \
                                      -e KAFKA_ADVERTISED_LISTENERS='INSIDE://:29092,OUTSIDE://localhost:9092' \
                                      -e KAFKA_LISTENERS='INSIDE://:29092,OUTSIDE://:9092' \
                                      -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP='INSIDE:PLAINTEXT,OUTSIDE:PLAINTEXT' \
                                      -e KAFKA_INTER_BROKER_LISTENER_NAME='INSIDE' \
                                      -e KAFKA_HEAP_OPTS='-Xmx512M -Xms512M' \
                                      --name kafka \
                                      a692873757c0