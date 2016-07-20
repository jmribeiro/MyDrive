package pt.tecnico.grupo6.myDrive.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.tecnico.grupo6.myDrive.exception.*;
import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.service.dto.*;

public class ChangeDirectoryServiceTest extends ServiceTest{
    
    private Long validToken, invalidToken, noPermissionToken;
    private final String validPath = "/home/newDir";
    
    String newDirPath;
    String newPlainFilePath;
    
    protected void populate() {
        try{
            
            MyDrive md = MyDrive.getInstance();
            md.reset();
            
            md.newUser("validTokenUsername");
            md.newUser("invalidTokenUsername");
            md.newUser("noPermissionUsername");

            User validTokenUser = md.getUserByUsername("validTokenUsername");
            User invalidTokenUser = md.getUserByUsername("invalidTokenUsername");
            User noPermissionTokenUser = md.getUserByUsername("noPermissionUsername");
            
            validTokenUser.setMask("rwxdrwxd");
            noPermissionTokenUser.setMask("--------");

            validToken = md.login("validTokenUsername", "validTokenUsername");
            invalidToken = md.login("invalidTokenUsername", "invalidTokenUsername");
            noPermissionToken = md.login("noPermissionUsername", "noPermissionUsername");

            md.logout(invalidToken);

            Directory home = md.getHomeFolder();

            // para criar com permissao root
            md.getRootUser().createDirectory ("newDir", home);
            md.getRootUser().createPlainFile ("newFile", home);

            Directory newDir = home.getDirectoryByName("newDir");
            newDirPath = newDir.getAbsolutePath();

            PlainFile newPlainFile = (PlainFile)home.getFileByName("newFile");
            newPlainFilePath = newPlainFile.getAbsolutePath();

        }catch(InvalidUsernameException | UserAlreadyExistsException e){
         
    }
}
    /**
    * Teste 1
    *permissionDenied(pt.tecnico.grupo6.myDrive.service.ChangeDirectoryServiceTest): Expected exception: pt.tecnico.grupo6.myDrive.exception.PermissionDeniedException*/
    @Test
    public void success() {
        
        ChangeDirectoryService service = new ChangeDirectoryService(validToken, validPath);
        service.execute();

        String result = ((ContentsDto)service.result()).getContent();

        assertEquals(newDirPath, result);
    }

    /**
    * Teste 2
    **/
    @Test(expected = PermissionDeniedException.class)
    public void permissionDenied() {
        
        ChangeDirectoryService service = new ChangeDirectoryService(noPermissionToken, validPath);
        service.execute();

    }

    /**
    * Teste 3
    **/
    @Test(expected = IsNotDirectoryException.class)
    public void invalidPath() {

        ChangeDirectoryService service = new ChangeDirectoryService(validToken, newPlainFilePath);
        service.execute();
        
    }

    /**
    * Teste 4
    **/
    @Test(expected = FileDoesntExistException.class)
    public void emptyPath() {
        
        ChangeDirectoryService service = new ChangeDirectoryService(validToken, "");
        service.execute();
        
    }

    /**
    * Teste 5
    **/
    @Test(expected = InvalidSessionException.class)
    public void invalidToken() {
        
        ChangeDirectoryService service = new ChangeDirectoryService(invalidToken, "DontCare");
        service.execute();
        
    }
    
    /**
    * Teste 6
    **/
    @Test(expected = InvalidSessionException.class)
    public void emptyToken() {
        
        ChangeDirectoryService service = new ChangeDirectoryService(new Long(0), "DontCare");
        service.execute();

    }

}