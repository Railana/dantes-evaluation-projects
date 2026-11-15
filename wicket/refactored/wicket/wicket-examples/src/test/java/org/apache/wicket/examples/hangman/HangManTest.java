/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.examples.hangman;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Assert;
import org.junit.Test;

/**
 * Testcase for the <code>Game</code> class.
 * 
 * @author Chris Turner
 * @version 1.0
 */
public class HangManTest extends Assert
{
	/**
	 * Tests the hangman class directly for a winning game.
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_1() throws Exception
	{
		Game hangman = new Game();
		hangman.newGame(5, new WordGenerator(new String[] { "testing" }));

		Integer magicNumber29199491 = 5; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		Assert.assertEquals(magicNumber29199491, hangman.getGuessesRemaining(), () -> "Add assertion message");
		Assert.assertFalse(hangman.isWon(), () -> "Add assertion message");
		Assert.assertFalse(hangman.isLost(), () -> "Add assertion message");

		doGuessTest(hangman, 'a', false);
		Assert.assertEquals(4, hangman.getGuessesRemaining(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: Assert.assertFalse(hangman.isWon(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: Assert.assertFalse(hangman.isLost(), () -> "Add assertion message");

		guess(hangman, 'a');
		// Line removed due to Duplicate Assert: Assert.assertEquals(4, hangman.getGuessesRemaining(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: Assert.assertFalse(hangman.isWon(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: Assert.assertFalse(hangman.isLost(), () -> "Add assertion message");

		doGuessTest(hangman, 't', true);
		// Line removed due to Duplicate Assert: Assert.assertEquals(4, hangman.getGuessesRemaining(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: Assert.assertFalse(hangman.isWon(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: Assert.assertFalse(hangman.isLost(), () -> "Add assertion message");

		doGuessTest(hangman, 'e', true);
		// Line removed due to Duplicate Assert: Assert.assertEquals(4, hangman.getGuessesRemaining(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: Assert.assertFalse(hangman.isWon(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: Assert.assertFalse(hangman.isLost(), () -> "Add assertion message");

		doGuessTest(hangman, 's', true);
		// Line removed due to Duplicate Assert: Assert.assertEquals(4, hangman.getGuessesRemaining(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: Assert.assertFalse(hangman.isWon(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: Assert.assertFalse(hangman.isLost(), () -> "Add assertion message");

		doGuessTest(hangman, 'i', true);
		// Line removed due to Duplicate Assert: Assert.assertEquals(4, hangman.getGuessesRemaining(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: Assert.assertFalse(hangman.isWon(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: Assert.assertFalse(hangman.isLost(), () -> "Add assertion message");

		doGuessTest(hangman, 'n', true);
		// Line removed due to Duplicate Assert: Assert.assertEquals(4, hangman.getGuessesRemaining(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: Assert.assertFalse(hangman.isWon(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: Assert.assertFalse(hangman.isLost(), () -> "Add assertion message");

		doGuessTest(hangman, 'g', true);
		// Line removed due to Duplicate Assert: Assert.assertEquals(4, hangman.getGuessesRemaining(), () -> "Add assertion message");
		Assert.assertTrue(hangman.isWon(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: Assert.assertFalse(hangman.isLost(), () -> "Add assertion message");
	}

	private Letter letter(Game hangman, char c)
	{
		for (Letter letter : hangman.getLetters())
		{
			if (letter.asString().equalsIgnoreCase(Character.toString(c)))
			{
				return letter;
			}
		}
		return null;
	}

	private boolean guess(Game hangman, char c)
	{
		return hangman.guess(letter(hangman, c));
	}

	/**
	 * Tests the hangman class directly for a lost game.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testHangmanLoseGame() throws Exception
	{
		Game hangman = new Game();
		hangman.newGame(2, new WordGenerator(new String[] { "foo" }));

		Assert.assertEquals(2, hangman.getGuessesRemaining(), () -> "Add assertion message");
		Assert.assertFalse(hangman.isWon(), () -> "Add assertion message");
		Assert.assertFalse(hangman.isLost(), () -> "Add assertion message");

		doGuessTest(hangman, 'z', false);
		Assert.assertEquals(1, hangman.getGuessesRemaining(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: Assert.assertFalse(hangman.isWon(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: Assert.assertFalse(hangman.isLost(), () -> "Add assertion message");

		doGuessTest(hangman, 'v', false);
		Assert.assertEquals(0, hangman.getGuessesRemaining(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: Assert.assertFalse(hangman.isWon(), () -> "Add assertion message");
		Assert.assertTrue(hangman.isLost(), () -> "Add assertion message");
	}

	private void clickLetter(WicketTester tester, char ch)
	{
		tester.clickLink("letters:" + (ch - 'a') + ":letter");
	}

	/**
	 * Tests the webapplication for a successful match.
	 */
	@Test
	public void testHangmanSuccessWebGame()
	{
		WicketTester tester = new WicketTester(new HangmanApplication());
		try
		{
			tester.startPage(Home.class, new PageParameters().set("word", "hangman"));
			tester.assertComponent("start", Link.class);
			tester.assertContains("Wicket Examples - hangman");
			tester.clickLink("start");
			tester.assertLabel("guessesRemaining", "5");
			clickLetter(tester, 'f');
			tester.assertLabel("guessesRemaining", "4");
			clickLetter(tester, 'h');
			tester.assertLabel("guessesRemaining", "4");
			clickLetter(tester, 'a');
			clickLetter(tester, 'n');
			clickLetter(tester, 'g');
			clickLetter(tester, 'm');
			tester.assertRenderedPage(Win.class);
		}
		finally
		{
			tester.destroy();
		}
	}

	/**
	 * Tests the webapplication for an unsuccessful match.
	 */
	@Test
	public void testHangmanFailureWebGame()
	{
		WicketTester tester = new WicketTester(new HangmanApplication());
		try
		{
			tester.startPage(Home.class, new PageParameters().set("word", "hangman"));
			tester.assertComponent("start", Link.class);
			tester.assertContains("Wicket Examples - hangman");
			tester.clickLink("start");
			tester.assertLabel("guessesRemaining", "5");
			clickLetter(tester, 'f');
			tester.assertLabel("guessesRemaining", "4");
			clickLetter(tester, 'e');
			tester.assertLabel("guessesRemaining", "3");
			clickLetter(tester, 't');
			tester.assertLabel("guessesRemaining", "2");
			clickLetter(tester, 'x');
			tester.assertLabel("guessesRemaining", "1");
			clickLetter(tester, 'z');
			tester.assertRenderedPage(Lose.class);
		}
		finally
		{
			tester.destroy();
		}
	}

	/**
	 * Performs a guess.
	 * 
	 * @param hangman
	 * @param c
	 * @param expected
	 */
	private void doGuessTest(Game hangman, char c, boolean expected)
	{
		Assert.assertFalse(letter(hangman, c).isGuessed());
		Assert.assertEquals(expected, guess(hangman, c));
		Assert.assertTrue(letter(hangman, c).isGuessed());
	}
}