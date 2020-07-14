@file:Suppress("unused")

package me.jakejmattson.kutils.api.arguments

import me.jakejmattson.kutils.api.dsl.arguments.*
import me.jakejmattson.kutils.api.dsl.command.CommandEvent

internal data class Left<out L>(val data: L) : Either<L, Nothing>()
internal data class Right<out R>(val data: R) : Either<Nothing, R>()

/**
 * Represent 2 possible types in a single object.
 */
sealed class Either<out L, out R> {
    /**
     * Map the actual internal value to either the left or right predicate.
     *
     * @param left The value map if the left element is present.
     * @param right The value map if the right element is present.
     */
    fun <T> map(left: (L) -> T, right: (R) -> T) =
        when (this) {
            is Left -> left.invoke(data)
            is Right -> right.invoke(data)
        }
}

/**
 * Accept either the left argument or the right [ArgumentType].
 *
 * @param left The first [ArgumentType] to attempt to convert the data to.
 * @param right The second [ArgumentType] to attempt to convert the data to.
 */
class EitherArg<L, R>(val left: ArgumentType<L>, val right: ArgumentType<R>, name: String = "") : ArgumentType<Either<L, R>>() {
    override val name = if (name.isNotBlank()) name else "${left.name} | ${right.name}"

    override fun convert(arg: String, args: List<String>, event: CommandEvent<*>): ArgumentResult<Either<L, R>> {
        val leftResult = left.convert(arg, args, event)
        val rightResult = right.convert(arg, args, event)

        return when {
            leftResult is Success -> Success(Left(leftResult.result), leftResult.consumed)
            rightResult is Success -> Success(Right(rightResult.result), rightResult.consumed)
            else -> Error("Could not match input with either expected argument.")
        }
    }

    override fun generateExamples(event: CommandEvent<*>): List<String> {
        val leftExample = left.generateExamples(event).takeIf { it.isNotEmpty() }?.random() ?: "<Example>"
        val rightExample = right.generateExamples(event).takeIf { it.isNotEmpty() }?.random() ?: "<Example>"

        return listOf("$leftExample | $rightExample")
    }
}

/**
 * Syntactic sugar for creating an EitherArg from the two given types.
 */
infix fun <L, R> ArgumentType<L>.or(right: ArgumentType<R>) = EitherArg(this, right)