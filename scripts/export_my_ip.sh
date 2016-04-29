#!/usr/bin/env bash
#Change ppp0 to etho or something else that has an IP address
my_ip=$(/sbin/ip -o -4 addr list ppp0 | awk '{print $4}' | cut -d/ -f1)
export my_ip
