plugins {
    id("root-plugin")
}

defaultTasks("build")

rootProject.group = "com.badbones69.crazyenvoys"
rootProject.description = "Drop custom envoys with any prize you want all over spawn for players to fight over."
rootProject.version = "1.7"

tasks {
    assemble {
        val jarsDir = File("$rootDir/jars")
        if (jarsDir.exists()) jarsDir.delete()

        subprojects.forEach { project ->
            dependsOn(":${project.name}:build")

            doLast {
                if (!jarsDir.exists()) jarsDir.mkdirs()

                val file = file("${project.buildDir}/libs/${rootProject.name}-${rootProject.version}.jar")

                copy {
                    from(file)
                    into(jarsDir)
                }
            }
        }
    }
}