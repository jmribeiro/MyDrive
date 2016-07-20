package pt.tecnico.grupo6.myDrive.presentation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Command {
  protected static final Logger log = LogManager.getRootLogger();
  
  private String _name, _help;
  private Shell _shell;

  public Command(Shell shell, String name) { 
    this(shell, name, "<no help>"); 
  }
  
  public Command(Shell shell, String name, String help) {
      _name = name;
      _help = help;
      (_shell = shell).add(this);
  }
  
  void help(String  help) { 
    _help = help;
  }

  public String name() { 
    return _name; 
  }

  public String help() { 
    return _help; 
  }

  public Shell shell() { 
    return _shell; 
  }

  abstract void execute(String[] args);

  public void print(String s) { 
    _shell.print(s); 
  }
  
  public void println(String s) { 
    _shell.println(s);
  }
  
  public void flush() { 
    _shell.flush(); 
  }
}
