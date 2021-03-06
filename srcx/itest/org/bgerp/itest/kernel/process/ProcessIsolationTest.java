package org.bgerp.itest.kernel.process;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.bgerp.itest.helper.ProcessHelper;
import org.bgerp.itest.helper.UserHelper;
import org.bgerp.itest.kernel.db.DbTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import ru.bgcrm.dao.process.ProcessDAO;
import ru.bgcrm.model.process.Process;
import ru.bgcrm.model.process.ProcessExecutor;
import ru.bgcrm.model.process.ProcessGroup;
import ru.bgcrm.model.user.User;
import ru.bgcrm.model.user.UserGroup;
import ru.bgcrm.util.ParameterMap;

@Test(groups = "processIsolation", dependsOnGroups = "dbInit")
public class ProcessIsolationTest {
    private static volatile int processTypeId;
    private static volatile int processTypeSpecialId;
    private static volatile int groupId;
    private static volatile User user;

    // addUserGroups - 1 group, ISOLATED, set config
    // addProcessTypes - only IDs, with different isolation
    // create processes, add groups, check selections

    @Test
    public void addGroups() throws Exception {
        groupId = UserHelper.addGroup("ISOLATED", 0);
    }

    @Test(dependsOnMethods = "addGroups")
    public void addTypes() throws Exception {
        processTypeId = ProcessHelper.addType("ISOLATED", 0, false, null);
        processTypeSpecialId = ProcessHelper.addType("ISOLATED SPECIAL", 0, false, null);
    }

    @Test(dependsOnMethods = "addTypes")
    public void addUser() throws Exception {
        user = UserHelper.addUser("ISOLATED", "isolated", List.of(new UserGroup(groupId, new Date(), null)));
    }

    @Test(dependsOnMethods = "addUser")
    public void testIsolationExecutor() throws Exception {
        user.setConfig(ParameterMap.getDataString(ParameterMap.of("isolation.process", "executor")));

        var dao = new ProcessDAO(DbTest.conRoot, user);

        var p = dao.updateProcess(new Process().withTypeId(processTypeId).withDescription("Isolated by executor"));
        Assert.assertNull(dao.getProcess(p.getId()));

        dao.updateProcessExecutors(Set.of(new ProcessExecutor(user.getId(), groupId, 0)), p.getId());
        Assert.assertNotNull(dao.getProcess(p.getId()));
    }

    @Test(dependsOnMethods = "addUser")
    public void testIsolationGroups() throws Exception {
        user.setConfig(ParameterMap.getDataString(ParameterMap.of("isolation.process", "group")));

        var dao = new ProcessDAO(DbTest.conRoot, user);

        var p = dao.updateProcess(new Process().withTypeId(processTypeId).withDescription("Isolated by group"));
        Assert.assertNull(dao.getProcess(p.getId()));

        var ps = dao.updateProcess(new Process().withTypeId(processTypeSpecialId).withDescription("Isolated by group special type"));
        Assert.assertNull(dao.getProcess(ps.getId()));

        dao.updateProcessGroups(Set.of(new ProcessGroup(groupId)), p.getId());
        Assert.assertNotNull(dao.getProcess(p.getId()));
        Assert.assertNull(dao.getProcess(ps.getId()));

        user.setConfig(ParameterMap.getDataString(ParameterMap.of("isolation.process", "group",
                "isolation.process.group.executor.typeIds", processTypeSpecialId + ", 0")));
        Assert.assertNotNull(dao.getProcess(p.getId()));
        Assert.assertNull(dao.getProcess(ps.getId()));

        dao.updateProcessGroups(Set.of(new ProcessGroup(groupId)), ps.getId());
        Assert.assertNull(dao.getProcess(ps.getId()));

        dao.updateProcessExecutors(Set.of(new ProcessExecutor(user.getId(), groupId, 0)), ps.getId());
        Assert.assertNotNull(dao.getProcess(ps.getId()));

        dao.updateProcessExecutors(Set.of(new ProcessExecutor(user.getId(), groupId, 0),
                new ProcessExecutor(-10, groupId, 5), new ProcessExecutor(22, 0, 5)), ps.getId());
        Assert.assertNotNull(dao.getProcess(ps.getId()));
    }

    // cleanup - delete everything
}
