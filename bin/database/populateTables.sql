DROP TABLE UserStats;
DROP TABLE Credentials;
DROP TABLE GameDetail;
DROP TABLE Game;

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
	gameHash varchar(64) NOT NULL,
	fileName varchar(64) NOT NULL,
	PRIMARY KEY (gameID),
	UNIQUE (gameHash)
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

CREATE TABLE UserStats
(
	userID int NOT NULL,
	totalGamePlayer int,
	winnings int,
	losses int,
	FOREIGN KEY (userID) REFERENCES Credentials(userID),
	PRIMARY KEY (userID)
);
