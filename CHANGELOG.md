# Change Log
All notable changes to this project will be documented in this file.

## [Unreleased]

## [0.8.1]

### Fixed
* Oxford: send proper AppId/AppKey to access library

### Changed
* Improve preference dialog UI

## [0.8.0]

### Added
* Can save preference in config file `~/.config/ebviewer/ebviewer.prefs` (#125)
* Can save credentials in enrypted form (#125)
* Add preference GUI menu (#125)

### Changed
* Oxford: use preference to save AppId/AppKey (#125)
* Bump versions
  * groovy-all@3.0.10
  * java-oxford-dictionaries@0.4.0
  * flatlaf@2.0.2
  * dsl4j@0.5.2
  * stardict4j@0.3.2
* Update native-image configuration

## [0.7.3]

### Fixed
* Jump to word failed when last one.

### Added
* Native Image build.(#111,#112)
* Online Oxford Dicitonaries(experimental)
  * merged but not enabled yet.

### Changed
* Use stardict4j#0.3.0
* Bump versions
  * spotless@6.2.B2
  * slf4j@1.7.36
  * flatlaf@2.0.1
  * spotbugs@5.0.5
  * mdict4j@0.3.0
  * pdic4j@0.3.3
  * eb4j@2.3.1
  * gradle-git-vesrion@0.13.0
  * dictzip@0.11.2
  * dsl4j@0.4.5

## [0.7.2]
### Fixed
* Allow MDX only dictionary for MDict

### Changed
* Use DSL4j library for LingvoDSL dictionary.
* Bump versions
  * JUnit@5.8.2
  * Spotbugs@4.8.0

## [0.7.1]
### Added
* Support MDict image and sound(#65)

### Changed
* Bump mdict4j@0.2.0(#65)

## [0.7.0]
### Added
- Support MDict dictionary format(#38,#64)
- Support JetBrains Projector(#47)
  - Can build for headless and remote execution

### Changed
- Sound tweaks(#46)
- Bump versions
  - icu4j@70.1 (#45)
  - websocket@1.5.2 (#49)
  - SLF4j@1.7.32 (#53)
  - Kotlin(JVM)@1.5.31 (#54)
  - spotless@6.0.0 (#56)
  - flatlaf@1.6.4 (#63)
  - spotbugs@4.7.10 (#61)

## [0.6.0]
### Added
- Support PDICW/Unicode dictionary format v6.00 and v6.10
- Support fuzzy match(#33)

### Changed
- Bump spotless@5.15.2
- Bump EB4j@2.3.0

## [0.5.2]
### Added
-  DSL: support image/sound tag

### Changed
- Launch script path

## [0.5.1]
### Fixed
- project.version handling

### Changed
- Bump spotbugs@4.7.6
 
## [0.5.0]
### Added
- Source distribution

## [0.4.3]
### Fixed
- Allow build from source distribution

### Changed
- Update README: run and build sections
- Improve javadoc syntax
- Externalize data URL protocol handler
- Bump versions
  - junit@5.8.1
  - spotless@5.15.1

## [0.4.2]
### Added
* System Tray icon
* Can close dictionaries
* Add guidance message when starting
* Add dictionaries names when add dictionary 
* Blacklisting unusable subbooks by title
* Linux desktop file
* Static code analysis
* Can Zoom up/down font size

### Fixed
* Text styleing for bold and italic on EPWING dictionary
* LingvoDSL: Styling update

### Changed
* Change menu names and shortcuts
* When minimize app, close to system tray
* Bump versions
  * gradle graalvm-native-image@1.4.1
  * spotless@5.15.0
  * flatlaf@1.6
  * spotbugs@4.7.5
  * groovy@3.0.9
  * commons-lang3@3.12.0

## [0.4.1]
### Added
* Applicaiton icon

### Changed
* Improve data protocol handler
* Add readme and copying to distribution
* Improve code style and update from deprecated methods
* disable tar distribution

## [0.4.0]

Beta release.

### Added
* Support multimedia contents (Movie, sound and graphics)(#10)
* Support Gaiji for EPWING contents(#8)
* Add Open menu to open dictionary directory(#11)
* Add dictionary selection UI(#13)
* Support StarDict format
* Support LingvoDSL format
* Support compressed StarDict (.dz) format
* Support compressed LingvoDSL (.dz) format

## [0.3.1]
* Bump EB4j@2.2.1
* Support appendix/furoku tree beside with dictionary catalogs

## [0.3.0]
* Add CI script
* Add heading pane

## [0.2.0]
* Use prefix word search

## 0.1.0
* First release

[Unreleased]: https://github.com/eb4j/ebviewer/compare/v0.8.1...HEAD
[0.8.1]: https://github.com/eb4j/ebviewer/compare/v0.8.0...v0.8.1
[0.8.0]: https://github.com/eb4j/ebviewer/compare/v0.7.3...v0.8.0
[0.7.3]: https://github.com/eb4j/ebviewer/compare/v0.7.2...v0.7.3
[0.7.2]: https://github.com/eb4j/ebviewer/compare/v0.7.1...v0.7.2
[0.7.1]: https://github.com/eb4j/ebviewer/compare/v0.7.0...v0.7.1
[0.7.0]: https://github.com/eb4j/ebviewer/compare/v0.6.0...v0.7.0
[0.6.0]: https://github.com/eb4j/ebviewer/compare/v0.5.2...v0.6.0
[0.5.2]: https://github.com/eb4j/ebviewer/compare/v0.5.1...v0.5.2
[0.5.1]: https://github.com/eb4j/ebviewer/compare/v0.5.0...v0.5.1
[0.5.0]: https://github.com/eb4j/ebviewer/compare/v0.4.3...v0.5.0
[0.4.3]: https://github.com/eb4j/ebviewer/compare/v0.4.2...v0.4.3
[0.4.2]: https://github.com/eb4j/ebviewer/compare/v0.4.1...v0.4.2
[0.4.1]: https://github.com/eb4j/ebviewer/compare/v0.4.0...v0.4.1
[0.4.0]: https://github.com/eb4j/ebviewer/compare/v0.3.1...v0.4.0
[0.3.1]: https://github.com/eb4j/ebviewer/compare/v0.3.0...v0.3.1
[0.3.0]: https://github.com/eb4j/ebviewer/compare/v0.2.0...v0.3.0
[0.2.0]: https://github.com/eb4j/ebviewer/compare/v0.1.0...v0.2.0
