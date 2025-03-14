import org.openapitools.generator.gradle.plugin.tasks.GenerateTask;

buildscript {
    dependencies {
        classpath("org.openapitools:openapi-generator-gradle-plugin:6.2.1")
    }
    repositories {
        maven {url = uri("https://repo1.maven.org/maven2/") }
        maven {url = uri("https://artifacts.cloudflight.io/repository/plugins-maven/") }
    }
}

val outputPath = "$buildDir/generated/sources/openapi"
val openApiPackage = "ru.random_walk.swagger"
val swaggerBasePath = "$projectDir/src/main/resources/swagger"

abstract class OpenApiGenerateTask @Inject constructor(
        inputSpecPath: String,
        outputDirPath: String,
        apiPackagePath: String,
        invokerPackagePath: String,
        modelPackagePath: String
) : GenerateTask() {
    init {
        inputSpec.set(inputSpecPath)
        outputDir.set(outputDirPath)
        generateApiTests.set(false)
        skipValidateSpec.set(true)
        generatorName.set("java")
        apiPackage.set(apiPackagePath)
        invokerPackage.set(invokerPackagePath)
        modelPackage.set(modelPackagePath)
        library.set("rest-assured")
        generateModelDocumentation.set(false)
        generateApiDocumentation.set(false)
        configOptions.set(
                mapOf(
                        "dateLibrary" to "java8",
                        "hideGenerationTimestamp" to "true"
                )
        )
    }
}

val openapiSpecs = mapOf(
        "auth_service" to "$swaggerBasePath/auth-service.json",
        "matcher_service" to "$swaggerBasePath/matcher-service.json",
        "chat_service" to "$swaggerBasePath/chat-service.json"
)

openapiSpecs.forEach {
        tasks.register<OpenApiGenerateTask>(
                "generate_${it.key}",
                it.value,
                outputPath,
                "$openApiPackage.${it.key}.api",
                "$openApiPackage.${it.key}.invoker",
                "$openApiPackage.${it.key}.model"
        )
}
