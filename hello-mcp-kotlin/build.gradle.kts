plugins {
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.serialization") version "2.1.20"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "org.feuyeux.ai"
version = "1.0.0"

repositories {
    // 国内 Maven 镜像仓库 - 优先使用
    maven {
        name = "Aliyun"
        url = uri("https://maven.aliyun.com/repository/public")
    }
    maven {
        name = "Aliyun Central"
        url = uri("https://maven.aliyun.com/repository/central")
    }
    maven {
        name = "Aliyun JCenter"
        url = uri("https://maven.aliyun.com/repository/jcenter")
    }
    maven {
        name = "Tencent"
        url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
    }
    maven {
        name = "Huawei"
        url = uri("https://repo.huaweicloud.com/repository/maven/")
    }
    
    // 备用官方仓库
    mavenCentral()
    gradlePluginPortal()
}

val mcpVersion = "0.5.0"
val slf4jVersion = "2.0.17"
val ktorVersion = "3.1.1"
val kotlinxCoroutinesVersion = "1.10.1"

dependencies {
    // Kotlin standard library and coroutines
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    // Official Kotlin MCP SDK
    implementation("io.modelcontextprotocol:kotlin-sdk:$mcpVersion")
    
    // Logging
    implementation("org.slf4j:slf4j-nop:$slf4jVersion")
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.4")
    
    // Ktor for HTTP client/server if needed
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-cio:$ktorVersion")
    implementation("io.ktor:ktor-server-sse:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    
    // Test dependencies
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinxCoroutinesVersion")
    testImplementation("io.mockk:mockk:1.13.17")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("org.feuyeux.ai.hello.HelloMcpApplication")
}

tasks.register<JavaExec>("runClient") {
    group = "application"
    description = "Run the MCP client with element query"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.feuyeux.ai.hello.HelloMcpApplication")
    args("client", "氢")
}

tasks.register<JavaExec>("runServer") {
    group = "application"
    description = "Run the MCP server"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.feuyeux.ai.hello.HelloMcpApplication")
    args("server")
}

tasks.register<JavaExec>("runTest") {
    group = "application"
    description = "Run MCP client test"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.feuyeux.ai.hello.HelloMcpApplication")
    args("test")
}

tasks.register<JavaExec>("runPeriodicTableServer") {
    group = "application"
    description = "Run periodic table MCP server directly"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.feuyeux.ai.hello.PeriodicTableServerKt")
}
