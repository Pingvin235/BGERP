package ru.bgcrm.event;

import ru.bgcrm.model.user.User;
import ru.bgcrm.struts.form.DynActionForm;

/**
 * Событие, возникающее в результате действия пользователя. 
 */
public class UserEvent implements Event {
    protected final DynActionForm form;

    public UserEvent(DynActionForm form) {
        this.form = form;
    }

    public DynActionForm getForm() {
        return form;
    }

    public User getUser() {
        return form.getUser();
    }
}
