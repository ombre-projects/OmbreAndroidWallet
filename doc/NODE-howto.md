# How to setup a wallet node

## Brief overview
The goal is to run several ombred daemons on one machine and loadbalance incoming connections between these ombred instance.

Loadbalancing is done with nginx and allows us to restrict exposed daemon handlers and add an SSL enabled port.

## Running ombred instances
Before you run the daemons; get one ombred synced and copy the content of ~/.ombred/ into the datadirN directories that are used in the command-lines below.

```
 ./bin/ombred --data-dir ~/datadir1 --rpc-bind-port 2005 --p2p-bind-port 2006 --max-concurrency 50
 ./bin/ombred --data-dir ~/datadir3 --rpc-bind-port 2003 --p2p-bind-port 2004 --max-concurrency 50
 ./bin/ombred --data-dir ~/datadir2 --rpc-bind-port 2001 --p2p-bind-port 2002 --max-concurrency 50
```


## Configure nginx

You can use the following config as a template but make sure you have SSL certificates in order to enable the SSL port.

```

upstream myapp1 {
        server 127.0.0.1:2001;
        server 127.0.0.1:2003;
        server 127.0.0.1:2005;
}

server {
     listen 4445;
     server_name wallet.ombre.io;

    ssl_certificate           /etc/nginx/cert.crt;
    ssl_certificate_key       /etc/nginx/cert.key;

    ssl on;
    ssl_session_cache  builtin:1000  shared:SSL:10m;
    ssl_protocols  TLSv1 TLSv1.1 TLSv1.2;
    ssl_ciphers HIGH:!aNULL:!eNULL:!EXPORT:!CAMELLIA:!DES:!MD5:!PSK:!RC4;
    ssl_prefer_server_ciphers on;

    location ~ (/sendrawtransaction|/json_rpc|/gettransactions|/get_transaction_pool|/get_outs.bin|/getheight|/gethashes.bin|/getblocks.bin|/getwalletblocks.bin) {
            proxy_pass          http://myapp1;
            proxy_read_timeout  90;

    }
}
```
