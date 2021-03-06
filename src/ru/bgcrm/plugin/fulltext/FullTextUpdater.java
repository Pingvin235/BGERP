package ru.bgcrm.plugin.fulltext;

import static ru.bgcrm.model.param.Parameter.TYPE_ADDRESS;
import static ru.bgcrm.model.param.Parameter.TYPE_BLOB;
import static ru.bgcrm.model.param.Parameter.TYPE_EMAIL;
import static ru.bgcrm.model.param.Parameter.TYPE_LIST;
import static ru.bgcrm.model.param.Parameter.TYPE_LISTCOUNT;
import static ru.bgcrm.model.param.Parameter.TYPE_PHONE;
import static ru.bgcrm.model.param.Parameter.TYPE_TEXT;
import static ru.bgcrm.model.param.Parameter.TYPE_TREE;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ru.bgcrm.cache.ParameterCache;
import ru.bgcrm.dao.CustomerDAO;
import ru.bgcrm.dao.ParamValueDAO;
import ru.bgcrm.dao.message.MessageDAO;
import ru.bgcrm.dao.process.ProcessDAO;
import ru.bgcrm.model.IdTitle;
import ru.bgcrm.model.customer.Customer;
import ru.bgcrm.model.message.Message;
import ru.bgcrm.model.param.Parameter;
import ru.bgcrm.model.param.ParameterAddressValue;
import ru.bgcrm.model.param.ParameterPhoneValue;
import ru.bgcrm.model.param.ParameterPhoneValueItem;
import ru.bgcrm.model.param.ParameterValuePair;
import ru.bgcrm.model.process.Process;
import ru.bgcrm.plugin.fulltext.dao.SearchDAO;
import ru.bgcrm.plugin.fulltext.model.Config;
import ru.bgcrm.plugin.fulltext.model.Config.ObjectType;
import ru.bgcrm.plugin.fulltext.model.SearchItem;
import ru.bgcrm.util.Setup;
import ru.bgcrm.util.sql.ConnectionPool;
import ru.bgerp.util.Log;

/**
 * Задача обновления полнотекстовых индексов. Запускается планировщиком раз в минуту.
 */
public class FullTextUpdater implements Runnable {
    private static final Log log = Log.getLog(); 
    
    private final Config config = Setup.getSetup().getConfig(Config.class);

    public FullTextUpdater() {}

    @Override
    public void run() {
        ConnectionPool connectionPool = Setup.getSetup().getConnectionPool();
        try (Connection con = connectionPool.getDBConnectionFromPool();
             Connection conSlave = connectionPool.getDBSlaveConnectionFromPool()) {
            
            SearchDAO searchDao = new SearchDAO(con);
            ParamValueDAO paramDao = new ParamValueDAO(conSlave);
            
            List<SearchItem> forUpdate = null; 
            while (!(forUpdate = searchDao.getScheduledUpdates(config.getIndexDelay(), 100)).isEmpty()) {
                for (SearchItem item : forUpdate) {
                    ObjectType typeConfig = config.getObjectTypeMap().get(item.getObjectType());
                    if (typeConfig == null) {
                        log.warn("Unconfigured object type: " + item.getObjectType());
                        searchDao.delete(item.getObjectType(), item.getObjectId());
                        continue;
                    }
                    
                    StringBuilder text = new StringBuilder(200);
                    if (Customer.OBJECT_TYPE.equals(item.getObjectType())) {
                        Customer customer = new CustomerDAO(conSlave).getCustomerById(item.getObjectId());
                        if (customer == null) {
                            log.warn("Customer not found: {}", item.getObjectId());
                            searchDao.delete(item);
                            continue;
                        }
                        text.append(customer.getTitle());
                        text.append('\n');
                    } else if (Process.OBJECT_TYPE.equals(item.getObjectType())) {
                        Process process = new ProcessDAO(conSlave).getProcess(item.getObjectId());
                        if (process == null) {
                            log.warn("Process not found: {}", item.getObjectId());
                            searchDao.delete(item);
                            continue;
                        }
                        text.append(process.getDescription());
                        text.append('\n');
                    } else if (Message.OBJECT_TYPE.equals(item.getObjectType())) {
                        Message message = new MessageDAO(conSlave).getMessageById(item.getObjectId());
                        if (message == null) {
                            log.warn("Message not found: {}", item.getObjectId());
                            searchDao.delete(item);
                            continue;
                        }
                        text.append(message.getText());
                        text.append('\n');
                    }
                    
                    List<Parameter> paramList = ParameterCache.getParameterMap().values().stream()
                        .filter(p -> p.getObject().equals(item.getObjectType()) && config.isParamConfigured(p))
                        .collect(Collectors.toList());

                    if (!paramList.isEmpty()) {
                        List<ParameterValuePair> paramValues = paramDao.loadParameters(paramList, item.getObjectId(), false);
                        
                        for (ParameterValuePair pair : paramValues) {
                            if (pair.getValue() == null) continue;

                            switch (pair.getParameter().getType()) {
                                case TYPE_TEXT:
                                case TYPE_BLOB:
                                    text.append(pair.getValue());
                                    text.append('\n');
                                    break;
                                case TYPE_EMAIL: {
                                    Map<Integer, String> valueMap = (Map) pair.getValue();
                                    for (String email : valueMap.values()) {
                                        // комментарий - в квадратных скобках
                                        text.append(email);
                                        text.append('\n');
                                    }
                                    break;
                                }
                                case TYPE_LIST:
                                case TYPE_LISTCOUNT:
                                case TYPE_TREE: {
                                    List<IdTitle> values = (List) pair.getValue();
                                    for (IdTitle value : values) {
                                        if (value == null || value.getTitle() == null)
                                            continue;
                                        // значение идёт после :, комментарий - в квадратных скобках
                                        text.append(value.getTitle().replace(':', ' '));
                                        text.append('\n');
                                    }
                                    break;
                                }
                                case TYPE_ADDRESS: {
                                    Map<Integer, ParameterAddressValue> valueMap = (Map) pair.getValue();
                                    for (ParameterAddressValue value : valueMap.values()) {
                                        text.append(value.getValue());
                                        text.append('\n');
                                    }
                                    break;
                                }
                                case TYPE_PHONE: {
                                    ParameterPhoneValue value = (ParameterPhoneValue) pair.getValue();
                                    for (ParameterPhoneValueItem valueItem : value.getItemList()) {
                                        text.append(valueItem.getPhone());
                                        text.append(' ');
                                        text.append(valueItem.getComment());
                                        text.append('\n');
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    
                    item.setText(text.toString());
                    searchDao.update(item);
                    
                    con.commit();
                    
                    log.debug("Item type: {}; id: {}; data: {}", item.getObjectType(), item.getObjectId(), text);
                }
            }
        } 
        catch (Exception e) {
            log.error(e);
        }
    }

}
