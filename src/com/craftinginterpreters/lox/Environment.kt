package com.craftinginterpreters.lox


import java.util.*

internal class Environment(val enclosing: Environment? = null) {
    private val values = HashMap<String, Any?>()

    fun define(name: String, value: Any?) {
        values[name] = value
    }

    operator fun get(name: Token): Any? {
        when {
            values.containsKey(name.lexeme) -> return values[name.lexeme]
            else -> if (enclosing != null) return enclosing[name]
        }

        throw RuntimeError(name, "Undefined variable '${name.lexeme}'.")
    }

    fun assign(name: Token, value: Any?) {
        when {
            values.containsKey(name.lexeme) -> values[name.lexeme] = value
            enclosing != null -> enclosing.assign(name, value)
            else -> throw RuntimeError(name, "Undefined variable '" + name.lexeme + "'.")
        }
    }
}