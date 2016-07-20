package pt.tecnico.grupo6.myDrive.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.tecnico.grupo6.myDrive.exception.*;
import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.service.dto.*;

import java.util.*;

public class AddVariableServiceTest extends ServiceTest{

	private Long validToken;
    private Long invalidToken;

	private final Long invalidTokenNull = new Long(0);

	private final String variableNameFilled = "newVariable";
	private final String variableNameAlreadyExist = "variableNameAlreadyExist"; //nao sei muito bem o que isto tem
	private final String variableNameNotFilled = "";
	private final String validValue = "/home/user/";
	private final String invalidValue = "/ home / hfd%vj / -+=} /";

//	Map <String, String> _mapa = new HasMap<String, String>();
    
    protected void populate() {
        try{
            
            MyDrive md = MyDrive.getInstance();
            md.reset();

            md.newUser("user1");
            md.newUser("user2");

            validToken = md.login("user1", "12345678");
            invalidToken = md.login("user2", "12345678");

            // user2 não tem sessão 
            md.logout(invalidToken);

            User user1 = md.getUserByUsername("user1");
            User user2 = md.getUserByUsername("user2");

        }catch(InvalidUsernameException | UserAlreadyExistsException e){
            //something went wrong!
        }
    }
    
	    /**
	    * Teste 1 
	    **/
	    @Test
	    public void success1() {
	    	Login log = MyDrive.getInstance().getLoginByToken(validToken);

            Map <String, String> _mapa = new HashMap<String, String>();

            _mapa.put(variableNameFilled, validValue);

	    	AddVariableService service = new AddVariableService(validToken, variableNameFilled, validValue);
	        service.execute();

	        for(String name : _mapa.keySet()){
	        	log.addVariable(name, _mapa.get(name));
	        }

	        Map<String, String> mapa2 = ((EnvironmentVariablesListDto)service.result()).getEnvironmentVariables();
			assertEquals(_mapa, mapa2);

	    }

	    /**
	    * Teste 2
	    **/
	    @Test
	    public void success2() {

	    	Login log = MyDrive.getInstance().getLoginByToken(validToken);

	    	Map <String, String> _mapa3 = new HashMap<String, String>();
            _mapa3.put(variableNameAlreadyExist, validValue);

	    	AddVariableService service = new AddVariableService(validToken, variableNameAlreadyExist, validValue);
	        service.execute();

	        for(String name : _mapa3.keySet()){
	        	log.addVariable(name, _mapa3.get(name));
	        }

	        Map<String, String> mapa4 = ((EnvironmentVariablesListDto)service.result()).getEnvironmentVariables();
	        assertEquals(_mapa3, mapa4);
	    }

	    /**
	    * Teste 3
	    **/ 
	    @Test(expected = InvalidSessionException.class)
	    public void invalidToken(){
	    	AddVariableService service = new AddVariableService(invalidToken, variableNameFilled, validValue);
	        service.execute();
	    }
		
		
	    /**
	    * Teste 4
	    **/
	   	@Test //penso que falta fazer uma excepção para uma variavel com um nome errado
	    public void invalidVariableName() {
	    	AddVariableService service = new AddVariableService(validToken, variableNameNotFilled, validValue);
	        service.execute();
	    }

	    /**
	    * Teste 5
	    **/
	    @Test //penso que falta fazer uma excepção para um value errado
	    public void invalidValue() {
	    	AddVariableService service = new AddVariableService(validToken, variableNameFilled, invalidValue);
	        service.execute();
	    }

	    /**
	    * Teste 6
	    **/
	    @Test(expected = InvalidSessionException.class)
     	public void invalidTokenNull(){
	        AddVariableService service = new AddVariableService(invalidTokenNull, variableNameFilled, validValue);
	        service.execute();
     	}
     	
}
