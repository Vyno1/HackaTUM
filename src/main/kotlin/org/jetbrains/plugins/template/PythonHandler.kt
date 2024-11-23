package org.jetbrains.plugins.template

object PythonHandler {
    fun call(scriptPath: String, args: List<String>): String {
        try {
            val processBuilder = ProcessBuilder("python3", scriptPath, *args.toTypedArray())
            processBuilder.redirectErrorStream(true)
            val process = processBuilder.start()
            val exitCode = process.waitFor()
            val debug = process.inputStream.bufferedReader().readText()
            return if (exitCode == 0) "" else "Something went wrong. (exit code: $exitCode)"
        } catch (e: Exception) {
            return e.message ?: "Unknown error."
        }
    }
}