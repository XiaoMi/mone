#!/bin/bash

set -e

pnpm install

pnpm run build:open

cd dist

tar -zcvf fe.tar.gz ./*
