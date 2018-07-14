package com.craftinginterpreters.lox

import java.util.*

internal class Environment(private val enclosing: Environment? = null) {
    private val values = HashMap<String, Any?>()

    fun define(name: String, value: Any?) {
        values[name] = value
    }

    private fun ancestor(distance: Int): Environment? {
        var environment: Environment? = this

        for (i in 0 until distance) {
            environment = environment?.enclosing
        }

        return environment
    }

    fun getAt(distance: Int, name: String): Any? {
        return ancestor(distance)!!.values[name]
    }

    fun assignAt(distance: Int, name: Token, value: Any?) {
        ancestor(distance)!!.values[name.lexeme] = value
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