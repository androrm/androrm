/**
 * 	Copyright (c) 2012 Philipp Giese
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.orm.androrm.migration;

import android.content.Context;

import com.orm.androrm.Model;

/**
 * Each migration has to implement this interface in order to
 * function correctly. It will make sure, that migrations comply
 * to a certain scheme, which is used in the internal database for 
 * migrations. 
 * 
 * @author Philipp
 *
 * @param <T>
 */
public interface Migratable<T extends Model> {

	/**
	 * This method will execute a given migration on the
	 * database. If the migration finishes successful, the method will
	 * return <code>true</code>. <code>false</code> indicates, that the
	 * migration has already been rolled out and that the execution has
	 * been aborted. 
	 * 
	 * @param context	{@link Context} instance to be able to execute the migration on the database.
	 * @param model		{@link Model} class, that this migration will be applied to. 
	 * @return			<code>true</code> if the migration was successful, <code>false</code> otherwise. 
	 */
	public boolean execute(Context context, Class<T> model);
	
	/**
	 * @param model		{@link Model} class, that this migration will be applied to. 
	 * @return			The value of this migration. This could for example be the name of a new field.
	 */
	public String getValue(Class<T> model);
	
	/**
	 * Migrations can have several actions. An action could be add_field, 
	 * if a migration is rolled out, that will add a new field to an
	 * existing table. 
	 * 
	 * @return			Action of that migration.
	 */
	public String getAction();
	
}
