package pt.tecnico.grupo6.myDrive.auxiliary;

import javax.transaction.*;

import org.apache.logging.log4j.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import pt.ist.fenixframework.*;
import pt.ist.fenixframework.core.*;

import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.*;
import pt.tecnico.grupo6.myDrive.service.dto.*;

public abstract class DomainTest {
    
    protected static final Logger log = LogManager.getRootLogger();

    @BeforeClass // run once berfore each test class
    public static void setUpBeforeAll() throws Exception {
	   // run tests with a clean database!!!
	   Main.init();
    }

    @Before // run before each test
    public void setUp() throws Exception {
        try {
            FenixFramework.getTransactionManager().begin(false);
            populate();
        } catch (WriteOnReadError | NotSupportedException | SystemException e1) {
            e1.printStackTrace();
        }
    }

    @After // rollback after each test
    public void tearDown() {
        try {
            FenixFramework.getTransactionManager().rollback();
        } catch (IllegalStateException | SecurityException | SystemException e) {
            e.printStackTrace();
        }
    }

    protected abstract void populate(); // each test adds its own data
}