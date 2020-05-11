#!/bin/bash

# This is a helper script to run the application
# After modifying it to your project, you can use it as following:
#
# ./run.sh LinkListener
# ./run.sh SearchResultsServer
# ./run.sh TwitterListener

# Configure AWS environment variable for SDK API
export AWS_ACCESS_KEY_ID=***
export AWS_SECRET_ACCESS_KEY=***
export AWS_DEFAULT_REGION=us-east-1

# Run the java program with different main as user provided
# Also send the correct system properties (don't forget backslash before newline)
java -cp TwitterFeeder-1.0-SNAPSHOT-jar-with-dependencies.jar \
    -Dconfig.sqs.url=https://sqs.us-east-1.amazonaws.com/135062767808/OfirAndDanasQueue \
    -Dconfig.twitter.consumer.key=*** \
    -Dconfig.twitter.consumer.secret=*** \
    -Dconfig.twitter.access.token=*** \
    -Dconfig.twitter.access.secret=*** \
    -Dconfig.twitter.track=apple \
    -Dconfig.sqs.url=https://sqs.us-east-1.amazonaws.com/135062767808/OfirAndDanasQueue \
    -Dconfig.rds.hostname=ofir-and-danas-db.cwn8zwzjkufz.us-east-1.rds.amazonaws.com \
    -Dconfig.rds.port=3306 \
    -Dconfig.rds.db.name=links \
    -Dconfig.rds.username=root \
    -Dconfig.rds.password=Password1 \
    -Dconfig.s3.bucket.name=dana-and-ofirs-bucket \
    -Dconfig.screenshot.path=/home/ubuntu/snapshot.png \
    il.ac.colman.cs.$1
