package ru.spbau.jvm.scala.task01.commands


final class CommandsSet {

  private var commands: List[AbstractCommand] = Nil

  /**
    * Register a new command in the set of used commands
    * @param command command to register
    */
  def registerCommand(command: AbstractCommand): Unit = {
    command.checkCommand()
    commands = commands.::(command)
  }

  /**
    * Command with name `name`. If multiple found the one with type `commandType` will be picked
    *
    * This functionality might seem tricky, but it helps if two commands share the same name (like subtraction
    * and unary minus)
    */
  def findCommand(name: String, commandType: CommandType.Value): AbstractCommand = {
    val foundCommands = commands.filter((cmd) => cmd.getName.equals(name))

    if (foundCommands.isEmpty) {
      throw new RuntimeException(s"Command $name not found")
    }

    foundCommands.find((cmd) => cmd.getCommandType == commandType) match {
      case Some(cmd) =>
        cmd
      case None =>
        foundCommands.head
    }
  }

}
