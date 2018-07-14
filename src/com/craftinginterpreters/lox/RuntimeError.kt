package com.craftinginterpreters.lox

open class RuntimeError(val token: Token, message: String) : RuntimeException(message)