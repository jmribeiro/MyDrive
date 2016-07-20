package pt.tecnico.grupo6.myDrive.service;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pt.tecnico.grupo6.myDrive.exception.*;
import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.service.dto.*;

public class CreateFileServiceTest extends ServiceTest{
    
    private Long validToken, invalidToken, validTokenNoPermission;

    protected void populate() {
        try{

            MyDrive md = MyDrive.getInstance();
            
            md.reset();
            
            md.newUser("user1");
            md.newUser("user2");
            md.newUser("user3");

            User user1 = md.getUserByUsername("user1");
            User user2 = md.getUserByUsername("user2");
            User user3 = md.getUserByUsername("user3");
            
            validToken = md.login("user1", "12345678");
            invalidToken = md.login("user2", "12345678");
            validTokenNoPermission = md.login("user3", "12345678");
            
            /* Ficheiro ja existente para haver conflito */
            user1.createPlainFile("conflict", user1.getHome());

            // User 3 é um user normal, nao tem permissao de escrita sobre o directorio "/"
            Directory rootDirectory = md.getRootFolder();
            md.getLoginByToken(validTokenNoPermission).setCurrentWorkingDirectory(rootDirectory);

            // Login invalido a partir deste momento
            md.logout(invalidToken);

            /* Neste ponto, apenas o user1 e o user3 têm uma sessao valida */

        }catch(InvalidUsernameException | UserAlreadyExistsException e){
            //Se cair aqui deviar ser valido la em cima
        }
    }

    /**
    * Teste 1
    **/
    @Test
    public void createDirectorySuccess(){
        
        String validName = "newDirectory";

        CreateFileService service = new CreateFileService(validToken, validName, FileType.DIRECTORY, "");
        service.execute();

        Login l = MyDrive.getInstance().getLoginByToken(validToken);
        
        Directory cwd = l.getCurrentWorkingDirectory();

        assertTrue(cwd.containsDirectory(validName));
    }

    /**
    * Teste 2
    **/
    @Test
    public void createApplicationSuccess(){
        
        String validName = "newApplication";
        String method = "System.out.println(\"Hello World\")";

        CreateFileService service = new CreateFileService(validToken, validName, FileType.APPLICATION, method);
        service.execute();

        Login l = MyDrive.getInstance().getLoginByToken(validToken);
        
        Directory cwd = l.getCurrentWorkingDirectory();

        assertTrue(cwd.containsApplication(validName) && ((Application)cwd.getFileByName(validName)).getMethod().equals(method));
    }

    /**
    * Teste 3
    **/
    @Test
    public void createPlainFileSuccess(){
        
        String validName = "newPlainFile";
        String content = "DONT_CARE";

        CreateFileService service = new CreateFileService(validToken, validName, FileType.PLAINFILE, content);
        service.execute();

        Login l = MyDrive.getInstance().getLoginByToken(validToken);
        
        Directory cwd = l.getCurrentWorkingDirectory();

        assertTrue(cwd.containsPlainFile(validName) && ((PlainFile)cwd.getFileByName(validName)).getContent().equals(content));
    }

    /**
    * Teste 4
    **/
    @Test
    public void createNonBrokenLinkSuccess(){
        
        String validName = "newLink";
        String path = "/home/user1/conflict";
        CreateFileService service = new CreateFileService(validToken, validName, FileType.LINK, path);
        service.execute();

        Login l = MyDrive.getInstance().getLoginByToken(validToken);
        
        Directory cwd = l.getCurrentWorkingDirectory();

        assertTrue(cwd.containsLink(validName) && ((Link)cwd.getFileByName(validName)).getPath().equals(path));
    }

    /**
    * Teste 5
    **/
    @Test(expected = InvalidSessionException.class)
    public void invalidToken(){
        
        CreateFileService service1 = new CreateFileService(invalidToken, "&#%$#&", FileType.DIRECTORY, "");
        service1.execute();

        CreateFileService service2 = new CreateFileService(invalidToken, "DontCareAboutName", FileType.DIRECTORY, "DONT_CARE");
        service2.execute();
    }


    /**
    * Teste 6
    **/
    @Test(expected = PermissionDeniedException.class)
    public void permissionDenied(){
        
        CreateFileService service1 = new CreateFileService(validTokenNoPermission, "&#%$#&", FileType.DIRECTORY, "");
        service1.execute();

        CreateFileService service2 = new CreateFileService(validTokenNoPermission, "DontCareAboutName", FileType.DIRECTORY, "DONT_CARE");
        service2.execute();
    }

    /**
    * Teste 7
    **/
    @Test(expected = InvalidFileNameException.class)
    public void invalidFileName(){
        
        String invalidName = "Invalid dir$/#&%$ecto&#/$ry name with spaces and stuff ;)";

        CreateFileService service1 = new CreateFileService(validToken, invalidName, FileType.DIRECTORY, "");
        service1.execute();

        CreateFileService service2 = new CreateFileService(validToken, invalidName, FileType.DIRECTORY, "DONT_CARE");
        service2.execute();
    }

    /**
    * Teste 8
    **/
    @Test(expected = InvalidContentException.class)
    public void invalidDirectoryContent(){
        
        String validName = "newDirectory";

        CreateFileService service = new CreateFileService(validToken, validName, FileType.DIRECTORY, "THIS IS NOT EMPTY, IT SHOULD BE");
        service.execute();

    }

    /**
    * Teste 9
    **/
    @Test(expected = InvalidContentException.class)
    public void invalidLinkContent(){
        
        String validName = "newLink";

        CreateFileService service = new CreateFileService(validToken, validName, FileType.LINK, "");
        service.execute();

    }

    /**
    * Teste 10
    **/
    @Test(expected = InvalidSessionException.class)
    public void nullToken(){
        
        /*
            DISCLAIMER
            "null" de nao preenchido é diferente de "null" de referência nula de java
            
            myLong = new Long(0); // long "nulo"
            ref = null; // referência "nula"
        */

        Long nullToken = new Long(0);

        CreateFileService service1 = new CreateFileService(nullToken, "&#%$#&", FileType.DIRECTORY, "");
        service1.execute();

        CreateFileService service2 = new CreateFileService(nullToken, "DontCareAboutName", FileType.DIRECTORY, "DONT_CARE");
        service2.execute();

    }

    /**
    * Teste 11
    **/
    @Test(expected = FileAlreadyExistsException.class)
    public void fileAlreadyExists(){

        String conflictedName = "conflict";

        CreateFileService service = new CreateFileService(validToken, conflictedName, FileType.PLAINFILE, "");
        service.execute();

    }

    /**
    * Teste 12
    **/
    @Test(expected = InvalidFileNameException.class)
    public void nameTooLongException(){
        
        String bigName = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

        CreateFileService service = new CreateFileService(validToken, bigName, FileType.PLAINFILE, "");
        service.execute();
    }

    /**
    * Teste 13
    **/
    @Test(expected = InvalidJavaMethodException.class)
    public void notAJavaFullyQualifiedName(){
        
        String validAppName = "proj";
        String invalidMethod = "S O P(\"HelloWorld\")";

        CreateFileService service = new CreateFileService(validToken, validAppName, FileType.APPLICATION, invalidMethod);
        service.execute();
    }

    /**
    * Teste 14
    **/
    @Test
    public void createBrokenLinkSuccess(){
        /*
            Se for suposto dar erro ao criar links sem target, basta vir aqui e colocar expected = InvalidPathException.class
            (e fazer apenas execute do serviço)
        */
        String validName = "newLink";
        String path = "/path/doesnt/matter/this/is/a/broken/link/it/can/happen/nonExistantFolder/anotherFolder/anotherSecondFolder/tooDeepAlready";
        CreateFileService service = new CreateFileService(validToken, validName, FileType.LINK, path);
        service.execute();

        Login l = MyDrive.getInstance().getLoginByToken(validToken);
        
        Directory cwd = l.getCurrentWorkingDirectory();

        assertTrue(cwd.containsLink(validName) && ((Link)cwd.getFileByName(validName)).getPath().equals(path));
    }
}

