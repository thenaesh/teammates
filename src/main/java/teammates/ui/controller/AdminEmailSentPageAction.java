package teammates.ui.controller;

import teammates.common.util.Const;

public class AdminEmailSentPageAction extends Action {

    @Override
    protected ActionResult execute() {
        gateKeeper.verifyAdminPrivileges(account);
        AdminEmailSentPageData data = new AdminEmailSentPageData(account);
        
        data.adminSentEmailList = logic.getSentAdminEmails();
            
        statusToAdmin = "adminEmailSentPage Page Load";
        
        data.init();
        
        return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);
            
    }

}
