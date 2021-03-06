package org.bgerp.action;

import java.util.Collections;
import java.util.List;

import ru.bgerp.l10n.Localizer;
import ru.bgerp.util.Log;

/**
 * Factory of list of titled actions.
 * 
 * @author Shamil Vakhitov
 */
public interface TitledActionFactory {
    public List<TitledAction> create(Localizer l);

    public static List<TitledAction> create(String actionFactory, Localizer l) {
        try {
            var factory = (TitledActionFactory) Class.forName(actionFactory).getConstructor().newInstance();
            return factory.create(l);
        } catch (Exception e) {
            Log.getLog().error(e);
        }
        return Collections.emptyList();
    }
}
