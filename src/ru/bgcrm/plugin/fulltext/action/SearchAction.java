package ru.bgcrm.plugin.fulltext.action;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ru.bgcrm.model.Pair;
import ru.bgcrm.model.SearchResult;
import ru.bgcrm.model.customer.Customer;
import ru.bgcrm.model.message.Message;
import ru.bgcrm.model.process.Process;
import ru.bgcrm.plugin.fulltext.dao.SearchDAO;
import ru.bgcrm.struts.action.BaseAction;
import ru.bgcrm.struts.form.DynActionForm;
import ru.bgcrm.util.sql.ConnectionSet;

public class SearchAction extends BaseAction {

    public ActionForward search(ActionMapping mapping, DynActionForm form, ConnectionSet conSet) throws Exception {
        String objectType = form.getParam("objectType");
        String filter = form.getParam("filter", "").trim();
        
        SearchDAO searchDao = new SearchDAO(conSet.getSlaveConnection());
        if (Customer.OBJECT_TYPE.equals(objectType)) {
            searchDao.searchCustomer(new SearchResult<Customer>(form), filter);
            return data(conSet, mapping, form, objectType);
        } else if (Process.OBJECT_TYPE.equals(objectType)) {
            searchDao.searchProcess(new SearchResult<Process>(form), filter);
            return mapping.findForward(objectType);
        } else if (Message.OBJECT_TYPE.equals(objectType)) {
            searchDao.searchMessages(new SearchResult<Pair<Message, Process>>(form), filter);
            return mapping.findForward(objectType);
        }

        return mapping.findForward(FORWARD_DEFAULT);
    }

}
