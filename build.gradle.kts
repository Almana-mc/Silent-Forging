plugins {
    `java-library`
    `maven-publish`
    idea
    id("net.neoforged.moddev") version "2.0.141"
}

val mod_version: String by project
val mod_group_id: String by project
val mod_id: String by project
val neo_version: String by project
val minecraft_version: String by project
val minecraft_version_range: String by project
val neo_version_range: String by project
val loader_version_range: String by project
val mod_name: String by project
val mod_license: String by project
val mod_authors: String by project
val mod_description: String by project

version = mod_version
group = mod_group_id

repositories {
    mavenLocal()
    maven {
        name = "CurseMaven"
        url = uri("https://cursemaven.com")
        content { includeGroup("curse.maven") }
    }
}

base {
    archivesName = "$mod_id-$minecraft_version"
}

java.toolchain.languageVersion = JavaLanguageVersion.of(25)

neoForge {
    version = neo_version

    runs {
        create("client") {
            client()
            systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
        }

        create("server") {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
        }

        create("gameTestServer") {
            type = "gameTestServer"
            systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
        }

        create("data") {
            serverData()
            programArguments.addAll(
                "--mod", mod_id,
                "--all",
                "--output", file("src/generated/resources/").absolutePath,
                "--existing", file("src/main/resources/").absolutePath
            )
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods {
        create(mod_id) {
            sourceSet(sourceSets["main"])
        }
    }
}

sourceSets["main"].resources.srcDir("src/generated/resources")

dependencies {
    implementation("curse.maven:silent-gear-297039:8089163")
    implementation("curse.maven:silent-lib-242998:7936002")
    implementation("curse.maven:jei-238222:8014757")
    implementation("curse.maven:guideme-1173950:8129704")
}

val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val replaceProperties = mapOf(
        "minecraft_version" to minecraft_version,
        "minecraft_version_range" to minecraft_version_range,
        "neo_version" to neo_version,
        "neo_version_range" to neo_version_range,
        "loader_version_range" to loader_version_range,
        "mod_id" to mod_id,
        "mod_name" to mod_name,
        "mod_license" to mod_license,
        "mod_version" to mod_version,
        "mod_authors" to mod_authors,
        "mod_description" to mod_description
    )
    inputs.properties(replaceProperties)
    expand(replaceProperties)
    from("src/main/templates")
    into("build/generated/sources/modMetadata")
}

sourceSets["main"].resources.srcDir(generateModMetadata)
neoForge.ideSyncTask(generateModMetadata)

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri(layout.projectDirectory.dir("repo"))
        }
    }
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
