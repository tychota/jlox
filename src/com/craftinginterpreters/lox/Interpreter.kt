package com.craftinginterpreters.lox

import com.craftinginterpreters.lox.TokenType.*




internal class Interpreter : Expr.Visitor<Any?> {
    fun interpret(expression: Expr) {
        try {
            val value = evaluate(expression)
            println(stringify(value))
        } catch (error: RuntimeError) {
            Lox.runtimeError(error)
        }

    }

    override fun visitAssignExpr(expr: Expr.Assign): Any? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            GREATER -> {
                checkNumberOperands(expr.operator, left, right)
                left as Double > right as Double
            }
            GREATER_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                left as Double >= right as Double
            }
            LESS -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) < right as Double
            }
            LESS_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                left as Double <= right as Double
            }
            BANG_EQUAL -> !isEqual(left, right)
            EQUAL_EQUAL -> isEqual(left, right)
            MINUS -> {
                checkNumberOperands(expr.operator, left, right)
                left as Double - right as Double
            }
            PLUS -> when {
                left is Double && right is Double -> left + right
                left is String && right is String -> left + right
                else -> throw RuntimeError(expr.operator, "Operands must be two numbers or two strings.")
            }
            SLASH -> {
                checkNumberOperands(expr.operator, left, right)
                left as Double / right as Double
            }
            STAR -> {
                checkNumberOperands(expr.operator, left, right)
                left as Double * right as Double
            }
        // Unreachable.
            else -> null
        }

    }

    override fun visitCallExpr(expr: Expr.Call): Any? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitGetExpr(expr: Expr.Get): Any? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Any? {
        return evaluate(expr.expression)
    }

    override fun visitLiteralExpr(expr: Expr.Literal): Any? {
        return expr.value
    }

    override fun visitLogicalExpr(expr: Expr.Logical): Any? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitSetExpr(expr: Expr.Set): Any? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitSuperExpr(expr: Expr.Super): Any? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitThisExpr(expr: Expr.This): Any? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitUnaryExpr(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            BANG -> isTruthy(right)
            MINUS -> {
                checkNumberOperand(expr.operator, right)
                -(right as Double)
            }
        // unreachable
            else -> null
        }
    }

    override fun visitVariableExpr(expr: Expr.Variable): Any? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun evaluate(expr: Expr): Any? {
        return expr.accept(this)
    }

    private fun isTruthy(obj: Any?): Boolean {
        return if (obj == null) false else obj as? Boolean ?: true
    }

    private fun isEqual(a: Any?, b: Any?): Boolean {
        // nil is only equal to nil.
        if (a == null && b == null) return true
        return if (a == null) false else a == b
    }

    private fun checkNumberOperand(operator: Token, operand: Any?) {
        if (operand is Double) return
        throw RuntimeError(operator, "Operand must be a number.")
    }

    private fun checkNumberOperands(operator: Token, left: Any?, right: Any?) {
        if (left is Double && right is Double) return

        throw RuntimeError(operator, "Operands must be numbers.")
    }

    private fun stringify(`object`: Any?): String {
        if (`object` == null) return "nil"

        // Hack. Work around Java adding ".0" to integer-valued doubles.
        if (`object` is Double) {
            var text = `object`.toString()
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length - 2)
            }
            return text
        }

        return `object`.toString()
    }
}