package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;

public class InstructorFeedbackPublishAction extends Action {
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String nextUrl = getRequestParamValue(Const.ParamsNames.NEXT_URL);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, courseId);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, feedbackSessionName);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        boolean isCreatorOnly = false;
        
        gateKeeper.verifyAccessible(instructor, session, isCreatorOnly,
                                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        
        try {
            logic.publishFeedbackSession(session);
            if (session.isPublishedEmailEnabled()) {
                taskQueuer.scheduleFeedbackSessionPublishedEmail(session.getCourseId(), session.getFeedbackSessionName());
            }
            
            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_PUBLISHED, StatusMessageColor.SUCCESS));
            statusToAdmin = "Feedback Session <span class=\"bold\">(" + feedbackSessionName + ")</span> "
                            + "for Course <span class=\"bold\">[" + courseId + "]</span> published.";
        } catch (InvalidParametersException e) {
            setStatusForException(e);
        }
        
        if (nextUrl == null) {
            nextUrl = Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE;
        }
        
        return createRedirectResult(nextUrl);
    }
}
