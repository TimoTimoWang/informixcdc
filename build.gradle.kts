plugins {
    kotlin("jvm") version "1.3.21"
    application
}

group = "com.canastic"
version = "0.1.0-alpha"

repositories {
    maven {
        url = uri("https://maven.aliyun.com/repository/public")
    }
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    //compile(files("${System.getenv("INFORMIXDIR")}/jdbc/lib/ifxjdbc.jar"))
    implementation(files("libs/gbasedbtjdbc_3.3.0_2_36477d.jar"))
   // implementation("ifxjdbc", "com.ibm.informix:jdbc:4.50.9")
    implementation("de.huxhorn.sulky:de.huxhorn.sulky.ulid:8.2.0")
    implementation("com.beust:klaxon:5.0.1")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.3.31")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}

application {
    // Define the main class for the application.
    mainClassName = "informixcdc.main.MainKt"
}
