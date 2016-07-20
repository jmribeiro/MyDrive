package pt.tecnico.grupo6.myDrive.service;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pt.tecnico.grupo6.myDrive.exception.*;
import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.service.dto.*;

public class LoginUserServiceTest extends ServiceTest{
    
    private final String username = "admin";

    private final String usernameThatDoesntExist = "hello";

    private final String validPassword = "12345678";
    private final String wrongPassword = "...";
    
    protected void populate() {
        try{
            MyDrive md = MyDrive.getInstance();
            md.reset();
            md.newUser(username);
        }catch(InvalidUsernameException | UserAlreadyExistsException e){
            //Se cair aqui deviar ser valido la em cima
        }
    }

    /**
    * Teste 1
    **/
    @Test
    public void success() {
        
        LoginUserService service = new LoginUserService(username, validPassword);
        service.execute();
        
        assertTrue(((TokenDto)service.result()).getToken()!=null && MyDrive.getInstance().tokenExists(((TokenDto)service.result()).getToken()));
    }

    /**
    * Teste 2
    **/
    @Test(expected = WrongPasswordException.class)
    public void wrongPassword(){
        LoginUserService service = new LoginUserService(username, wrongPassword);
        service.execute();
    }

    /**
    * Teste 3
    **/
    @Test(expected = EmptyLoginFieldsException.class)
    public void filledUserButEmptyPassword(){
        LoginUserService service = new LoginUserService(username, "");
        service.execute();
    }

    /**
    * Teste 4
    **/
    @Test(expected = UserDoesntExistException.class)
    public void userNotRegisteredButFilledPassword(){
        LoginUserService service = new LoginUserService(usernameThatDoesntExist, "irrelevant");
        service.execute();
    }

    /**
    * Teste 5
    **/
    @Test(expected = EmptyLoginFieldsException.class)
    public void userNotRegisteredButEmptyPassword(){
        LoginUserService service = new LoginUserService(usernameThatDoesntExist, "");
        service.execute();
    }

    /**
    * Teste 6
    **/
    @Test(expected = EmptyLoginFieldsException.class)
    public void emptyUser(){
        LoginUserService service = new LoginUserService("", "irrelevant");
        service.execute();
    }

    /**
    * Teste 7
    **/
    @Test(expected = EmptyLoginFieldsException.class)
    public void emptyUserEmptyPassword(){
        LoginUserService service = new LoginUserService("", "");
        service.execute();
    }
}