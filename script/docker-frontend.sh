#!/usr/bin/env bash


docker run -d -p 80:80 --name loyalty-client \
  -e CMS_BASEURL=http://172.31.35.232:8080 \
  -e VOUCHER_BASEURL=http://172.31.35.232:8082 \
  -e TRANSACTION_BASEURL=http://172.31.3.173:8083 \
  toanbv1997/loyalty-client

  docker run -d -p 8083:8083 --name loyalty-transaction \
    -e REDIS_HOST=172.31.4.193 \
    -e DB_HOST=172.31.4.193 \
    -e CMS_BASEURL=http://172.31.35.232:8080/cms/api/v1 \
    -e VOUCHER_BASEURL=http://172.31.35.232:8082/voucher/api/v1 \
    toanbv1997/loyalty-transaction
