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
public class EnvironmentLinkServiceTest extends ServiceTest {

	private MyDrive md;

	private EnvironmentLinkService service;

	// Populate
	private final String validPathWithVarInTheMiddle = "/home/root/$B/c"; // Expected = /home/root/a/b/c
	private final String validPathWithVarInTheEnd = "/home/root/a/$C"; // Expected = /home/root/a/b/c
	private final String validPathWithVarInTheBeggining = "/$HOME/a/b/c"; //Expected = /home/root/a/b/c 
	private final String invalidPath = "/a 42 /b";
	private final String invalidPathBroken = "/a/b/makeSure/thisFolders/dontExist/linkBroken/youSee";
	private final String nonFilledPath = "";

	private final String invalidVariablePath = "/home/$BROKEN1/a/b/c";
	private final String brokenVariablePath = "/home/$BROKEN2/a/b/c";

	private EnvironmentVariable validVariable1; // $B = /home/root/a/b
	private EnvironmentVariable validVariable2; // $C = /home/root/a/b/c
	private EnvironmentVariable validVariable3; // $HOME = /home/root

	private EnvironmentVariable invalidVariableInvalidPath; // $BROKEN1 = /home 42 /user
	private EnvironmentVariable invalidVariableBrokenPath; // $BROKEN2 = /home/folderThatDoesntExist

	private Link validLink1;
	private Link validLink2;
	private Link validLink3;
	private Link validLink4;
	private Link validLink5;

	private Link invalidLink1;
	private Link invalidLink2;
	private Link invalidLink3;

	// Args for testing
	private String linkPath1; //validPathWithVarInTheMiddle
	private String linkPath2; //validPathWithVarInTheEnd
	private String linkPath3; //validPathWithVarInTheBeggining
	private String linkPath4; //invalidPath
	private String linkPath5; //invalidPathBroken
	private String linkPath6; //nonFilledPath

	private String linkPath7;
	private String linkPath8;

	private Long validToken;							//Logged with permission
	private Long invalidTokenExpired;					//Not logged - expired			
	private Long noPermissionToken;						//Logged without read permission
	private final Long invalidTokenDumb = new Long(42); //Dumb
	private final Long nonFilledToken = new Long(0);	//Not filled

	protected void populate() {
		
		md = MyDrive.getInstance();
		md.reset();

		Root root = md.getRootUser();
		Directory rootHome = root.getHome();
		
		// POPULATE USERS
		md.newUser("user");
		md.newUser("expiredTokenUser");
		md.newUser("noPermissionUser");

		User noPermissionUser = md.getUserByUsername("noPermissionUser");
		noPermissionUser.setMask("--------");

		// POPULATE DIRECTORIES
		// /home/root/a/b/c
		root.createDirectory("a", rootHome);
		Directory a = rootHome.getDirectoryByName("a");

		root.createDirectory("b", rootHome);
		Directory b = rootHome.getDirectoryByName("b");

		root.createDirectory("c", rootHome);
		Directory c = rootHome.getDirectoryByName("c");

		// POPULATE LINKS
		//All Links in folder /home/root

		root.createLink("validLink1", validPathWithVarInTheMiddle, rootHome);
		validLink1 = rootHome.getLinkByPath("/home/root/validLink1");
		linkPath1 = validLink1.getAbsolutePath();

		root.createLink("validLink2", validPathWithVarInTheEnd, rootHome);
		validLink2 = rootHome.getLinkByPath("/home/root/validLink2");
		linkPath2 = validLink2.getAbsolutePath();
		
		root.createLink("validLink3", validPathWithVarInTheBeggining, rootHome);
		validLink3 = rootHome.getLinkByPath("/home/root/validLink3");		
		linkPath3 = validLink3.getAbsolutePath();
		
		root.createLink("invalidLink1", invalidPath, rootHome);
		invalidLink1 = rootHome.getLinkByPath("/home/root/invalidLink1");	
		linkPath4 = invalidLink1.getAbsolutePath();

		root.createLink("invalidLink2", invalidPathBroken, rootHome);
		invalidLink2 = rootHome.getLinkByPath("/home/root/invalidLink2");	
		linkPath5 = invalidLink2.getAbsolutePath();

		root.createLink("invalidLink3", nonFilledPath, rootHome);
		invalidLink3 = rootHome.getLinkByPath("/home/root/invalidLink3");	
		linkPath6 = invalidLink3.getAbsolutePath();

		root.createLink("validLink4", invalidVariablePath, rootHome);
		validLink4 = rootHome.getLinkByPath("/home/root/validLink4");
		linkPath7 = validLink4.getAbsolutePath();

		root.createLink("validLink5", brokenVariablePath, rootHome);
		validLink5 = rootHome.getLinkByPath("/home/root/validLink5");
		linkPath8 = validLink5.getAbsolutePath();

		// POPULATE LOGINS
		validToken = md.login("user", "12345678");
		Login l1 = md.getLoginByToken(validToken);

		invalidTokenExpired = md.login("expiredTokenUser", "expiredTokenUser");
		noPermissionToken = md.login("noPermissionUser", "noPermissionUser");

		md.logout(invalidTokenExpired); // Now expired

		//POPULATE VARIABLES
		validVariable1 = new EnvironmentVariable(l1, "B", "/home/root/a/b");
		validVariable2 = new EnvironmentVariable(l1, "C", "/home/root/a/b/c");
		validVariable3 = new EnvironmentVariable(l1, "HOME", "/home/root");

		invalidVariableInvalidPath = new EnvironmentVariable(l1, "BROKEN1", "/home 42 /user");
		invalidVariableBrokenPath = new EnvironmentVariable(l1, "BROKEN2", "/home/folderThatDoesntExist");
	
    }
	
	@Test
    public void varInMiddle(){
    	new MockUp<EnvironmentLinkService>() {
			@Mock
			Dto result() { 
				return new ContentsDto("/home/root/a/b/c"); 
			}
		};

		service = new EnvironmentLinkService(validToken, linkPath1);
		service.execute();
		assertEquals(((ContentsDto)service.result()).getContent(), "/home/root/a/b/c");
    }

    @Test
    public void varInTheEnd(){
    	new MockUp<EnvironmentLinkService>() {
			@Mock
			Dto result() { 
				return new ContentsDto("/home/root/a/b/c"); 
			}
		};

		service = new EnvironmentLinkService(validToken, linkPath2);
		service.execute();
		assertEquals(((ContentsDto)service.result()).getContent(), "/home/root/a/b/c");
    }

    @Test
    public void varInTheBeggining(){
    	new MockUp<EnvironmentLinkService>() {
			@Mock
			Dto result() { 
				return new ContentsDto("/home/root/a/b/c"); 
			}
		};

		service = new EnvironmentLinkService(validToken, linkPath3);
		service.execute();
		assertEquals(((ContentsDto)service.result()).getContent(), "/home/root/a/b/c");
    }

    @Test(expected = InvalidPathException.class)
    public void invalidPathWithinLink(){
    	new MockUp<EnvironmentLinkService>() {
			@Mock
			void execute() throws MyDriveException{ 
				throw new InvalidPathException();
			}
		};
		service = new EnvironmentLinkService(validToken, linkPath4);
		service.execute();
    }

    @Test(expected = InvalidPathException.class)
    public void brokenPathWithinLink(){
    	new MockUp<EnvironmentLinkService>() {
			@Mock
			void execute() throws MyDriveException{ 
				throw new InvalidPathException();
			}
		};
		service = new EnvironmentLinkService(validToken, linkPath5);
		service.execute();
    }
    
    @Test(expected = InvalidPathException.class)
    public void emptyPathWithinLink(){
    	new MockUp<EnvironmentLinkService>() {
			@Mock
			void execute() throws MyDriveException{ 
				throw new InvalidPathException();
			}
		};
		service = new EnvironmentLinkService(validToken, linkPath6);
		service.execute();
    }

    @Test(expected = InvalidSessionException.class)
    public void invalidToken(){
    	new MockUp<EnvironmentLinkService>() {
			@Mock
			void execute() throws MyDriveException{ 
				throw new InvalidSessionException();
			}
		};
		service = new EnvironmentLinkService(invalidTokenExpired, "DONT CARE");
		service.execute();
    }

    @Test(expected = PermissionDeniedException.class)
    public void readPermissionDenied(){
    	new MockUp<EnvironmentLinkService>() {
			@Mock
			void execute() throws MyDriveException{ 
				throw new PermissionDeniedException();
			}
		};
		service = new EnvironmentLinkService(noPermissionToken, linkPath1);
		service.execute();
    }

    @Test(expected = InvalidSessionException.class)
    public void dumbTokenTest(){
    	new MockUp<EnvironmentLinkService>() {
			@Mock
			void execute() throws MyDriveException{ 
				throw new InvalidSessionException();
			}
		};
		service = new EnvironmentLinkService(invalidTokenDumb, "DONT CARE");
		service.execute();

    }
    
    @Test(expected = InvalidSessionException.class)
    public void emptyTokenTest(){
    	new MockUp<EnvironmentLinkService>() {
			@Mock
			void execute() throws MyDriveException{ 
				throw new InvalidSessionException();
			}
		};
		service = new EnvironmentLinkService(nonFilledToken, "DONT CARE");
		service.execute();
    }

    @Test(expected = InvalidEnvironmentVariableException.class)
    public void goodLinkInvalidVariablePath(){
    	new MockUp<EnvironmentLinkService>() {
			@Mock
			void execute() throws MyDriveException{ 
				throw new InvalidEnvironmentVariableException();
			}
		};
		service = new EnvironmentLinkService(validToken, linkPath7);
		service.execute();
    }

    @Test(expected = InvalidEnvironmentVariableException.class)
    public void goodLinkBrokenVariablePath(){
    	new MockUp<EnvironmentLinkService>() {
			@Mock
			void execute() throws MyDriveException{ 
				throw new InvalidEnvironmentVariableException();
			}
		};
		service = new EnvironmentLinkService(validToken, linkPath8);
		service.execute();
    }
}
