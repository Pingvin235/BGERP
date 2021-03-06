package ru.bgcrm.model.customer.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.bgcrm.model.IdStringTitle;
import ru.bgcrm.util.Config;
import ru.bgcrm.util.ParameterMap;
import ru.bgcrm.util.Utils;

public class ProcessLinkModesConfig extends Config {
    private final Map<String, String> modeMap = new HashMap<>();
    private final List<IdStringTitle> modeList = new ArrayList<>();

    public ProcessLinkModesConfig(ParameterMap setup) {
        super(setup);
        for (String token : Utils.toList(setup.get("processCustomerLinkRoles", "customer:Контрагент"))) {
            String[] pair = token.trim().split(":");
            if (pair.length == 2) {
                IdStringTitle item = new IdStringTitle(pair[0], pair[1]);
                modeMap.put(item.getId(), item.getTitle());
                modeList.add(item);
            }
        }
    }

    public Map<String, String> getModeMap() {
        return modeMap;
    }

    public List<IdStringTitle> getModeList() {
        return modeList;
    }
}
