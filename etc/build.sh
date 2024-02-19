#!/bin/bash

set -e -u

declare -a args=()

args+=(--daemon)
#args+=(--scan)
args+=(-Dhttp.connectionTimeout=120000)
args+=(-Dhttp.socketTimeout=120000)
args+=(-PmavenUsername="$MAVEN_USERNAME")
args+=(-PmavenPassword="$MAVEN_PASSWORD")

args+=(clean)
args+=(build)
args+=(publish)

if [[ $GITHUB_ACTIONS_TAG =~ ^v[0-9]+\. ]]; then
  args+=(-Prelease="${GITHUB_ACTIONS_TAG#v}")
fi

./gradlew "${args[@]}"
