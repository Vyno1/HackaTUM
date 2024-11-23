package org.jetbrains.plugins.template


object PythonHandler {
    fun call(scriptPath: String, args: List<String>): String {
        try {
            val processBuilder = ProcessBuilder("python", scriptPath) //, *args.toTypedArray())
            val process = processBuilder.start()
            val exit_code = process.waitFor()
            print(process.info())
            print(exit_code)
            return ""
        } catch (e: Exception) {
            return e.message ?: "Unknown error."
        }
    }
}