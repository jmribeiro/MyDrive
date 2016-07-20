package pt.tecnico.grupo6.myDrive.service;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pt.tecnico.grupo6.myDrive.exception.*;
import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.service.dto.*;

public class ReadFileServiceTest extends ServiceTest{
    
    private final String username = "admin";
    private final String password = "12345678";
    private final String fileEx = "file";
    private final String linkName = "link";
    private final String fileNotEx = "fake";
    private final String fileNoPermission = "rootFile";
    private final String fileNotReadable = "unreadable";
    private final long invalidToken = 1234567890;
    private long token; 
    
    protected void populate() {
    	try{
            MyDrive md = MyDrive.getInstance();
            md.reset();
            md.newUser(username);
            token = md.login(username, "12345678");
            User user = md.getUserByUsername(username);
            Directory cur = user.getHome();  
            Root r = md.getRootUser();
            r.createPlainFile(fileNoPermission, cur); 
            user.createPlainFile(fileEx, cur);
            user.createLink(linkName, cur);
            user.createDirectory(fileNotReadable, cur);
            Link link = (Link)cur.getFileByName(linkName);
            link.setPath("/home/admin/file");
            user.editPlainFile((PlainFile)cur.getFileByName(fileEx), "teste");

        }catch(InvalidUsernameException | PermissionDeniedException | UserAlreadyExistsException e){
            //deu asneira
        }
        
    }

    /**
    * Teste 1 
    **/
    @Test
    public void successPlain() {
        Login log = MyDrive.getInstance().getLoginByToken(token);
        ReadFileService service = new ReadFileService(token, fileEx);
        service.execute();
        String conteudo = ((ContentsDto)service.result()).getContent();
        boolean output = conteudo != null && conteudo.equals("teste");
        assertTrue(output);
    }

     /**
    * Teste 1a 
    **/
    @Test
    public void successLink() {
        Login log = MyDrive.getInstance().getLoginByToken(token);
        ReadFileService service = new ReadFileService(token, linkName);
        service.execute();
        String conteudo = ((ContentsDto)service.result()).getContent();
        boolean output = conteudo != null && conteudo.equals("teste");
        assertTrue(output);
    }

    /**
    * Teste 2
    **/
    @Test(expected = PermissionDeniedException.class)
    public void permissionTest() {
        ReadFileService service = new ReadFileService(token, fileNoPermission);
        service.execute();

    }

    /**
    * Teste 3
    **/
    @Test(expected = FileDoesntExistException.class)
    public void fileExistTest(){
        ReadFileService service = new ReadFileService(token, fileNotEx);
        service.execute();
    }

    /**
    * Teste 4
    **/
    @Test(expected = InvalidSessionException.class)
    public void invalidSession(){       
        ReadFileService service = new ReadFileService(invalidToken, "ANY");    
        service.execute();
    }

    /**
    * Teste 5
    **/
    @Test(expected = FileDoesntExistException.class)
    public void nullFile(){
        ReadFileService service = new ReadFileService(token, "");
        service.execute();
    }

    /**
    * Teste 6
    **/
    @Test(expected = InvalidSessionException.class)
    public void nullToken(){
        ReadFileService service = new ReadFileService(new Long(0), "ANY");
        service.execute();
    }

    /**
    * Teste 7
    **/
    @Test(expected = IsNotPlainFileException.class)
    public void notReadable(){
    	ReadFileService service = new ReadFileService(token, fileNotReadable);
    	service.execute();
    }

}