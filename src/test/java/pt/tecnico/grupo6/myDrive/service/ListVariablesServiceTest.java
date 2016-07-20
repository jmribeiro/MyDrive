package pt.tecnico.grupo6.myDrive.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.tecnico.grupo6.myDrive.exception.*;
import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.service.dto.*;
import pt.tecnico.grupo6.myDrive.service.*;
import java.util.*;

public class ListVariablesServiceTest extends ServiceTest{
            
    String existingVar = "var1";
    String value = "value1";
    Long validToken;
    Map<String, String> expectedVars = new TreeMap<String, String>();

    protected void populate() {
        try{
            
            MyDrive md = MyDrive.getInstance();
            md.reset();

            md.newUser("user1");

            validToken = md.login("user1", "12345678");

            Login validLogin = md.getLoginByToken(validToken);            
            Set<EnvironmentVariable> varsSet = validLogin.addVariable(existingVar, value);
          	
			for(EnvironmentVariable var : varsSet){
				expectedVars.put(new String(var.getName()), new String(var.getValue()));
			}


        }catch(Exception e){
            //something went wrong!
        }
    }

    @Test
    public void listVarsSuccess(){
    	
    	ListVariablesService service = new ListVariablesService(validToken);
    	service.execute();

    	EnvironmentVariablesListDto res = (EnvironmentVariablesListDto)service.result();
    	Map<String, String> vars = res.getEnvironmentVariables();

    	assertEquals(vars, expectedVars);
    }
	
	@Test
	public void listVarSuccess(){

		ListVariablesService service = new ListVariablesService(validToken, existingVar);
    	service.execute();

    	EnvironmentVariablesListDto res = (EnvironmentVariablesListDto)service.result();

    	String resValue = res.getValue();

    	assertEquals(resValue, value);
	}
     	
}
