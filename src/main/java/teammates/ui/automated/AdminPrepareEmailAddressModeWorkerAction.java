package teammates.ui.automated;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import teammates.common.datatransfer.AdminEmailAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const.ParamsNames;

/**
 * Task queue worker action: prepares admin email to be sent via task queue in address mode,
 * i.e. using the address list given directly.
 */
public class AdminPrepareEmailAddressModeWorkerAction extends AutomatedAction {
    
    @Override
    protected String getActionDescription() {
        return null;
    }
    
    @Override
    protected String getActionMessage() {
        return null;
    }
    
    @Override
    public void execute() {
        log.info("Preparing admin email task queue in address mode...");
        
        String emailId = getRequestParamValue(ParamsNames.ADMIN_EMAIL_ID);
        Assumption.assertNotNull(emailId);
        
        String addressReceiverListString = getRequestParamValue(ParamsNames.ADMIN_EMAIL_ADDRESS_RECEIVERS);
        Assumption.assertNotNull(addressReceiverListString);
        
        AdminEmailAttributes adminEmail = logic.getAdminEmailById(emailId);
        Assumption.assertNotNull(adminEmail);
        List<String> addressList = new ArrayList<String>();
        
        if (addressReceiverListString.contains(",")) {
            addressList.addAll(Arrays.asList(addressReceiverListString.split(",")));
        } else {
            addressList.add(addressReceiverListString);
        }
        
        for (String emailAddress : addressList) {
            taskQueuer.scheduleAdminEmailForSending(emailId, emailAddress, adminEmail.getSubject(),
                                                    adminEmail.getContent().getValue());
        }
    }
    
}
