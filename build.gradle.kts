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

val versionDetails: groovy.lang.Closure<com.palantir.gradle.gitversion.VersionDetails> by extra
val details = versionDetails()
if (details.isCleanTag) {
    version = details.lastTag.substring(1)
} else {
    version = details.lastTag.substring(1) + "-" + details.commitDistance + "-" + details.gitHash + "-SNAPSHOT"
}

tasks.register("writeVersionFile") {
    val folder = project.file("src/main/resources");
    if (!folder.exists()) {
        folder.mkdirs()
    }
    val props = project.file("src/main/resources/version.properties")
    props.delete()
    props.appendText("version=" + project.version + "\n")
    props.appendText("commit=" + details.gitHashFull + "\n")
    props.appendText("branch=" + details.branchName)
}

tasks.getByName("jar") {
    dependsOn("writeVersionFile")
}

group = "io.github.eb4j"

application {
    mainClassName = "io.github.eb4j.ebview.EBViewer"
    applicationName = "ebviewer"
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("io.github.eb4j:eb4j:2.2.1-3-daf1af5f9e-SNAPSHOT")
    implementation("org.slf4j:slf4j-simple:1.7.32")

    implementation("commons-io:commons-io:2.11.0")
    implementation("org.apache.commons:commons-lang3:3.11")

    implementation("io.github.dictzip:dictzip:0.9.5")
    implementation("com.github.takawitter:trie4j:0.9.8")

    implementation("com.formdev:flatlaf:1.2")
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
