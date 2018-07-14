package com.craftinginterpreters.lox


internal class LoxClass(val name: String, val methods : Map<String, LoxFunction>): LoxCallable {
    override fun arity(): Int {
        return 0
    }

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
        return LoxInstance(this)
    }

    override fun toString(): String {
        return name
    }

    fun findMethod(instance: LoxInstance, name: String): LoxFunction? {
        return if (methods.containsKey(name)) {
            methods[name]
        } else null

    }
}