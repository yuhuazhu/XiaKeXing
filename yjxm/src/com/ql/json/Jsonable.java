package com.ql.json;

/**
 * 可转为json的实体接口
 * @author dream
 */
public interface Jsonable {
	public Jsonable from(String json);
}
