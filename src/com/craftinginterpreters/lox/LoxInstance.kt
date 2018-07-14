package com.craftinginterpreters.lox

import java.util.*

internal class LoxInstance(private val klass: LoxClass) {
    private val fields = HashMap<String, Any?>()

    override fun toString(): String {
        return klass.name + " instance"
    }

    operator fun get(name: Token): Any? {
        if (fields.containsKey(name.lexeme)) {
            return fields[name.lexeme]
        }

        val method = klass.findMethod(this, name.lexeme)
        if (method != null) return method

        throw RuntimeError(name, "Undefined property '" + name.lexeme + "'.")
    }

    operator fun set(name: Token, value: Any?) {
        fields[name.lexeme] = value
    }
}