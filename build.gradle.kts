import java.io.File
import java.io.FileInputStream
import java.util.Properties

plugins {
    groovy
    java
    checkstyle
    jacoco
    application
    distribution
    id("com.github.spotbugs") version "4.7.9"
    id("com.diffplug.spotless") version "5.17.1"
    id("com.github.kt3k.coveralls") version "2.12.0"
    id("org.mikeneck.graalvm-native-image") version "1.4.1"
    id("com.palantir.git-version") version "0.12.3" apply false
}

fun getProps(f: File): Properties {
    val props = Properties()
    props.load(FileInputStream(f))
    return props
}

// we handle cases without .git directory
val props = project.file("src/main/resources/version.properties")
val dotgit = project.file(".git")
if (dotgit.exists()) {
    apply(plugin = "com.palantir.git-version")
    val versionDetails: groovy.lang.Closure<com.palantir.gradle.gitversion.VersionDetails> by extra
    val details = versionDetails()
    val baseVersion = details.lastTag.substring(1)
    if (details.isCleanTag) {  // release version
        version = baseVersion
    } else {  // snapshot version
        version = baseVersion + "-" + details.commitDistance + "-" + details.gitHash + "-SNAPSHOT"
    }
} else if (props.exists()) { // when version.properties already exist, just use it.
    version = getProps(props).getProperty("version")
}

tasks.register("writeVersionFile") {
    val folder = project.file("src/main/resources");
    if (!folder.exists()) {
        folder.mkdirs()
    }
    props.delete()
    props.appendText("version=" + project.version)
}

tasks.getByName("jar") {
    dependsOn("writeVersionFile")
}

group = "io.github.eb4j"

application {
    mainClass.set("io.github.eb4j.ebview.EBViewer")
}

application.applicationDistribution.into("") {
    from("README.md", "COPYING")
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("io.github.eb4j:eb4j:2.3.0")
    implementation("io.github.eb4j:mdict4j:0.1.1")
    implementation("org.slf4j:slf4j-simple:1.7.32")

    implementation("commons-io:commons-io:2.11.0")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("tokyo.northside:url-protocol-handler:0.1.4")
    implementation("com.ibm.icu:icu4j-charset:70.1")

    implementation("io.github.dictzip:dictzip:0.9.5")
    implementation("com.github.takawitter:trie4j:0.9.8")

    implementation("uk.co.caprica:vlcj:4.7.1")

    implementation("com.formdev:flatlaf:1.6.1")
    testImplementation("org.codehaus.groovy:groovy-all:3.0.9")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()

    // Test in headless mode with ./gradlew test -Pheadless
    if (project.hasProperty("headless")) {
        systemProperty("java.awt.headless", "true")
    }
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

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
    options.compilerArgs.add("-Xlint:unchecked")
}

// Disable .tar distributions
tasks.getByName("distTar").enabled = false

nativeImage {
    graalVmHome = System.getProperty("java.home")
    buildType {
        it.executable {
            main = "io.github.eb4j.ebview.EBViewer"
        }
    }
    executableName = "ebviewer"
    outputDirectory = file("$buildDir/bin")
    arguments {
        add("--verbose")
        add("--native-image-info")
        add("--no-fallback")
        add("-Djava.awt.headless=false")
        add("-H:JNIConfigurationFiles=config/native-image/jni-config.json")
        add("-H:ResourceConfigurationFiles=config/native-image/resource-config.json")
        add("-H:ReflectionConfigurationFiles=config/native-image/reflect-config.json")
        add("--initialize-at-build-time=org.slf4j")
        add("--enable-all-security-services")
        add("--initialize-at-run-time=sun.awt.dnd.SunDropTargetContextPeer\$EventDispatcher")
        add("--report-unsupported-elements-at-runtime")
    }
}

application {
    executableDir = ""
}

distributions {
    create("source") {
        contents {
            from (".")
            exclude ("out", "build", ".gradle", ".github", ".idea", ".gitignore")
        }
    }
}
