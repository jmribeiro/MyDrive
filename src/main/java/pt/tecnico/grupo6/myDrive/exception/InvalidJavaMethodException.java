package pt.tecnico.grupo6.myDrive.exception;

public class InvalidJavaMethodException extends MyDriveException{

	private String _method;

	public InvalidJavaMethodException(String method){
		_method = method;
	}

	@Override
	public String getMessage(){
		return _method+" is not a Java Fully Qualified Name.";
	}
}