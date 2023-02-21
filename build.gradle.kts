plugins {
    java
    application
}

println("Cool print here")
group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:16.0.2")
    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

val coolTask = tasks.register("coolTask") {
    group = "build"
    println("I'm ${this.name}")
    doFirst {
        println("I'm cool and rolling")
    }
}

java {
    sourceSets {
        main {
            java.setSrcDirs(listOf("src/main"))
            resources.setSrcDirs(listOf("src/resources"))
        }
        test {
            java.setSrcDirs(listOf("src/test"))
        }
    }
}

tasks.compileJava {
    dependsOn(coolTask)
    options.release.set(11)
}

tasks.test {
    useJUnitPlatform()
}
