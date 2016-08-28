![Logo](http://i.imgur.com/Wjti8vi.png)

Tool that will enable the volunteer or employee working in a GLAM institution to simplify choosing and uploading selected files to [Wikimedia Commons](https://commons.wikimedia.org/). Created thanks to Wikimedia Foundation [IEG Grant](https://meta.wikimedia.org/wiki/Grants:IEG/Batch_uploader_for_small_GLAM_projects).

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
java -jar pattypan.jar -test
java -jar pattypan.jar "test.wikipedia.org"
```

If you have problems with program running, check [article on project wiki](https://github.com/yarl/pattypan/wiki/Run).

### License
Copyright (c) 2016 Paweł Marynowski.

Source code is available under the [MIT License](https://github.com/yarl/pattypan/blob/master/LICENSE). See the LICENSE file for more info. Program is using external libraries listed [here](https://github.com/yarl/pattypan/tree/master/lib).

#### Credits
Name by Wojciech Pędzich. Logo by [Rickterto](//commons.wikimedia.org/wiki/User:Rickterto), licensed under the Creative Commons [BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/deed.en) license.


