# Environment
Java 8 and Tomcat 9.

# Components
- BoardState: contains the game state and the logic; it supports different board sizes;
- KalahServlet: contains the players state and logic.

# Limitations
- no authentication/authorization;
- only one game room (no parallel game): only the first two users can play, all the other one will be guests;
- no persistence, all game data are stored in the servlet context: if the server is restarted, the game is lost; also players data are stored in the session;
- simple static web interface, no push mechanism: after a user does a move, the other one should click on the refresh button to see the changes and be enabled to play.

# Usage
1. Connect to http://localhost:8080/kalah/board with two browser (which will be the two players);
2. Click on "Refresh" with the player that has the turn;
3. To move write the number of the house you want to seed in the field "Move house" and click "Submit"
4. Repeat from 2