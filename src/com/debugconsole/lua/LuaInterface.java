package com.debugconsole.lua;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Prototype;
import org.luaj.vm2.compiler.LuaC;

import com.badlogic.gdx.utils.Array;
import com.debugconsole.data.ScriptEditor;
import com.debugconsole.data.ScriptEditor.MessageData;
import com.debugconsole.data.ScriptEditor.ScriptData;

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

	//??errrr, I don't like this, it's going to go away
	@Override
	public ScriptData getData(Object of) {
		if (of == null) {
			ScriptData ret = new ScriptData();
			ret.self = globals;
			ret.parent = null;
			for (LuaValue v : globals.keys()) {
				ret.data.put(v.tojstring(), globals.get(v));
			}
			return ret;
		} else {
			if (of instanceof LuaTable) {
				ScriptData ret = new ScriptData();
				ret.self = of;
				ret.parent = null; //???
				for (LuaValue v : ((LuaTable)of).keys()) {
					ret.data.put(v.tojstring(), ((LuaTable)of).get(v));
				}
				return ret;
			} else if (of instanceof LuaValue) {
				//??errm
			}
		}
		return null;
	}

	@Override
	public void setData(ScriptData sd) {
		//???errmm I don't like this either
	}

}
