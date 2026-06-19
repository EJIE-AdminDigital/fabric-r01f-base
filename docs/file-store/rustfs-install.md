
https://rustfs.com/
Download JUST the server https://github.com/rustfs/rustfs/releases/
(do NOT download the installer wi9th the launcher) 


Install dirs
============  
```
	/file-store
		/rustfs
			/rustfs.exe
		/s3-data-store
```
		
Start from the command line:
============================  
```
	c:\develop\file-store\rustfs\rustfs.exe server c:\develop\file-store\s3-data-store --address ":9000" --console-address ":9001" --access-key "admin" --secret-key "password"
```

Install as windows service
==========================  

The following should work:  
```
	sc.exe create r01-rustFS binPath= "c:\develop\file-store\rustfs\rustfs.exe server c:\develop\file-store\s3-data-store --address \":9000\" --console-address \":9001\" --access-key \"admin\" --secret-key \"password\"" DisplayName= "r01-rustFS" 
```
... BUT it does NOT works  
... so SERVY must be used (https://servy-win.github.io/)  
1. Download the PORTABLE version (not the installer) at https://github.com/aelassas/servy/releases  
2. Start Servy and CONFIGURE a new service  
   - Name: r01-rustFS
   - DisplayName: r01-rustFS
   - Service Description: r01-rustFS
   - Process Path: c:\develop\file-store\rustfs\rustfs.exe 
   - Startup dir: c:\develop\file-store\rustfs
   - Process Parameters  
     ```
	 server c:\develop\file-store\s3-data-store --address ":9000" --console-address ":9001" --access-key "admin" --secret-key "password"
	 ```

To delete the service use Servy or `sc.exe delete r01-rustFS`

Admin console: http://localhost:9001/rustfs/console
