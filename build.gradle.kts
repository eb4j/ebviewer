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
    kotlin("jvm") version "1.6.0"
    id("com.github.spotbugs") version "5.0.5"
    id("com.diffplug.spotless") version "6.2.2"
    id("com.github.kt3k.coveralls") version "2.12.0"
    id("org.mikeneck.graalvm-native-image") version "1.4.1"
    id("com.palantir.git-version") version "0.13.0" apply false
}

fun getProps(f: File): Properties {
    val props = Properties()
    try {
        props.load(FileInputStream(f))
    } catch (t: Throwable) {
        println("Can't read $f: $t, assuming empty")
    }
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
    version = if (details.isCleanTag) {  // release version
        baseVersion
    } else {  // snapshot version
        baseVersion + "-" + details.commitDistance + "-" + details.gitHash + "-SNAPSHOT"
    }
} else if (props.exists()) { // when version.properties already exist, just use it.
    version = getProps(props).getProperty("version")
}

tasks.register("writeVersionFile") {
    val folder = project.file("src/main/resources")
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

val home = System.getProperty("user.home")!!
tasks.register<JavaExec>("projectorRun") {
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("io.github.eb4j.ebview.EBViewer")
    systemProperty("org.jetbrains.projector.server.enable", "true")
    args = listOf("$home/Dicts")
    group = "application"
}

application.applicationDistribution.into("") {
    from("README.md", "COPYING")
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("io.github.eb4j:eb4j:2.3.1")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("tokyo.northside:url-protocol-handler:0.1.4")

    // for pdic
    implementation("io.github.eb4j:pdic4j:0.3.3")

    // for stardict
    implementation("io.github.dictzip:dictzip:0.11.2")
    implementation("com.github.takawitter:trie4j:0.9.8")

    // for lingvodsl
    implementation("io.github.eb4j:dsl4j:0.4.5")

    // for mdict
    implementation("io.github.eb4j:mdict4j:0.3.0")
    implementation("org.jsoup:jsoup:1.14.3")

    // for oxford-api
    implementation("tokyo.northside:java-oxford-dictionaries:0.3.1")

    // for video replay
    implementation("uk.co.caprica:vlcj:4.7.1")

    implementation("com.formdev:flatlaf:2.0.1")

    // for projector support
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.slf4j:slf4j-simple:1.7.36")
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.java-websocket:Java-WebSocket:1.5.2")
    implementation("dnsjava:dnsjava:2.1.9")
    implementation("org.javassist:javassist:3.27.0-GA")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")

    testImplementation("org.codehaus.groovy:groovy-all:3.0.9")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
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
        xml.required.set(true)
        html.required.set(true)
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
