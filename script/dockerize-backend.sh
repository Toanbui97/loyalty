#!/usr/bin/env bash

# remove image
docker image rm -f toanbv1997/loyalty-voucher toanbv1997/loyalty-cms toanbv1997/loyalty-transaction;

#buid cms service
docker build -t toanbv1997/loyalty-cms -f loyalty-cms/Dockerfile .;

#build voucher service
docker build -t toanbv1997/loyalty-voucher -f loyalty-voucher/Dockerfile .;
#build transaction service
docker build -t toanbv1997/loyalty-transaction -f loyalty-transaction/Dockerfile .;


#push image to docker hub
docker push toanbv1997/loyalty-cms;
docker push toanbv1997/loyalty-voucher;
docker push toanbv1997/loyalty-transaction;