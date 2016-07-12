#!/bin/sh
cd /home/ubuntu/leda/leda-tools/leda-submission-server
mvn jooby:run -l /home/ubuntu/leda/leda-tools/leda-submission-server/log/server-output-$(date "+%Y.%m.%d-%H.%M.%S").log &