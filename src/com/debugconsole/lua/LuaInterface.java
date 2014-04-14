package com.debugconsole.lua;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.Prototype;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.debugconsole.SearchResult;
import com.debugconsole.data.ScriptEditor;
import com.debugconsole.data.ScriptEditor.MessageData;
import com.debugconsole.data.ScriptEditor.ObjectData;

public class LuaInterface implements ScriptEditor.ScriptProvider {
	LuaTable globals;
	
	public LuaInterface(LuaTable globalRoot) {
		globals = globalRoot;
	}
	
	@Override
	public boolean compile(String code, Array<MessageData> messages) {
		try {
			Prototype proto = LuaC.compile(new ByteArrayInputStream(code.getBytes()), "script");
			LuaClosure c = new LuaClosure(proto, globals);
			c.call();
		} catch (Exception e) {
			messages.add(new MessageData(0,ScriptEditor.MessageKind.Error, e.getMessage()));
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void getData(String[] path, Array<ObjectData> holder) {
		LuaTable target = seek(globals,path,0);
		if (target != null) {
			list(target,holder,getPath(path));
		}
	}
	
	public void deleteData(String[] path, String deleteName) {
		LuaTable target = seek(globals,path,0);
		if (target != null) {
			try {
				final int idx = Integer.parseInt(deleteName);
				target.remove(idx);
			} catch (Exception ex) {
				target.set(deleteName, LuaValue.NIL);
			}
		}
	}
	
	public void setData(String[] path, Map<String,String> values) {
		LuaTable target = seek(globals,path,0);
		if (target != null) {
			synchronized (target) {
				for (Map.Entry<String,String> ent : values.entrySet()) {
					try {
						int value = Integer.parseInt(ent.getKey());
						LuaValue v = target.get(value);
						if (v.isnil())
							throw new Exception("hack");
						if (v != null) {
							
							if (v.isboolean()) {
								target.set(ent.getKey(), Boolean.parseBoolean(ent.getValue()) ? LuaValue.TRUE : LuaValue.FALSE);
							} else if (v.isint()) {
								target.set(value, toLua(Integer.parseInt(ent.getValue())));
							} else if (v.isnumber()) {
								target.set(value, toLua(Double.parseDouble(ent.getValue())));
							} else if (v.isstring()) {
								target.set(value, toLua(ent.getValue()));
							}
						}
					} catch (Exception ex) {
						LuaValue v = target.get(ent.getKey());
						if (v != null) {
							if (v.isboolean()) {
								target.set(ent.getKey(), Boolean.parseBoolean(ent.getValue()) ? LuaValue.TRUE : LuaValue.FALSE);
							} else if (v.isint()) {
								target.set(ent.getKey(), Integer.parseInt(ent.getValue()));
							} else if (v.isnumber()) {
								target.set(ent.getKey(), Double.parseDouble(ent.getValue()));
							} else if (v.isstring()) {
								target.set(ent.getKey(), ent.getValue());
							}
						}
					}
				}
			}
		}
	}
	
	String getPath(String[] path) {
		StringBuilder sb = new StringBuilder();
		for (String str : path) {
			sb.append("/").append(str);
		}
		return sb.toString();
	}
	
	LuaTable seek(LuaTable current, String[] path, int index) {
		if (path.length - 1 - index == 0)
			return current;
		LuaValue v = current.get(path[index+1]);
		if (v != null && !v.isnil()) {
			if (v.istable() && index != path.length - 1)
				return seek((LuaTable)v,path,index+1);
			else if (v.istable())
				return ((LuaTable)v);
		}
		return null;
	}

	void list(LuaTable table, Array<ObjectData> holder, String basePath) {
		for (LuaValue key : table.keys()) {
			LuaValue v = table.get(key);
			if (v.isboolean()) {
				holder.add(new ScriptEditor.ObjectData(key.tojstring(), v.typename(), "" + v.checkboolean(), "", true));
			} else if (v.isint()) {
				holder.add(new ScriptEditor.ObjectData(key.tojstring(), v.typename(), "" + v.checkint(), "", true));
			} else if (v.isnumber()) {
				holder.add(new ScriptEditor.ObjectData(key.tojstring(), v.typename(), "" + v.checkdouble(), "", true));
			} else if (v.isstring()) {
				holder.add(new ScriptEditor.ObjectData(key.tojstring(), v.typename(), v.tojstring(), "", true));
			} else if (v.istable()) {
				holder.add(new ScriptEditor.ObjectData(key.tojstring(), v.typename(), "" + ((LuaTable)v).length(), basePath + "/" + key.tojstring(),false));
			} else if (v.isfunction()) {
				LuaValue evn = ((LuaFunction)v).getfenv();
				holder.add(new ScriptEditor.ObjectData(key.tojstring(), v.typename(), "", "",false));
			} else if (v.isnil()) {
				holder.add(new ScriptEditor.ObjectData(key.tojstring(), v.typename(), "", "",false));
			} else if (v.isuserdata()) {
				holder.add(new ScriptEditor.ObjectData(key.tojstring(), v.typename(), "", "", false));
			}
		}
	}
	
	public static LuaValue toLua(Object javaValue) {
	    return javaValue == null ? LuaValue.NIL : 
	    	javaValue instanceof LuaValue ? (LuaValue) javaValue : CoerceJavaToLua.coerce(javaValue);
	}

	@Override
	public void search(String[] query, String url, Array<SearchResult> results) {
		Array<Object> checked = new Array<Object>();
		search(globals,url,query,results,0, checked);
	}
	
	void search(LuaTable table, String url, String[] query, Array<SearchResult> results, int depth, Array<Object> checked) {
		if (checked.contains(table, true))
			return;
		checked.add(table);
		if (depth > 6)
			return;
		for (String str : query) {
			LuaValue v = table.get(str);
			if (v != null && !v.isnil()) {
				results.add(new SearchResult("Scripting-key",str,url));
				break;
			}
		}
		for (LuaValue key : table.keys()) {
			LuaValue val = table.get(key);
			if (val != null && !val.isnil()) {
				if (val.istable()) {
					search(((LuaTable)val),url + "/" + key.tojstring(), query, results, depth + 1,checked);
				} else {
					for (String str : query) {
						if (val.tojstring().contains(str)) {
							results.add(new SearchResult("Scripting-value",val.tojstring(),url));
							break;
						}
					}
				}
			}
		}
	}
}
