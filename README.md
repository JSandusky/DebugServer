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
			
			
Features
============
- Explore and download files from internal storage
- See what's loaded in any number of AssetManager instances, download those resources if desired
- Execute scripts (requires implementing a simple compiler interface)
	- You will need grab the appropriate ACE plugins for your scripting language of choice and make a minor tweak to "script.html" as I greatly stripped down ACE to save on file size (everything in "web" ends up in your jar)
- Explore object graphs
- Make minor changes and instantiate objects (provided they have a nullary constructor)

To-Do
============
- Loading script files in the file explorer into the script editor (for convenience vs. downloading them)
- Refine file exploration a bit more, right now it's limited to internal
- Add ranking to search (# of hits most likely)
- Script object exploration interface and reference example for Lua via LuaJ
- Object img interface for drawing debug images (such as overhead maps of the game-world, box2d info, etc)
- Add base StringDumpContent type of content, implement a few abstract methods to add an arbitrary text dump
	- for adding content without being forced to always have the debug server in the project
