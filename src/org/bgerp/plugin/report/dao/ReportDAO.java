package org.bgerp.plugin.report.dao;

import ru.bgcrm.dao.CommonDAO;
import ru.bgcrm.struts.form.DynActionForm;

/**
 * Old report definition before switched to actions.
 */
@Deprecated
public abstract class ReportDAO extends CommonDAO {

    public ReportDAO() {
        super(null);
    }
    
    /**
     * Returns JSP file for data presentation.
     * @return
     */
    public abstract String getJspFile();

    /**
     * Does the report data retrieving logic.
     * @param form
     */
    public abstract void get(DynActionForm form) throws Exception;

}