package pt.tecnico.grupo6.myDrive.exception;

public class ExecutionErrorException extends MyDriveException{

	public ExecutionErrorException(){
		super("An error has occured while executing a file");
	}

	public ExecutionErrorException(String message){
		super(message);
	}
}
