package pt.tecnico.grupo6.myDrive.exception;

public class InvalidExecutionException extends MyDriveException{

	public InvalidExecutionException(){
		super("Arguments are incompatible with execution");
	}

	public InvalidExecutionException(String message){
		super(message);
	}
}
