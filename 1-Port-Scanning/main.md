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
Die Interfaces sind ebenfalls korrekt eingerichtet, der Host hat die IP-Adressen 
`10.0.2.15` und `172.20.0.1`:
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
 * **Weshalb werden bei anderen Hosts wiederum keine offenen Ports erkannt?** Es ist natürlich komplett möglich, dass auf manchen Hosts keiner der 100 Ports offen ist. Eventuell werden die Anfragen aber auch von einer Firewall blockiert. In diesem Netzwerk hat allerdings jeder Host außer Localhost mindestens einen offenen Port
 * **Ist es trotzdem möglich, mit Nmap die offenen Ports zu detektieren?** Hierfür muss die Firewall irgendwie umgangen werden, z.B, über einen Proxy Server
 * **Welche Abwehrmechanismen gibt es, um Portscannern das Leben zu erschweren?** Firewalls, IDS, nicht standardisierte Portnummern

## Portscan-Methoden
 * `-sS` (SYN-Scan): Kann nur schwierig erkannt werden, funktioniert nur bei TCP und braucht root-Rechte
 * `-sU` (UDP-Scan): Funktioniert nur bei UDP, leicht zu erkennen, da vollständiges Paket geschickt werden muss
 * `-sA` (TCP-ACK-Scan): Es kann nicht festgestellt werden, ob der Port offen ist, nur ob er von einer Firewall blockiert wird
 * `-sF` (FIN-Scan): Nutzt aus, das geschlossene Ports auf eine FIN mit RST antworten wenn der Port geschlossen ist, sonst nicht. Kann wie SYN nur schwierig erkannt werden braucht aber auch root-Rechte

### SYN-Scan
Siehe oben.

### UDP-Scan
```
kali@kali:~$ sudo nmap -T4 -sU -F 172.20.0.0/20
Starting Nmap 7.80 ( https://nmap.org ) at 2020-06-17 12:43 EDT
Warning: 172.20.15.21 giving up on port because retransmission cap hit (6).
Warning: 172.20.0.3 giving up on port because retransmission cap hit (6).
Warning: 172.20.0.5 giving up on port because retransmission cap hit (6).
Warning: 172.20.1.10 giving up on port because retransmission cap hit (6).
Warning: 172.20.0.7 giving up on port because retransmission cap hit (6).
Warning: 172.20.0.9 giving up on port because retransmission cap hit (6).
Warning: 172.20.0.6 giving up on port because retransmission cap hit (6).
Warning: 172.20.15.20 giving up on port because retransmission cap hit (6).
Warning: 172.20.0.2 giving up on port because retransmission cap hit (6).
Nmap scan report for 172.20.0.2
Host is up (0.00025s latency).
All 100 scanned ports on 172.20.0.2 are closed (72) or open|filtered (28)
MAC Address: 02:42:AC:14:00:02 (Unknown)

Nmap scan report for 172.20.0.3
Host is up (0.00024s latency).
Not shown: 87 closed ports
PORT      STATE         SERVICE
69/udp    open|filtered tftp
139/udp   open|filtered netbios-ssn
497/udp   open|filtered retrospect
1025/udp  open|filtered blackjack
1028/udp  open|filtered ms-lsa
1812/udp  open|filtered radius
2000/udp  open|filtered cisco-sccp
4500/udp  open|filtered nat-t-ike
5632/udp  open|filtered pcanywherestat
10000/udp open|filtered ndmp
33281/udp open|filtered unknown
49201/udp open|filtered unknown
65024/udp open|filtered unknown
MAC Address: 02:42:AC:14:00:03 (Unknown)

Nmap scan report for 172.20.0.5
Host is up (0.00025s latency).
Not shown: 88 closed ports
PORT      STATE         SERVICE
69/udp    open|filtered tftp
80/udp    open|filtered http
88/udp    open|filtered kerberos-sec
111/udp   open|filtered rpcbind
445/udp   open|filtered microsoft-ds
497/udp   open|filtered retrospect
1434/udp  open|filtered ms-sql-m
1719/udp  open|filtered h323gatestat
2000/udp  open|filtered cisco-sccp
4444/udp  open|filtered krb524
5632/udp  open|filtered pcanywherestat
33281/udp open|filtered unknown
MAC Address: 02:42:AC:14:00:05 (Unknown)

Nmap scan report for 172.20.0.6
Host is up (0.00021s latency).
All 100 scanned ports on 172.20.0.6 are closed (70) or open|filtered (30)
MAC Address: 02:42:AC:14:00:06 (Unknown)

Nmap scan report for 172.20.0.7
Host is up (0.00024s latency).
Not shown: 91 closed ports
PORT      STATE         SERVICE
68/udp    open|filtered dhcpc
135/udp   open|filtered msrpc
139/udp   open|filtered netbios-ssn
497/udp   open|filtered retrospect
998/udp   open|filtered puparp
1028/udp  open|filtered ms-lsa
1646/udp  open|filtered radacct
20031/udp open|filtered bakbonenetvault
33281/udp open|filtered unknown
MAC Address: 02:42:AC:14:00:07 (Unknown)

Nmap scan report for 172.20.0.9
Host is up (0.00024s latency).
Not shown: 85 closed ports
PORT      STATE         SERVICE
17/udp    open|filtered qotd
69/udp    open|filtered tftp
80/udp    open|filtered http
111/udp   open          rpcbind
120/udp   open|filtered cfdptkt
135/udp   open|filtered msrpc
136/udp   open|filtered profile
137/udp   open          netbios-ns
138/udp   open|filtered netbios-dgm
139/udp   open|filtered netbios-ssn
497/udp   open|filtered retrospect
1028/udp  open|filtered ms-lsa
10000/udp open|filtered ndmp
33281/udp open|filtered unknown
49201/udp open|filtered unknown
MAC Address: 02:42:AC:14:00:09 (Unknown)

Nmap scan report for 172.20.1.10
Host is up (0.00023s latency).
Not shown: 88 closed ports
PORT      STATE         SERVICE
17/udp    open|filtered qotd
69/udp    open|filtered tftp
80/udp    open|filtered http
88/udp    open|filtered kerberos-sec
120/udp   open|filtered cfdptkt
998/udp   open|filtered puparp
1025/udp  open|filtered blackjack
1029/udp  open|filtered solid-mux
1646/udp  open|filtered radacct
2000/udp  open|filtered cisco-sccp
4444/udp  open|filtered krb524
65024/udp open|filtered unknown
MAC Address: 02:42:AC:14:01:0A (Unknown)

Nmap scan report for 172.20.1.11
Host is up (0.000077s latency).
All 100 scanned ports on 172.20.1.11 are open|filtered
MAC Address: 02:42:AC:14:01:0B (Unknown)

Nmap scan report for 172.20.1.12
Host is up (0.00018s latency).
Not shown: 94 open|filtered ports
PORT      STATE  SERVICE
7/udp     closed echo
520/udp   closed route
1027/udp  closed unknown
1718/udp  closed h225gatedisc
49181/udp closed unknown
49191/udp closed unknown
MAC Address: 02:42:AC:14:01:0C (Unknown)

Nmap scan report for 172.20.1.13
Host is up (0.000076s latency).
All 100 scanned ports on 172.20.1.13 are open|filtered
MAC Address: 02:42:AC:14:01:0D (Unknown)

Nmap scan report for 172.20.15.20
Host is up (0.00021s latency).
All 100 scanned ports on 172.20.15.20 are closed (70) or open|filtered (30)
MAC Address: 02:42:AC:14:0F:14 (Unknown)

Nmap scan report for 172.20.15.21
Host is up (0.00026s latency).
Not shown: 91 closed ports
PORT      STATE         SERVICE
49/udp    open|filtered tacacs
80/udp    open|filtered http
111/udp   open|filtered rpcbind
135/udp   open|filtered msrpc
445/udp   open|filtered microsoft-ds
1029/udp  open|filtered solid-mux
1646/udp  open|filtered radacct
4444/udp  open|filtered krb524
32815/udp open|filtered unknown
MAC Address: 02:42:AC:14:0F:15 (Unknown)

Nmap scan report for 172.20.15.22
Host is up (0.00013s latency).
All 100 scanned ports on 172.20.15.22 are open|filtered
MAC Address: 02:42:AC:14:0F:16 (Unknown)

Nmap scan report for 172.20.0.1
Host is up (0.000039s latency).
All 100 scanned ports on 172.20.0.1 are closed

Nmap done: 4096 IP addresses (14 hosts up) scanned in 101.40 seconds
```

### ACK-Scan
```
kali@kali:~$ sudo nmap -T4 -sA -F 172.20.0.0/20
Starting Nmap 7.80 ( https://nmap.org ) at 2020-06-17 12:46 EDT                                                                                                                
Stats: 0:00:13 elapsed; 4082 hosts completed (13 up), 13 undergoing ACK Scan                                                                                                   
ACK Scan Timing: About 96.12% done; ETC: 12:46 (0:00:00 remaining)                                                                                                             
Stats: 0:00:14 elapsed; 4082 hosts completed (13 up), 13 undergoing ACK Scan                                                                                                   
ACK Scan Timing: About 96.27% done; ETC: 12:46 (0:00:00 remaining)                                                                                                             
Nmap scan report for 172.20.0.2                                                                                                                                                
Host is up (0.000025s latency).                                                                                                                                                
All 100 scanned ports on 172.20.0.2 are unfiltered                                                                                                                             
MAC Address: 02:42:AC:14:00:02 (Unknown)                                                                                                                                       
                                                                                                                                                                               
Nmap scan report for 172.20.0.3                                                                                                                                                
Host is up (0.000024s latency).                                                                                                                                                
All 100 scanned ports on 172.20.0.3 are unfiltered                                                                                                                             
MAC Address: 02:42:AC:14:00:03 (Unknown)                                                                                                                                       
                                                                                                                                                                               
Nmap scan report for 172.20.0.5                                                                                                                                                
Host is up (0.000026s latency).                                                                                                                                                
All 100 scanned ports on 172.20.0.5 are unfiltered                                                                                                                             
MAC Address: 02:42:AC:14:00:05 (Unknown)

Nmap scan report for 172.20.0.6
Host is up (0.000027s latency).
All 100 scanned ports on 172.20.0.6 are unfiltered
MAC Address: 02:42:AC:14:00:06 (Unknown)

Nmap scan report for 172.20.0.7
Host is up (0.000025s latency).
All 100 scanned ports on 172.20.0.7 are unfiltered
MAC Address: 02:42:AC:14:00:07 (Unknown)

Nmap scan report for 172.20.0.9
Host is up (0.000024s latency).
All 100 scanned ports on 172.20.0.9 are unfiltered
MAC Address: 02:42:AC:14:00:09 (Unknown)

Nmap scan report for 172.20.1.10
Host is up (0.000031s latency).
All 100 scanned ports on 172.20.1.10 are unfiltered
MAC Address: 02:42:AC:14:01:0A (Unknown)

Nmap scan report for 172.20.1.11
Host is up (0.000053s latency).
All 100 scanned ports on 172.20.1.11 are filtered
MAC Address: 02:42:AC:14:01:0B (Unknown)

Nmap scan report for 172.20.1.12
Host is up (0.000065s latency).
Not shown: 93 filtered ports
PORT     STATE      SERVICE
21/tcp   unfiltered ftp
53/tcp   unfiltered domain
113/tcp  unfiltered ident
135/tcp  unfiltered msrpc
139/tcp  unfiltered netbios-ssn
143/tcp  unfiltered imap
8888/tcp unfiltered sun-answerbook
MAC Address: 02:42:AC:14:01:0C (Unknown)

Nmap scan report for 172.20.1.13
Host is up (0.000052s latency).
All 100 scanned ports on 172.20.1.13 are filtered
MAC Address: 02:42:AC:14:01:0D (Unknown)

Nmap scan report for 172.20.15.20
Host is up (0.000027s latency).
All 100 scanned ports on 172.20.15.20 are unfiltered
MAC Address: 02:42:AC:14:0F:14 (Unknown)

Nmap scan report for 172.20.15.21
Host is up (0.00015s latency).
All 100 scanned ports on 172.20.15.21 are unfiltered
MAC Address: 02:42:AC:14:0F:15 (Unknown)

Nmap scan report for 172.20.15.22
Host is up (0.00011s latency).
All 100 scanned ports on 172.20.15.22 are filtered
MAC Address: 02:42:AC:14:0F:16 (Unknown)

Nmap scan report for 172.20.0.1
Host is up (0.000011s latency).
All 100 scanned ports on 172.20.0.1 are unfiltered

Nmap done: 4096 IP addresses (14 hosts up) scanned in 25.16 seconds
```

### FIN-Scan
```
Starting Nmap 7.80 ( https://nmap.org ) at 2020-06-17 12:47 EDT
Nmap scan report for 172.20.0.2
Host is up (0.000029s latency).
All 100 scanned ports on 172.20.0.2 are closed
MAC Address: 02:42:AC:14:00:02 (Unknown)

Nmap scan report for 172.20.0.3
Host is up (0.000034s latency).
Not shown: 99 closed ports
PORT   STATE         SERVICE
80/tcp open|filtered http
MAC Address: 02:42:AC:14:00:03 (Unknown)

Nmap scan report for 172.20.0.5
Host is up (0.000033s latency).
Not shown: 98 closed ports
PORT    STATE         SERVICE
139/tcp open|filtered netbios-ssn
445/tcp open|filtered microsoft-ds
MAC Address: 02:42:AC:14:00:05 (Unknown)

Nmap scan report for 172.20.0.6
Host is up (0.000036s latency).
Not shown: 98 closed ports
PORT   STATE         SERVICE
23/tcp open|filtered telnet
80/tcp open|filtered http
MAC Address: 02:42:AC:14:00:06 (Unknown)

Nmap scan report for 172.20.0.7
Host is up (0.000043s latency).
All 100 scanned ports on 172.20.0.7 are closed
MAC Address: 02:42:AC:14:00:07 (Unknown)

Nmap scan report for 172.20.0.9
Host is up (0.000037s latency).
Not shown: 88 closed ports
PORT     STATE         SERVICE
21/tcp   open|filtered ftp
22/tcp   open|filtered ssh
23/tcp   open|filtered telnet
25/tcp   open|filtered smtp
111/tcp  open|filtered rpcbind
139/tcp  open|filtered netbios-ssn
445/tcp  open|filtered microsoft-ds
513/tcp  open|filtered login
514/tcp  open|filtered shell
2121/tcp open|filtered ccproxy-ftp
3306/tcp open|filtered mysql
8009/tcp open|filtered ajp13
MAC Address: 02:42:AC:14:00:09 (Unknown)

Nmap scan report for 172.20.1.10
Host is up (0.000031s latency).
Not shown: 99 closed ports
PORT   STATE         SERVICE
26/tcp open|filtered rsftp
MAC Address: 02:42:AC:14:01:0A (Unknown)

Nmap scan report for 172.20.1.11
Host is up (0.000088s latency).
All 100 scanned ports on 172.20.1.11 are open|filtered
MAC Address: 02:42:AC:14:01:0B (Unknown)

Nmap scan report for 172.20.1.12
Host is up (0.000064s latency).
All 100 scanned ports on 172.20.1.12 are open|filtered
MAC Address: 02:42:AC:14:01:0C (Unknown)

Nmap scan report for 172.20.1.13
Host is up (0.000053s latency).
All 100 scanned ports on 172.20.1.13 are open|filtered
MAC Address: 02:42:AC:14:01:0D (Unknown)

Nmap scan report for 172.20.15.20
Host is up (0.000035s latency).
Not shown: 99 closed ports
PORT   STATE         SERVICE
22/tcp open|filtered ssh
MAC Address: 02:42:AC:14:0F:14 (Unknown)

Nmap scan report for 172.20.15.21
Host is up (0.000031s latency).
Not shown: 99 closed ports
PORT   STATE         SERVICE
22/tcp open|filtered ssh
MAC Address: 02:42:AC:14:0F:15 (Unknown)

Nmap scan report for 172.20.15.22
Host is up (0.000087s latency).
All 100 scanned ports on 172.20.15.22 are open|filtered
MAC Address: 02:42:AC:14:0F:16 (Unknown)

Nmap scan report for 172.20.0.1
Host is up (0.0000090s latency).
All 100 scanned ports on 172.20.0.1 are closed

Nmap done: 4096 IP addresses (14 hosts up) scanned in 13.66 seconds
```

### Unterschiede
Sowohl beim FIN als auch beim ACK-Scan werden die Ports von 172.20.1.10 nicht alle als offen angezeigt.
Über den ACK-Scan kann man für alle Hosts herausfinden ob sie von einer Firewall blockiert werden,
was bei einigen Hosts der Fall ist.

## Schwachstellen
```
kali@kali:~$ sudo nmap -T4 -A 172.20.0.9
Starting Nmap 7.80 ( https://nmap.org ) at 2020-06-17 12:59 EDT
Nmap scan report for 172.20.0.9
Host is up (0.00026s latency).
Not shown: 983 closed ports
PORT     STATE SERVICE     VERSION
21/tcp   open  ftp         vsftpd 2.3.4
|_ftp-anon: Anonymous FTP login allowed (FTP code 230)
| ftp-syst: 
|   STAT: 
| FTP server status:
|      Connected to 172.20.0.1
|      Logged in as ftp
|      TYPE: ASCII
|      No session bandwidth limit
|      Session timeout in seconds is 300
|      Control connection is plain text
|      Data connections will be plain text
|      vsFTPd 2.3.4 - secure, fast, stable
|_End of status
22/tcp   open  ssh         OpenSSH 4.7p1 Debian 8ubuntu1 (protocol 2.0)
| ssh-hostkey: 
|   1024 60:0f:cf:e1:c0:5f:6a:74:d6:90:24:fa:c4:d5:6c:cd (DSA)
|_  2048 56:56:24:0f:21:1d:de:a7:2b:ae:61:b1:24:3d:e8:f3 (RSA)
23/tcp   open  telnet      Linux telnetd
25/tcp   open  smtp        Postfix smtpd
|_smtp-commands: metasploitable.localdomain, PIPELINING, SIZE 10240000, VRFY, ETRN, STARTTLS, ENHANCEDSTATUSCODES, 8BITMIME, DSN, 
|_ssl-date: 2020-06-17T17:02:49+00:00; 0s from scanner time.
| sslv2: 
|   SSLv2 supported
|   ciphers: 
|     SSL2_RC2_128_CBC_WITH_MD5
|     SSL2_RC4_128_WITH_MD5
|     SSL2_DES_192_EDE3_CBC_WITH_MD5
|     SSL2_RC2_128_CBC_EXPORT40_WITH_MD5
|     SSL2_RC4_128_EXPORT40_WITH_MD5
|_    SSL2_DES_64_CBC_WITH_MD5
111/tcp  open  rpcbind     2 (RPC #100000)
139/tcp  open  netbios-ssn Samba smbd 3.X - 4.X (workgroup: WORKGROUP)
445/tcp  open  netbios-ssn Samba smbd 3.0.20-Debian (workgroup: WORKGROUP)
512/tcp  open  exec        netkit-rsh rexecd
513/tcp  open  login
514/tcp  open  tcpwrapped
1099/tcp open  java-rmi    GNU Classpath grmiregistry
1524/tcp open  ingreslock?
| fingerprint-strings: 
|   GenericLines: 
|     ]0;@ff6fb2819c29: /
|     root@ff6fb2819c29:/# 
|     ]0;@ff6fb2819c29: /
|     root@ff6fb2819c29:/# 
|     ]0;@ff6fb2819c29: /
|     root@ff6fb2819c29:/# 
|     ]0;@ff6fb2819c29: /
|     root@ff6fb2819c29:/# 
|     ]0;@ff6fb2819c29: /
|     root@ff6fb2819c29:/#
|   GetRequest: 
|     ]0;@ff6fb2819c29: /
|     root@ff6fb2819c29:/# GET / HTTP/1.0
|     <HTML>
|     <HEAD>
|     <TITLE>Directory /</TITLE>
|     <BASE HREF="file:/">
|     </HEAD>
|     <BODY>
|     <H1>Directory listing of /</H1>
|     <UL>
|     <LI><A HREF="./">./</A>
|     <LI><A HREF="../">../</A>
|     <LI><A HREF=".dockerenv">.dockerenv</A>
|     <LI><A HREF="bin/">bin/</A>
|     <LI><A HREF="boot/">boot/</A>
|     <LI><A HREF="cdrom/">cdrom/</A>
|     <LI><A HREF="core">core</A>
|     <LI><A HREF="dev/">dev/</A>
|     <LI><A HREF="etc/">etc/</A>
|     <LI><A HREF="home/">home/</A>
|     <LI><A HREF="initrd/">initrd/</A>
|     <LI><A HREF="initrd.img">initrd.img</A>
|     <LI><A HREF="lib/">lib/</A>
|     <LI><A HREF="lost%2Bfound/">lost+found/</A>
|     <LI><A HREF="media/">media/</A>
|     <LI><A HREF="mnt/">mnt/</A>
|     <LI><A HREF="nohup.out">nohup.out</A>
|     <LI><A HREF="opt/">opt/</A>
|     <LI><A HREF="proc/">proc/</A>
|     <LI><A HREF="root/">root/</A>
|     <LI><A HREF="sbin/">sbin/</A>
|     <LI><A HREF="srv/">srv/</A>
|     <LI><A HREF="sys/">sys/</A>
|     <LI><A HREF="tmp/
|   HTTPOptions: 
|     ]0;@ff6fb2819c29: /
|     root@ff6fb2819c29:/# OPTIONS / HTTP/1.0
|     bash: OPTIONS: command not found
|     ]0;@ff6fb2819c29: /
|     root@ff6fb2819c29:/# 
|     ]0;@ff6fb2819c29: /
|     root@ff6fb2819c29:/# 
|     ]0;@ff6fb2819c29: /
|     root@ff6fb2819c29:/# 
|     ]0;@ff6fb2819c29: /
|     root@ff6fb2819c29:/#
|   NULL: 
|     ]0;@ff6fb2819c29: /
|     root@ff6fb2819c29:/#
|   RTSPRequest: 
|     ]0;@ff6fb2819c29: /
|     root@ff6fb2819c29:/# OPTIONS / RTSP/1.0
|     bash: OPTIONS: command not found
|     ]0;@ff6fb2819c29: /
|     root@ff6fb2819c29:/# 
|     ]0;@ff6fb2819c29: /
|     root@ff6fb2819c29:/# 
|     ]0;@ff6fb2819c29: /
|     root@ff6fb2819c29:/# 
|     ]0;@ff6fb2819c29: /
|_    root@ff6fb2819c29:/#
2121/tcp open  ftp         ProFTPD 1.3.1
3306/tcp open  mysql       MySQL 5.0.51a-3ubuntu5
| mysql-info: 
|   Protocol: 10
|   Version: 5.0.51a-3ubuntu5
|   Thread ID: 9
|   Capabilities flags: 43564
|   Some Capabilities: ConnectWithDatabase, Support41Auth, Speaks41ProtocolNew, SupportsCompression, SupportsTransactions, LongColumnFlag, SwitchToSSLAfterHandshake
|   Status: Autocommit
|_  Salt: t00>*UOkL{F-Lv1/8i).
6667/tcp open  irc         UnrealIRCd
| irc-info: 
|   users: 1
|   servers: 1
|   lusers: 1
|   lservers: 0
|   server: irc.Metasploitable.LAN
|   version: Unreal3.2.8.1. irc.Metasploitable.LAN 
|   uptime: 0 days, 2:59:21
|   source ident: nmap
|   source host: 17F6711A.802FA63B.4F589F96.IP
|_  error: Closing Link: jsubmmlur[172.20.0.1] (Quit: jsubmmlur)
8009/tcp open  ajp13       Apache Jserv (Protocol v1.3)
|_ajp-methods: Failed to get a valid response for the OPTION request
8180/tcp open  http        Apache Tomcat/Coyote JSP engine 1.1
|_http-favicon: Apache Tomcat
|_http-server-header: Apache-Coyote/1.1
|_http-title: Apache Tomcat/5.5
1 service unrecognized despite returning data. If you know the service/version, please submit the following fingerprint at https://nmap.org/cgi-bin/submit.cgi?new-service :
SF-Port1524-TCP:V=7.80%I=7%D=6/17%Time=5EEA4C13%P=x86_64-pc-linux-gnu%r(NU
SF:LL,2A,"\x1b\]0;@ff6fb2819c29:\x20/\x07root@ff6fb2819c29:/#\x20")%r(Gene
SF:ricLines,D6,"\x1b\]0;@ff6fb2819c29:\x20/\x07root@ff6fb2819c29:/#\x20\n\
SF:x1b\]0;@ff6fb2819c29:\x20/\x07root@ff6fb2819c29:/#\x20\n\x1b\]0;@ff6fb2
SF:819c29:\x20/\x07root@ff6fb2819c29:/#\x20\n\x1b\]0;@ff6fb2819c29:\x20/\x
SF:07root@ff6fb2819c29:/#\x20\n\x1b\]0;@ff6fb2819c29:\x20/\x07root@ff6fb28
SF:19c29:/#\x20")%r(GetRequest,7CA,"\x1b\]0;@ff6fb2819c29:\x20/\x07root@ff
SF:6fb2819c29:/#\x20GET\x20/\x20HTTP/1\.0\n<HTML>\n<HEAD>\n<TITLE>Director
SF:y\x20/</TITLE>\n<BASE\x20HREF=\"file:/\">\n</HEAD>\n<BODY>\n<H1>Directo
SF:ry\x20listing\x20of\x20/</H1>\n<UL>\n<LI><A\x20HREF=\"\./\">\./</A>\n<L
SF:I><A\x20HREF=\"\.\./\">\.\./</A>\n<LI><A\x20HREF=\"\.dockerenv\">\.dock
SF:erenv</A>\n<LI><A\x20HREF=\"bin/\">bin/</A>\n<LI><A\x20HREF=\"boot/\">b
SF:oot/</A>\n<LI><A\x20HREF=\"cdrom/\">cdrom/</A>\n<LI><A\x20HREF=\"core\"
SF:>core</A>\n<LI><A\x20HREF=\"dev/\">dev/</A>\n<LI><A\x20HREF=\"etc/\">et
SF:c/</A>\n<LI><A\x20HREF=\"home/\">home/</A>\n<LI><A\x20HREF=\"initrd/\">
SF:initrd/</A>\n<LI><A\x20HREF=\"initrd\.img\">initrd\.img</A>\n<LI><A\x20
SF:HREF=\"lib/\">lib/</A>\n<LI><A\x20HREF=\"lost%2Bfound/\">lost\+found/</
SF:A>\n<LI><A\x20HREF=\"media/\">media/</A>\n<LI><A\x20HREF=\"mnt/\">mnt/<
SF:/A>\n<LI><A\x20HREF=\"nohup\.out\">nohup\.out</A>\n<LI><A\x20HREF=\"opt
SF:/\">opt/</A>\n<LI><A\x20HREF=\"proc/\">proc/</A>\n<LI><A\x20HREF=\"root
SF:/\">root/</A>\n<LI><A\x20HREF=\"sbin/\">sbin/</A>\n<LI><A\x20HREF=\"srv
SF:/\">srv/</A>\n<LI><A\x20HREF=\"sys/\">sys/</A>\n<LI><A\x20HREF=\"tmp/")
SF:%r(HTTPOptions,109,"\x1b\]0;@ff6fb2819c29:\x20/\x07root@ff6fb2819c29:/#
SF:\x20OPTIONS\x20/\x20HTTP/1\.0\nbash:\x20OPTIONS:\x20command\x20not\x20f
SF:ound\n\x1b\]0;@ff6fb2819c29:\x20/\x07root@ff6fb2819c29:/#\x20\n\x1b\]0;
SF:@ff6fb2819c29:\x20/\x07root@ff6fb2819c29:/#\x20\n\x1b\]0;@ff6fb2819c29:
SF:\x20/\x07root@ff6fb2819c29:/#\x20\n\x1b\]0;@ff6fb2819c29:\x20/\x07root@
SF:ff6fb2819c29:/#\x20")%r(RTSPRequest,109,"\x1b\]0;@ff6fb2819c29:\x20/\x0
SF:7root@ff6fb2819c29:/#\x20OPTIONS\x20/\x20RTSP/1\.0\nbash:\x20OPTIONS:\x
SF:20command\x20not\x20found\n\x1b\]0;@ff6fb2819c29:\x20/\x07root@ff6fb281
SF:9c29:/#\x20\n\x1b\]0;@ff6fb2819c29:\x20/\x07root@ff6fb2819c29:/#\x20\n\
SF:x1b\]0;@ff6fb2819c29:\x20/\x07root@ff6fb2819c29:/#\x20\n\x1b\]0;@ff6fb2
SF:819c29:\x20/\x07root@ff6fb2819c29:/#\x20");
MAC Address: 02:42:AC:14:00:09 (Unknown)
No exact OS matches for host (If you know what OS is running on it, see https://nmap.org/submit/ ).
TCP/IP fingerprint:
OS:SCAN(V=7.80%E=4%D=6/17%OT=21%CT=1%CU=32689%PV=Y%DS=1%DC=D%G=Y%M=0242AC%T
OS:M=5EEA4CC0%P=x86_64-pc-linux-gnu)SEQ(SP=F8%GCD=1%ISR=104%TI=Z%CI=Z%II=I%
OS:TS=A)OPS(O1=M5B4ST11NW7%O2=M5B4ST11NW7%O3=M5B4NNT11NW7%O4=M5B4ST11NW7%O5
OS:=M5B4ST11NW7%O6=M5B4ST11)WIN(W1=FE88%W2=FE88%W3=FE88%W4=FE88%W5=FE88%W6=
OS:FE88)ECN(R=Y%DF=Y%T=40%W=FAF0%O=M5B4NNSNW7%CC=Y%Q=)T1(R=Y%DF=Y%T=40%S=O%
OS:A=S+%F=AS%RD=0%Q=)T2(R=N)T3(R=N)T4(R=Y%DF=Y%T=40%W=0%S=A%A=Z%F=R%O=%RD=0
OS:%Q=)T5(R=Y%DF=Y%T=40%W=0%S=Z%A=S+%F=AR%O=%RD=0%Q=)T6(R=Y%DF=Y%T=40%W=0%S
OS:=A%A=Z%F=R%O=%RD=0%Q=)T7(R=Y%DF=Y%T=40%W=0%S=Z%A=S+%F=AR%O=%RD=0%Q=)U1(R
OS:=Y%DF=N%T=40%IPL=164%UN=0%RIPL=G%RID=G%RIPCK=G%RUCK=G%RUD=G)IE(R=Y%DFI=N
OS:%T=40%CD=S)

Network Distance: 1 hop
Service Info: Hosts:  metasploitable.localdomain, irc.Metasploitable.LAN; OSs: Unix, Linux; CPE: cpe:/o:linux:linux_kernel

Host script results:
|_clock-skew: mean: 1h20m00s, deviation: 2h18m34s, median: 0s
|_nbstat: NetBIOS name: FF6FB2819C29, NetBIOS user: <unknown>, NetBIOS MAC: <unknown> (unknown)
| smb-os-discovery: 
|   OS: Unix (Samba 3.0.20-Debian)
|   Computer name: ff6fb2819c29
|   NetBIOS computer name: 
|   Domain name: 
|   FQDN: ff6fb2819c29
|_  System time: 2020-06-17T13:02:41-04:00
| smb-security-mode: 
|   account_used: guest
|   authentication_level: user
|   challenge_response: supported
|_  message_signing: disabled (dangerous, but default)
|_smb2-time: Protocol negotiation failed (SMB2)

TRACEROUTE
HOP RTT     ADDRESS
1   0.26 ms 172.20.0.9

OS and Service detection performed. Please report any incorrect results at https://nmap.org/submit/ .
Nmap done: 1 IP address (1 host up) scanned in 179.20 seconds
```
Es läuft ein FTP-Server, der wohl Nachrichten im Klartext annimmt, dieser kann eventuell für einen FTP-Bounce-Scan
genutzt werden. Des weiteren läuft ein SSH-Server, dieser bietet vermutlich aber nicht viel Angriffsfläche.
Außerdem läuft ein Telnet-Server, über diesen kann direkt auf die Maschine zugegriffen werden.
Der SMTP-Server ist von weniger Interesse, da kein vermutlich kein Mail Verkehr stattfindet.
Unter den weiteren Server ist noch ein ingreslock Server auf Port 1524, dieser zeigt einem bereits ein Teil des Dateisystems
beim Scan, das kann eventuell noch weiter untersucht werden. Der Apache Server und der MySQL Server könnten eventuell
auch Schwachstellen aufweisen.

# Firewall Evasion
Im ersten Schritt die Firewall genauer Anschauen
```
kali@kali:~$ sudo nmap -sA -T4 172.20.1.8/29
Starting Nmap 7.80 ( https://nmap.org ) at 2020-06-17 13:16 EDT
Nmap scan report for 172.20.1.10
Host is up (0.00023s latency).
All 1000 scanned ports on 172.20.1.10 are unfiltered
MAC Address: 02:42:AC:14:01:0A (Unknown)

Nmap scan report for 172.20.1.11
Host is up (0.000017s latency).
All 1000 scanned ports on 172.20.1.11 are filtered
MAC Address: 02:42:AC:14:01:0B (Unknown)

Nmap scan report for 172.20.1.12
Host is up (0.000076s latency).
Not shown: 993 filtered ports
PORT     STATE      SERVICE
80/tcp   unfiltered http
135/tcp  unfiltered msrpc
443/tcp  unfiltered https
554/tcp  unfiltered rtsp
587/tcp  unfiltered submission
1720/tcp unfiltered h323q931
3389/tcp unfiltered ms-wbt-server
MAC Address: 02:42:AC:14:01:0C (Unknown)

Nmap scan report for 172.20.1.13
Host is up (0.000015s latency).
All 1000 scanned ports on 172.20.1.13 are filtered
MAC Address: 02:42:AC:14:01:0D (Unknown)

Nmap done: 8 IP addresses (4 hosts up) scanned in 226.03 seconds
```
es fällt auch das sich die offenen Ports bei mehrfachem wiederholen ändern, auf gut glück werden die Ports durchprobiert:
```
kali@kali:~$ curl 172.20.1.12:76
<!DOCTYPE html>
<html>
    <body>
        <h1>Well Done!</h1>
    </body>
</html>
```


Auf den in der vorherigen Aufgabe gefunden Host kann über telnet zugegriffen werden: `telnet 172.20.0.9`, dann kann
auch von diesem Host ein Netzwerk-Scan durchgeführt werden:
```
msfadmin@ff6fb2819c29:~$ nmap -sS -T4 172.20.1.8/29
....
All 1714 scanned ports on psec-container11.psec-net (172.20.1.11) are filtered
MAC Address: 02:42:AC:14:01:0B (Unknown)

Interesting ports on psec-container12.psec-net (172.20.1.12):
Not shown: 1707 filtered ports
PORT     STATE  SERVICE
53/tcp   closed domain
80/tcp   closed http
113/tcp  closed auth
256/tcp  closed FW1-secureremote
443/tcp  closed https
636/tcp  closed ldapssl
3389/tcp closed ms-term-serv
MAC Address: 02:42:AC:14:01:0C (Unknown)

Interesting ports on psec-container13.psec-net (172.20.1.13):
Not shown: 1713 closed ports
PORT   STATE SERVICE
53/tcp open  domain
MAC Address: 02:42:AC:14:01:0D (Unknown)
```
bei 172.20.1.10 werden alle Ports als offen gelistet, 11 gar keine, bei 13 Port 53.
Wenn mit Curl versucht wird eine HTTP-Anfrage an 172.20.1.13 auf Port 53 zu stellen erhält man eine Antwort
des Webservers:
```
msfadmin@ff6fb2819c29:~$ curl 172.20.1.13:53
<!DOCTYPE html>
<html>
    <body>
        <h1>Well Done!</h1>
    </body>
</html>
```
Auf 10 lässt sich so ebenfalls der Webserver durch ausprobieren finden:
```
msfadmin@ff6fb2819c29:~$ curl 172.20.1.10:26
<!DOCTYPE html>
<html>
    <body>
        <h1>Well Done!</h1>
    </body>
</html>
```

#  Nmap vs. ZMap
## IP-Block der Uni
Der IP-Block der Uni ist 134.60.0.0/16 (also 134.60.X.X).

## Scan mit NMAP

NMap scanned den Bereich in 3.05 Sekunden und findet 103 Hosts (`nmap -p 80 134.60.0.0/23`).

## Scan mit ZMap
ZMap findet deutlich weniger Geräte (varriert zwischen den Scans), braucht dafür aber auch etwas kürzer (` sudo zmap -p 80 134.60.0.0/23`).

## Vergleich NMAp mit nur offenen Ports
Wenn mit NMap nur Geräte mit offenem Port 80 gescanned werden is NMap ähnlich schnell wie ZMap (`nmap -iL hosts -p80`).

# Inventarisierung des Uninetzes
## HTTPS-Server
Es werden alle Geräte mit offenem Port 443 gesucht:
```
sudo zmap -p 443 -B 10M 134.60.0.0/16 | grep 134.60 | wc -l
```
zum Zeitpunkt des Scannens waren es zwischen 238 und 255 Geräte.

## Apache
Zuerst wird nur eine Liste aller Geräte mit offenem Port 80 angelegt:
```
sudo zmap -p 80 -B 10M 134.60.0.0/16 2> /dev/null | grep 134.60 > apache
```
mit nmap kann dann die Version/Software identifiziert werden:
```
nmap -n -T16 -iL apache -sV -p 80 > scan
```
dann muss nur noch das Vorkommen von Apache gezählt werden:
```
cat scan | grep Apache | wc -l
```
zum Zeitpunkt des Scannens liefen 368 Apache Server im Uni Netz.

## SSL
Mit der Liste aus dem vorherigen Aufgabenteil:
```
nmap --script ssl-enum-ciphers -p 443 -iL apache > tls
```
dann wird mit einem Regex nach DES gesucht, wobei Dopplungen für einzelne Hosts vermieden werden, der Einfachheit
halber wurde hierfür ein kleine Python Script geschrieben:
```python
import re

test_str = open("/home/paul/tls").read()
print(test_str)

regex = r"443\/tcp open  https\n\|.*\n\|\s.+\n\|.+\n(\|.+\n)*\|(.*DES.*)"

matches = re.finditer(regex, test_str, re.MULTILINE)

count = 0
for matchNum, match in enumerate(matches, start=1):
    print(f"Result: {match.group(2)}")
    count += 1

print(count)
```
das Skript gibt aus das es 403 Server gibt die noch eine Verschlüsselung aus der DES-Familie unterstützen.

## Betriebssystem
Zuerst von allen Geräten das Betriebssystem herausfinden
```
sudo nmap -O 134.60.0.0/16 > os
```
dann mit einem kleinen Python Skript die Plots generieren
```python
import re
import matplotlib.pyplot as plt

regex = r"Running( \(JUST GUESSING\))?: ([a-zA-Z0-9 \.]+) \([0-9]+%\)"

test_str = open("/home/paul/os").read()

matches = re.finditer(regex, test_str, re.MULTILINE)

os_occurences = dict()
number_devices = 0
for matchNum, match in enumerate(matches, start=1):
    os = match.group(2)
    if os in os_occurences:
        os_occurences[os] += 1
    else:
        os_occurences[os] = 1
    number_devices += 1

x = []
y = []

for os, occ in os_occurences.items():
    x.append(os)
    y.append(occ/number_devices)


x_pos = [i for i, _ in enumerate(x)]
plt.bar(x_pos, y, color='green')
plt.xlabel("OS")
plt.ylabel("Frequency")
plt.title("OSs at Ulm University")

plt.xticks(x_pos, x, rotation=90)
plt.subplots_adjust(bottom=0.2)

plt.savefig("os.svg")
```
das resultierende Verteilungsdiagram, sowie der selbe Plot nur mit Betriebssystemen die nicht "embedded" enthalten, 
also primär Desktop und Server Betriebssysteme:

![Betriebssystemverteilung](os.eps)

![Betriebssystemverteilung ohne Embedded](os_without_embedded.eps)

# Fazit
In der Übung wurden in mehreren Aufgaben diverse Netzwerke gescanned, sowohl mit NMap als auch mit ZMap. Dadurch
wurde die Benutzung von NMap und ZMap erlernt, sowie die wichtigsten Konzepte aus der Vorlesung geübt.

Die Übung war unterhaltsam, vor allem die Capture-the-Flag ähnlich Aufgabe, in Summe hat die Bearbeitung der Übung
jedoch deutlich länger als die veranschlagten 90 Minuten gedauert (selbst wenn die Zeit die nur zum Scannen genutzt
wurde ausgenommen wurde).
