# MySQL install
=======================================  
see MySql Install [https://dev.mysql.com/doc/refman/9.6/en/windows-install-archive.html]


The following instructions are for installing MySQL Server


## [1]: Install MySQL Community Server

a) Download the [MySQL Community Server] (ZIP distribution) from https://dev.mysql.com/downloads/mysql/

> BEWARE!! Download the [Windows (x86, 64-bit), ZIP Archive]

b) Extract the contents of the [MySQL Community Server] archive to `/{dev_home}/db/mysql-server`

c) Create a directory for MySQL's database's data files at `/{dev_home}/db/mysql-data`

d) Create a directory for MySQL's database logging at `/{dev_home}/db/mysql-logs`

e) Create a MySQL options file at `/{dev_home}/db/mysql-server/my.ini` (see my.ini file)

```
# For advice on how to change settings please see
# https://dev.mysql.com/doc/refman/9.6/en/option-files.html

[mysqld]

sql_mode = NO_ENGINE_SUBSTITUTION,STRICT_TRANS_TABLES
# set basedir to your installation path
basedir = "c:/develop/db/mysql-server-9.6.0-winx64"
# set datadir to the location of your data directory
datadir = "c:/develop/db/mysql-data"
# The port number to use when listening for TCP/IP connections. On Unix and Unix-like systems, the port number must be
# 1024 or higher unless the server is started by the root system user.
port = "3306"
# Log errors and startup messages to this file.
log-error = "c:/develop/db/mysql-logs/error_log.err"

[mysqladmin]

user = "root"
port = "3306"
```

## [2]: Initialize MySQL database

> This will create a database files in the location specified in the configuration file.
> It will have root user with no password
> Error messages will be printed on current console window.
> see: https://dev.mysql.com/doc/refman/9.6/en/data-directory-initialization.html

a) Open CMD window:

	{mysql-server}\bin\mysqld.exe --defaults-file="{mysql-server}/my.ini" --initialize-insecure --console
	
> BEWARE!!!!
> If the initialization fails or no message is printed in the console  
> check if this command works:  
>
>      `mysqld.exe --verbose --help`  
> if it does also fail, install the Microsoft Visual C++ Redistributable latest supported downloads  
>
>     `https://learn.microsoft.com/en-us/cpp/windows/latest-supported-vc-redist`

b) Create a batch file to start the MySQL database server with this content:

	{mysql-server}\bin\mysqld.exe --defaults-file="{mysql-server}/my.ini" --console
	
c) Create a batch file to shutdown the MySQL database server with this content:

	{mysql-server}\bin\mysqladmin.exe --defaults-file="{mysql-server}/my.ini" shutdown

## [3]: Install MySQL as a service
	
a) Open CMD window:

	{mysql-server}\bin\mysqld --install r01-mysql --defaults-file={mysql-server}\my.ini
	
## [99]: Recover lost root password: https://dev.mysql.com/doc/refman/5.7/en/resetting-permissions.html

1.- Create a setRootPasswd.pl file with: 

``` 
ALTER USER 'root'@'localhost' IDENTIFIED BY 'newpass'; 
```

2.- Stop mysql

3.- use the command line to start again mysql running the previous script

```
mysqld --defaults-file={mysql-server}\my.ini --init-file={path to the setRootPasswd.pl file} --console
```
