package com.craftinginterpreters.lox

import java.util.ArrayList

import com.craftinginterpreters.lox.TokenType.*


internal class Scanner(private val source: String) {
    private val tokens = ArrayList<Token>()

    private var start = 0
    private var current = 0
    private var line = 1

    companion object {
        val keywords: Map<String, TokenType> = hashMapOf(
                "and" to AND,
                "class" to CLASS,
                "else" to ELSE,
                "and" to AND,
                "class" to CLASS,
                "else" to ELSE,
                "false" to FALSE,
                "for" to FOR,
                "fun" to FUN,
                "if" to IF,
                "nil" to NIL,
                "or" to OR,
                "print" to PRINT,
                "return" to RETURN,
                "super" to SUPER,
                "this" to THIS,
                "true" to TRUE,
                "var" to VAR,
                "while" to WHILE
        )
    }

    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current
            scanToken()
        }

        addEndOfFile()
        return tokens
    }

    private fun isAtEnd(): Boolean {
        return current >= source.length
    }

    private fun scanToken() {
        val c = advance()
        when (c) {
            '(' -> addToken(LEFT_PAREN)
            ')' -> addToken(RIGHT_PAREN)
            '{' -> addToken(LEFT_BRACE)
            '}' -> addToken(RIGHT_BRACE)
            ',' -> addToken(COMMA)
            '.' -> addToken(DOT)
            '-' -> addToken(MINUS)
            '+' -> addToken(PLUS)
            ';' -> addToken(SEMICOLON)
            '*' -> addToken(STAR)
            '!' -> addToken(if (match('=')) BANG_EQUAL else BANG)
            '=' -> addToken(if (match('=')) EQUAL_EQUAL else EQUAL)
            '<' -> addToken(if (match('=')) LESS_EQUAL else LESS)
            '>' -> addToken(if (match('=')) GREATER_EQUAL else GREATER)
            '/' -> if (match('/')) {
                // A comment goes until the end of the line.
                while (peek() != '\n' && !isAtEnd()) advance()
            } else {
                addToken(SLASH)
            }
            ' ', '\r', '\t' -> {
            }

            '\n' -> line++
            '"' -> string()
            else -> when {
                isDigit(c) -> number()
                isAlpha(c) -> identifier()
                else -> Lox.error(line, "Unexpected character.")
            }
        }// Ignore whitespace.
    }

    private fun string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++
            advance()
        }

        // Unterminated string.
        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.")
            return
        }

        // The closing ".
        advance()

        // Trim the surrounding quotes.
        val value = source.substring(start + 1, current - 1)
        addToken(STRING, value)
    }

    private fun number() {
        while (isDigit(peek())) advance()

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance()

            while (isDigit(peek())) advance()
        }

        addToken(NUMBER, java.lang.Double.parseDouble(source.substring(start, current)))
    }

    private fun identifier() {
        while (isAlphaNumeric(peek())) advance()

        // See if the identifier is a reserved word.
        val text = source.substring(start, current)

        var type = keywords[text]
        if (type == null) type = IDENTIFIER
        addToken(type)
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source[current] != expected) return false

        current++
        return true
    }

    private fun peek(): Char {
        return if (isAtEnd()) '\u0000' else source[current]
    }

    private fun peekNext(): Char {
        return if (current + 1 >= source.length) '\u0000' else source[current + 1]
    }


    private fun advance(): Char {
        current++
        return source[current - 1]
    }

    private fun isDigit(c: Char): Boolean {
        return c in '0'..'9'
    }

    private fun isAlpha(c: Char): Boolean {
        return c in 'a'..'z' ||
                c in 'A'..'Z' ||
                c == '_'
    }

    private fun isAlphaNumeric(c: Char): Boolean {
        return isAlpha(c) || isDigit(c)
    }

    private fun addToken(type: TokenType) {
        addToken(type, null)
    }

    private fun addToken(type: TokenType, literal: Any?) {
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }

    private fun addEndOfFile() {
        tokens.add(Token(EOF, "", null, line))
    }
}