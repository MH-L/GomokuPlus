#!/bin/bash
# Removes package declaration of the source files because
# the file structures are different.
echo "$(tail -n +3 ServerMain.java)" > ServerMain.java
echo "$(tail -n +3 ServerGame.java)" > ServerGame.java
echo "$(tail -n +3 ServerConstants.java)" > ServerConstants.java
echo "$(tail -n +3 ConnectionManager.java)" > ConnectionManager.java
echo "$(tail -n +3 HashHelper.java)" > HashHelper.java
echo "$(tail -n +3 LoggingHelper.java)" > LoggingHelper.java
echo "$(tail -n +3 RecordCreator.java)" > RecordCreator.java
echo "$(tail -n +3 XMLException.java)" > XMLException.java
echo "$(tail -n +3 AuthConstants.java)" > AuthConstants.java
echo "$(tail -n +3 AuthService.java)" > AuthService.java
echo "$(tail -n +3 IMove.java)" > IMove.java
echo "$(tail -n +3 ConfHelper.java)" > ConfHelper.java

# find package declaration in file imports (for self-defined classes only)
# and then remove them

# Maybe add a C file to do this??

# Kills the process running on port 1031 (Which is the default port for the game server)
kill -9 $(lsof -i:1031 -t)
# Cleans all .class files
rm -f *.class

# Compiles source files

# Runs source files
