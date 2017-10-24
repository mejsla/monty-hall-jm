/*
 * Copyright (c) 2014, Johan Maasing <johan.maasing@mejsla.se>
 * All rights reserved.

 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package se.mejsla.montyhall;

import java.security.SecureRandom;

public class MontyHall {

	private boolean runSimulation = false;
	private int simulationRuns = -1;
	private final StringBuilder extraMessages = new StringBuilder();

	public static void main(final String... args) {
		MontyHall app = new MontyHall();
		app.parseArgs(args);
		app.run();
	}

	private void parseArgs(final String[] args) {
		if (args.length > 0) {
			try {
				this.simulationRuns = Integer.parseInt(args[0]);
				this.runSimulation = true;
			} catch (NumberFormatException e) {
				extraMessages.append("I'm sorry, I could not parse: '");
				extraMessages.append(args[0]);
				extraMessages.append("' as a number so I will not run the simulation.");
				this.runSimulation = false;
			}
		}
	}

	private void run() {
		reportInstructions();
		if (this.runSimulation) {
			runSimulation();
		} else {
			runCalculation();
			extraMessages.append("\n");
			extraMessages.append("If you want this program to run a simulation enter a number as a command line ");
			extraMessages.append("argument telling the program how many simulations to run.");
		}
		report(extraMessages);
	}

	private void runCalculation() {
		report("This program has calculated that the chances of winning are:");
		final Result result = calculateResult();
		report(String.format("If you do not switch: %.2f%%", result.notSwitch));
		report(String.format("If you do switch: %.2f%%", result.doSwitch));
	}

	private void runSimulation() {
		final Result result = simulateResult();
		report(String.format("After %d simulations the chances of winning are:", this.simulationRuns)) ;
		report(String.format("If you do not switch: %.2f%%", result.notSwitch));
		report(String.format("If you do switch: %.2f%%", result.doSwitch));
	}

	private void report(final CharSequence message) {
		System.out.println(message);
	}

	private Result calculateResult() {
		// Player selects door one, enumerate the permutations of which door the car is behind
		int accumulatedSwitchWins = 0;
		int accumulatedNotSwitchWins = 0;
		int NUMBER_OF_DOORS = 3;
		for (int n = 0; n < NUMBER_OF_DOORS; n++) {
			// The prize is behind the door the player selected initally
			if (n == 0) {
				accumulatedNotSwitchWins += 1; // The player did not switch so did win
			} else {
				// The prize is behind one of the doors the player did not select initially. 
				// Monty opens the remaining doors with goats except 1, so if player switches it is a win.
				accumulatedSwitchWins += 1;
			}
		}
		return new Result(
				(double) accumulatedNotSwitchWins / (double) NUMBER_OF_DOORS * 100.0, 
				(double) accumulatedSwitchWins / (double) NUMBER_OF_DOORS * 100.0);
	}

	private Result simulateResult() {
		SecureRandom rnd = new SecureRandom();
		report("Running simulation...");
		int accumulatedSwitchWins = 0;
		int accumulatedSwitches = 0 ;
		int accumulatedNotSwitchWins = 0;
		int accumulatedNotSwitches = 0 ;
		for (int n = 0; n < this.simulationRuns; n++) {
			final int winningDoor = rnd.nextInt() % 3;
			if (rnd.nextBoolean()) {
				// Player switches door
				accumulatedSwitches++ ;
				// Since there is only one door to switch too (the others have been opened) player wins if the
				// winning door is not the initial door
				if (winningDoor != 0) {
					accumulatedSwitchWins++;
				}
			} else {
				// Player does not switch door
				accumulatedNotSwitches++ ;
				if (winningDoor == 0) {
					accumulatedNotSwitchWins++;
				}
			}
		}
		return new Result(
				(double) accumulatedNotSwitchWins / (double) accumulatedSwitches * 100.0,
				(double) accumulatedSwitchWins / (double) accumulatedNotSwitches * 100.0);
	}

	private void reportInstructions() {
		report("Welcome to Monty Hall, a brain teaser");
		report("Suppose you're on a game show, and you're given the choice of three doors: Behind one door is a car; behind the others, goats.");
		report("You pick a door, say No. 1, and the host, who knows what's behind the doors, opens another door, say No. 3, which has a goat.");
		report("He then says to you, 'Do you want to pick door No. 2?' Is it to your advantage to switch your choice?");
		report("");
	}

	static final class Result {

		public final double notSwitch;
		public final double doSwitch;

		public Result(double notSwitch, double doSwitch) {
			this.notSwitch = notSwitch;
			this.doSwitch = doSwitch;
		}
	}
}
