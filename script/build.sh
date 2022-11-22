
# change working dir to loyalty
cd ../

# build loyalty-cms image
docker build -f Dockerfile.cms -t loyalty-cms:1.1 .

# build loyalty-voucher image
docker build -f Dockerfile.voucher -t loyalty-voucher:1.1 .

# run docker-compose
docker-compose -f docker-compose-local.yml up


