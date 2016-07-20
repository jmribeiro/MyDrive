package pt.tecnico.grupo6.myDrive.service;

import static org.junit.Assert.*;

import org.junit.Test;

import pt.tecnico.grupo6.myDrive.exception.*;
import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.service.dto.*;


public class DeleteFileServiceTest extends ServiceTest{
    
    private final String username = "admin";
    private final String password = "12345678";

    private final String fileExist = "file";
    private String fileExistPath;
    private final String fileDoesntExist = "Sabotage";
    private final String fileNoPermission = "rootFile";

    private final Long invalidToken = new Long(300300300);

    private Long token;
    
    protected void populate() {

    	try{
            MyDrive md = MyDrive.getInstance();
            md.reset();
            md.newUser(username);
            token = md.login(username, "12345678");
            User user = md.getUserByUsername(username);
            Directory hf = user.getHome();  
            Root r = md.getRootUser();
            r.createPlainFile(fileNoPermission, hf); 
            user.createPlainFile(fileExist, hf);
            fileExistPath = hf.getFileByName(fileExist).getAbsolutePath();

        }catch(InvalidUsernameException | UserAlreadyExistsException e){
            //something went wrong!
        }
        
    }

    /**
    * Teste 1 
    **/
    @Test
    public void success() {

        Login log = MyDrive.getInstance().getLoginByToken(token);

        DeleteFileService service = new DeleteFileService(token, fileExist);
        service.execute();
        
        assertTrue(!log.getLoggedUser().ownsFile(fileExistPath));
    }

    /**
    * Teste 2
    **/
    @Test(expected = PermissionDeniedException.class)
    public void permissionTest() {
        
        DeleteFileService service = new DeleteFileService(token, fileNoPermission);

        service.execute();

    }

    /**
    * Teste 3
    **/
    @Test(expected = FileDoesntExistException.class)
    public void fileExistTest(){
        
        DeleteFileService service = new DeleteFileService(token, fileDoesntExist);

        service.execute();
    }

    /**
    * Teste 4
    **/
    @Test(expected = InvalidSessionException.class)
    public void invalidSession(){
        
        DeleteFileService service = new DeleteFileService(invalidToken, "ANY");
    
        service.execute();
    }

    /**
    * Teste 5
    **/
    @Test(expected = FileDoesntExistException.class)
    public void nullFile(){

        DeleteFileService service = new DeleteFileService(token, "");

        service.execute();
    }

    /**
    * Teste 6
    **/
    @Test(expected = InvalidSessionException.class)
    public void nullToken(){

        DeleteFileService service = new DeleteFileService(new Long(0), "ANY");

        service.execute();
    }

}