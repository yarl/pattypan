![Logo](http://i.imgur.com/Wjti8vi.png)

Tool that simplifies [Wikimedia Commons](https://commons.wikimedia.org/) batch file uploading for [GLAM institution](https://outreach.wikimedia.org/wiki/GLAM) volunteers and employees. Created thanks to Wikimedia Foundation [IEG Grant](https://meta.wikimedia.org/wiki/Grants:IEG/Batch_uploader_for_small_GLAM_projects).

For more information on usage, see [Commons:Pattypan](https://commons.wikimedia.org/wiki/Commons:Pattypan).

__[:arrow_down: Download](https://github.com/yarl/pattypan/releases)__

----

### Build and run
Program is being written using [NetBeans IDE](https://netbeans.org/) and [Apache Ant](https://ant.apache.org/) is used for building. In order to download and build source code, do following:

```
git clone https://github.com/yarl/pattypan.git
cd pattypan
ant package-for-store
```
You will find compiled `.jar` file in `store` directory.

```
cd store
java -jar pattypan.jar
```

You can also set test server or any other server:

```
java -jar pattypan.jar wiki="test.wikipedia.org"
java -jar pattypan.jar wiki="test2.wikipedia.org" protocol="https://" scriptPath="/w"

```

Please note, that on test server file upload may be disabled for regular users. Admin account is suggested, you can request rights [here](https://test.wikipedia.org/wiki/Wikipedia:Requests/Permissions). If you have problems with program running, check [article on project wiki](https://github.com/yarl/pattypan/wiki/Run).

### Additional information for Ubuntu, Debian and Fedora based distributions
These linux distributions may require additional ```openjfx``` package. After installing Java on your system, download and install the ```openjfx``` package. You can install it by running:

``` 
sudo apt-get install openjfx
``` 

on your terminal.
You can also check this link: https://pkgs.org/download/openjfx for more information on ```openjfx``` package for these distributions.

Starting with Ubuntu 18.10 (and around the same time in Debian Sid), openjfx version 8 is no more shipped with the distribution. Though it is possible to force the openjfx to stay in version 8, and run pattypan, this is pretty much a hack. If you still wish to proceed, you can gain access to the Ubuntu 18.04 repository by adding the following line to your `/etc/apt/sources.list` file:

```
deb-src http://fr.archive.ubuntu.com/ubuntu/ bionic universe multiverse
``` 

You can then run these commands to perfom the installation and pin these versions:
```
apt purge openjfx
apt install openjfx=8u161-b12-1ubuntu2 libopenjfx-jni=8u161-b12-1ubuntu2 libopenjfx-java=8u161-b12-1ubuntu2
apt-mark hold openjfx libopenjfx-jni libopenjfx-java
```


### License
Copyright (c) 2016 Paweł Marynowski.

Source code is available under the [MIT License](https://github.com/yarl/pattypan/blob/master/LICENSE). See the LICENSE file for more info. Program is using external libraries listed [here](https://github.com/yarl/pattypan/tree/master/lib).

#### Credits
Name by Wojciech Pędzich. Logo by [Rickterto](//commons.wikimedia.org/wiki/User:Rickterto), licensed under the Creative Commons [BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/deed.en) license.


