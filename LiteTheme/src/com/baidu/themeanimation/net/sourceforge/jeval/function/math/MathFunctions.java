/*
 * Copyright 2002-2007 Robert Breidecker.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baidu.themeanimation.net.sourceforge.jeval.function.math;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.baidu.themeanimation.net.sourceforge.jeval.Evaluator;
import com.baidu.themeanimation.net.sourceforge.jeval.function.Function;
import com.baidu.themeanimation.net.sourceforge.jeval.function.FunctionGroup;

/**
 * A groups of functions that can loaded at one time into an instance of
 * Evaluator. This group contains all of the functions located in the
 * com.baidu.themeanimation.net.sourceforge.jeval.function.math package.
 */
public class MathFunctions implements FunctionGroup {
	/**
	 * Used to store instances of all of the functions loaded by this class.
	 */
	private List functions = new ArrayList();

	/**
	 * Default contructor for this class. The functions loaded by this class are
	 * instantiated in this constructor.
	 */
	public MathFunctions() {
		functions.add(new Abs());
		functions.add(new Acos());
		functions.add(new Asin());
		functions.add(new Atan());
		functions.add(new Atan2());
		functions.add(new Ceil());
		functions.add(new Cos());
		functions.add(new Exp());
		functions.add(new Floor());
		functions.add(new IEEEremainder());
		functions.add(new Log());
		functions.add(new Max());
		functions.add(new Min());
		functions.add(new Pow());
		functions.add(new Random());
		functions.add(new Rint());
		functions.add(new Round());
		functions.add(new Sin());
		functions.add(new Sqrt());
		functions.add(new Tan());
		functions.add(new ToDegrees());
		functions.add(new ToRadians());
		// add by jianglin
		functions.add(new Eq());
		functions.add(new Ne());
		functions.add(new Gt());
		functions.add(new Ge());
		functions.add(new Lt());
		functions.add(new Le());
		functions.add(new Digit());
		functions.add(new IsNull());
		functions.add(new Not());
		functions.add(new Paras());
		// add by jianglin
		functions.add(new Len());
	}

	/**
	 * Returns the name of the function group - "numberFunctions".
	 * 
	 * @return The name of this function group class.
	 */
	public String getName() {
		return "numberFunctions";
	}

	/**
	 * Returns a list of the functions that are loaded by this class.
	 * 
	 * @return A list of the functions loaded by this class.
	 */
	public List getFunctions() {
		return functions;
	}

	/**
	 * Loads the functions in this function group into an instance of Evaluator.
	 * 
	 * @param evaluator
	 *            An instance of Evaluator to load the functions into.
	 */
	public void load(final Evaluator evaluator) {
		Iterator functionIterator = functions.iterator();

		while (functionIterator.hasNext()) {
			evaluator.putFunction((Function) functionIterator.next());
		}
	}

	/**
	 * Unloads the functions in this function group from an instance of
	 * Evaluator.
	 * 
	 * @param evaluator
	 *            An instance of Evaluator to unload the functions from.
	 */
	public void unload(final Evaluator evaluator) {
		Iterator functionIterator = functions.iterator();

		while (functionIterator.hasNext()) {
			evaluator.removeFunction(((Function) functionIterator.next())
					.getName());
		}
	}
}
