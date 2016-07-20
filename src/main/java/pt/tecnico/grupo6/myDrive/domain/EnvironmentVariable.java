package pt.tecnico.grupo6.myDrive.domain;

public class EnvironmentVariable extends EnvironmentVariable_Base {
    
    private EnvironmentVariable() {
        super();
    }

    public EnvironmentVariable(Login login, String name, String value){
    	super();
    	init(login, name, value);
    }

    public EnvironmentVariable(Login login, String name){
    	super();
    	init(login, name, "");
    }

    private void init(Login login, String name, String value){
    	setLogin(login);
    	setName(name);
    	setValue(value);
    }

    public void changeValue(String value){
    	setValue(value);
    }
    
    public void remove(){
    	setLogin(null);
    	deleteDomainObject();
    }

}
