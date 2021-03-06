package org.bgerp.plugin.svc.backup.action;

import org.apache.struts.action.ActionForward;
import org.bgerp.plugin.svc.backup.Plugin;
import org.bgerp.util.Files;

import ru.bgcrm.servlet.ActionServlet.Action;
import ru.bgcrm.struts.action.BaseAction;
import ru.bgcrm.struts.form.DynActionForm;
import ru.bgcrm.util.distr.Scripts;
import ru.bgcrm.util.sql.ConnectionSet;

@Action(path = "/admin/plugin/backup/backup")
public class BackupAction extends BaseAction {
    private static final String JSP_PATH = PATH_JSP_ADMIN_PLUGIN + "/" + Plugin.ID;

    public static final Files FILE_BACKUP = new Files(BackupAction.class, "fileBackup", "backup", "*");

    @Override
    public ActionForward unspecified(DynActionForm form, ConnectionSet conSet) throws Exception {
        return html(conSet, form, JSP_PATH + "/backup.jsp");
    }

    public ActionForward backup(DynActionForm form, ConnectionSet conSet) throws Exception {
        new Scripts().backup(form.getParamBoolean("db"));
        return json(conSet, form);
    }

    public ActionForward downloadFileBackup(DynActionForm form, ConnectionSet conSet) throws Exception {
        return FILE_BACKUP.download(form);
    }
}
