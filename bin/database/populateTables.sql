DROP TABLE Credentials;
DROP TABLE Game;
DROP TABLE GameDetail;

CREATE TABLE Credentials
(
	username varchar(10) NOT NULL,
	userID int NOT NULL AUTO_INCREMENT,
	credential varchar(32),
	password varchar(16),
	PRIMARY KEY (userID),
	UNIQUE (credential)
);

CREATE TABLE Game
(
	gameID int NOT NULL AUTO_INCREMENT,
	fileName varchar(32) NOT NULL,
	PRIMARY KEY (gameID)
);

CREATE TABLE GameDetail
(
	gameID int NOT NULL,
	player1ID int NOT NULL,
	player2ID int NOT NULL,
	gameResult tinyint NOT NULL,
	player1Withdraw int NOT NULL,
	player2Withdraw int NOT NULL,
	FOREIGN KEY (gameID) REFERENCES Game (gameID),
	PRIMARY KEY (gameID)
);
