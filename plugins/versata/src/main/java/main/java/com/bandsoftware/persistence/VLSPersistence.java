package com.bandsoftware.persistence;

import com.bandsoftware.exception.BSDException;
import versata.common.VSMetaColumn;
import versata.common.VSMetaQuery;
import versata.common.VSQueryColumnDefinition;
import versata.common.VSSession;
import versata.vfc.*;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/*
 *    Title:         VLSPersistence
 *    Description:   Versata Foundation CLass (VFC) Wrappers that communicates with the
 *                   Versata Transaction Logic Engine.
 *    Copyright:     Copyright (c) 2003
 *    Company:       Band Software Design, LLC
 *    @author        Tyler Band
 *    @version       1.0
 */


public class VLSPersistence {
    private VSSession session;
    private Hashtable protectedAttrs = null;

    public VLSPersistence(Object TheSession) {
        this.session = (VSSession) TheSession;
    }

    /**
     * @return A reference to the VSSession object, the connection to the VLS
     */
    public VSSession getSession() {
        return (session);
    }

    /**
     * Set column values for a row of a business object
     * Note that columns that have not been specified (null) in the BSDField's, will
     * be set to their original value.
     *
     * @param bo     The business object data row
     * @param Fields All data fields, nicely packed in BSDFields
     */
    public void setRowValues(VSRow bo, Hashtable Fields) throws Exception {
        VSData Data;
        BSDField f;

        for (Enumeration e = Fields.elements(); e.hasMoreElements(); ) {
            // Get field reference
            f = (BSDField) e.nextElement();

            // Get value object for this field
            Data = bo.getData(f.getName());
// Tyler Band patch - a bad name will cause a null runtime pointer
            if (Data != null) {
                // Do we have a new value for this column ?
                if (f.getValue() == null) {
                    // No, use existing value
                    Data.setData(new VSDataBasic(bo, Data.getColumnPosition(), true));
                } else {
                    // Yes, set it
                    Data.setString(f.getValue());
                }
            } else {
                if (BSDLogger.isInfo()) {
                    BSDLogger.log(BSDLogger.TYPE_INFO, this.getClass().getName(), "setRowValues", "Skipping Data Attribute " + f.getName() + " with Null Value");
                }
            }
        }
    }

    /**
     * Creates a new row in a business object. The method creates a dummy query, which
     * can produce a new result set, which can insert data into the business object.
     *
     * @param boName The name of the business object
     * @return a new business row for this business object
     */
    public VSRow getNewBusinessObjectRow(String boName) throws Exception {
        VSQuery q;

        try {
            if (BSDLogger.isInfo()) {
                BSDLogger.log(BSDLogger.TYPE_INFO, this.getClass().getName(), "getNewBusinessObjectRow", "Creating new row in " + boName);
            }

            q = new VSQuery(session, boName, "", "");
            //q.execute();  //Avoid the query and result set processing

            return (q.getNewResultSet().insert());
        } catch (RuntimeException e) {
            // This is really bad, Versata tends to throw NullPointerExceptions from
            // deep within. Catch them and throw something readable
            if (BSDLogger.isError()) {
                BSDLogger.log(BSDLogger.TYPE_ERROR, this.getClass().getName(), "getNewBusinessObjectRow", e);
            }
            throw new Exception("Cannot insert row into " + boName);
        }
    }

    /**
     * Retrieve a specific business row frmo a business object
     *
     * @param boName      The name of the business object
     * @param whereClause The where clause to use on the object
     * @return The business row or null on empty result set
     */
    public VSRow getBusinessObjectRow(String boName, String whereClause) throws Exception {
        VSResultSet ResultSet;
        VSQuery Query;
        VSRow Row;

        if (BSDLogger.isInfo()) {
            BSDLogger.log(BSDLogger.TYPE_INFO, this.getClass().getName(), "getBusinessObjectRow", "Getting row from " + boName + " where " + whereClause);
        }

        try {
            // Create query, run it and get the result set
            Query = new VSQuery(session, boName, whereClause, "");
            ResultSet = Query.execute();

            // Moving the cursor to the last position allows getRowCount to work
            ResultSet.last();

            // Return the first row
            Row = ResultSet.first();
            //ResultSet.getQuery().close(); // cannot close Row before it is consumed
            return (Row);
        } catch (RuntimeException rte) {
            if (BSDLogger.isError()) {
                BSDLogger.log(BSDLogger.TYPE_ERROR, this.getClass().getName(), "getBusinessObjectRow", rte, "Cannot get row from " + boName + " where " + whereClause);
            }
            throw new Exception("Cannot get row from " + boName + " where " + whereClause);
        }
    }

    /**
     * Retrieve a specific business row frmo a business object
     *
     * @param boName      The name of the business object
     * @param whereClause The where clause to use on the object
     * @return The business row or null on empty result set
     */
    public VSResultSet QueryBusinessObject(String BusinessObjectName, String WhereClause) throws Exception {
        VSResultSet ResultSet;
        VSQuery Query;

        if (BSDLogger.isInfo()) {
            BSDLogger.log(BSDLogger.TYPE_INFO, this.getClass().getName(), "QueryBusinessObject", "Querying " + BusinessObjectName + " where " + WhereClause);
        }

        try {
            // Create query, run it and get the result set
            Query = new VSQuery(session, BusinessObjectName, WhereClause, "");
            ResultSet = Query.execute();

            // Moving the cursor to the last position allows getRowCount to work
            ResultSet.last();
            ResultSet.first();

            // Return the first row
            return (ResultSet);
        } catch (RuntimeException rte) {
            if (BSDLogger.isError()) {
                BSDLogger.log(BSDLogger.TYPE_ERROR, this.getClass().getName(), "QueryBusinessObject", rte, "Cannot query " + BusinessObjectName + " where " + WhereClause);
            }
            throw new Exception("Cannot query " + BusinessObjectName + " where " + WhereClause);
        }
    }

    /**
     * Inserts a new row of data into a business object. After the insert, the
     * values of all columns of the inserted row will be read back. The method will
     * then update the Fields hashtable.
     *
     * @param boName Name of the business object
     * @param Fields The hashtable that contains all the BSDFields
     * @return The updated hashtable, will contain all row fields
     */
    public Hashtable insertData(String boName, Hashtable Fields) throws Exception {
        VSRow boRow;
        VSData c;
        BSDField f;

        if (BSDLogger.isInfo()) {
            BSDLogger.log(BSDLogger.TYPE_INFO, this.getClass().getName(), "insertData", "Inserting into " + boName + " with " + Fields);
        }

        // Get a new row in this business object
        boRow = getNewBusinessObjectRow(boName);

        // Set the new values for this row
        setRowValues(boRow, Fields);

        // Save changes to result set
        boRow.save();

        // Apply changes to database
        boRow.getResultSet().updateDataSource();

        // And refresh the data row
        boRow.refresh();

        // Read back all values into the Vxml Fields
        for (int i = 1; i < boRow.getColumnCount(); i++) {
            // Get data from row
            c = boRow.getData(i);

            // Find the corresponding field
            if ((f = (BSDField) Fields.get(c.getName())) == null) {
                // Create a new field for this column
                Fields.put(c.getName(), new BSDField(c.getName(), c.getString()));
            } else {
                // Update the data for this field
                f.setValue(c.getString());
            }
        }
        //BUG? MUST CLOSE VSQUERY - If not you get memory leak /Tyler
        VSResultSet rs = boRow.getResultSet();
        if (rs != null)
            rs.getQuery().close();

        return (Fields);
    }

    public Hashtable updateData(String boName, Hashtable Data, Hashtable Keys) throws Exception {
        VSRow boRow;
        VSData c;
        BSDField f;
        String WhereClause;

        // Compose where-clause from key objects
        WhereClause = getKeyForBusinessObject(boName, Keys);

        // Get the row
        if ((boRow = getBusinessObjectRow(boName, WhereClause)) == null) {
            if (BSDLogger.isError()) {
                BSDLogger.log(BSDLogger.TYPE_ERROR, this.getClass().getName(), "updateData", "update: cannot find rows for business object: " + boName);
            }
            throw new Exception("update: cannot find rows for business object: " + boName);
        }

        // refresh the data row before setting row value
        // this mysteriously suppress the error of
        // "data changed by another user since ..."
        boRow.refresh();

        // Set the new values for this row
        setRowValues(boRow, Data);

        // Save changes to result set
        boRow.save();

        // Apply changes to database
        boRow.getResultSet().updateDataSource();

        // And refresh the data row
        boRow.refresh();

        // Read back all values into the Vxml Fields
        for (int i = 1; i < boRow.getColumnCount(); i++) {
            // Get data from row
            c = boRow.getData(i);

            // Find the corresponding field
            if ((f = (BSDField) Data.get(c.getName())) == null) {
                // Create a new field for this column
                Data.put(c.getName(), new BSDField(c.getName(), c.getString()));
            } else {
                // Update the data for this field
                f.setValue(c.getString());
            }
        }
        VSResultSet rs = boRow.getResultSet();
        if (rs != null)
            rs.getQuery().close();

        return (Data);
    }

    public Hashtable insertUpdateData(String boName, Hashtable Data, Hashtable Keys) throws Exception {
        boolean Insert = false;
        VSRow boRow;
        VSData c;
        BSDField f;

        // Get the row. If the row cannot be found, insert a new one
        if ((boRow = getBusinessObjectRow(boName, getKeyForBusinessObject(boName, Keys))) == null) {
            // The row does not exist yet in the database, insert a new row
            Data = insertData(boName, Data);
        } else {
            // The row already exists in the database, update it
            Data = updateData(boName, Data, Keys);
        }
        // tmb Fix memory leak
        if (boRow != null) {
            VSResultSet rs = boRow.getResultSet();
            if (rs != null) {
                VSQuery q = rs.getQuery();
                if (q != null)
                    q.close();
            }
        }
        return (Data);
    }

    public Hashtable readData(String boName, Hashtable Data, Hashtable Keys) throws Exception {
        String WhereClause;
        VSData c;
        BSDField f;
        VSRow boRow;

        // Compose where-clause from key objects
        WhereClause = getKeyForBusinessObject(boName, Keys);

        // Get the row
        if ((boRow = getBusinessObjectRow(boName, WhereClause)) == null) {
            if (BSDLogger.isError()) {
                BSDLogger.log(BSDLogger.TYPE_ERROR, this.getClass().getName(), "readData", "Cannot find rows for business object: " + boName);
            }
            throw new Exception("readData: cannot find rows for business object: " + boName);
        }

        // Read back all values into the Vxml Fields
        for (int i = 1; i < boRow.getColumnCount(); i++) {
            // Get data from row
            c = boRow.getData(i);

            // Find the corresponding field
            if ((f = (BSDField) Data.get(c.getName())) == null) {
                // Create a new field for this column
                Data.put(c.getName(), new BSDField(c.getName(), c.getString()));
            } else {
                // Update the data for this field
                f.setValue(c.getString());
            }
        }
        VSResultSet rs = boRow.getResultSet();
        if (rs != null)
            rs.getQuery().close();

        return (Data);
    }

    public void deleteData(String boName, Hashtable Keys) throws Exception {
        String WhereClause;
        VSData c;
        BSDField f;
        VSRow boRow;

        // Compose where-clause from key objects
        WhereClause = getKeyForBusinessObject(boName, Keys);

        // Get the row
        if ((boRow = getBusinessObjectRow(boName, WhereClause)) == null) {
            if (BSDLogger.isError()) {
                BSDLogger.log(BSDLogger.TYPE_ERROR, this.getClass().getName(), "deleteData", "delete: cannot find rows for business object: " + boName);
            }
            throw new Exception("delete: cannot find rows for business object: " + boName);
        }

        // Delete the row
        boRow.getResultSet().delete();
        boRow.getResultSet().updateDataSource();
        VSResultSet rs = boRow.getResultSet();
        if (rs != null)
            rs.getQuery().close();
    }

    /**
     * This method will query the VSL for the meta data of the table and colums and will
     * use the supplied BSDFields to build the key for this query
     */
    public String getKeyForBusinessObject(String boName, Hashtable KeyFields) throws Exception {
        VSMetaQuery Mq;
        VSMetaColumn Mc;
        VSQueryColumnDefinition Qc;
        StringBuffer sb = new StringBuffer();
        BSDField f;
        boolean useQuotes;
        String KeyName,
                KeyValue;

        if (BSDLogger.isInfo()) {
            BSDLogger.log(BSDLogger.TYPE_INFO, this.getClass().getName(), "getKeyForBusinessObject", "Getting key for " + boName + " with " + KeyFields);
        }

        //System.out.println( "Session=" + getSession() );

        try {
            // Get meta query
            Mq = getSession().getMetaQuery(boName);

            for (Enumeration e = KeyFields.elements(); e.hasMoreElements(); ) {
                // Get this Vxml field
                f = (BSDField) e.nextElement();

                // Get meta data for this column name
                if ((Mc = Mq.getMetaColumn(f.getName())) == null) {
                    throw new BSDException("Cannot find meta data for column " + f.getName() +
                            " in business object " + boName);
                }

                // Use quotes on some data types
                switch (Mc.getColumnType()) {
                    case VSData.CHAR:
                    case VSData.VARCHAR:
                    case VSData.LONGVARCHAR:
                    case VSData.DATE: {
                        useQuotes = true;
                        break;
                    }

                    default: {
                        useQuotes = false;
                        break;
                    }
                }

                // Get query column definition
                Qc = Mq.getQueryColumn(f.getName());

                // Build a key name that consists out of the table name and the column name
                KeyName = Qc.getTableAlias() + "." + Qc.getAlias();
                KeyValue = (useQuotes ? "'" : "") + new String(f.getValue()) + (useQuotes ? "'" : "");

                // Not the first key to add to where clause ?
                if (sb.length() != 0) {
                    sb.append(" AND ");
                }

                sb.append(KeyName);
                sb.append("=");
                sb.append(KeyValue);
            }

            return (sb.toString());
        } catch (Throwable t) {
            if (BSDLogger.isError()) {
                BSDLogger.log(BSDLogger.TYPE_ERROR, this.getClass().getName(), "getKeyForBusinessObject", "Cannot get key for " + boName + " with " + KeyFields);
            }
            throw new Exception("Cannot get key for " + boName + " with " + KeyFields);
        }
    }

    public String getColumnNameForWhereClause(String BusinessObjectName, String KeyName) {
        VSMetaQuery Mq;
        VSQueryColumnDefinition QueryCol;

        // Get meta query
        Mq = session.getMetaQuery(BusinessObjectName);

        // Get the column definition
        QueryCol = Mq.getQueryColumn(KeyName);

        // Compose the column name for use in a where clause
        return (QueryCol.getTableAlias() + "." + QueryCol.getAlias());
    }


    // Tyler Added this to find the PKEY list of the base table
    // return empty enumeration if not found
    public Enumeration getProtectedAttrs(String boName) {
        VSMetaQuery Mq;
        VSMetaColumn Mc;
        Vector v = new Vector();
        if (boName != null) {
            if (getProtectedAttrs().get(boName) == null) {
                Mq = getSession().getMetaQuery(boName);
                for (int c = 0; c < Mq.getColumnCount(); c++) {
                    Mc = Mq.getMetaColumn(c + 1);
                    if (Mc.isNotAlterable()) {
                        v.addElement(Mc.getName());
                    }
                }
            } else {
                v = (Vector) getProtectedAttrs().get(boName);
            }

            getProtectedAttrs().put(boName, v);
        }
        return v.elements();
    }

    // create local hashtable so we don't have to get this data too many times
    private Hashtable getProtectedAttrs() {
        if (protectedAttrs == null)
            protectedAttrs = new Hashtable();
        return protectedAttrs;
    }

    /**
     * OPTIMIZATION: Bulk entry of rows to same BO
     * Inserts a block of new row sof data into a business object. After the insert, the
     * values of all columns of the inserted row will be read back. The method will
     * then update the Fields hashtable.
     *
     * @param boName Name of the business object
     * @param rows   The Enumeration that contains a Hashtable of all BSDFields (rows) for 1 update
     * @return void     don't return results
     */
    public void insertBlockData(String boName, Enumeration rows) throws Exception {

        Hashtable fields;
        VSRow boRow;
        VSResultSet rs;
        boRow = getNewBusinessObjectRow(boName);
        VSQuery q = boRow.getResultSet().getQuery();//     ; new VSQuery( session , boName , "", "" );
        //q.execute(); // avoid the query and result set time

        rs = q.getNewResultSet();
        while (rows.hasMoreElements()) {
            fields = (Hashtable) rows.nextElement();
            // Set the new values for this row
            boRow = rs.insert();
            setRowValues(boRow, fields);
            // Save changes to result set
            boRow.save();
        }
        // Apply changes to database
        rs.updateDataSource();

        if (rs != null)
            rs.getQuery().close();
    }
}
