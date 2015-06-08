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

package com.baidu.themeanimation.net.sourceforge.jeval.function.string;

import java.util.ArrayList;

import com.baidu.themeanimation.net.sourceforge.jeval.EvaluationConstants;
import com.baidu.themeanimation.net.sourceforge.jeval.Evaluator;
import com.baidu.themeanimation.net.sourceforge.jeval.function.Function;
import com.baidu.themeanimation.net.sourceforge.jeval.function.FunctionConstants;
import com.baidu.themeanimation.net.sourceforge.jeval.function.FunctionException;
import com.baidu.themeanimation.net.sourceforge.jeval.function.FunctionHelper;
import com.baidu.themeanimation.net.sourceforge.jeval.function.FunctionResult;

/**
 * This class is a function that executes within Evaluator. The function returns
 * the absolute value of a double value. See the Math.abs(double) method in the
 * JDK for a complete description of how this function works.
 */
public class Ifelse implements Function {
	/**
	 * Returns the name of the function - "isnull".
	 * 
	 * @return Whether the argument has been assign a value .
	 */
	public String getName() {
		return "ifelse";
	}

	/**
	 * Executes the function for the specified argument. This method is called
	 * internally by Evaluator.
	 * 
	 * @param evaluator
	 *            An instance of Evaluator.
	 * @param arguments
	 *            A string argument that will be converted to a double value and
	 *            evaluated.
	 * 
	 * @return The absolute value of the argument.
	 * 
	 * @exception FunctionException
	 *                Thrown if the argument(s) are not valid for this function.
	 */
	public FunctionResult execute(final Evaluator evaluator, final String arguments)
			throws FunctionException {
		
		String result = null;
		
		String exceptionMessage = "One string argument and two character "
				+ "arguments are required.";

		ArrayList values = FunctionHelper.getStrings(arguments, 
				EvaluationConstants.FUNCTION_ARGUMENT_SEPARATOR);

//		if (values.size() != 3) {
//			throw new FunctionException(exceptionMessage);
//		}

		try {
			Double argumentTwo = Double.valueOf((String)values.get(0));
			
			if(argumentTwo == 0){
				result = (String)values.get(2);
			}else {
				result = (String)values.get(1);
			}
			
		}  catch (Exception e) {
			throw new FunctionException(exceptionMessage, e);
		}
		try {
			Double result_true = Double.valueOf(result); 
			return new FunctionResult(result, 
					FunctionConstants.FUNCTION_RESULT_TYPE_NUMERIC);
		}catch (Exception e){
			return new FunctionResult(result, 
					FunctionConstants.FUNCTION_RESULT_TYPE_STRING);
		}
		
	}
}