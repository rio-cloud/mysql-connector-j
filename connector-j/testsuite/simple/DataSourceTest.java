/*
 Copyright (C) 2002 MySQL AB

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
   
 */
package testsuite.simple;

import java.io.File;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;

import javax.sql.DataSource;

import testsuite.BaseTestCase;


/** 
 *
 * @author  Administrator
 * @version 
 */
public class DataSourceTest
    extends BaseTestCase
{

    //~ Instance/static variables .............................................

    private File tempDir;
    
    private Context ctx;

    //~ Constructors ..........................................................

    public DataSourceTest(String name)
    {
        super(name);
    }

    //~ Methods ...............................................................

    public static void main(String[] args)
    {
        new DataSourceTest("testDataSource").run();
    }

	/**
	 * Sets up this test, calling registerDataSource() to bind a 
	 * DataSource into JNDI, using the FSContext JNDI provider from Sun
	 */
    public void setUp()
               throws Exception
    {
        super.setUp();
        registerDataSource();
    }

	/**
	 * Un-binds the DataSource, and cleans up the filesystem
	 */
    public void tearDown()
                  throws Exception
    {
        ctx.unbind(tempDir.getAbsolutePath() + "/test");
        ctx.close();
        tempDir.delete();
        super.tearDown();
    }

	/**
	 * Tests that we can get a connection from the DataSource bound
	 * in JNDI during test setup
	 */
    public void testDataSource()
                        throws Exception
    {
    	Object obj = ctx.lookup(
                                tempDir.getAbsolutePath() + "/test");
                                
        DataSource ds = (DataSource)ctx.lookup(
                                tempDir.getAbsolutePath() + "/test");
        assertTrue("Datasource not bound", ds != null);

        Connection con = ds.getConnection();
        con.close();
        assertTrue("Connection can not be obtained from data source", 
                   con != null);
    }

    /**
     * This method is separated from the rest of the example since you
     * normally would NOT register a JDBC driver in your code.  It would
     * likely be configered into your naming and directory service using some
     * GUI.
     * @throws Exception DOCUMENT ME!
     */
    private void registerDataSource()
                             throws Exception
    {
        tempDir = File.createTempFile("jnditest", null);
        tempDir.delete();
        tempDir.mkdir();
        tempDir.deleteOnExit();

        com.mysql.jdbc.jdbc2.optional.MysqlDataSource ds;
        
        Hashtable                                     env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, 
                "com.sun.jndi.fscontext.RefFSContextFactory");
        ctx = new InitialContext(env);
        
        assertTrue("Naming Context not created", ctx != null);
        
        ds = new com.mysql.jdbc.jdbc2.optional.MysqlDataSource();
        
        ds.setUrl(dbUrl); // from BaseTestCase
        ctx.bind(tempDir.getAbsolutePath() + "/test", ds);
      
    }
}