package pt.tecnico.grupo6.myDrive.presentation;

public abstract class MdCommand extends Command {
  
  public MdCommand(MdShell shell, String name) { 
  	super(shell, name); 
  }

  public MdCommand(MdShell shell, String name, String help) { 
  	super(shell, name, help); 
  }
  
  @Override
  public MdShell shell() { 
    return (MdShell)super.shell(); 
  }
}