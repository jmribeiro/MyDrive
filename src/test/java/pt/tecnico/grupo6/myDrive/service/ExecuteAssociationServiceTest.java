package pt.tecnico.grupo6.myDrive.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.exception.*;
import pt.tecnico.grupo6.myDrive.service.dto.*;

@RunWith(JMockit.class)
public class ExecuteAssociationServiceTest extends ServiceTest {

    private Long validToken, invalidToken;
    
    private final String validName = "newPlainFile";
    private final String validPath = "/home/newPlainFile";
    private final String brokenPath = "/home/dir/newPlainFile";
    private final String invalidPath = "/home / newPlainFile";
    protected void populate() {
       
            MyDrive md = MyDrive.getInstance();
            md.reset();
            
            md.newUser("validTokenUsername");
            md.newUser("invalidTokenUsername");
            
            User validTokenUser = md.getUserByUsername("validTokenUsername");
            User invalidTokenUser = md.getUserByUsername("invalidTokenUsername");
            
            validToken = md.login("validTokenUsername", "validTokenUsername");
            invalidToken = md.login("invalidTokenUsername", "invalidTokenUsername");
            
            Login l = md.getLoginByToken(validToken);

            md.logout(invalidToken);
            
            Directory cwd = l.getCurrentWorkingDirectory();

            validTokenUser.createPlainFile(validName, cwd);     
        
    }
    
    @Test
    public void executeAssociationSuccess(){
        
        
        new MockUp<ExecuteAssociationService>() {
            @Mock
            Dto result() { 
                return new ContentsDto("File "+ validName + " is running with pdfViewer"); 
            }
        };
        ExecuteAssociationService service = new ExecuteAssociationService(validToken, validPath);
        service.execute();

        assertEquals(((ContentsDto)service.result()).getContent(), "File newPlainFile is running with pdfViewer");
    }


    @Test(expected = InvalidPathException.class)
    public void brokenPath(){
        new MockUp<ExecuteAssociationService>() {
            @Mock
            void execute() throws MyDriveException{ 
                throw new InvalidPathException();
            }
        };
        ExecuteAssociationService service = new ExecuteAssociationService(validToken, brokenPath);
        service.execute();
    }

    @Test(expected = InvalidPathException.class)
    public void invalidPath(){
        new MockUp<ExecuteAssociationService>() {
            @Mock
            void execute() throws MyDriveException{ 
                throw new InvalidPathException();
            }
        };
        ExecuteAssociationService service = new ExecuteAssociationService(validToken, invalidPath);
        service.execute();
    }

    @Test(expected = InvalidPathException.class)
    public void noPath(){
        new MockUp<ExecuteAssociationService>() {
            @Mock
            void execute() throws MyDriveException{ 
                throw new InvalidPathException();
            }
        };
        ExecuteAssociationService service = new ExecuteAssociationService(validToken, "");
        service.execute();
    }

    @Test(expected = NoAssociationException.class)
    public void noAssociation(){
        new MockUp<ExecuteAssociationService>() {
            @Mock
            void execute() throws MyDriveException{ 
                throw new NoAssociationException();
            }
        };
        ExecuteAssociationService service = new ExecuteAssociationService(validToken, validPath);
        service.execute();
    }

    @Test(expected = InvalidSessionException.class)
    public void invalidToken(){
        new MockUp<ExecuteAssociationService>() {
            @Mock
            void execute() throws MyDriveException{ 
                throw new InvalidSessionException();
            }
        };
        ExecuteAssociationService service = new ExecuteAssociationService(invalidToken, "ANY");
        service.execute();
    }

    @Test(expected = InvalidSessionException.class)
    public void nullToken(){
        new MockUp<ExecuteAssociationService>() {
            @Mock
            void execute() throws MyDriveException{ 
                throw new InvalidSessionException();
            }
        };
        ExecuteAssociationService service1 = new ExecuteAssociationService(new Long(0), "ANY");
        service1.execute();
    }
    
    @Test(expected = PermissionDeniedException.class)
    public void noPermission(){
        new MockUp<ExecuteAssociationService>() {
            @Mock
            void execute() throws MyDriveException{ 
                throw new PermissionDeniedException();
            }
        };
        ExecuteAssociationService service1 = new ExecuteAssociationService(validToken, validPath);
        service1.execute();
    }
}