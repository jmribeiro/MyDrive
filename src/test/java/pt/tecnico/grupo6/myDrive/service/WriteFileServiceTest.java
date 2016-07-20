package pt.tecnico.grupo6.myDrive.service;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.tecnico.grupo6.myDrive.exception.*;
import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.service.dto.*;

public class WriteFileServiceTest extends ServiceTest{

    private Long validToken;
    private Long invalidToken;
    private Long validTokenNoPermission;

    private final Long invalidTokenNull = new Long(0);

    private final String fileNotExist = "fileNotExist";
    private final String fileNull = "";
    private final String fileExist ="newFile";
    private final String content = "abcdf";

	protected void populate() {

        try{
            
            MyDrive md = MyDrive.getInstance();
            md.reset();

            md.newUser("user1");
            md.newUser("user2");
            md.newUser("user3");

            validToken = md.login("user1", "12345678");
            invalidToken = md.login("user2", "12345678");
            validTokenNoPermission = md.login("user3", "12345678");


            User user1 = md.getUserByUsername("user1");
            User user2 = md.getUserByUsername("user2");
            User user3 = md.getUserByUsername("user3");

            user1.createPlainFile(fileExist, user1.getHome());

            user3.createPlainFile(fileExist, user3.getHome());
            user3.setMask("--------");

            // user2 não tem sessão 
            md.logout(invalidToken);

        }catch(InvalidUsernameException | UserAlreadyExistsException e){
            //something went wrong!
        }

	}


    /**
    * Teste 1 
    **/
    @Test
    public void success() {
        WriteFileService service = new WriteFileService(validToken, fileExist, content);
        service.execute();

        String result = ((ContentsDto)service.result()).getContent();
        assertEquals(content, result);
    }

    /**
    * Teste 2
    **/
     @Test(expected = PermissionDeniedException.class)
     public void permissionDenied() {
        WriteFileService service = new WriteFileService(validTokenNoPermission, fileExist, content);
        service.execute();
     }

    /**
    * Teste 3
    **/
    @Test(expected = FileDoesntExistException.class)
    public void FileDoesntExists(){

        WriteFileService service1 = new WriteFileService(validToken, fileNotExist, content);
        service1.execute();

    }

    /**
    * Teste 4
    **/
    
    @Test(expected = InvalidSessionException.class)
    public void InvalidSessionExists(){
        
        WriteFileService service1 = new WriteFileService(invalidToken, fileExist, content);
        service1.execute();

    }

    /**
    * Teste 5
    **/
    @Test(expected = FileDoesntExistException.class)
    public void InvalidFileNameExists(){
        
        WriteFileService service = new WriteFileService(validToken, fileNull, content);
        service.execute();

    }


    // /**
    // * Teste 6
    // **/
     @Test(expected = InvalidSessionException.class)
     public void InvalidTokenNull(){

        WriteFileService service = new WriteFileService(invalidTokenNull, fileExist, content);
        service.execute();

     }
}