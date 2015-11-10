#!/bin/bash
# Removes package declaration of the source files because
# the file structures are different.
echo "$(tail -n +3 ServerMain.java)" > ServerMain.java
echo "$(tail -n +3 ServerGame.java)" > ServerGame.java
echo "$(tail -n +3 ServerConstants.java) > ServerConstants.java"
echo "$(tail -n +3 ConnectionManager.java) > ConnectionManager.java"

# Kills the process running on port 1031 (Which is the default port for the game server)
kill -9 $(lsof -i:1031 -t)

# Compiles source files

# Runs source files