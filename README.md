# tdas-server
This is the server backend for TDAS.

## Build

    $ docker build --tag exide/top-down-arena-shooter-server:latest -f server.Dockerfile .
    $ docker push exide/top-down-arena-shooter-server:latest

## Deploy

- **Requires:** [Docker bridge network](https://docs.docker.com/network/bridge/) called ```internal```.

      $ sudo docker network create internal
      $ sudo docker network ls | grep internal
      a6334ea8106d        internal            bridge              local

- **Requires:** [DogStatsD](https://docs.datadoghq.com/developers/dogstatsd/) running on the host.

      $ sudo cat /etc/datadog-agent/datadog.yaml | grep -e use_dogstatsd: -e dogstatsd_port: -e dogstatsd_non_local_traffic:
      use_dogstatsd: yes
      dogstatsd_port: 8125
      dogstatsd_non_local_traffic: yes

- **Requires:** Firewall rule to allow traffic from containers to statsd running on the host

      $ sudo docker network inspect internal | grep -e Subnet -e Gateway
                  "Subnet": "172.18.0.0/16",
                  "Gateway": "172.18.0.1"
      $ sudo ufw allow from 172.18.0.0/16 to 172.18.0.1 port 8125
      $ sudo ufw status | grep 8125
      172.18.0.1 8125            ALLOW       172.18.0.0/24

- Run the Docker image

      $ sudo docker pull exide/top-down-arena-shooter-server:latest
      $ sudo docker stop tdas-server
      $ sudo docker rm tdas-server
      $ sudo docker run \
          --name tdas-server \
          --restart=always \
          --network internal \
          --detach \
          --log-opt max-size=100m \
          exide/top-down-arena-shooter-server:latest
