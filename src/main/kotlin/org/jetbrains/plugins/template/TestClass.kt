package org.jetbrains.plugins.template

import org.bouncycastle.util.test.FixedSecureRandom.BigInteger
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption


class TestClass {
    fun main(options: List<Options>) {
        val path = Paths.get("/Users/maxi/Desktop/example.txt")

        // Create the file if it does not exist
        if (!Files.exists(path)) {
            Files.createFile(path)
        }
        // Write some content to the file
        var content = options.joinToString(", ")
        Files.write(path, content.toByteArray(), StandardOpenOption.APPEND)

        println("File created and content written.")
    }
}
