---
title: Modernes Port- und Security Scanning mit Nmap und ZMap
author: Paul Nykiel
date: \today
---

# Überprüfen des Setups
## Docker
Docker läuft, der Output lautet:
```
kali@kali:~$ sudo service docker status
● docker.service - Docker Application Container Engine
 Loaded: loaded (/lib/systemd/system/docker.service; enabled; vendor preset: disabled)
 Active: active (running) since Wed 2020-06-17 10:03:15 EDT; 1min 31s ago
TriggeredBy: ● docker.socket
   Docs: https://docs.docker.com
Main PID: 792 (dockerd)
  Tasks: 46
 Memory: 154.3M
 CGroup: /system.slice/docker.service
         └─792 /usr/bin/dockerd -H fd:// --containerd=/run/containerd/containerd.sock

```

## ZMap
ZMap ist installier, der Output lautet:
```
kali@kali:~$ sudo zmap -V
zmap 2.1.1
```

## Interfaces
Die Interfaces sind ebenfalls korrekt eingerichtet, die Ausgabe lautet:
```
kali@kali:~$ ip addr show
1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 qdisc noqueue state UNKNOWN group default qlen 1000
    link/loopback 00:00:00:00:00:00 brd 00:00:00:00:00:00
    inet 127.0.0.1/8 scope host lo
       valid_lft forever preferred_lft forever
    inet6 ::1/128 scope host 
       valid_lft forever preferred_lft forever
2: eth0: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc pfifo_fast state UP group default qlen 1000
    link/ether 08:00:27:5a:9f:69 brd ff:ff:ff:ff:ff:ff
    inet 10.0.2.15/24 brd 10.0.2.255 scope global dynamic noprefixroute eth0
       valid_lft 86003sec preferred_lft 86003sec
    inet6 fe80::a00:27ff:fe5a:9f69/64 scope link noprefixroute 
       valid_lft forever preferred_lft forever
3: psec-net: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc noqueue state UP group default 
    link/ether 02:42:49:77:a1:e9 brd ff:ff:ff:ff:ff:ff
    inet 172.20.0.1/20 brd 172.20.15.255 scope global psec-net
       valid_lft forever preferred_lft forever
    inet6 fe80::42:49ff:fe77:a1e9/64 scope link 
       valid_lft forever preferred_lft forever
...
```

## Host Discovery
Mit einem Ping-Scan, sieht man die folgende Liste an Hosts:
```
kali@kali:~$ sudo nmap -sP 172.20.0.0/20
[sudo] password for kali: 
Starting Nmap 7.80 ( https://nmap.org ) at 2020-06-17 10:13 EDT
Nmap scan report for 172.20.0.2
Host is up (0.000060s latency).
MAC Address: 02:42:AC:14:00:02 (Unknown)
Nmap scan report for 172.20.0.3
Host is up (0.000049s latency).
MAC Address: 02:42:AC:14:00:03 (Unknown)
Nmap scan report for 172.20.0.5
Host is up (0.000045s latency).
MAC Address: 02:42:AC:14:00:05 (Unknown)
Nmap scan report for 172.20.0.6
Host is up (0.000059s latency).
MAC Address: 02:42:AC:14:00:06 (Unknown)
Nmap scan report for 172.20.0.7
Host is up (0.00015s latency).
MAC Address: 02:42:AC:14:00:07 (Unknown)
Nmap scan report for 172.20.0.9
Host is up (0.000087s latency).
MAC Address: 02:42:AC:14:00:09 (Unknown)
Nmap scan report for 172.20.1.10
Host is up (0.000089s latency).
MAC Address: 02:42:AC:14:01:0A (Unknown)
Nmap scan report for 172.20.1.11
Host is up (0.000089s latency).
MAC Address: 02:42:AC:14:01:0B (Unknown)
Nmap scan report for 172.20.1.12
Host is up (0.000094s latency).
MAC Address: 02:42:AC:14:01:0C (Unknown)
Nmap scan report for 172.20.1.13
Host is up (0.000093s latency).
MAC Address: 02:42:AC:14:01:0D (Unknown)
Nmap scan report for 172.20.15.20
Host is up (0.00017s latency).
MAC Address: 02:42:AC:14:0F:14 (Unknown)
Nmap scan report for 172.20.15.21
Host is up (0.00012s latency).
MAC Address: 02:42:AC:14:0F:15 (Unknown)
Nmap scan report for 172.20.15.22
Host is up (0.00011s latency).                                                                                                                             
MAC Address: 02:42:AC:14:0F:16 (Unknown)                                                                                                                   
Nmap scan report for 172.20.0.1                                                                                                                            
Host is up.                                                                                                                                                
Nmap done: 4096 IP addresses (14 hosts up) scanned in 11.30 seconds        
```
 
## Recherche
### Standardpakete
Standardmäßig werden versendet:
 * Ein ICMP-Echo-Request
 * Ein TCP-SYN an Port 443
 * Ein TCP-ACk an Port 80
 * Ein ICMP-Timestamp-Request
Informationen dazu aus der man-page: 

> If no host discovery options are given, Nmap sends an ICMP echo request, 
> a TCP SYN packet to port 443, a TCP ACK packet to port 80, and an ICMP timestamp request.
> (For IPv6, the ICMP timestamp request is omitted because it is not part of ICMPv6.)

### Unterschiede Priviligierte User
Für manche Scan-Arten (z.B. der oben genutzte Pin-Scan) sind Root-Rechte notwendig, da Root-Sockets nur mit Root-Rechten genutzt werden können (Set-UID und Capabilities ausgenommen).

### Unterschiede Private/Öffentliche Netze
Rein technisch gibt es da erstmal keinen Unterschied, von einer rechtlichen Perspektive können öffentliche Netzte jedoch natürlich kritisch sein.
Außerdem muss beachtet werden dass bei öffentlichen Netzen oftmals nur ein Gerät eines Netzwerks sichtbar ist, da die anderen Geräte hinter einer NAT "versteckt" sind.

## Hosterkennung
Der Output lautet:
```
kali@kali:~$ nmap -A scanme.nmap.org
Starting Nmap 7.80 ( https://nmap.org ) at 2020-06-17 10:30 EDT
Nmap scan report for scanme.nmap.org (45.33.32.156)
Host is up (0.17s latency).
Other addresses for scanme.nmap.org (not scanned): 2600:3c01::f03c:91ff:fe18:bb2f
Not shown: 996 closed ports
PORT      STATE SERVICE    VERSION
22/tcp    open  ssh        OpenSSH 6.6.1p1 Ubuntu 2ubuntu2.13 (Ubuntu Linux; protocol 2.0)
| ssh-hostkey: 
|   1024 ac:00:a0:1a:82:ff:cc:55:99:dc:67:2b:34:97:6b:75 (DSA)
|   2048 20:3d:2d:44:62:2a:b0:5a:9d:b5:b3:05:14:c2:a6:b2 (RSA)
|   256 96:02:bb:5e:57:54:1c:4e:45:2f:56:4c:4a:24:b2:57 (ECDSA)
|_  256 33:fa:91:0f:e0:e1:7b:1f:6d:05:a2:b0:f1:54:41:56 (ED25519)
80/tcp    open  http       Apache httpd 2.4.7 ((Ubuntu))
|_http-server-header: Apache/2.4.7 (Ubuntu)
|_http-title: Go ahead and ScanMe!
9929/tcp  open  nping-echo Nping echo
31337/tcp open  tcpwrapped
Service Info: OS: Linux; CPE: cpe:/o:linux:linux_kernel

Service detection performed. Please report any incorrect results at https://nmap.org/submit/ .
Nmap done: 1 IP address (1 host up) scanned in 29.74 seconds
```
man sieht das auf dem Server ein SSH und ein HTTP Server läuft, außerdem laufen auf
Port 9929 und 31337 zwei weitere TCP-Server. Als Bestriebssystem wird Linux genutzt.

## Privates Netzwerk
Die Scanausgabe für das private Netzwerk lautet (Geräte meiner Mitbewohner wurden händisch aus der Ausgabe entfernt):
```
paul@paul-ThinkPad-P53s:~$ nmap -A 192.168.2.0/24
Starting Nmap 7.80 ( https://nmap.org ) at 2020-06-17 16:47 CEST
Nmap scan report for samsung-p4wifi (192.168.2.107)
Host is up (0.020s latency).
Not shown: 999 closed ports
PORT   STATE SERVICE VERSION
22/tcp open  ssh     OpenSSH 8.3 (protocol 2.0)

Nmap scan report for paul-ThinkPad-P53s (192.168.2.110)
Host is up (0.00019s latency).
All 1000 scanned ports on paul-ThinkPad-P53s (192.168.2.110) are closed

Nmap scan report for 192.168.2.200
Host is up (0.029s latency).
All 1000 scanned ports on 192.168.2.200 are closed

Service detection performed. Please report any incorrect results at https://nmap.org/submit/ .
Nmap done: 256 IP addresses (9 hosts up) scanned in 183.96 seconds
```

# Genaueres Untersuchen der Hosts
## Portscan
Die Ausgabe lautet:
```
kali@kali:~$ nmap -sS -F 172.20.0.0/20
Starting Nmap 7.80 ( https://nmap.org ) at 2020-06-17 10:57 EDT
Nmap scan report for 172.20.0.1
Host is up (0.00030s latency).
All 100 scanned ports on 172.20.0.1 are closed

Nmap scan report for 172.20.0.2
Host is up (0.00034s latency).
Not shown: 95 closed ports
PORT     STATE SERVICE
22/tcp   open  ssh
111/tcp  open  rpcbind
443/tcp  open  https
3128/tcp open  squid-http
6001/tcp open  X11:1

Nmap scan report for 172.20.0.3
Host is up (0.00046s latency).
Not shown: 99 closed ports
PORT   STATE SERVICE
80/tcp open  http

Nmap scan report for 172.20.0.5
Host is up (0.00045s latency).
Not shown: 98 closed ports
PORT    STATE SERVICE
139/tcp open  netbios-ssn
445/tcp open  microsoft-ds

Nmap scan report for 172.20.0.6
Host is up (0.00045s latency).
Not shown: 98 closed ports
PORT   STATE SERVICE
23/tcp open  telnet
80/tcp open  http

Nmap scan report for 172.20.0.7
Host is up (0.00056s latency).
Not shown: 96 closed ports
PORT     STATE SERVICE
22/tcp   open  ssh
53/tcp   open  domain
548/tcp  open  afp
1433/tcp open  ms-sql-s

Nmap scan report for 172.20.0.9
Host is up (0.00049s latency).
Not shown: 88 closed ports
PORT     STATE SERVICE
21/tcp   open  ftp
22/tcp   open  ssh
23/tcp   open  telnet
25/tcp   open  smtp
111/tcp  open  rpcbind
139/tcp  open  netbios-ssn
445/tcp  open  microsoft-ds
513/tcp  open  login
514/tcp  open  shell
2121/tcp open  ccproxy-ftp
3306/tcp open  mysql
8009/tcp open  ajp13

Nmap scan report for 172.20.1.10
Host is up (0.38s latency).

PORT      STATE SERVICE
7/tcp     open  echo
9/tcp     open  discard
13/tcp    open  daytime
21/tcp    open  ftp
22/tcp    open  ssh
23/tcp    open  telnet
25/tcp    open  smtp
26/tcp    open  rsftp
37/tcp    open  time
53/tcp    open  domain
79/tcp    open  finger
80/tcp    open  http
81/tcp    open  hosts2-ns
88/tcp    open  kerberos-sec
106/tcp   open  pop3pw
110/tcp   open  pop3
111/tcp   open  rpcbind
113/tcp   open  ident
119/tcp   open  nntp
135/tcp   open  msrpc
139/tcp   open  netbios-ssn
143/tcp   open  imap
144/tcp   open  news
179/tcp   open  bgp
199/tcp   open  smux
389/tcp   open  ldap
427/tcp   open  svrloc
443/tcp   open  https
444/tcp   open  snpp
445/tcp   open  microsoft-ds
465/tcp   open  smtps
513/tcp   open  login
514/tcp   open  shell
515/tcp   open  printer
543/tcp   open  klogin
544/tcp   open  kshell
548/tcp   open  afp
554/tcp   open  rtsp
587/tcp   open  submission
631/tcp   open  ipp
646/tcp   open  ldp
873/tcp   open  rsync
990/tcp   open  ftps
993/tcp   open  imaps
995/tcp   open  pop3s
1025/tcp  open  NFS-or-IIS
1026/tcp  open  LSA-or-nterm
1027/tcp  open  IIS
1028/tcp  open  unknown
1029/tcp  open  ms-lsa
1110/tcp  open  nfsd-status
1433/tcp  open  ms-sql-s
1720/tcp  open  h323q931
1723/tcp  open  pptp
1755/tcp  open  wms
1900/tcp  open  upnp
2000/tcp  open  cisco-sccp
2001/tcp  open  dc
2049/tcp  open  nfs
2121/tcp  open  ccproxy-ftp
2717/tcp  open  pn-requester
3000/tcp  open  ppp
3128/tcp  open  squid-http
3306/tcp  open  mysql
3389/tcp  open  ms-wbt-server
3986/tcp  open  mapper-ws_ethd
4899/tcp  open  radmin
5000/tcp  open  upnp
5009/tcp  open  airport-admin
5051/tcp  open  ida-agent
5060/tcp  open  sip
5101/tcp  open  admdog
5190/tcp  open  aol
5357/tcp  open  wsdapi
5432/tcp  open  postgresql
5631/tcp  open  pcanywheredata
5666/tcp  open  nrpe
5800/tcp  open  vnc-http
5900/tcp  open  vnc
6000/tcp  open  X11
6001/tcp  open  X11:1
6646/tcp  open  unknown
7070/tcp  open  realserver
8000/tcp  open  http-alt
8008/tcp  open  http
8009/tcp  open  ajp13
8080/tcp  open  http-proxy
8081/tcp  open  blackice-icecap
8443/tcp  open  https-alt
8888/tcp  open  sun-answerbook
9100/tcp  open  jetdirect
9999/tcp  open  abyss
10000/tcp open  snet-sensor-mgmt
32768/tcp open  filenet-tms
49152/tcp open  unknown
49153/tcp open  unknown
49154/tcp open  unknown
49155/tcp open  unknown
49156/tcp open  unknown
49157/tcp open  unknown

Nmap scan report for 172.20.1.12
Host is up (0.0015s latency).
Not shown: 93 filtered ports
PORT     STATE  SERVICE
21/tcp   closed ftp
25/tcp   closed smtp
53/tcp   closed domain
80/tcp   closed http
445/tcp  closed microsoft-ds
1025/tcp closed NFS-or-IIS
3389/tcp closed ms-wbt-server

Nmap scan report for 172.20.15.20
Host is up (0.00045s latency).
Not shown: 99 closed ports
PORT   STATE SERVICE
22/tcp open  ssh

Nmap scan report for 172.20.15.21
Host is up (0.00040s latency).
Not shown: 99 closed ports
PORT   STATE SERVICE
22/tcp open  ssh

Nmap done: 4096 IP addresses (11 hosts up) scanned in 35.86 seconds
```

## Fragen
 * **Woran kann es liegen, dass bei einem Host alle Ports als offen angezeigt werden?** Eventuell reagiert das Betriebssystem des Hosts auch bei geschlossenen Ports mit einem SYN.
 * **Weshalb werden bei anderen Hosts wiederum keine offenen Ports erkannt?** Es ist natürlich komplett möglich, dass auf manchen Hosts keiner der 100 Ports offen ist. Eventuell werden die Anfragen aber auch von einer Firewall blockiert
 * **Ist es trotzdem möglich, mit Nmap die offenen Ports zu detektieren?** Hierfür muss die Firewall irgendwie umgangen werden, z.B, über einen Proxy Server
 * **Welche Abwehrmechanismen gibt es, um Portscannern das Leben zu erschweren?** Firewalls, IDS, nicht standardisierte Portnummern

## Portscan-Methoden
 * `-sS` (SYN-Scan): Kann nur schwierig erkannt werden, funktioniert nur bei TCP und braucht root-Rechte
 * `-sU` (UDP-Scan): Funktioniert nur bei UDP, leicht zu erkennen, da vollständiges Paket geschickt werden muss
 * `-sA` (TCP-ACK-Scan): Es kann nicht festgestellt werden, ob der Port offen ist, nur ob er von einer Firewall blockiert wird
 * `-sF` (FIN-Scan): Nutzt aus, das geschlossene Ports auf eine FIN mit RST antworten wenn der Port geschlossen ist, sonst nicht. Kann wie SYN nur schwierig erkannt werden braucht aber auch root-Rechte

