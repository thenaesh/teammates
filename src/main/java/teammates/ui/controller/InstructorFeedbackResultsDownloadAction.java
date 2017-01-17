package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.ExceedingRangeException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;

public class InstructorFeedbackResultsDownloadAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String section = getRequestParamValue(Const.ParamsNames.SECTION_NAME);
        boolean isMissingResponsesShown = getRequestParamAsBoolean(
                Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES);
        String filterText = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_FILTER_TEXT);
        boolean isStatsShown = getRequestParamAsBoolean(Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS);

        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        boolean isCreatorOnly = true;

        gateKeeper.verifyAccessible(instructor, session, !isCreatorOnly);

        String fileContent = "";
        String fileName = "";
        try {
            if (section == null || "All".equals(section)) {
                fileContent = logic.getFeedbackSessionResultSummaryAsCsv(
                        courseId, feedbackSessionName, instructor.email, filterText, isMissingResponsesShown, isStatsShown);
                fileName = courseId + "_" + feedbackSessionName;
                statusToAdmin = "Summary data for Feedback Session " + feedbackSessionName
                              + " in Course " + courseId + " was downloaded";
            } else {
                fileContent = logic.getFeedbackSessionResultSummaryInSectionAsCsv(
                        courseId, feedbackSessionName, instructor.email, section,
                        filterText, isMissingResponsesShown, isStatsShown);
                fileName = courseId + "_" + feedbackSessionName + "_" + section;
                statusToAdmin = "Summary data for Feedback Session " + feedbackSessionName
                              + " in Course " + courseId + " within " + section + " was downloaded";
            }
        } catch (ExceedingRangeException e) {
            // not tested as the test file is not large enough to reach this catch block
            statusToUser.add(new StatusMessage("There are too many responses. "
                                                       + "Please download the feedback results by section",
                                               StatusMessageColor.DANGER));
            isError = true;
            RedirectResult result = createRedirectResult(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE);
            result.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);
            result.addResponseParam(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
            return result;
        }

        return createFileDownloadResult(fileName, fileContent);
    }

}
