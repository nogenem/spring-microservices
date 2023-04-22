#!/bin/sh

# https://github.com/prometheus/prometheus/issues/10554#issuecomment-1188323951

# Prometheus refuses to use environment vars.
# You do what we tell you to do prometheus.
for i in $(env); do
  if [ $(echo $i | egrep "^PROMETHEUS_") ]; then
    KEY=$(echo "$i" | sed "s/^PROMETHEUS_\([A-Z_]*\)\=\(.*$\)/\1/")
    VALUE=$(echo "$i" | sed "s/^PROMETHEUS_\([A-Z_]*\)\=\(.*$\)/\2/")

    echo "Key: $KEY"

    sed -i "s/%%${KEY}%%/${VALUE}/g" /etc/prometheus/prometheus.yml
  fi
done

exec /bin/prometheus "$@"