CREATE TABLE Credentials
(
username varchar(10) NOT NULL,
userID int NOT NULL AUTO_INCREMENT,
credential varchar(16),
password varchar(16),
PRIMARY KEY (userID)
);

CREATE TABLE GameDetail
(
player1ID int NOT NULL,
player2ID int NOT NULL,
gameResult tinyint NOT NULL,
player1Withdraw int NOT NULL,
player2Withdraw int NOT NULL
);

CREATE TABLE Game
(
gameID int NOT NULL AUTO_INCREMENT,
fileName varchar(32) NOT NULL,
gameDetail GameDetail,
PRIMARY KEY (gameID)
);