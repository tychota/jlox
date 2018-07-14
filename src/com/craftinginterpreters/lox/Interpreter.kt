package com.craftinginterpreters.lox

import com.craftinginterpreters.lox.TokenType.*

internal class Interpreter : Expr.Visitor<Any?>, Stmt.Visitor<Unit> {
    private var environment = Environment()

    fun interpret(statements: List<Stmt?>) {
        try {
            for (statement in statements) {
                execute(statement)
            }
        } catch (error: RuntimeError) {
            Lox.runtimeError(error)
        }
    }

    override fun visitAssignExpr(expr: Expr.Assign): Any? {
        val value = evaluate(expr.value)

        environment.assign(expr.name, value)
        return value
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
        return environment[expr.name]
    }

    override fun visitBlockStmt(stmt: Stmt.Block) {
        executeBlock(stmt.statements, Environment(environment))
    }

    override fun visitClassStmt(stmt: Stmt.Class) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitExpressionStmt(stmt: Stmt.Expression) {
        evaluate(stmt.expression)
    }

    override fun visitFunctionStmt(stmt: Stmt.Function) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitIfStmt(stmt: Stmt.If) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitPrintStmt(stmt: Stmt.Print) {
        val value = evaluate(stmt.expression)
        println(stringify(value))
    }

    override fun visitReturnStmt(stmt: Stmt.Return) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitVarStmt(stmt: Stmt.Var) {
        var value: Any? = null
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer)
        }

        environment.define(stmt.name.lexeme, value)
    }

    override fun visitWhileStmt(stmt: Stmt.While) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun execute(stmt: Stmt?) {
        stmt?.accept(this)
    }

    private fun executeBlock(statements: List<Stmt?>, environment: Environment) {
        val previous = this.environment
        try {
            this.environment = environment

            for (statement in statements) {
                execute(statement)
            }
        } finally {
            this.environment = previous
        }
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

    private fun stringify(obj: Any?): String {
        if (obj == null) return "nil"

        // Hack. Work around Java adding ".0" to integer-valued doubles.
        if (obj is Double) {
            var text = obj.toString()
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length - 2)
            }
            return text
        }

        return obj.toString()
    }
}