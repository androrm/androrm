/**
 * 	Copyright (c) 2010 Philipp Giese
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
package com.orm.androrm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;

/**
 * @author Philipp Giese
 *
 * @param <L>	Type of the origin class.
 * @param <R>	Type of the target class.
 */
public class ManyToManyField<L extends Model, 
							 R extends Model> 
extends AbstractToManyRelation<L, R> {

	private String mTableName;
	
	public ManyToManyField(Class<L> origin, 
			Class<R> target) {
		
		mOriginClass = origin;
		mTargetClass = target;
		mValues = new ArrayList<R>();
		
		mTableName = createTableName();
	}
	
	private String createTableName() {
		List<String> tableNames = new ArrayList<String>();
		tableNames.add(DatabaseBuilder.getTableName(mOriginClass));
		tableNames.add(DatabaseBuilder.getTableName(mTargetClass));
		
		Collections.sort(tableNames);
		
		return tableNames.get(0) + "_" + tableNames.get(1);
	}
	
	@Override
	public QuerySet<R> get(Context context, L origin) {
		QuerySet<R> querySet = new QuerySet<R>(context, mTargetClass);
		querySet.injectQuery(getQuery(origin.getId()));
		
		return querySet;
	}
	
	private JoinStatement getJoin(String leftAlias, String rightAlias, int id) {
		JoinStatement join = new JoinStatement();
		
		join.left(DatabaseBuilder.getTableName(mTargetClass), leftAlias)
			.right(getRightJoinSide(id), rightAlias)
			.on(Model.PK, DatabaseBuilder.getTableName(mTargetClass));
		
		return join;
	}
	
	public ForeignKeyField<L> getLeftLinkDescriptor() {
		return new ForeignKeyField<L>(mOriginClass);
	}
	
	public ForeignKeyField<R> getRightHandDescriptor() {
		return new ForeignKeyField<R>(mTargetClass);
	}
	
	private SelectStatement getQuery(int id) {
		SelectStatement select = new SelectStatement();
		
		select.select("a.*")
		  	  .from(getJoin("a", "b", id));
		
		return select;
	}
	
	public String getRelationTableName() {
		return mTableName;
	}
	
	private SelectStatement getRightJoinSide(int id) {
		String leftTable = DatabaseBuilder.getTableName(mOriginClass);
		String rightTable = DatabaseBuilder.getTableName(mTargetClass);
		
		Where where = new Where();
		where.setStatement(new Statement(leftTable, id));
		
		SelectStatement relation = new SelectStatement();
		relation.from(mTableName)
				.select(leftTable, rightTable)
		 		.where(where);
		
		JoinStatement join = new JoinStatement();
		join.left(relation, "left")
			.right(rightTable, "right")
			.on(rightTable, Model.PK);
		
		SelectStatement select = new SelectStatement();
		select.from(join)
			  .select("left." + rightTable + " AS " + rightTable);
		
		return select;
	}
}
