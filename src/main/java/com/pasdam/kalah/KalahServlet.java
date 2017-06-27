package com.pasdam.kalah;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that manage a Kalah game between two players
 * 
 * @author paco
 * @version 0.1
 */
public class KalahServlet extends HttpServlet {

	private static final long serialVersionUID = -4617372229443892621L;

	// attributes keys
	private static final String ATTR_CURRENT_PLAYER = "currentPlayer";
	private static final String ATTR_SESSION_PLAYER = "sessionPlayer";
	private static final String ATTR_GAME_STARTED   = "started";
	private static final String ATTR_GAME_STATE     = "state";
	private static final String ATTR_HOUSE          = "house";
	private static final String ATTR_TOKEN          = "token";
	private static final String ATTR_P0_STORE       = "p0Store";
	private static final String ATTR_P0_HOUSES      = "p0Houses";
	private static final String ATTR_P1_STORE       = "p1Store";
	private static final String ATTR_P1_HOUSES      = "p1Houses";

	/**
	 * Retrieves the current game state from the servlet context; if no state
	 * exists it will create a new one and add it to the context.
	 * 
	 * @param request
	 *            used to get the servlet context
	 * @return the current state of the game (not null)
	 */
	private static State getState(HttpServletRequest request) {
		State state = (State) request.getServletContext().getAttribute(ATTR_GAME_STATE);
		if (state == null) {
			state = new State();
			request.getServletContext().setAttribute(ATTR_GAME_STATE, state);
		}
		return state;
	}

	/**
	 * Returns the player's index of the current user (0 or 1), or -1 if the
	 * user is not playing
	 * 
	 * @param request
	 *            used to get the session data
	 * @param state
	 *            current game state
	 * @return the player's index of the current user
	 */
	private static int sessionPlayer(HttpServletRequest request, State state) {
		String sessionToken = (String) request.getSession().getAttribute(ATTR_TOKEN);
		if (sessionToken != null) {
			for (int i = 0; i < state.playerTokens.length; i++) {
				if (sessionToken.equals(state.playerTokens[i])) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * Forwards the state data to a JSP page that displays the board
	 * 
	 * @param request
	 *            servlet request to which set the attributes to read from the
	 *            JSP
	 * @param response
	 *            where write the page
	 * @param state
	 *            current game state
	 * @param sessionPlayer
	 *            player's index of the current user
	 * @throws ServletException
	 *             if the target resource throws this exception
	 * @throws IOException
	 *             if the target resource throws this exception
	 */
	private static void dispatch(HttpServletRequest request, HttpServletResponse response, State state, int sessionPlayer) throws ServletException, IOException {
		boolean started = state.board != null;
		request.setAttribute(ATTR_CURRENT_PLAYER, started ? state.board.getCurrentPlayer() : -1);
		request.setAttribute(ATTR_SESSION_PLAYER, sessionPlayer);
		request.setAttribute(ATTR_GAME_STARTED, started);
		if (started) {
			request.setAttribute(ATTR_P0_HOUSES, state.board.getPlayer0Houses());
			request.setAttribute(ATTR_P0_STORE, state.board.getPlayer0Score());
			request.setAttribute(ATTR_P1_HOUSES, state.board.getPlayer1Houses());
			request.setAttribute(ATTR_P1_STORE, state.board.getPlayer1Score());
		}

		request.getRequestDispatcher("/WEB-INF/board.jsp").forward(request, response);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		State state = getState(request);

		int sessionPlayer = sessionPlayer(request, state);
		if (sessionPlayer < 0) {
			// a token is not associated to the session
			if (state.playerTokens[0] == null) {
				// first player to join
				sessionPlayer = 0;
				state.playerTokens[sessionPlayer] = UUID.randomUUID().toString();
				
				request.getSession().setAttribute(ATTR_TOKEN, state.playerTokens[sessionPlayer]);
				
			} else if (state.playerTokens[1] == null) {
				// second player to join
				sessionPlayer = 1;
				state.playerTokens[sessionPlayer] = UUID.randomUUID().toString();
				
				// initialize board
				state.board = new BoardState(BoardType.KALAH_6_6, new Random().nextInt(2));
				
				request.getSession().setAttribute(ATTR_TOKEN, state.playerTokens[sessionPlayer]);
			} // else: game is full (user is a guest)
		}

		dispatch(request, response, state, sessionPlayer);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		State state = getState(request);
		int sessionPlayer = sessionPlayer(request, state);

		if (sessionPlayer >= 0) {
			if (state.board != null) {
				if (sessionPlayer == state.board.getCurrentPlayer() && !state.board.isCompleted()) {
					String house = request.getParameter(ATTR_HOUSE);
					if (house != null && house.matches("\\d+")) {
						state.board.move(Integer.parseInt(house));

						dispatch(request, response, state, sessionPlayer);
						return;

					} else {
						// bad request: invalid parameter value
						response.setStatus(400); 
					}
				} else {
					// conflict: user can't make the play (it's not his turn or the game is ended)
					response.setStatus(409);
				}
			} else {
				 // not found: game not started yet
				response.setStatus(404);
			}
		} else {
			// unauthorized: user is not playing
			response.setStatus(401);
		}
		
		response.getWriter().write("<h1>Error: " + response.getStatus() + "</h1>");
	}

	/** Game state struct */
	private static class State {
		
		/** Players tokens: [ p0_token, p1_token] */
		public String[] playerTokens = new String[2];
		
		/**	Current board state */
		public BoardState board;
	}
}
