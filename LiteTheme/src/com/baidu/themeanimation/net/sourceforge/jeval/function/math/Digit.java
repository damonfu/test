package com.baidu.themeanimation.net.sourceforge.jeval.function.math;

import java.util.ArrayList;

import com.baidu.themeanimation.net.sourceforge.jeval.EvaluationConstants;
import com.baidu.themeanimation.net.sourceforge.jeval.Evaluator;
import com.baidu.themeanimation.net.sourceforge.jeval.function.Function;
import com.baidu.themeanimation.net.sourceforge.jeval.function.FunctionConstants;
import com.baidu.themeanimation.net.sourceforge.jeval.function.FunctionException;
import com.baidu.themeanimation.net.sourceforge.jeval.function.FunctionHelper;
import com.baidu.themeanimation.net.sourceforge.jeval.function.FunctionResult;

/*
 * digit(number, index): return the index digit in the number
 */
public class Digit implements Function{

	public String getName() {
		return "digit";
	}

	public FunctionResult execute(final Evaluator evaluator, final String arguments)
			throws FunctionException {
		int result = 0;

		ArrayList numbers = FunctionHelper.getDoubles(arguments, 
				EvaluationConstants.FUNCTION_ARGUMENT_SEPARATOR);

		if (numbers.size() != 2) {
			throw new FunctionException("Two numeric arguments are required.");
		}

		try {
			double argumentOne = Math.abs(((Double) numbers.get(0)).doubleValue());
			double argumentTwo = Math.abs(((Double) numbers.get(1)).doubleValue());
			
			while (argumentOne > 0) {
				result = (int) (argumentOne%10);
				argumentOne /= 10;
				if (argumentTwo == 0) {
					break;
				}
				argumentTwo--;
			}
			
			if (argumentTwo > 0) {
				result = 0;
			}
		} catch (Exception e) {
			throw new FunctionException("Two numeric arguments are required.", e);
		}

		return new FunctionResult(String.valueOf(result), 
				FunctionConstants.FUNCTION_RESULT_TYPE_NUMERIC);
	}

}
