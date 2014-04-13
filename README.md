DebugServer
===========

The default port is 8091

So http://127.0.0.1:8091 will take you to the debug console while it's running.

Example of use:

in LibGDX Application create:

      debugServer = new DebugServer(assets);
			DebugServer.root = this;
			
			//this adds access to an AssetManager's loaded resources
			debugServer.addContent(new AssetContent(debugServer, assets));
			
			//this adds access to an arbitrary object and everything that we can find in it via reflection
			debugServer.addContent(new ObjectContent(debugServer,"Application Root", this,"root"));
			
			try {
				debugServer.start();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
To-Do
============

- Searching through objects
- Exploring files in internal and local storage
