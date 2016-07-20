package pt.tecnico.grupo6.myDrive.service;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.tecnico.grupo6.myDrive.exception.*;
import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.service.dto.*;

import java.util.*;

public class ImportXmlFileServiceTest extends ServiceTest{
    private Long token;

    protected void populate() {
        MyDrive md = MyDrive.getInstance();
        md.reset();
        md.newUser("user1");
    }

    @Test
    public void success() throws Exception {
	
        ImportXmlFileService service = new ImportXmlFileService("drive.xml");
        service.execute();

        MyDrive md = MyDrive.getInstance();
		assertTrue(md.hasUser("nobody"));
		assertTrue(md.hasUser("root"));
    }
}