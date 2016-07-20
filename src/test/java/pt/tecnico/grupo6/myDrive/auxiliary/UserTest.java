package pt.tecnico.grupo6.myDrive.auxiliary;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.tecnico.grupo6.myDrive.exception.*;
import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.service.dto.*;

import java.util.*;

public class UserTest extends DomainTest{
	
	MyDrive md;

    protected void populate() {
        try{
            
            md = MyDrive.getInstance();
            md.reset();

        }catch(Exception e){
            //something went wrong!
        }
    }
    
    @Test
	public void success(){
		User u = new User(md, "validUsername", "validPassword");
	}

	@Test(expected = InvalidUsernameException.class)
	public void invalidUsernameEmpty(){
		User u = new User(md, "", "validPassword");
	}

	@Test(expected = InvalidUsernameException.class)
	public void invalidUsernameInvalidChars(){
		User u = new User(md, "valid us3rname ;&%", "validPassword");
	}

	@Test(expected = InvalidUsernameException.class)
	public void invalidUsernameLessThan3(){
		User u = new User(md, "12", "validPassword");
	}

	@Test(expected = InvalidPasswordException.class)
	public void invalidPasswordEmpty(){
		User u = new User(md, "validUsername", "");
	}

	@Test(expected = InvalidPasswordException.class)
	public void invalidPasswordLessThan8(){
		User u = new User(md, "validUsername", "123");
	}

	@Test(expected = UserAlreadyExistsException.class)
	public void alreadyExists(){
		User u = new User(md, "root", "validPassword");
	}

	
}
