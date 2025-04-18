plugins {
    java
    application
    id("io.freefair.lombok") version "6.6.3"
    id("org.springframework.boot") version "2.7.5"
    id("com.diffplug.spotless") version "6.19.0"
    id("org.openapi.generator") version "4.3.0"
    id("io.spring.dependency-management") version "1.1.6"
    id("io.qameta.allure") version "2.12.0"
}

group = "random-walk.automation"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(18))
    }
}

sourceSets {
    main {
        java {
            srcDir("$buildDir/generated/sources/openapi/src/main/java")
        }
    }
}
apply {
    from("openapi.gradle.kts")
}

dependencies {
    val springVersion = "2.7.5"
    val allureVersion = "2.27.0"

    implementation("org.postgresql:postgresql:42.6.0")

    implementation("io.rest-assured:rest-assured")
    implementation("io.qameta.allure:allure-rest-assured:$allureVersion")
    implementation("io.qameta.allure:allure-junit5:$allureVersion")
    implementation("org.aspectj:aspectjweaver:1.8.3")
    implementation("com.github.mwiede:jsch:0.2.17")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa:$springVersion")
    implementation("org.springframework.boot:spring-boot-starter-validation:$springVersion")
    implementation("org.springframework.boot:spring-boot-starter-aop:$springVersion")
    implementation("org.springframework.boot:spring-boot-starter-test:$springVersion")

    implementation("org.springframework.retry:spring-retry:1.3.3")
    implementation("ch.qos.logback:logback-core:1.2.9")
    implementation("org.slf4j:slf4j-api")

    implementation("com.google.code.gson:gson:2.8.5")
    implementation("io.gsonfire:gson-fire:1.8.4")

    implementation("io.qameta.allure:allure-java-commons:$allureVersion")
    implementation("org.awaitility:awaitility:4.2.0")
    implementation("org.apache.commons:commons-text:1.10.0")
    implementation("commons-codec:commons-codec:1.13")

    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.0")

    implementation("com.google.api-client:google-api-client:2.7.0")
    implementation("org.seleniumhq.selenium:selenium-java:4.26.0")

    implementation("com.squareup.okio:okio:1.0.0")
    implementation("io.swagger:swagger-annotations:1.6.1")
    implementation("io.swagger.codegen.v3:swagger-codegen:3.0.30")

    implementation("org.hamcrest:hamcrest:2.2")
    implementation("org.apache.pdfbox:pdfbox:2.0.25")
    implementation("com.vladmihalcea:hibernate-types-52:2.11.1")

    implementation("org.openapitools:jackson-databind-nullable:0.2.4")

    implementation("com.google.protobuf:protobuf-java:3.22.2")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("com.github.viclovsky:swagger-coverage-rest-assured:1.5.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
}

tasks.compileJava {
    if (!file("$buildDir").exists()) {
        dependsOn(
                "generate_auth_service",
                "generate_matcher_service",
                "generate_chat_service"
        )
    }
}
tasks.bootJar {
    enabled = false
}
tasks.clean {
    delete("$rootDir/allure-results")
    delete("$rootDir/out")
}
tasks.test {

    systemProperty("junit.jupiter.extensions.autodetection.enabled", "true")
    systemProperty("org.aspectj.weaver.Dump.exception", "false")
    systemProperty("user.timezone", "Europe/Moscow")
    /**
     * Обязательно использование следующих правил
     * https://junit.org/junit5/docs/current/user-guide/#running-tests-tag-expressions
     *
     */
    jvmArgs = listOf("-Xmx2g", "-Xms1g", "-Xss512k", "-Dfile.encoding=UTF-8", "-Dorg.aspectj.weaver.Dump.exception=false", "-Djavax.net.ssl.trustStoreType=jks", "-Djavax.net.ssl.trustStore=", "-Djavax.net.ssl.trustStorePassword=", "-Dorg.aspectj.weaver.loadtime.configuration=ignored")
    outputs.cacheIf { false }
    useJUnitPlatform {

        val includedTags = project.properties["includeTags"] as String?
        val excludedTags = project.properties["excludeTags"] as String?
        if (!includedTags.isNullOrBlank()) {
            includeTags(includedTags)
        }
        if (!excludedTags.isNullOrBlank()) {
            excludeTags(excludedTags)
        }
    }
}

spotless {
    java {
        eclipse("4.26").configFile("src/main/resources/config/codestyle.xml")
        formatAnnotations()
        importOrder("", "java|javax", "\\#")
        removeUnusedImports()

        targetExclude("build/generated/**/*.java")
    }
}