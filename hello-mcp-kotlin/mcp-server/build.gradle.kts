plugins {
    application
}

application {
    mainClass.set("org.feuyeux.ai.hello.HelloMcpServerKt")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
