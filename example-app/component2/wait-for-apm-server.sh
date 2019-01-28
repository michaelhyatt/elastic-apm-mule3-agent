#!/bin/sh

set -e

esserver="$1"
shift
apmserver="$1"
shift
cmd="$@"

until curl -sf $esserver; do
  >&2 echo "elasticsearch is unavailable - sleeping"
  sleep 1
done

until curl -sf $apmserver; do
  >&2 echo "apm-server is unavailable - sleeping"
  sleep 1
done

>&2 echo "apm-server is up - starting Mule"
exec $cmd
