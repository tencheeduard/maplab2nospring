package com.eax.petshop.classes.base;


import com.eax.petshop.classes.annotations.PrimaryKey;

import java.lang.reflect.Field;
import java.util.Arrays;

public abstract class Table {

    public Field[] primaryKeys;

    public Table() throws Exception {
        primaryKeys = new Field[0];
        if(!checkPrimaryKey())
            throw new Exception("Table does not have a primary Key.");
    }

    public Field[] getFields()
    {
        return getClass().getDeclaredFields();
    }
    public Field getField(Integer index)
    {
        Field[] fields = getFields();

        if(index < fields.length && index >= 0)
            return fields[index];
        return null;
    }
    public Field getField(String name)
    {

        Field[] fields = getFields();

        for (Field field : fields) {
            if (field.getName().equalsIgnoreCase(name))
                return field;
        }

        return null;
    }

    public Field[] getPrimaryKeys()
    {
        return primaryKeys;
    }

    public Object getProperty(Integer index) throws IllegalAccessException
    {
        return getField(index).get(this);
    }

    public Object getProperty(String name) throws IllegalAccessException
    {
        return getField(name).get(this);
    }

    public void setProperty(Integer index, Object value) throws Exception
    {
        Field property = getField(index);

        if(value.getClass().equals(property.getType()))
        {
            property.set(this, value);
        }
        else
            throw new Exception("Value does not match the Field");
    }

    public void setProperty(String name, Object value) throws Exception
    {
        Field property = getField(name);

        if(value.getClass().equals(property.getType()))
            property.set(this, value);
        else
            throw new Exception("Value " + value.toString() + "does not match the Field " + name);
    }

    @Override
    // Returns true if it's same class and primary keys are equal
    public boolean equals(Object obj)
    {
        boolean equal = true;

        if(obj instanceof Table other && this.getClass() == other.getClass())
                if (primaryKeys.length == other.primaryKeys.length)
                {
                    for (int i = 0; i < primaryKeys.length; i++)
                    {
                        try {
                            if (primaryKeys[i].getType().equals(other.primaryKeys[i].getType()) &&
                                    !primaryKeys[i].get(this).equals(other.primaryKeys[i].get(other)))
                                equal = false;
                            }
                        catch(IllegalArgumentException e)
                        {
                            System.out.println("Could not compare values due to IllegalArgument");
                        }
                        catch(IllegalAccessException e)
                        {
                            System.out.println("Could not compare values due to IllegalAccess");
                        }
                    }
                    return equal;
                }

        return false;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        for(int i = 0; i < primaryKeys.length; i++)
        {
            try {
                result = prime * result + primaryKeys[i].get(this).hashCode();
            }
            catch (Exception e) {}
        }

        return result;
    }




    private boolean checkPrimaryKey() throws Exception
    {
        Field[] fields = getFields();

        for(Field field: fields)
            if(field.isAnnotationPresent(PrimaryKey.class))
            {
                if(!field.getType().isPrimitive())
                {
                    primaryKeys = Arrays.copyOf(primaryKeys, primaryKeys.length + 1);
                    primaryKeys[primaryKeys.length - 1] = field;
                }
                else throw new Exception("Primary Key may not be Primitive.");
            }

        return primaryKeys.length > 0;
    }

    @Override
    public String toString()
    {
        String primaryKeysStr = "";

        Field[] fields = getFields();

        for(Field primaryKey: primaryKeys)
        {
            try {
                primaryKeysStr += primaryKey.getName() + "(" + primaryKey.getType().getSimpleName() + ")" + " = " + primaryKey.get(this) + ", ";
            }
            catch (IllegalAccessException e) { }
        }

        String columnsStr = "";

        for(Field field: fields)
        {
            try {
                columnsStr += field.getName() + "(" + field.getType().getSimpleName() + ")" + " = " + field.get(this) + ", ";
            }
            catch (IllegalAccessException e) { }
        }

        return this.getClass().getSimpleName() + " with Primary Keys: " + primaryKeysStr + "Fields: " + columnsStr;
    }

}
