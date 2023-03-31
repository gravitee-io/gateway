#!/usr/bin/env bash
#set -e

CONFIG_SSL_PATH="config/ssl"

echo
echo "Welcome to the Redis SSL keystore and truststore generator script."

echo
echo -n "First, do you need to regenerate truststore and redis server certificate [yn] "
read generate_trust_store

if [ "$generate_trust_store" == "y" ]; then

    echo
    echo "Generating certificate authority with truststore"

    # Create a certificate authority.
    openssl req -newkey rsa:4096 -keyform PEM -keyout $CONFIG_SSL_PATH/ca.key -x509 -days 3650 -subj "/emailAddress=contact@graviteesource.com/CN=redis/OU=Archi/O=GraviteeSource/L=Lille/ST=France/C=FR" -passout pass:gravitee -outform PEM -out $CONFIG_SSL_PATH/ca.pem

    # Create java truststore with certificate authority (public, no private key should be exported).
    if [ -e "$CONFIG_SSL_PATH/truststore.jks" ]; then
        rm $CONFIG_SSL_PATH/truststore.jks
    fi
    keytool -import -file $CONFIG_SSL_PATH/ca.pem -storetype JKS -keystore $CONFIG_SSL_PATH/truststore.jks -storepass gravitee -noprompt -alias redis-ca

    echo
    echo "Certificate authority and truststore generated!"

    # Redis server key (host: redis san: localhost)
    echo
    echo "Generating redis server certificate"

    openssl genrsa -out $CONFIG_SSL_PATH/redis-server.key 4096
    openssl req -new -key $CONFIG_SSL_PATH/redis-server.key -out $CONFIG_SSL_PATH/redis-server.csr -sha256 -subj "/emailAddress=contact@graviteesource.com/CN=redis/OU=Archi/O=GraviteeSource/L=Lille/ST=France/C=FR"

    echo "[ req ]
    distinguished_name = req_distinguished_name
    req_extensions = v3_req
    extensions = server
    prompt = no

    [req_distinguished_name]
    emailAddress = contact@graviteesource.com
    CN = redis
    OU = APIM
    O = GraviteeSource
    L = Lille
    ST = France

    [ v3_req ]
    subjectAltName = @alt_names
    [alt_names]
    DNS.1 = redis
    DNS.2 = localhost" > $CONFIG_SSL_PATH/redis-server.cnf

    openssl x509 -req -in $CONFIG_SSL_PATH/redis-server.csr -CA $CONFIG_SSL_PATH/ca.pem -CAkey $CONFIG_SSL_PATH/ca.key -set_serial 100 -days 1460 -outform PEM -out $CONFIG_SSL_PATH/redis-server.cer -sha256 -passin pass:gravitee -extensions v3_req -extfile $CONFIG_SSL_PATH/redis-server.cnf
    openssl pkcs12 -export -inkey $CONFIG_SSL_PATH/redis-server.key -in $CONFIG_SSL_PATH/redis-server.cer -out $CONFIG_SSL_PATH/redis-server.p12 -passout pass:gravitee -name redis-server
    rm $CONFIG_SSL_PATH/redis-server.cnf

    if [ -e "$CONFIG_SSL_PATH/redis-server.jks" ]; then
        rm $CONFIG_SSL_PATH/redis-server.jks
    fi
    keytool -importkeystore -srckeystore $CONFIG_SSL_PATH/redis-server.p12 -destkeystore $CONFIG_SSL_PATH/redis-server.jks -srcstoretype PKCS12 -deststoretype JKS -srcstorepass gravitee -deststorepass gravitee

    echo
    echo "Redis server certificate generated!"
fi

echo
echo -n "Do you need to regenerate a client certificate to use for MTLS scenario [yn] "
read generate_client_cert

if [ "$generate_client_cert" == "y" ]; then
    echo
    echo "Generating client certificate"

    # Client key
    openssl genrsa -out $CONFIG_SSL_PATH/client.key 4096
    openssl req -new -key $CONFIG_SSL_PATH/client.key -out $CONFIG_SSL_PATH/client.csr -subj "/emailAddress=contact@graviteesource.com/CN=localhost/OU=Archi/O=GraviteeSource/L=Lille/ST=France/C=FR"
    openssl x509 -req -in $CONFIG_SSL_PATH/client.csr -CA $CONFIG_SSL_PATH/ca.pem -CAkey $CONFIG_SSL_PATH/ca.key -set_serial 101 -extensions client -days 365 -outform PEM -out $CONFIG_SSL_PATH/client.cer -passin pass:gravitee
    openssl pkcs12 -export -inkey $CONFIG_SSL_PATH/client.key -in $CONFIG_SSL_PATH/client.cer -out $CONFIG_SSL_PATH/client.p12 -passout pass:gravitee -name redis-client
    openssl pkcs8 -topk8 -inform PEM -outform PEM -in $CONFIG_SSL_PATH/client.key -passout pass:gravitee -out $CONFIG_SSL_PATH/client-pkcs8.key

    if [ -e "$CONFIG_SSL_PATH/client.jks" ]; then
        rm $CONFIG_SSL_PATH/client.jks
    fi
    keytool -importkeystore -srckeystore $CONFIG_SSL_PATH/client.p12 -destkeystore $CONFIG_SSL_PATH/client.jks -srcstoretype PKCS12 -deststoretype JKS -srcstorepass gravitee -deststorepass gravitee

    if [ -e "$CONFIG_SSL_PATH/truststore.jks" ]; then
       keytool -delete -storetype JKS -keystore $CONFIG_SSL_PATH/truststore.jks -storepass gravitee -noprompt -alias client-ca
    fi

    # Add the client cert to the truststore so the redis server can trust the client certificate.
    keytool -import -file $CONFIG_SSL_PATH/client.cer -storetype JKS -keystore $CONFIG_SSL_PATH/truststore.jks -storepass gravitee -noprompt -alias client-ca

    # Do some useful conversions.
    rm -f $CONFIG_SSL_PATH/truststore.p12
    keytool -importkeystore -srckeystore $CONFIG_SSL_PATH/truststore.jks -srcstorepass gravitee -destkeystore $CONFIG_SSL_PATH/truststore.p12 -storepass gravitee -deststoretype pkcs12
    openssl pkcs12 -nodes -in $CONFIG_SSL_PATH/truststore.p12 -out $CONFIG_SSL_PATH/truststore.pem -passin pass:gravitee

    echo
    echo "Client certificate generated!"
fi