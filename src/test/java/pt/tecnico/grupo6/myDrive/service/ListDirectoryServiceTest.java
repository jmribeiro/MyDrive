package pt.tecnico.grupo6.myDrive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pt.tecnico.grupo6.myDrive.exception.*;
import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.service.dto.*;

public class ListDirectoryServiceTest extends ServiceTest{
    

    private Long token, tokenNoPermission;
    private Long invalidToken = new Long(999999999);
    private String homePath, invalidPath;
    
    protected void populate() {

        try{
            
            MyDrive md = MyDrive.getInstance();
            md.reset();

            md.newUser("user1");
            md.newUser("user2");

            token = md.login("user1", "12345678");
            tokenNoPermission = md.login("user2", "12345678");

            User user = md.getUserByUsername("user1");

            Directory home = user.getHome();


            user.createPlainFile("file1", home);
            user.createDirectory("folder1", home);
            user.createPlainFile("file2", home);
            user.createDirectory("folder2", home);
            user.createDirectory("emptyFolder", home); //pode se usar esta para testar o list a uma pasta vazia

            // user2 numa directoria onde nao tem permissoes de execuçao
            homePath = home.getAbsolutePath();
            invalidPath = " / home   /cenas/#$%&%$#/naoPossoExistir";
            Directory rootDirectory = md.getRootFolder();
            md.getLoginByToken(tokenNoPermission).setCurrentWorkingDirectory(rootDirectory);

        }catch(InvalidUsernameException | UserAlreadyExistsException e){
            //log.trace()
        }  

    }

    /**
    * Teste 1
    **/
    @Test
    public void success() {

        ListDirectoryService service = new ListDirectoryService(token);
        service.execute();

        String resultado = ".  ..  emptyFolder  folder2  file2  folder1  file1  \n";
        String filesList = ((ContentsDto)service.result()).getContent();

        boolean coiso = filesList != null;
  
        boolean output = filesList != null && filesList.equals(resultado);
        assertTrue(output);
    }

    /**
    * Teste 2
    **/
    @Test
    public void successComPath() {

        ListDirectoryService service = new ListDirectoryService(token, homePath);
        service.execute();

        String resultado = ".  ..  emptyFolder  folder2  file2  folder1  file1  \n";
        String filesList = ((ContentsDto)service.result()).getContent();

        boolean coiso = filesList != null;
  
        boolean output = filesList != null && filesList.equals(resultado);
        assertTrue(output);
    }


    /**
    * Teste 3
    **/
    @Test (expected = FileDoesntExistException.class)
    public void invalidPath() {
        ListDirectoryService service = new ListDirectoryService(token, invalidPath);
        service.execute();
    }

    /**
    * Teste 4
    **/
    @Test (expected = InvalidSessionException.class)
    public void invalidToken() {
        ListDirectoryService service = new ListDirectoryService(invalidToken);
        service.execute();
    }

    /**
    * Teste 5
    **/
    @Test (expected = PermissionDeniedException.class)
    public void permissionDenied() {
        ListDirectoryService service = new ListDirectoryService(tokenNoPermission);
        service.execute();
    }

}