
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