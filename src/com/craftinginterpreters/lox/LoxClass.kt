package com.craftinginterpreters.lox


internal class LoxClass(val name: String, private val methods : Map<String, LoxFunction>): LoxCallable {
    override fun arity(): Int {
        val initializer = methods["init"] ?: return 0
        return initializer.arity()
    }

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
        val instance = LoxInstance(this)

        val initializer = methods["init"]
        initializer?.bind(instance)?.call(interpreter, arguments)

        return instance
    }

    override fun toString(): String {
        return name
    }

    fun findMethod(instance: LoxInstance, name: String): LoxFunction? {
        return if (methods.containsKey(name)) {
            methods[name]!!.bind(instance)
        } else null
    }
}