package me.aberrantfox.kjdautils.internal.arguments

import me.aberrantfox.kjdautils.api.dsl.command.CommandEvent
import me.aberrantfox.kjdautils.internal.command.*

open class EveryArg(override val name: String = "Text") : ArgumentType<String>() {
    companion object : EveryArg()

    override fun convert(arg: String, args: List<String>, event: CommandEvent<*>) = ArgumentResult.Success(args.joinToString(" "), args.size)

    override fun generateExamples(event: CommandEvent<*>) = listOf("This is a sample sentence.")
}