package pt.tecnico.grupo6.myDrive.exception;

public class NoAssociationException extends MyDriveException{

	public NoAssociationException(){
		super("No extension exists for this file");
	}
}
