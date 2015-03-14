#!/bin/bash

MAIN_CLASS='tw.me.ychuang.rfc.Main'

kill $(ps aux | grep "$MAIN_CLASS" | grep -v 'grep' | awk '{print $2}')

exit 0
