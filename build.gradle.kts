plugins {
    groovy
    java
    checkstyle
    jacoco
    application
    id("com.github.spotbugs") version "4.7.3"
    id("com.diffplug.spotless") version "5.14.3"
    id("com.github.kt3k.coveralls") version "2.12.0"
    id("org.mikeneck.graalvm-native-image") version "v1.4.0"
    id("com.palantir.git-version") version "0.12.3"
}

// calculate version string from git tag, hash and commit distance
fun getVersionDetails(): com.palantir.gradle.gitversion.VersionDetails = (extra["versionDetails"] as groovy.lang.Closure<*>)() as com.palantir.gradle.gitversion.VersionDetails
if (getVersionDetails().isCleanTag) {
    version = getVersionDetails().lastTag.substring(1)
} else {
    version = getVersionDetails().lastTag.substring(1) + "-" + getVersionDetails().commitDistance + "-" + getVersionDetails().gitHash + "-SNAPSHOT"
}

group = "io.github.eb4j"

application {
    mainClassName = "io.github.eb4j.ebview.Main"
    applicationName = "ebviewer"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.eb4j:eb4j:2.2.1")
    implementation("org.slf4j:slf4j-simple:1.7.32")
    implementation("org.apache.commons:commons-lang3:3.11")
    testImplementation("org.codehaus.groovy:groovy-all:3.0.5")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

jacoco {
    toolVersion="0.8.6"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true  // coveralls plugin depends on xml format report
        html.isEnabled = true
    }
}

coveralls {
    jacocoReportPath = "build/reports/jacoco/test/jacocoTestReport.xml"
}

nativeImage {
    graalVmHome = System.getProperty("java.home")
    buildType { it.executable {
        main = "io.github.eb4j.ebview.Main"
    }}
    executableName = "ebviewer"
    outputDirectory = file("$buildDir/bin")
    arguments {
        add("--verbose")
        add("--native-image-info")
        add("--no-fallback")
        add("-H:ReflectionConfigurationFiles=config/native-image/reflect-config.json")
        add("--initialize-at-build-time=org.slf4j")
        add("--enable-all-security-services")
        add("--initialize-at-run-time=sun.awt.dnd.SunDropTargetContextPeer\$EventDispatcher")
        add("--report-unsupported-elements-at-runtime")
    }

}