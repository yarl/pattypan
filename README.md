![Logo](http://i.imgur.com/Wjti8vi.png)

Tool that simplifies [Wikimedia Commons](https://commons.wikimedia.org/) batch file uploading for [GLAM institution](https://outreach.wikimedia.org/wiki/GLAM) volunteers and employees. Created thanks to Wikimedia Foundation [IEG Grant](https://meta.wikimedia.org/wiki/Grants:IEG/Batch_uploader_for_small_GLAM_projects).

For more information on usage, see [Commons:Pattypan](https://commons.wikimedia.org/wiki/Commons:Pattypan).

__[:arrow_down: Download](https://github.com/yarl/pattypan/releases)__

----

### Build and run
[Apache Ant](https://ant.apache.org/) is used for building Pattypan. You need to have JDK 11 or later installed as well as [a download of OpenJFX](https://gluonhq.com/products/javafx/) for each platform you want to support. In order to download and build source code, do the following:

```
git clone https://github.com/yarl/pattypan.git
cd pattypan
ant
```

This will run the default `build` target. It assumes that the current directory contains the OpenJFX SDK ZIP(s) and will unpack the required files to the correct locations. The resulting JAR will support Linux, Windows or both. The ZIPs present dictates what platforms will be supported. Note that the ZIPs should have their default name to be included.

A temporary directory will be used during the build process and removed afterwards. It's default path is *tmp/* and can be set using `ant -Dtmp=...`

You can also set test server or any other server:

```
java -jar pattypan.jar wiki="test.wikipedia.org"
java -jar pattypan.jar wiki="test2.wikipedia.org" protocol="https://" scriptPath="/w"

```

Please note, that on test server file upload may be disabled for regular users. Admin account is suggested, you can request rights [here](https://test.wikipedia.org/wiki/Wikipedia:Requests/Permissions). If you have problems with program running, check [article on project wiki](https://github.com/yarl/pattypan/wiki/Run).

### License
Copyright (c) 2016 Paweł Marynowski.

Source code is available under the [MIT License](https://github.com/yarl/pattypan/blob/master/LICENSE). See the LICENSE file for more info. Program is using external libraries listed [here](https://github.com/yarl/pattypan/tree/master/lib).

#### Credits
Name by Wojciech Pędzich. Logo by [Rickterto](//commons.wikimedia.org/wiki/User:Rickterto), licensed under the Creative Commons [BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/deed.en) license.


