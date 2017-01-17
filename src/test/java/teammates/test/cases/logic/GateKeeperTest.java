package teammates.test.cases.logic;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.UserType;
import teammates.logic.api.GateKeeper;
import teammates.logic.api.Logic;
import teammates.test.cases.BaseComponentTestCase;

public class GateKeeperTest extends BaseComponentTestCase {

    private static GateKeeper gateKeeper = new GateKeeper();
    private static DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        removeAndRestoreTypicalDataBundle();
    }

    @Test
    public void testGetLoginUrl() {
        gaeSimulation.logoutUser();
        assertEquals("/_ah/login?continue=www.abc.com", gateKeeper.getLoginUrl("www.abc.com"));
    }

    @Test
    public void testGetLogoutUrl() {
        gaeSimulation.loginUser("any.user");
        assertEquals("/_ah/logout?continue=www.def.com", gateKeeper.getLogoutUrl("www.def.com"));
    }
    
    //TODO: test isUserLoggedIn method

    @Test
    public void testGetCurrentUser() throws Exception {

        ______TS("admin+instructor+student");

        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        CourseAttributes course = dataBundle.courses.get("typicalCourse2");
        gaeSimulation.loginAsAdmin(instructor.googleId);
        // also make this user a student of another course
        StudentAttributes instructorAsStudent = new StudentAttributes(
                "Section 1", "Team 1", "Instructor As Student", "instructorasstudent@yahoo.com", "", course.getId());
        instructorAsStudent.googleId = instructor.googleId;
        new Logic().createStudentWithoutDocument(instructorAsStudent);

        UserType user = gateKeeper.getCurrentUser();
        assertEquals(instructor.googleId, user.id);
        assertTrue(user.isAdmin);
        assertTrue(user.isInstructor);
        assertTrue(user.isStudent);

        ______TS("unregistered");

        gaeSimulation.loginUser("unknown");

        user = gateKeeper.getCurrentUser();
        assertEquals("unknown", user.id);
        assertFalse(user.isAdmin);
        assertFalse(user.isInstructor);
        assertFalse(user.isStudent);

        ______TS("not logged in");

        // check for user not logged in
        gaeSimulation.logoutUser();
        assertEquals(null, gateKeeper.getCurrentUser());
    }
    
    @AfterClass
    public static void classTearDown() {
        printTestClassFooter();
    }

}
