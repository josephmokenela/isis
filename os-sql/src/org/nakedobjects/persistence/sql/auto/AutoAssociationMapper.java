package org.nakedobjects.persistence.sql.auto;

import org.apache.log4j.Logger;
import org.nakedobjects.object.NakedClass;
import org.nakedobjects.object.NakedClassManager;
import org.nakedobjects.object.NakedObject;
import org.nakedobjects.object.ResolveException;
import org.nakedobjects.object.collection.InternalCollection;
import org.nakedobjects.object.reflect.Field;
import org.nakedobjects.persistence.sql.AbstractObjectMapper;
import org.nakedobjects.persistence.sql.CollectionMapper;
import org.nakedobjects.persistence.sql.DatabaseConnector;
import org.nakedobjects.persistence.sql.Results;
import org.nakedobjects.persistence.sql.SqlObjectStoreException;
import org.nakedobjects.persistence.sql.SqlOid;

public class AutoAssociationMapper extends AbstractObjectMapper implements CollectionMapper {
	private static final Logger LOG = Logger.getLogger(AutoAssociationMapper.class);
	private String table;
	private String parentColumn;
	private String elementIdColumn;
	private String elementClassColumn;
	private Field field;
	private AbstractAutoMapper mapper;

	public AutoAssociationMapper(AbstractAutoMapper mapper, NakedClass nakedClass, Field field) throws SqlObjectStoreException {
		this.mapper = mapper;
		this.field = field;

		// TODO load in properties
		String className = nakedClass.getShortName().toLowerCase();

		parentColumn = "FK" + className;
	
		String columnName = mapper.fieldMapper.getColumnName(field.getName());
		elementIdColumn = "PK" + columnName;
	
		elementClassColumn = columnName + "Class";

		table = className + "_" + columnName;
		if(nakedClass.fullName().startsWith("org.nakedobjects.")) {
			table = "no_" + table;
		}
	}

	public boolean needsTables(DatabaseConnector connector) throws SqlObjectStoreException {
		return ! connector.hasTable(table);
	}
	
	public void createTables(DatabaseConnector connector) throws SqlObjectStoreException {
		// TODO load in properties

		String columns = parentColumn + " int, " + elementClassColumn + " varchar(255), " + elementIdColumn + " int";
		connector.update("create table " + table + " (" + columns + ")");
	}

	public void loadInternalCollection(DatabaseConnector connector, NakedObject parent)
			throws ResolveException, SqlObjectStoreException {
		InternalCollection collection = (InternalCollection) field.get(parent);
		LOG.debug("Loading internal collection " + collection);
		long parentId = mapper.primaryKey(parent.getOid());
		
		String statement = "select " + elementIdColumn + "," + elementClassColumn + " from " + table + " where "
				+ parentColumn + " = " + parentId;
		Results rs = connector.select(statement);
		while (rs.next()) {
			int id = rs.getInt(elementIdColumn);
			String cls = rs.getString(elementClassColumn);
			NakedClass elementCls = NakedClassManager.getInstance().getNakedClass(cls);
			NakedObject element = mapper.loadObject(elementCls, new SqlOid(id, cls));
			LOG.debug("  element  " + element.getOid());
			collection.added(element);
		}
		rs.close();
	}

	public void saveInternalCollection(DatabaseConnector connector, NakedObject parent) throws SqlObjectStoreException {
		InternalCollection collection = (InternalCollection) field.get(parent);
		LOG.debug("Saving internal collection " + collection);
		long parentId = mapper.primaryKey(parent.getOid());
		
		connector.update("delete from " + table + " where " + parentColumn + " = " + parentId);
		
		String columns = parentColumn + ", " + elementIdColumn + ", " + elementClassColumn;
		int size = collection.size();
		for (int i = 0; i < size; i++) {
			NakedObject element = collection.elementAt(i);
			
			long elementId = mapper.primaryKey(element.getOid());
			String cls = element.getNakedClass().fullName();
			String values = parentId + "," + elementId + ", '" + cls + "'";
			String statement = "insert into " + table + " (" + columns + ") values (" + values + ")";
			connector.update(statement);
		}
	}

}