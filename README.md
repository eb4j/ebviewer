# ebviewer

Very simple dictionary search application supporting;

- EPWING(w/ Graphics, Sounds and Movies)
- EPWING ebz compression (.ebz) 
- LingvoDSL (.dsl)
- LingvoDSL dz compression (.dsl.dz)
- StarDict (.ifo .dict)
- StarDict (.dict.dz)

![Application image](https://raw.githubusercontent.com/eb4j/ebviewer/main/docs/img/screen_image.png)

## Install

You can download a binary zip archive from [github release](https://github.com/eb4j/ebviewer/releases)

* [Download v0.5.0 binary distribution](https://github.com/eb4j/ebviewer/releases/download/v0.5.0/ebviewer-0.5.0.zip)

After downloading the zip archive, please extract zip archive to arbitary folder.
The applicaiton is written by Java, you can run the application on any PC platforms, such as macOS, Windows and Linux.

## Run

On linux or macOS which already have java 11 or later installed and set PATH properly,

```console
ebviewer-0.5.0/ebviewer
```

On windows

```console
ebviewer-0.5.0\ebviewer.bat
```

When you have java but there is no PATH set,

```console
set JAVA_HOME=C:\Program Files\Java\jdk11.0.123.456\
ebviewer-0.5.0\ebviewer.bat
```


## Dependency

You need to install Java Runtime Version 11 or 16 on your operating system.
When you want to watch movie in your dictionary, VLC is required.

## Build from source

ebviewer uses Java and Gradle build system.

From source distribution

```console
unzip ebviewer-source-0.5.0.zip
cd ebviewer-source-0.5.0
./buildw.bat build
```

From github git repository

```console
git clone https://github.com/eb4j/ebviewer
cd ebviewer
./gradlew build
```

## Origin

Some codes are borrowed from omegat-plugin-epwing and OmegaT project.

## License

Copyright (C) 2016-2021 Hiroshi Miura

Copyright (C) 2020 Suguru Ono

Copyright (C) 2015-2020 Aaron Madlon-Kay

Copyright (C) 2007-2015 Didier Briel

Copyright (C) 2008-2010 Alex Buloichik

Copyright (C) 2007 Zoltan Bartko

Copyright (C) 2000-2006 Keith Godfrey, Maxym Mykhalchuk, and Henry Pijffers

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
