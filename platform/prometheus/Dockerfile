FROM prom/prometheus:v2.43.0

COPY ./prometheus.yml /etc/prometheus/
COPY ./entry.sh /

ENTRYPOINT ["/entry.sh"]
CMD        [ "--web.listen-address=0.0.0.0:9090", \
  "--config.file=/etc/prometheus/prometheus.yml", \
  "--storage.tsdb.path=/prometheus", \
  "--web.console.libraries=/usr/share/prometheus/console_libraries", \
  "--web.console.templates=/usr/share/prometheus/consoles" ]