package teammates.ui.controller;

import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;

import com.google.appengine.api.blobstore.BlobstoreFailureException;

public class AdminEmailTrashDeleteAction extends Action {

    @Override
    protected ActionResult execute() {
        
        gateKeeper.verifyAdminPrivileges(account);
        
        boolean emptyTrashBin = getRequestParamAsBoolean(Const.ParamsNames.ADMIN_EMAIL_EMPTY_TRASH_BIN);
        
        if (emptyTrashBin) {
            try {
                logic.deleteAllEmailsInTrashBin();
                statusToAdmin = "All emails in trash bin has been deleted";
                statusToUser.add(new StatusMessage("All emails in trash bin has been deleted", StatusMessageColor.SUCCESS));
            } catch (BlobstoreFailureException e) {
                statusToAdmin = "Blobstore connection failure";
                statusToUser.add(new StatusMessage("Blobstore connection failure", StatusMessageColor.DANGER));
            }
        }
        
        return createRedirectResult(Const.ActionURIs.ADMIN_EMAIL_TRASH_PAGE);
    }

}
