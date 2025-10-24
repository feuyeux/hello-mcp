plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
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

// 版本管理
val mcpKotlinSdkVersion = "0.7.2"  // Kotlin MCP SDK
val ktorVersion = "3.0.3"
val slf4jVersion = "2.0.17"
val kotlinxCoroutinesVersion = "2.0.21-coroutines-KBA-001"
val junitVersion = "5.11.4"

// 所有子项目的公共配置
subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

    repositories {
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
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        // Kotlin standard library and coroutines
        add("implementation", "org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
        add("implementation", "org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

        // MCP Kotlin SDK
        add("implementation", "io.modelcontextprotocol:kotlin-sdk-core:$mcpKotlinSdkVersion")
        add("implementation", "io.modelcontextprotocol:kotlin-sdk-client:$mcpKotlinSdkVersion")
        add("implementation", "io.modelcontextprotocol:kotlin-sdk-server:$mcpKotlinSdkVersion")

        // Ktor for HTTP server
        add("implementation", "io.ktor:ktor-server-core:$ktorVersion")
        add("implementation", "io.ktor:ktor-server-netty:$ktorVersion")
        add("implementation", "io.ktor:ktor-server-content-negotiation:$ktorVersion")
        add("implementation", "io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

        // Ktor for HTTP client
        add("implementation", "io.ktor:ktor-client-core:$ktorVersion")
        add("implementation", "io.ktor:ktor-client-cio:$ktorVersion")

        // Jackson for JSON processing (for Ollama client)
        add("implementation", "com.fasterxml.jackson.core:jackson-databind:2.18.2")

        // Logging
        add("implementation", "org.slf4j:slf4j-api:$slf4jVersion")
        add("implementation", "ch.qos.logback:logback-classic:1.5.18")
        add("implementation", "io.github.oshai:kotlin-logging-jvm:7.0.13")

        // Test dependencies
        add("testImplementation", kotlin("test"))
        add("testImplementation", "org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinxCoroutinesVersion")
        add("testImplementation", "org.junit.jupiter:junit-jupiter:$junitVersion")
    }

    tasks.test {
        useJUnitPlatform()
    }

    kotlin {
        jvmToolchain(21)
    }
}