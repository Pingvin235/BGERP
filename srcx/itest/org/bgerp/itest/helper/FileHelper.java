package org.bgerp.itest.helper;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;
import org.bgerp.itest.kernel.db.DbTest;
import org.testng.Assert;

import ru.bgcrm.dao.FileDataDAO;
import ru.bgcrm.model.FileData;

public class FileHelper {
    
    public static FileData addFile(File file) throws Exception {
        var con = DbTest.conRoot;
        var dao = new FileDataDAO(con);

        var fd = new FileData();
        fd.setTitle(file.getName());
        dao.add(fd);

        Assert.assertTrue(fd.getId() > 0);

        IOUtils.copy(new FileInputStream(file), fd.getOutputStream());

        return fd;
    }

}