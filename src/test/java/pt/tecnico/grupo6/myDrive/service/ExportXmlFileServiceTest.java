package pt.tecnico.grupo6.myDrive.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.tecnico.grupo6.myDrive.exception.*;
import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.service.dto.*;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Attribute;

import java.util.*;

public class ExportXmlFileServiceTest extends ServiceTest{
    
    private Document doc;
    private Long validToken, invalidToken;
    private final String validFile = "drive";


    
    protected void populate() {
 		 try{

            MyDrive md = MyDrive.getInstance();
            
            md.reset();
            
            md.newUser("user1");
            md.newUser("user2");
            
            User user1 = md.getUserByUsername("user1");
            User user2 = md.getUserByUsername("user2");
            user1.createDirectory("myDir", user1.getHome());
            user1.createApplication("myApp", user1.getHome().getDirectoryByName("myDir"));
            user1.createPlainFile("myPlainFile", user1.getHome().getDirectoryByName("myDir"));
            validToken = md.login("user1", "12345678");
            invalidToken = md.login("user2", "12345678");
            
            md.logout(invalidToken);


            doc = md.xmlExport();

        }catch(InvalidUsernameException | UserAlreadyExistsException e){
            //Se cair aqui deviar ser  valido la em cima
        }
    }
    @Test
    public void success(){
        ExportXmlFileService service = new ExportXmlFileService(validToken, validFile);
        service.execute();

        // check exported data
        Element e = doc.getRootElement();
        assertEquals(2, e.getChildren().size());
        Element fs = e.getChild("filesystem");
        for (Element p: fs.getChildren("directory")) {
            String name = p.getChild("name").getValue();
            if (name.equals("home")) {
            assertEquals(p.getChild("owner").getValue(), "root");
            }
            else if (name.equals("root")){
                assertEquals(p.getChild("owner").getValue(), "root");
            }
            else if (name.equals("myDir")){
                assertEquals(p.getChild("owner").getValue(), "user1");
            }
        }
        for (Element p: fs.getChildren("directoryFiles")) {
            String name = p.getChild("name").getValue();
            if (name.equals("myPlainFile")) {
            assertEquals(p.getChild("owner").getValue(), "user1");
            }
            else if (name.equals("myApp")) {
            assertEquals(p.getChild("owner").getValue(), "user1");
            }
        }      
        Element users = e.getChild("users");
        for (Element p: users.getChildren("user")) {
            String username = p.getChild("username").getValue();
            if (username.equals("root")) {
            assertEquals(p.getChild("name").getValue(), "Super User");
            assertEquals(p.getChild("password").getValue(), "***");
            assertEquals(p.getChild("mask").getValue(), "rwxdr-x-");
            }
            else if (username.equals("nobody")) {
            assertEquals(p.getChild("name").getValue(), "Guest");
            assertEquals(p.getChild("password").getValue(), "");
            assertEquals(p.getChild("mask").getValue(), "rwxdr-x-");
            }
            else if (username.equals("user1")) {
            assertEquals(p.getChild("name").getValue(), "user1");
            assertEquals(p.getChild("password").getValue(), "12345678");
            assertEquals(p.getChild("mask").getValue(), "rwxd----");
            }      
            else if (username.equals("user2")) {
            assertEquals(p.getChild("name").getValue(), "user2");
            assertEquals(p.getChild("password").getValue(), "12345678");
            assertEquals(p.getChild("mask").getValue(), "rwxd----");
            }  
        }
    }

    @Test
    public void successNoFile(){
        ExportXmlFileService service = new ExportXmlFileService(validToken);
        service.execute();

    }

    @Test(expected = InvalidSessionException.class)
    public void invalidTokenNoFile(){
        
        ExportXmlFileService service = new ExportXmlFileService(invalidToken);
        service.execute();
    }

     @Test(expected = InvalidSessionException.class)
    public void invalidToken(){
        
        ExportXmlFileService service = new ExportXmlFileService(invalidToken, "drive.xml");
        service.execute();
    }

    @Test(expected = InvalidSessionException.class)
    public void nullTokenNoFile(){
        
        ExportXmlFileService service = new ExportXmlFileService(new Long(0));
        service.execute();
    }

     @Test(expected = InvalidSessionException.class)
    public void nullToken(){
        
        ExportXmlFileService service = new ExportXmlFileService(new Long(0), "drive.xml");
        service.execute();
    }

}