package com.craftinginterpreters.lox

import java.io.IOException
import java.nio.charset.Charset
import java.nio.file.Paths
import java.nio.file.Files
import java.io.BufferedReader
import java.io.InputStreamReader


object Lox {
    private var hadError: Boolean = false

    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        when {
            args.size > 1 -> {
                println("Usage: jlox [script]")
                System.exit(64)
            }
            args.size == 1 -> runFile(args[0])
            else -> runPrompt()
        }
    }

    @Throws(IOException::class)
    private fun runFile(path: String) {
        val bytes = Files.readAllBytes(Paths.get(path))
        run(String(bytes, Charset.defaultCharset()))

        if (hadError) System.exit(65)
    }

    @Throws(IOException::class)
    private fun runPrompt() {
        val input = InputStreamReader(System.`in`)
        val reader = BufferedReader(input)

        while (true) {
            print("> ")
            run(reader.readLine())
            hadError = false
        }
    }

    private fun run(source: String) {
        /*val scanner = Scanner(source)
        val tokens = scanner.scanTokens()

        // For now, just print the tokens.
        for (token in tokens) {
            System.out.println(token)
        }*/
    }

    fun error(line: Int, message: String) {
        report(line, "", message)
    }

    private fun report(line: Int, where: String, message: String) {
        System.err.println("[line $line] Error$where: $message")
        hadError = true
    }
}
